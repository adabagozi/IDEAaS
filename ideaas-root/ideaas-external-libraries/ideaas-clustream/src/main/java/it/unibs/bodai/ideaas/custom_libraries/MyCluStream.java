package it.unibs.bodai.ideaas.custom_libraries;
/*
 *    MyClustream.java
 *    Copyright (C) 2010 RWTH Aachen University, Germany
 *    @author Jansen (moa@cs.rwth-aachen.de)
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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;

import moa.core.Measurement;

/** Citation: CluStream: Charu C. Aggarwal, Jiawei Han, Jianyong Wang, Philip S. Yu:
 * A Framework for Clustering Evolving Data Streams. VLDB 2003: 81-92
 */
public class MyCluStream extends MyAbstractClusterer{

	private static final long serialVersionUID = 1L;

	public IntOption timeWindowOption = new IntOption("horizon",
			'h', "Rang of the window.", 1000);

	public IntOption maxNumKernelsOption = new IntOption(
			"maxNumKernels", 'k',
			"Maximum number of micro kernels to use.", 100);

	public IntOption kernelRadiFactorOption = new IntOption(
			"kernelRadiFactor", 't',
			"Multiplier for the kernel radius", 2);

	private int timeWindow;
	private long timestamp = -1;
	private MyClustreamKernel[] kernels;
	private ArrayList<Integer> oldKernelsId = new ArrayList<>();
	private boolean initialized;
	private List<MyClustreamKernel> buffer; // Buffer for initialization with kNN
	private int bufferSize;
	private double t;
	private int m; //TODO sembra essere massimo numero di kernel da usare

	public MyCluStream() {
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


	public void resetLearningImpl(MyClustreamKernel[] oldKernels) {
		resetLearningImpl();
		if(oldKernels.length >= this.bufferSize) {
			this.initialized = true;
			this.kernels = oldKernels;
			for (MyClustreamKernel mCK: oldKernels) {
				mCK.setParentId(new ArrayList<>());
				oldKernelsId.add(mCK.getIdMicroCluster());
			}
		}else {
			for (MyClustreamKernel mCK: oldKernels) {
				mCK.setParentId(new ArrayList<>());
				buffer.add( mCK );
				oldKernelsId.add(mCK.getIdMicroCluster());
				
			}
		}
	}
	@Override
	public void trainOnInstanceImpl(Instance instance) {
		int dim = instance.numValues();
		timestamp++;
		// 0. Initialize
		if ( !initialized ) {
			if ( buffer.size() < bufferSize ) {
				buffer.add( new MyClustreamKernel(instance,dim, timestamp, t, m, -1, buffer.size()) );
				return;
			}

			int k = kernels.length;
			//System.err.println("k="+k+" bufferSize="+bufferSize);
			assert (k <= bufferSize);

			MyClustreamKernel[] centers = new MyClustreamKernel[k];
			for ( int i = 0; i < k; i++ ) {
				centers[i] = buffer.get( i ); // TODO: make random!
			}
			MyClustering kmeans_clustering = kMeans(k, centers, buffer); //?Capire prchè lo fa?
//			Clustering kmeans_clustering = kMeans(k, buffer);

			for ( int i = 0; i < kmeans_clustering.size(); i++ ) {
				kernels[i] = new MyClustreamKernel( new DenseInstance(1.0,centers[i].getCenter()), dim, timestamp, t, m, i, i);
			}

			buffer.clear();
			initialized = true;
//			return;
		}


		// 1. Determine closest kernel
		MyClustreamKernel closestKernel = null;
		double minDistance = Double.MAX_VALUE;
		for ( int i = 0; i < kernels.length; i++ ) {
			//System.out.println(i+" "+kernels[i].getWeight()+" "+kernels[i].getDeviation());
			double distance = distance(instance.toDoubleArray(), kernels[i].getCenter() );
			if ( distance < minDistance ) {
				closestKernel = kernels[i];
				minDistance = distance;
			}
		}

		// 2. Check whether instance fits into closestKernel
		
		double radius = 0.0;
		/**
		 * TODO Aricolo pag.5 (*1)
		 * getWight() in questo caso ritorna N
		 */
		if ( closestKernel.getWeight() == 1 ) {
			// Special case: estimate radius by determining the distance to the
			// next closest cluster
			radius = Double.MAX_VALUE;
			double[] center = closestKernel.getCenter();
			for ( int i = 0; i < kernels.length; i++ ) {
				if ( kernels[i] == closestKernel ) {
					continue;
				}

				double distance = distance(kernels[i].getCenter(), center );
				radius = Math.min( distance, radius );
			}
		} else {
			radius = closestKernel.getRadius();
		}

		if ( minDistance < radius ) {
			// Date fits, put into kernel and be happy
			closestKernel.insert( instance, timestamp );
			return;
		}

		// 3. Date does not fit, we need to free
		// some space to insert a new kernel
		long threshold = timestamp - timeWindow; // Kernels before this can be forgotten
		/**TODO sembrerebbe lavorare su un calore incrementale piuttosto che su un vero e proprio timestamp.
		* Articolo p.4 sembrerebbe dire che la fase dei micro-kernel non sia influenzata dai dati inseiriti dall'utente
		* (come il time horizont o la granularità)
		* Articolo p.5 (*2)
		*/
		// 3.1 Try to forget old kernels
		for ( int i = 0; i < kernels.length; i++ ) {
			if ( kernels[i].getRelevanceStamp() < threshold ) {
				kernels[i] = new MyClustreamKernel( instance, dim, timestamp, t, m, this.getMaxID(), i);
				return;
			}
		}
		/**TODO 
		* Articolo p.5 (*3)
		*/
		// 3.2 Merge closest two kernels
		int closestA = 0;
		int closestB = 0;
		minDistance = Double.MAX_VALUE;
		for ( int i = 0; i < kernels.length; i++ ) {
			double[] centerA = kernels[i].getCenter();
			for ( int j = i + 1; j < kernels.length; j++ ) {
				double dist = distance( centerA, kernels[j].getCenter() );
				if ( dist < minDistance ) {
					minDistance = dist;
					closestA = i;
					closestB = j;
				}
			}
		}
		assert (closestA != closestB);

		int idClosestA = kernels[closestA].getIdMicroCluster();
		kernels[closestA].add( kernels[closestB] );
		kernels[closestA].setIdMicroCluster(this.getMaxID());
		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(idClosestA)) {
			kernels[closestA].addParentId(idClosestA);	
		}
		if (!this.oldKernelsId.isEmpty() && this.oldKernelsId.contains(kernels[closestB].getIdMicroCluster())) {
			kernels[closestA].addParentId(kernels[closestB].getIdMicroCluster());
		}
		kernels[closestB] = new MyClustreamKernel( instance, dim, timestamp, t,  m, this.getMaxID(), closestB);
	}

	@Override
	public MyClustering getMicroClusteringResult() {
		if ( !initialized ) {
			return new MyClustering( new MyCluster[0] );
		}

		MyClustreamKernel[] res = new MyClustreamKernel[kernels.length];
		for ( int i = 0; i < res.length; i++ ) {
			res[i] = new MyClustreamKernel( kernels[i], t, m, kernels[i].getIdMicroCluster(), i);
		}

		return new MyClustering( res );
	}

	@Override
	public boolean implementsMicroClusterer() {
		return true;
	}

	@Override
	public MyClustering getClusteringResult() {
		return null;
	}

	public String getName() {
		return "Clustream " + timeWindow;
	}

	private static double distance(double[] pointA, double [] pointB){
		double distance = 0.0;
		for (int i = 0; i < pointA.length; i++) {
			double d = pointA[i] - pointB[i];
			distance += d * d;
		}
		return Math.sqrt(distance);
	}

	//wrapper... we need to rewrite kmeans to points, not clusters, doesnt make sense anymore
	//    public static Clustering kMeans( int k, ArrayList<Instance> points, int dim ) {
	//        ArrayList<ClustreamKernel> cl = new ArrayList<ClustreamKernel>();
	//        for(Instance inst : points){
	//            cl.add(new ClustreamKernel(inst, dim , 0, 0, 0));
	//        }
	//        Clustering clustering = kMeans(k, cl);
	//        return clustering;
	//    }

	public static MyClustering kMeans( int k, List<? extends MyCluster> data ) {
		Random random = new Random(0);
		MyCluster[] centers = new MyCluster[k];
		for (int i = 0; i < centers.length; i++) {
			int rid = random.nextInt(k);
			centers[i] = new MySphereCluster(data.get(rid).getCenter(),0);
		}
		MyClustering clustering = kMeans(k, centers, data);
		return clustering;
	}





	public static MyClustering kMeans( int k, MyCluster[] centers, List<? extends MyCluster> data ) {
		assert (centers.length == k);
		assert (k > 0);

		int dimensions = centers[0].getCenter().length;

		ArrayList<ArrayList<MyCluster>> clustering = new ArrayList<ArrayList<MyCluster>>();
		for ( int i = 0; i < k; i++ ) {
			clustering.add( new ArrayList<MyCluster>() );
		}

		int repetitions = 100;
		while ( repetitions-- >= 0 ) {
			// Assign points to clusters
			for ( MyCluster point : data ) {
				double minDistance = distance( point.getCenter(), centers[0].getCenter() );
				int closestCluster = 0;
				for ( int i = 1; i < k; i++ ) {
					double distance = distance( point.getCenter(), centers[i].getCenter() );
					if ( distance < minDistance ) {
						closestCluster = i;
						minDistance = distance;
					}
				}

				clustering.get( closestCluster ).add( point );
			}

			// Calculate new centers and clear clustering lists
			MySphereCluster[] newCenters = new MySphereCluster[centers.length];
			for ( int i = 0; i < k; i++ ) {
				newCenters[i] = calculateCenter( clustering.get( i ), dimensions );
				clustering.get( i ).clear();
			}
			centers = newCenters;
		}

		return new MyClustering( centers );
	}

	private static MySphereCluster calculateCenter( ArrayList<MyCluster> cluster, int dimensions ) {
		double[] res = new double[dimensions];
		for ( int i = 0; i < res.length; i++ ) {
			res[i] = 0.0;
		}

		if ( cluster.size() == 0 ) {
			return new MySphereCluster( res, 0.0 );
		}

		for ( MyCluster point : cluster ) {
			double [] center = point.getCenter();
			for (int i = 0; i < res.length; i++) {
				res[i] += center[i];
			}
		}

		// Normalize
		for ( int i = 0; i < res.length; i++ ) {
			res[i] /= cluster.size();
		}

		// Calculate radius
		double radius = 0.0;
		for ( MyCluster point : cluster ) {
			double dist = distance( res, point.getCenter() );
			if ( dist > radius ) {
				radius = dist;
			}
		}
		MySphereCluster sc = new MySphereCluster( res, radius );
		sc.setWeight(cluster.size());
		return sc;
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void getModelDescription(StringBuilder out, int indent) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isRandomizable() {
		return false;
	}

	public double[] getVotesForInstance(Instance inst) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getMaxID() {
		int id = -1;
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
}

