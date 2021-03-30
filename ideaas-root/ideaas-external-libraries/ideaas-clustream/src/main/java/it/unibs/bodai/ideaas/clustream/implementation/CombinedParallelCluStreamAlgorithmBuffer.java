package it.unibs.bodai.ideaas.clustream.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.directory.api.util.exception.NotImplementedException;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm;
import it.unibs.bodai.ideaas.custom_libraries.MyAbstractClusterer;
import it.unibs.bodai.ideaas.custom_libraries.MyCluster;
import it.unibs.bodai.ideaas.custom_libraries.MyClustering;
import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.distance_computer.DistanceComputerFactory;
import it.unibs.bodai.ideaas.distance_computer.IDistanceComputer;
import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputer;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;
import moa.core.Measurement;
import utils.CluStreamAlgorithmImplementation;
import utils.StaticVariables;
import utils.UniqueList;

public class CombinedParallelCluStreamAlgorithmBuffer extends MyAbstractClusterer implements ICluStreamAlgorithm {

	private static final long serialVersionUID = 1L;
	
//	private DistanceComputer distanceComputer;
	private IDistanceComputer iDistanceComputerNearest;
	private IDistanceComputer iDistanceComputerOld;
	private IDistanceComputer iDistanceComputerMerge;

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
	private int assignedKernels = 0;
	private List<Integer> oldKernelsId = new UniqueList<>();
	private List<Integer> oldKernelsHistoryId = new UniqueList<>();
	private boolean initialized;
//	private List<MyClustreamKernel> buffer; // Buffer for initialization with kNN
	private int maxQ;
	private double t;
	private int m;
	
//	CluStreamAlgorithmImplementation cluStreamImpl = CluStreamAlgorithmImplementation.NO_PARALLEL;
	
//	public CombinedParallelCluStreamAlgorithmBuffer() throws Exception {
//		this.distanceComputer = new DistanceComputer();
//		this.iDistanceComputer = DistanceComputerFactory.getDAOFactory().getDistanceComputer(CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES);
////		resetLearningImpl();
//	}
//	
	public CombinedParallelCluStreamAlgorithmBuffer(CluStreamAlgorithmImplementation algNearest, CluStreamAlgorithmImplementation algOldMicro, CluStreamAlgorithmImplementation algMerge) throws Exception {
//		this.distanceComputer = new DistanceComputer();
		this.iDistanceComputerNearest = DistanceComputerFactory.getDAOFactory().getDistanceComputer(algNearest);
		this.iDistanceComputerOld = DistanceComputerFactory.getDAOFactory().getDistanceComputer(algOldMicro);
		this.iDistanceComputerMerge = DistanceComputerFactory.getDAOFactory().getDistanceComputer(algMerge);
	}
	
	@Override
	public void resetLearningImpl() {
//		this.kernels = new MyClustreamKernel[maxNumKernelsOption.getValue()];
		this.kernels = new MyClustreamKernel[maxNumKernelsOption.getValue()];
		this.timeWindow = timeWindowOption.getValue();
		this.initialized = false;
//		this.buffer = new LinkedList<MyClustreamKernel>();
		this.maxQ = maxNumKernelsOption.getValue();
		t = kernelRadiFactorOption.getValue();
		m = maxNumKernelsOption.getValue();
		assignedKernels = 0;
	}

//	/* (non-Javadoc)
//	 * @see it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm#resetLearningImpl(it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel[])
//	 */
//	@Override
	public void resetLearningImpl(MyClustreamKernel[] oldKernels, long timestamp) {
		resetLearningImpl();
		this.timestamp = timestamp;
//		if(oldKernels.length >= this.bufferSize) {
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
	}

//	/* (non-Javadoc)
//	 * @see it.unibs.bodai.ideaas.custom_libraries.MyAbstractClusterer#trainOnInstanceImpl(com.yahoo.labs.samoa.instances.Instance)
//	 */
//	@Override
	public void trainOnInstanceImpl(Instances instances) {
		timestamp++;

//		System.out.println("maxQ: "+ maxQ);
//		System.out.println("kernels: "+kernels.length);
//
//		System.out.println("Intput Instances size: "+instances.size());
		
		while (this.assignedKernels < maxQ && instances.size() > 0) {
			Instance inst = instances.get(0);
			
			MyClustreamKernel kern = new MyClustreamKernel(inst, inst.numValues(), timestamp, t, m,  this.kernels.length, this.kernels.length);

//			System.out.println("assignedKernelse: "+this.assignedKernels);
			this.kernels[this.assignedKernels] = kern;
			instances.delete(0);
			this.assignedKernels++;
		}
		if(instances.size() == 0) {
		 return;
		}
//		System.out.println("Instances size: "+instances.size());
//		System.out.println("kernels: "+kernels.size());
//		printKernelsTest(this.kernels);
		List<Integer> outliearsIndex = new ArrayList<>();	
		IntStream.range(0, instances.size()).parallel().forEach(k -> {
			
			System.out.print("=");
//			System.out.println("i= "+i);	
			Instance inst = instances.get(k);
			// 1. Determine closest kernel
			NearestKernel nearestKernel = iDistanceComputerNearest.foundNearestKernel(instances.get(k), kernels);
			
			// 2. Determine if the point belongs to his nearest kernel
			double computeRadius = this.computeRadius(nearestKernel.getNearestKernel(), kernels);

//			System.out.println("Nearest Kernel: "+nearestKernel.getMinDistance());
//			System.out.println("computeRadius: "+computeRadius);
			if ( nearestKernel.getMinDistance() < computeRadius) {
				// Date fits, put into kernel and be happy
				nearestKernel.getNearestKernel().insert(inst, timestamp );

//				printKernelsTest(this.kernels);
				return;
			}else {
				outliearsIndex.add(k);
				
			}
		});

//		printKernelsTest(this.kernels);
		
//		System.out.println("Outliers Instances size: "+outliearsIndex.size());
//		System.out.println("Instances size: "+instances.size());
		
		if(outliearsIndex.size()<=0) {
			return;
		}
//		IntStream.range(0, outliearsIndex.size()).forEach(j -> {
		outliearsIndex.stream().forEach(index -> {

			System.out.print("=");
			// 3. Date does not fit, we need to free
			// some space to insert a new kernel
			//TODO qualche volta è null... Non capisco come cavolo sia possibile...
			if (index != null) {
				Instance inst = instances.get(index);
				if(!this.removeOldKernel(inst, kernels)) {
		
					// 3.2 Merge closest two kernels
					this.mergeClosestKernels(inst, kernels);
				}
			}
		});
		
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
//	
//	public MyClustreamKernel[] getKernelsArray() {
//		MyClustreamKernel[] k_list = new MyClustreamKernel[this.kernels.size()];
//		for (int i=0; i< this.kernels.size(); i++) {
//			k_list[i] = this.kernels.get(i);
//		}
//		return k_list;
//	}
	
	
	
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
			radius = iDistanceComputerNearest.computeMinDistanceKernels(kernel, kernels);
//			System.out.println("Radius: "+radius);
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
		
		KernelAge kernelAge = iDistanceComputerOld.getOldestKernel(kernels);
		
		if (kernelAge.getAge()<threshold) {
//			System.out.println("Remove based on density");
//			MyClustreamKernel k = kernels.get(kernelAge.getOldestKernel().getPosition());
			MyClustreamKernel newK = new MyClustreamKernel( instance, instance.numValues(), timestamp, t, m, this.getMaxID(), kernelAge.getOldestKernel().getPosition());
			kernels[kernelAge.getOldestKernel().getPosition()] =  newK;
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
		
		NearesKernelsPositionsInArray nearestPositions = iDistanceComputerMerge.foundPositionsNearestKernels(kernels);
		
		assert (nearestPositions.getClosestA() != nearestPositions.getClosestB());

//		System.out.println("closestA: "+nearestPositions.getClosestA());
//		System.out.println("closestB: "+nearestPositions.getClosestB());
		

		MyClustreamKernel kernelA =  kernels[nearestPositions.getClosestA()];
		MyClustreamKernel kernelB =  kernels[nearestPositions.getClosestB()];
				
		int idClosestA = kernelA.getIdMicroCluster();
		kernelA.add( kernelB );
		kernelA.setIdMicroCluster(this.getMaxID());
		
		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(idClosestA)) {
			kernelA.addParentId(idClosestA);	
		}
		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(kernelB.getIdMicroCluster())) {
			kernelA.addParentId(kernelB.getIdMicroCluster());
		}
		if (!this.oldKernelsHistoryId.isEmpty() && this.oldKernelsHistoryId.contains(idClosestA)) {
			kernelA.addParentHistoryId(idClosestA);
		}
		if (!this.oldKernelsHistoryId.isEmpty() && this.oldKernelsHistoryId.contains(kernelB.getIdMicroCluster())) {
			kernelA.addParentHistoryId(kernelB.getIdMicroCluster());
		}
		
		MyClustreamKernel newKernel = new MyClustreamKernel( instance, instance.numValues(), timestamp, t,  m, this.getMaxID(), nearestPositions.getClosestB());
		kernels[nearestPositions.getClosestB()] = newKernel;
		//		kernelB = newKernel;
	}

	public void setNumMicroKernels(int maxKernels) {
		this.maxQ = maxKernels;
		this.maxNumKernelsOption = new IntOption(
				"maxNumKernels", 'm',
				"Maximum number of micro kernels to use.", maxKernels);
	}
	

	public void setTimeHorizont(int timeHorizont) {
		this.timeWindowOption = new IntOption("horizon",
				'h', "Rang of the window.", timeHorizont);
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public MyClustering getClusteringResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trainOnInstanceImpl(Instance inst) {
		throw new NotImplementedException("In this version is not possible to train single Instances");
		// TODO Auto-generated method stub
		
	}
	
	public void printKernelsTest(List<MyClustreamKernel> kernels) {
		System.out.print("\n[");
		for (MyClustreamKernel myClustreamKernel : kernels) {
			System.out.print(" "+myClustreamKernel.getCenter()[0]+";");
		}
		System.out.print("]\n");
	}
}
