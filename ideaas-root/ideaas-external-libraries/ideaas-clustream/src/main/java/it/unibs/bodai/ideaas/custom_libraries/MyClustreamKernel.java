package it.unibs.bodai.ideaas.custom_libraries;

/*
 *    ClustreamKernel.java
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.yahoo.labs.samoa.instances.Instance;

import scala.Serializable;
import utils.UniqueList;


public class MyClustreamKernel extends MyCFCluster implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private final static double EPSILON = 0.00005;
	public static final double MIN_VARIANCE = 1e-50;

	protected double LST;
	protected double SST;

	int m;
	double t;
	int position = -1;
	int idMicroCluster;
	List<Integer> parentIdList = new UniqueList<>();
	List<Integer> parentHistoryIdList = new UniqueList<>();
	//TODO 
	public ArrayList<Instance> instances = new ArrayList<>();

	public MyClustreamKernel( Instance instance, int dimensions, long timestamp , double t, int m, int idMicroCluster, int position) {
		super(instance, dimensions);
		this.t = t;
		this.m = m;
		this.LST = timestamp;
		this.SST = timestamp*timestamp;
		this.idMicroCluster = idMicroCluster;
		this.position = position;
	}

	public MyClustreamKernel( MyClustreamKernel cluster, double t, int m, int id, int position) {
		super(cluster);
		this.t = t;
		this.m = m;
		this.LST = cluster.LST;
		this.SST = cluster.SST;
		this.idMicroCluster = id;
		this.position = position;
	}

	public void insert( Instance instance, long timestamp) {
		N++;
		LST += timestamp;
		SST += timestamp*timestamp;

		for ( int i = 0; i < instance.numValues(); i++ ) {
			LS[i] += instance.value(i);
			SS[i] += instance.value(i)*instance.value(i);
		}
		//		instances.add(instance);
	}

	@Override
	public void add( MyCFCluster other2 ) {
		MyClustreamKernel other = (MyClustreamKernel) other2;
		assert( other.LS.length == this.LS.length );
		this.N += other.N;
		this.LST += other.LST;
		this.SST += other.SST;

		for ( int i = 0; i < LS.length; i++ ) {
			this.LS[i] += other.LS[i];
			this.SS[i] += other.SS[i];
		}
	}

	public double getRelevanceStamp() {
		if ( N < 2*m )
			return getMuTime();

		return getMuTime() + getSigmaTime() * getQuantile( ((double)m)/(2*N) );
	}

	private double getMuTime() {
		return LST / N;
	}

	private double getSigmaTime() {
		return Math.sqrt(SST/N - (LST/N)*(LST/N));
	}

	private double getQuantile( double z ) {
		assert( z >= 0 && z <= 1 );
		return Math.sqrt( 2 ) * inverseError( 2*z - 1 );
	}

	@Override
	public double getRadius() {
		//trivial cluster
		if(N == 1) return 0;
		if(t==1)
			t=1;

		return getDeviation()*radiusFactor;
	}

	@Override
	public MyCFCluster getCF(){
		return this;
	}


	private double getDeviation(){
		double[] variance = getVarianceVector();
		double sumOfDeviation = 0.0;
		for (int i = 0; i < variance.length; i++) {
			double d = Math.sqrt(variance[i]);
			sumOfDeviation += d;
		}

		//		System.out.println("dev1: "+sumOfDeviation);

		//		double dev = 0.0;
		//		for (int i = 0; i < variance.length; i++) {
		//			dev = dev + variance[i];
		//		}

		//		System.out.println("dev2: "+Math.sqrt(dev));

		return sumOfDeviation / variance.length;
	}

	/**
	 * @return this kernels' center
	 */
	@Override
	public double[] getCenter() {
		assert (!this.isEmpty());
		double res[] = new double[this.LS.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = this.LS[i] / N;
		}
		return res;
	}

	/**
	 * See interface <code>Cluster</code>
	 * @param point
	 * @return
	 */
	@Override
	public double getInclusionProbability(Instance instance) {
		//trivial cluster
		if(N == 1){
			double distance = 0.0;
			for (int i = 0; i < LS.length; i++) {
				double d = LS[i] - instance.value(i);
				distance += d * d;
			}
			distance = Math.sqrt(distance);
			if( distance < EPSILON )
				return 1.0;
			return 0.0;
		}
		else{
			double dist = calcNormalizedDistance(instance.toDoubleArray());
			if(dist <= getRadius()){
				return 1;
			}
			else{
				return 0;
			}
			//	            double res = AuxiliaryFunctions.distanceProbabilty(dist, LS.length);
			//	            return res;
		}
	}

	private double[] getVarianceVector() {
		double[] res = new double[this.LS.length];
		for (int i = 0; i < this.LS.length; i++) {
			double ls = this.LS[i];
			double ss = this.SS[i];

			double lsDivN = ls / this.getWeight();
			double lsDivNSquared = lsDivN * lsDivN;
			double ssDivN = ss / this.getWeight();
			res[i] = ssDivN - lsDivNSquared;

			// Due to numerical errors, small negative values can occur.
			// We correct this by settings them to almost zero.
			if (res[i] <= 0.0) {
				if (res[i] > -EPSILON) {
					res[i] = MIN_VARIANCE;
				}
			}
		}
		return res;
	}

	/**
	 * Check if this cluster is empty or not.
	 * @return <code>true</code> if the cluster has no data points,
	 * <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return this.N == 0;
	}

	/**
	 * Calculate the normalized euclidean distance (Mahalanobis distance for
	 * distribution w/o covariances) to a point.
	 * @param other The point to which the distance is calculated.
	 * @return The normalized distance to the cluster center.
	 *
	 * TODO: check whether WEIGHTING is correctly applied to variances
	 */
	//???????
	private double calcNormalizedDistance(double[] point) {
		double[] variance = getVarianceVector();
		double[] center = getCenter();
		double res = 0.0;

		for (int i = 0; i < center.length; i++) {
			double diff = center[i] - point[i];
			res += (diff * diff);// variance[i];
		}
		return Math.sqrt(res);
	}

	/**
	 * Approximates the inverse error function. Clustream needs this.
	 * @param z
	 */
	public static double inverseError(double x) {
		double z = Math.sqrt(Math.PI) * x;
		double res = (z) / 2;

		double z2 = z * z;
		double zProd = z * z2; // z^3
		res += (1.0 / 24) * zProd;

		zProd *= z2;  // z^5
		res += (7.0 / 960) * zProd;

		zProd *= z2;  // z^7
		res += (127 * zProd) / 80640;

		zProd *= z2;  // z^9
		res += (4369 * zProd) / 11612160;

		zProd *= z2;  // z^11
		res += (34807 * zProd) / 364953600;

		zProd *= z2;  // z^13
		res += (20036983 * zProd) / 797058662400d;

		return res;
	}

	@Override
	protected void getClusterSpecificInfo(ArrayList<String> infoTitle, ArrayList<String> infoValue) {
		super.getClusterSpecificInfo(infoTitle, infoValue);
		infoTitle.add("Deviation");

		double[] variance = getVarianceVector();
		double sumOfDeviation = 0.0;
		for (int i = 0; i < variance.length; i++) {
			double d = Math.sqrt(variance[i]);
			sumOfDeviation += d;
		}

		sumOfDeviation/= variance.length;

		infoValue.add(Double.toString(sumOfDeviation));
	}


	public double getLST() {
		return LST;
	}

	public double getSST() {
		return SST;
	}

	public double getT() {
		return t;
	}

	public void setLST(double lST) {
		LST = lST;
	}

	public void setSST(double sST) {
		SST = sST;
	}

	public void setT(double t) {
		this.t = t;
	}

	/**
	 * @return the id
	 */
	public int getIdMicroCluster() {
		return this.idMicroCluster;
	}

	/**
	 * @param id the id to set
	 */
	public void setIdMicroCluster(int idMicroCluster) {
		this.idMicroCluster = idMicroCluster;
	}

	/**
	 * @return the parentIdArrayList
	 */
	public List<Integer> getParentId() {
		if(this.parentIdList == null) {
			this.parentIdList = new UniqueList<>();
		}
		return parentIdList;
	}

	/**
	 * @param parentIdList the parentIdArrayList to set
	 */
	public void setParentId(List<Integer> parentIdList) {
		this.parentIdList = parentIdList;
	}

	public void addParentId(int parentId) {
		if(this.parentIdList == null) {
			this.parentIdList = new UniqueList<>();
		}
		this.parentIdList.add(parentId);
	}

	/**
	 * @return the parentIdArrayList
	 */
	public List<Integer> getParentHistoryId() {
		if(this.parentHistoryIdList == null) {
			this.parentHistoryIdList = new UniqueList<>();
		}
		return parentHistoryIdList;
	}

	/**
	 * @param parentIdList the parentIdArrayList to set
	 */
	public void setParentHistoryId(List<Integer> parentHistoryIdArrayList) {
		this.parentHistoryIdList = parentHistoryIdArrayList;
	}

	public void addParentHistoryId(int parentHistoryId) {
		if(this.parentHistoryIdList == null) {
			this.parentHistoryIdList = new UniqueList<>();
		}
		this.parentHistoryIdList.add(parentHistoryId);
	}

	/**
	 * Temo che la parola density non sia adeguata
	 * @return
	 */
	public double getDensity() {

		//return Math.pow(this.getDeviation(), this.LS.length)/this.getN();
		return this.getN()/Math.pow(this.getDeviation(), this.LS.length);
	}

	@Override
	public String toString() {
		String s = "( "; 
		for (double d : this.getCenter()) {
			s = s+d+"; ";
		}
		s = s+" )";
		return s;
	}

	/**
	 * 
	 * @return a stream where each item is a pair where the first entry is the corrindate index while
	 * 	the second one is the value of said coordinate of the center of this kernel 
	 */
	public Stream<scala.Tuple2<Integer, Double>> streamOfCenterCoordinates() {
		return IntStream.range(0, this.getCenter().length).mapToObj(i -> new scala.Tuple2<Integer, Double>(i, this.getCenter()[i]));
	}

	public int getPosition() {
		return this.position;
	}
}
