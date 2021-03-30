package it.unibs.bodai.ideaas.clustream.implementation;

/**
 * [CluStream_kMeans.java]
 * CluStream with k-means as macroclusterer
 * 
 * Appeared in seminar paper "Understanding of Internal Clustering Validation Measure in Streaming Environment" (Yunsu Kim)
 * for the course "Seminar: Data Mining and Multimedia Retrival" in RWTH Aachen University, WS 12/13
 * 
 * @author Yunsu Kim
 * based on the code of Timm Jansen (moa@cs.rwth-aachen.de)
 * Data Management and Data Exploration Group, RWTH Aachen University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 *    
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm;
import it.unibs.bodai.ideaas.custom_libraries.MyAbstractClusterer;
import it.unibs.bodai.ideaas.custom_libraries.MyCFCluster;
import it.unibs.bodai.ideaas.custom_libraries.MyCluster;
import it.unibs.bodai.ideaas.custom_libraries.MyClustering;
import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.custom_libraries.MySphereCluster;
import it.unibs.bodai.ideaas.distance_computer.DistanceComputerFactory;
import it.unibs.bodai.ideaas.distance_computer.IDistanceComputer;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import moa.core.Measurement;
import scala.NotImplementedError;
import utils.StaticVariables;
import utils.UniqueList;
import utils.VectorsUtility;

public class MyCluStreamAlgorithmNoDensity extends MyAbstractClusterer implements ICluStreamAlgorithm {

	private static final long serialVersionUID = 1L;
	
	private IDistanceComputer iDistanceComputer;

	public IntOption timeWindowOption = new IntOption("horizon",
			'h', "Rang of the window.", StaticVariables.TIME_WINDOW);

	public IntOption maxNumKernelsOption = new IntOption(
			"maxNumKernels", 'm',
			"Maximum number of micro kernels to use.", 100);

	public IntOption kernelRadiFactorOption = new IntOption(
			"kernelRadiFactor", 't',
			"Multiplier for the kernel radius", StaticVariables.KERNEL_RADIUS_FACTOR);

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
	
	public MyCluStreamAlgorithmNoDensity() {
		this.iDistanceComputer = DistanceComputerFactory.getDAOFactory().getDistanceComputer();
	}
	
	public MyCluStreamAlgorithmNoDensity(String distanceIdentificator) {
		this.iDistanceComputer = DistanceComputerFactory.getDAOFactory().getDistanceComputer(distanceIdentificator);
	}

	@Override
	public void resetLearningImpl() {

//		System.out.println("Based only on age");
//		System.out.println(maxNumKernelsOption.getValue());
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

//			System.out.println(Arrays.toString(oldKernels));
			for (MyClustreamKernel mCK: oldKernels) {
//				System.out.println("Kernel timestamp: " + mCK.getT() + "     LST: " + mCK.getLST()+ "     SST: " + mCK.getSST());
				mCK.setParentId(new UniqueList<>());
				
				mCK.addParentId(mCK.getIdMicroCluster());
				
				oldKernelsId.add(mCK.getIdMicroCluster());
//				System.out.println( mCK.getParentHistoryId().toString());
				oldKernelsHistoryId.addAll(mCK.getParentHistoryId());
				oldKernelsHistoryId.add(mCK.getIdMicroCluster());
				
			
			}
			if(oldKernelsHistoryId.isEmpty()) {
				oldKernelsHistoryId.addAll(oldKernelsId);
			}
		}else {
			for (MyClustreamKernel mCK: oldKernels) {
//				System.out.println("Kernel timestamp: " + mCK.getT() + "     LST: " + mCK.getLST()+ "     SST: " + mCK.getSST());
				mCK.setParentId(new UniqueList<>());
				mCK.addParentId(mCK.getIdMicroCluster());
				buffer.add( mCK );
				oldKernelsId.add(mCK.getIdMicroCluster());
//				System.out.println( mCK.getParentHistoryId().toString());
				oldKernelsHistoryId.addAll(mCK.getParentHistoryId());
				oldKernelsHistoryId.add(mCK.getIdMicroCluster());
				
			}
			if(oldKernelsHistoryId.isEmpty()) {
				oldKernelsHistoryId.addAll(oldKernelsId);
			}
		}
//		System.out.println(Arrays.toString(kernels));
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.custom_libraries.MyAbstractClusterer#trainOnInstanceImpl(com.yahoo.labs.samoa.instances.Instance)
	 */
	@Override
	public void trainOnInstanceImpl(Instance instance) {
//		int dim = instance.numValues();
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
					kernels[i] = new MyClustreamKernel(new DenseInstance(1.0, buffer.get(i).getCenter()), instance.numValues(), timestamp, t, m, i, buffer.size());
//					kernels[i].instances.add(instance);
					
				}
				buffer.clear();
				initialized = true;
//				return; e' necessario togliere return per evitare di perdere il dato di confine
			}
		}
		
		// 1. Determine closest kernel
		NearestKernel nearestKernel = iDistanceComputer.foundNearestKernel(instance, kernels);
		// 2. Determine if the point belongs to his nearest kernel
		if ( nearestKernel.getMinDistance() < this.computeRadius(nearestKernel.getNearestKernel(), kernels)) {
			// Date fits, put into kernel and be happy
			nearestKernel.getNearestKernel().insert( instance, timestamp );
			
			return;
		}
		// 3. Date does not fit, we need to free
		// some space to insert a new kernel
		this.removeOldKernel(instance, kernels);

		// 3.2 Merge closest two kernels
		this.mergeClosestKernels(instance, kernels);
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

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.custom_libraries.MyClusterer#getClusteringResult()
	 */
	@Override
	public MyClustering getClusteringResult() {
		if (!initialized) {
			return new MyClustering(new MyCluster[0]);
		}
		MyClustering temp = kMeans_rand(kOption.getValue(), getMicroClusteringResult());
		return temp;
	}

	public MyClustering getClusteringResult(MyClustering gtClustering) {
		return kMeans_gta(kOption.getValue(), getMicroClusteringResult(), gtClustering);
	}

	public String getName() {
		return "CluStreamWithKMeans " + timeWindow;
	}



	/**
	 * k-means of (micro)clusters, with ground-truth-aided initialization.
	 * (to produce best results) 
	 * 
	 * @param k
	 * @param data
	 * @return (macro)clustering - CFClusters
	 */
	public static MyClustering kMeans_gta(int k, MyClustering clustering, MyClustering gtClustering) {

		ArrayList<MyCFCluster> microclusters = new ArrayList<MyCFCluster>();
		for (int i = 0; i < clustering.size(); i++) {
			if (clustering.get(i) instanceof MyCFCluster) {
				microclusters.add((MyCFCluster)clustering.get(i));
			} else {
				System.out.println("Unsupported Cluster Type:" + clustering.get(i).getClass() + ". Cluster needs to extend moa.cluster.CFCluster");
			}
		}

		int n = microclusters.size();
		assert (k <= n);

		/* k-means */
		Random random = new Random(0);
		MyCluster[] centers = new MyCluster[k];
		int K = gtClustering.size();

		for (int i = 0; i < k; i++) {
			if (i < K) {	// GT-aided
				centers[i] = new MySphereCluster(gtClustering.get(i).getCenter(), 0);
			} else {		// Randomized
				int rid = random.nextInt(n);
				centers[i] = new MySphereCluster(microclusters.get(rid).getCenter(), 0);
			}
		}

		return cleanUpKMeans(kMeans(k, centers, microclusters), microclusters);
	}

	/**
	 * k-means of (micro)clusters, with randomized initialization. 
	 * 
	 * @param k
	 * @param data
	 * @return (macro)clustering - CFClusters
	 */
	public static MyClustering kMeans_rand(int k, MyClustering clustering) {

		ArrayList<MyCFCluster> microclusters = new ArrayList<MyCFCluster>();
		for (int i = 0; i < clustering.size(); i++) {
			if (clustering.get(i) instanceof MyCFCluster) {
				microclusters.add((MyCFCluster) clustering.get(i));
			} else {
				System.out.println("Unsupported Cluster Type:" + clustering.get(i).getClass() + ". Cluster needs to extend moa.cluster.CFCluster");
			}
		}

		System.out.println("k: "+k+"       microk: "+microclusters.size());

		int n = microclusters.size();
		assert (n > k);

		/* k-means */
		Random random = new Random(0);
		MyCluster[] centers = new MyCluster[k];

		for (int i = 0; i < k; i++) {
			int rid = random.nextInt(n);
			centers[i] = new MySphereCluster(microclusters.get(rid).getCenter(), 0);
		}

		//		System.out.println("centers: "+ centers.length);

		MyClustering kM = kMeans(k, centers, microclusters);
		MyClustering temp = cleanUpKMeans(kM, microclusters);
		return temp;
	}

	/**
	 * (The Actual Algorithm) k-means of (micro)clusters, with specified initialization points.
	 * 
	 * @param k
	 * @param centers - initial centers
	 * @param data
	 * @return (macro)clustering - SphereClusters
	 */
	protected static MyClustering kMeans(int k, MyCluster[] centers, List<? extends MyCluster> data) {
		assert (centers.length == k);
		assert (k > 0);

		int dimensions = centers[0].getCenter().length;

		ArrayList<ArrayList<MyCluster>> clustering = new ArrayList<ArrayList<MyCluster>>();
		for (int i = 0; i < k; i++) {
			clustering.add(new ArrayList<MyCluster>());
		}

		while (true) {
			// Assign points to clusters
			for (MyCluster point : data) {
				double minDistance = VectorsUtility.distance(point.getCenter(), centers[0].getCenter());
				int closestCluster = 0;
				for (int i = 1; i < k; i++) {
					double distance = VectorsUtility.distance(point.getCenter(), centers[i].getCenter());
					if (distance < minDistance) {
						closestCluster = i;
						minDistance = distance;
					}
				}

				clustering.get(closestCluster).add(point);
			}



			// Calculate new centers and clear clustering lists
			MySphereCluster[] newCenters = new MySphereCluster[centers.length];
			for (int i = 0; i < k; i++) {
				newCenters[i] = calculateCenter(clustering.get(i), dimensions);
				clustering.get(i).clear();
			}

			// Convergence check
			boolean converged = true;
			for (int i = 0; i < k; i++) {
				if (!Arrays.equals(centers[i].getCenter(), newCenters[i].getCenter())) {
					converged = false;
					break;
				}
			}

			if (converged) {
				break;
			} else {
				centers = newCenters;
			}
		}

		return new MyClustering(centers);
	}

	/**
	 * Rearrange the k-means result into a set of CFClusters, cleaning up the redundancies.
	 * 
	 * @param kMeansResult
	 * @param microclusters
	 * @return
	 */
	protected static MyClustering cleanUpKMeans(MyClustering kMeansResult, ArrayList<MyCFCluster> microclusters) {
		/* Convert k-means result to CFClusters */		int k = kMeansResult.size();
		MyCFCluster[] converted = new MyCFCluster[k];

		for (MyCFCluster mc : microclusters) {
			// Find closest kMeans cluster
			double minDistance = Double.MAX_VALUE;
			int closestCluster = 0;
			for (int i = 0; i < k; i++) {
				double distance = VectorsUtility.distance(kMeansResult.get(i).getCenter(), mc.getCenter());
				if (distance < minDistance) {
					closestCluster = i;
					minDistance = distance;
				}
			}

			// Add to cluster
 			ArrayList<MyClustreamKernel> kernels = mc.getKernels();
			if ( converted[closestCluster] == null ) {
				converted[closestCluster] = (MyCFCluster) mc.copy();
				converted[closestCluster].setKernels(kernels);
			} else {
				converted[closestCluster].add(mc);
				converted[closestCluster].getKernels().addAll(kernels);
			}

		}

		// Clean up
		int count = 0;
		for (int i = 0; i < converted.length; i++) {
			if (converted[i] != null)
				count++;
		}

		MyCFCluster[] cleaned = new MyCFCluster[count];
		count = 0;
		for (int i = 0; i < converted.length; i++) {
			if (converted[i] != null)
				cleaned[count++] = converted[i];
		}

		return new MyClustering(cleaned);
	}



	/**
	 * k-means helper: Calculate a wrapping cluster of assigned points[microclusters].
	 * 
	 * @param assigned
	 * @param dimensions
	 * @return SphereCluster (with center and radius)
	 */
	private static MySphereCluster calculateCenter(ArrayList<MyCluster> assigned, int dimensions) {
		double[] result = new double[dimensions];
		for (int i = 0; i < result.length; i++) {
			result[i] = 0.0;
		}

		if (assigned.size() == 0) {
			return new MySphereCluster(result, 0.0);
		}

		for (MyCluster point : assigned) {
			double[] center = point.getCenter();
			for (int i = 0; i < result.length; i++) {
				result[i] += center[i];
			}
		}

		// Normalize
		for (int i = 0; i < result.length; i++) {
			result[i] /= assigned.size();
		}

		// Calculate radius: biggest wrapping distance from center
		double radius = 0.0;

		ArrayList<MyClustreamKernel> kernels = new ArrayList<MyClustreamKernel>();
		for (MyCluster point : assigned) {
			double dist = VectorsUtility.distance(result, point.getCenter());
			if (dist > radius) {
				radius = dist;
			}
			kernels.addAll(point.getKernels());
		}
		MySphereCluster sc = new MySphereCluster(result, radius, kernels);
		//		sc.setKernels(kernels);
		sc.setWeight(assigned.size());
		return sc;
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
	private void removeOldKernel(Instance instance, MyClustreamKernel[] kernels) {

		long threshold = timestamp - timeWindow; // Kernels before this can be forgotten
		//TODO vedere bene la definizione della threshold
		// 3.1 Try to forget old kernels
		for ( int i = 0; i < kernels.length; i++ ) {
			if ( kernels[i].getRelevanceStamp() < threshold ) {
//				System.out.println("Remove based only on age");
				kernels[i] = new MyClustreamKernel( instance, instance.numValues(), timestamp, t, m, this.getMaxID(), i);
				return;
			}
		}
	}
	
	/**
	 * Metodo per l'unione dei due kernel piu' vicini al fine di liberare spazio per il nuovo punto
	 * @param instance
	 * @param kernels
	 */
	private void mergeClosestKernels(Instance instance, MyClustreamKernel[] kernels) {
		NearesKernelsPositionsInArray nearestPositions = iDistanceComputer.foundPositionsNearestKernels(kernels);
		
		assert (nearestPositions.getClosestA() != nearestPositions.getClosestB());
		int idClosestA = kernels[nearestPositions.getClosestA()].getIdMicroCluster();
		int idClosestB = kernels[nearestPositions.getClosestB()].getIdMicroCluster();
		kernels[nearestPositions.getClosestA()].add( kernels[nearestPositions.getClosestB()] );
		kernels[nearestPositions.getClosestA()].setIdMicroCluster(this.getMaxID());
//		System.out.println("La sintesi: " + idClosestA + " si è unita alla sintesi "+idClosestB + " generando la sintesi: "+kernels[nearestPositions.getClosestA()].getIdMicroCluster());
		
		
		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(idClosestA)) {
//			System.out.println("--> La sintesi "+idClosestA+ " è dello step precedente");
			kernels[nearestPositions.getClosestA()].addParentId(idClosestA);	
		}
		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(kernels[nearestPositions.getClosestB()].getIdMicroCluster())) {
//			System.out.println("--> La sintesi "+idClosestB+ " è dello step precedente");
			kernels[nearestPositions.getClosestA()].addParentId(kernels[nearestPositions.getClosestB()].getIdMicroCluster());
		}
		if (!this.oldKernelsHistoryId.isEmpty() && this.oldKernelsHistoryId.contains(idClosestA)) {
//			System.out.println("--> La sintesi "+idClosestA+ " è nella history");
			kernels[nearestPositions.getClosestA()].addParentHistoryId(idClosestA);
		}
		if (!this.oldKernelsHistoryId.isEmpty() && this.oldKernelsHistoryId.contains(kernels[nearestPositions.getClosestB()].getIdMicroCluster())) {
//			System.out.println("--> La sintesi "+idClosestB+ " è nella history");
			kernels[nearestPositions.getClosestA()].addParentHistoryId(kernels[nearestPositions.getClosestB()].getIdMicroCluster());
		}
		
		
		MyClustreamKernel newKernel = new MyClustreamKernel( instance, instance.numValues(), timestamp, t,  m, this.getMaxID(), nearestPositions.getClosestB());
		kernels[nearestPositions.getClosestB()] = newKernel;
//		kernels[nearestPositions.getClosestB()].instances.add(instance);
	}

	@Override
	public void setNumMicroKernels(int maxKernels) {
		this.maxNumKernelsOption = new IntOption(
				"maxNumKernels", 'm',
				"Maximum number of micro kernels to use.", maxKernels);
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}
	
	@Override
	public void setTimeHorizont(int timeHorizont) {
		this.timeWindowOption = new IntOption("horizon",
				'h', "Rang of the window.", timeHorizont);
		
	}
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm#trainOnInstanceImpl(com.yahoo.labs.samoa.instances.Instances)
	 */
	@Override
	public void trainOnInstanceImpl(Instances instances) {
		throw new NotImplementedError("Train instances is not implemented in this class");
		
	}
}
