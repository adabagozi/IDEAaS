package it.unibs.bodai.ideaas.clustream.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.spark.api.java.JavaSparkContext;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm;
import it.unibs.bodai.ideaas.custom_libraries.MyAbstractClusterer;
import it.unibs.bodai.ideaas.custom_libraries.MyCluster;
import it.unibs.bodai.ideaas.custom_libraries.MyClustering;
import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.distance_computer.DistanceComputerFactory;
import it.unibs.bodai.ideaas.distance_computer.IDistanceComputer;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;
import moa.core.Measurement;
import scala.NotImplementedError;
import utils.CluStreamAlgorithmImplementation;
import utils.StaticVariables;
import utils.UniqueList;

public class MyParallelCluStreamAlgorithm extends MyAbstractClusterer implements ICluStreamAlgorithm {

	private static final long serialVersionUID = 1L;

	private IDistanceComputer iDistanceComputer;

	public IntOption timeWindowOption = new IntOption("horizon",
			'h', "Rang of the window.", StaticVariables.TIME_WINDOW);

	public IntOption maxNumKernelsOption = new IntOption(
			"maxNumKernels", 'm',
			"Maximum number of micro kernels to use.", 100);

	public IntOption kernelRadiFactorOption = new IntOption(
			"kernelRadiFactor", 't',
			"Multiplier for the kernel radius", 2);

	public IntOption kOption = new IntOption(
			"k", 'k',
			"k of macro k-means (number of clusters)", 5);

	private int timeWindow;
	private long timestamp = -1;
	private MyClustreamKernel[] kernels;
	private List<Integer> oldKernelsId = new UniqueList<>();
	private List<Integer> oldKernelsHistoryId = new UniqueList<>();
	private boolean initialized;
	private List<MyClustreamKernel> buffer; // Buffer for initialization with kNN
	private int bufferSize;
	private double t;
	private int m;

	CluStreamAlgorithmImplementation cluStreamImpl = CluStreamAlgorithmImplementation.NO_PARALLEL;

	public MyParallelCluStreamAlgorithm() {
		this.iDistanceComputer = DistanceComputerFactory.getDAOFactory().getDistanceComputer();
	}

	public MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation cluStreamImpl) throws Exception {
		this.cluStreamImpl = cluStreamImpl;
		if(cluStreamImpl == CluStreamAlgorithmImplementation.SPARK_PARALLEL_BOTH || 
				cluStreamImpl == CluStreamAlgorithmImplementation.SPARK_PARALLEL_FEATURES ||
				cluStreamImpl == CluStreamAlgorithmImplementation.SPARK_PARALLEL_SYNTHESES) {

			this.iDistanceComputer = DistanceComputerFactory.getDAOFactory().getDistanceComputer(cluStreamImpl);
		}else {

			this.iDistanceComputer = DistanceComputerFactory.getDAOFactory().getDistanceComputer(cluStreamImpl);
		}
	}

	public MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation cluStreamImpl, JavaSparkContext sparkContext) throws Exception {
		this.cluStreamImpl = cluStreamImpl;
		this.iDistanceComputer = DistanceComputerFactory.getDAOFactory().getDistanceComputer(cluStreamImpl, sparkContext);
	}
	
	@Override
	public void resetLearningImpl() {
		this.kernels = new MyClustreamKernel[maxNumKernelsOption.getValue()];
		this.timeWindow = timeWindowOption.getValue();
		this.initialized = false;
		this.buffer = new LinkedList<MyClustreamKernel>();
		this.bufferSize = maxNumKernelsOption.getValue();
		t = kernelRadiFactorOption.getValue();
		m = maxNumKernelsOption.getValue();
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm#resetLearningImpl(it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel[])
	 */
	@Override
	public void resetLearningImpl(MyClustreamKernel[] oldKernels, long timestamp) {
		resetLearningImpl();
		this.timestamp = timestamp;
		if(oldKernels.length >= this.bufferSize) {
			this.initialized = true;

			this.kernels = oldKernels.clone(); //Costosa in termini computazionale

			for (MyClustreamKernel mCK: oldKernels) {
				mCK.setParentId(new UniqueList<>());

				mCK.addParentId(mCK.getIdMicroCluster());
				oldKernelsId.add(mCK.getIdMicroCluster());
				oldKernelsHistoryId.addAll(mCK.getParentHistoryId());
				oldKernelsHistoryId.add(mCK.getIdMicroCluster());


			}
			if(oldKernelsHistoryId.isEmpty()) {
				oldKernelsHistoryId.addAll(oldKernelsId);
			}
		}else {
			for (MyClustreamKernel mCK: oldKernels) {
				mCK.setParentId(new UniqueList<>());
				mCK.addParentId(mCK.getIdMicroCluster());
				buffer.add( mCK );
				oldKernelsId.add(mCK.getIdMicroCluster());
				oldKernelsHistoryId.addAll(mCK.getParentHistoryId());
				oldKernelsHistoryId.add(mCK.getIdMicroCluster());

			}
			if(oldKernelsHistoryId.isEmpty()) {
				oldKernelsHistoryId.addAll(oldKernelsId);
			}
		}
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.custom_libraries.MyAbstractClusterer#trainOnInstanceImpl(com.yahoo.labs.samoa.instances.Instance)
	 */
	@Override
	public void trainOnInstanceImpl(Instance instance) {
		timestamp++;

		// 0. Initialize
		if (!initialized) {
			if (buffer.size() < bufferSize) {
				MyClustreamKernel kern = new MyClustreamKernel(instance, instance.numValues(), timestamp, t, m, -1, buffer.size());
				buffer.add(kern);

				return;
			} else {
				//TODO valutare se possibile inserire un parallel
				for (int i = 0; i < buffer.size(); i++) {
					kernels[i] = new MyClustreamKernel(new DenseInstance(1.0, buffer.get(i).getCenter()), instance.numValues(), timestamp, t, m, i, i);
					//					kernels[i].instances.add(instance);

				}
				buffer.clear();
				initialized = true;
			}
		}

		// 1. Determine closest kernel
		NearestKernel nearestKernel = iDistanceComputer.foundNearestKernel(instance, kernels);

		// 2. Determine if the point belongs to his nearest kernel

//		System.out.println("Nearest Kernel: "+nearestKernel.getMinDistance());

		double computeRadius = this.computeRadius(nearestKernel.getNearestKernel(), kernels);

//		System.out.println("computeRadius: "+computeRadius);
		if ( nearestKernel.getMinDistance() < this.computeRadius(nearestKernel.getNearestKernel(), kernels)) {
			// Date fits, put into kernel and be happy
			nearestKernel.getNearestKernel().insert( instance, timestamp );

			return;
		}
		// 3. Date does not fit, we need to free
		// some space to insert a new kernel
		if(!this.removeOldKernel(instance, kernels)){
			// 3.2 Merge closest two kernels
			this.mergeClosestKernels(instance, kernels);
		}
	}

	@Override
	public MyClustering getMicroClusteringResult() {
		if (!initialized) {
			return new MyClustering(new MyCluster[0]);
		}

		//		MyClustreamKernel[] result = new MyClustreamKernel[kernels.length];
		MyCluster[] resultCluster = new MyCluster[kernels.length];
		for (int i = 0; i < resultCluster.length; i++) {
			MyClustreamKernel temp = new MyClustreamKernel(kernels[i], t, m, kernels[i].getIdMicroCluster(), i);
			resultCluster[i] = temp;
			resultCluster[i].addKernel(temp);
		}
		//		System.out.println("result: "+resultCluster.length);
		MyClustering t = new MyClustering(resultCluster);
		return t;
	}


	public String getName() {
		return "CluStreamWithKMeans " + timeWindow;
	}






	/** Miscellaneous **/

	@Override
	public boolean implementsMicroClusterer() {
		return true;
	}

	public boolean isRandomizable() {
		return false;
	}

	public double[] getVotesForInstance(Instance inst) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void getModelDescription(StringBuilder out, int indent) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	public int getMaxID() {
		int id = -1;
		//TODO Parallel stream
		for (MyClustreamKernel k: this.kernels){
			if(id <= k.getIdMicroCluster()) {
				id = k.getIdMicroCluster();
			}
		}
		return id+1;
	}

	public MyClustreamKernel[] getKernels() {
		return this.kernels;
	}


	/*private NearestKernel foundNearestKernel(Instance instance, MyClustreamKernel[] kernels) {
		// 1. Determine closest kernel
		MyClustreamKernel closestKernel = null;
		double minDistance = Double.MAX_VALUE;
		for ( int i = 0; i < kernels.length; i++ ) {
			double distance = distance(instance.toDoubleArray(), kernels[i].getCenter());
			if (distance < minDistance) {
				closestKernel = kernels[i];
				minDistance = distance;
			}
		}
		return new NearestKernel(minDistance, closestKernel);
	}*/

	/**
	 * Calcola il raggio di un kernel. Se il kernel ha un solo punto allora il suo raggio e' pari 
	 * alla distanza tra il kernel ed un altro kernel diverso più vicino
	 * @param kernel
	 * @param kernels
	 * @return
	 */
	private double computeRadius(MyClustreamKernel kernel, MyClustreamKernel[] kernels) {
		// 2. Check whether instance fits into closestKernel
		double radius = 0.0;
		if ( kernel.getWeight() == 1 ) {
			// Special case: estimate radius by determining the distance to the
			// next closest cluster
			radius = iDistanceComputer.computeMinDistanceKernels(kernel, kernels);
		} else {
			radius = kernel.getRadius();
		}
		return radius;
	}



	/**
	 * Metodo per l'eliminazione di un kernel che non supera la threshold.
	 * TODO implementare l'eliminazione a due step.
	 * @param instance
	 * @param kernels
	 */
	private boolean removeOldKernel(Instance instance, MyClustreamKernel[] kernels) {

		long threshold = timestamp - timeWindow; // Kernels before this can be forgotten

		KernelAge kernelAge = iDistanceComputer.getOldestKernel(kernels);

		if (kernelAge.getAge()<threshold) {
			//			System.out.println("Remove based on density");
			kernels[kernelAge.getOldestKernel().getPosition()] = new MyClustreamKernel( instance, instance.numValues(), timestamp, t, m, this.getMaxID(), kernelAge.getOldestKernel().getPosition());
			return true;
		}
		return false;
	}


	/**
	 * Metodo per l'unione dei due kernel piu' vicini al fine di liberare spazio per il nuovo punto
	 * @param instance
	 * @param kernels
	 */
	private void mergeClosestKernels(Instance instance, MyClustreamKernel[] kernels) {

		NearesKernelsPositionsInArray nearestPositions = iDistanceComputer.foundPositionsNearestKernels(kernels);

		assert (nearestPositions.getClosestA() != nearestPositions.getClosestB());

		//		System.out.println("closestA: "+nearestPositions.getClosestA());
		//		System.out.println("closestB: "+nearestPositions.getClosestB());

		int idClosestA = kernels[nearestPositions.getClosestA()].getIdMicroCluster();
		int idClosestB = kernels[nearestPositions.getClosestB()].getIdMicroCluster();
		kernels[nearestPositions.getClosestA()].add( kernels[nearestPositions.getClosestB()] );
		kernels[nearestPositions.getClosestA()].setIdMicroCluster(this.getMaxID());

		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(idClosestA)) {
			kernels[nearestPositions.getClosestA()].addParentId(idClosestA);	
		}
		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(kernels[nearestPositions.getClosestB()].getIdMicroCluster())) {
			kernels[nearestPositions.getClosestA()].addParentId(kernels[nearestPositions.getClosestB()].getIdMicroCluster());
		}
		if (!this.oldKernelsHistoryId.isEmpty() && this.oldKernelsHistoryId.contains(idClosestA)) {
			kernels[nearestPositions.getClosestA()].addParentHistoryId(idClosestA);
		}
		if (!this.oldKernelsHistoryId.isEmpty() && this.oldKernelsHistoryId.contains(kernels[nearestPositions.getClosestB()].getIdMicroCluster())) {
			kernels[nearestPositions.getClosestA()].addParentHistoryId(kernels[nearestPositions.getClosestB()].getIdMicroCluster());
		}


		MyClustreamKernel newKernel = new MyClustreamKernel( instance, instance.numValues(), timestamp, t,  m, this.getMaxID(), nearestPositions.getClosestB());
		kernels[nearestPositions.getClosestB()] = newKernel;
	}

	@Override
	public void setNumMicroKernels(int maxKernels) {
		this.maxNumKernelsOption = new IntOption(
				"maxNumKernels", 'm',
				"Maximum number of micro kernels to use.", maxKernels);
	}


	@Override
	public void setTimeHorizont(int timeHorizont) {
		this.timeWindowOption = new IntOption("horizon",
				'h', "Rang of the window.", timeHorizont);
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public MyClustering getClusteringResult() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm#trainOnInstanceImpl(com.yahoo.labs.samoa.instances.Instances)
	 */
	@Override
	public void trainOnInstanceImpl(Instances instances) {
		throw new NotImplementedError("Train instances is not implemented in this class");
		
	}


}
