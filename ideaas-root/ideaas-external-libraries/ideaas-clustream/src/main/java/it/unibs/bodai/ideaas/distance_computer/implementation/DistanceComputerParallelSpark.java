package it.unibs.bodai.ideaas.distance_computer.implementation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.yahoo.labs.samoa.instances.Instance;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.distance_computer.IDistanceComputer;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;
import scala.Tuple2;
import utils.lists.ReadOnlyArrayList;

/**
 * @author ada
 *
 */
public class DistanceComputerParallelSpark  implements IDistanceComputer, Serializable, scala.Serializable {

	private static final long serialVersionUID = 1L;
	public final String identificator = "DistanceComputerSerial";
	JavaSparkContext sparkContext;
	public DistanceComputerParallelSpark(JavaSparkContext sparkContext) {
        this.sparkContext = sparkContext;
	}
	
	public JavaRDD<Double> getRDDFromDoubleArray(JavaSparkContext sc, double[] array) {
		return sc.parallelize(Arrays.stream(array).boxed().collect(Collectors.toList()));
	}
	
	public JavaRDD<Integer> getRDDFromDoubleArray(JavaSparkContext sc, int[] array) {
		return sc.parallelize(Arrays.stream(array).boxed().collect(Collectors.toList()));
	}
	
	@Override
	public NearestKernel foundNearestKernel(Instance instance, MyClustreamKernel[] kernels) {
			ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);

			List<Double> doubles = new ArrayList<>();
			for (int i=0; i<instance.toDoubleArray().length; i++) {
				doubles.add(Double.valueOf(i));
			}
//			JavaRDD<Double> dimensionIndexRDD = sparkContext.parallelize(doubles);
			JavaRDD<MyClustreamKernel> kernelsRDD = sparkContext.parallelize(readOnlyKernels);
		
			//we generate a RDD pair where each key is a kernel and each value is a pair associating each center coordinate of a given kernel with its value
			/* assuming K1=(5,4,3) and K2=(7,6,5) then:
			 * K1 -> [(1,5),(2,4),(3,3)]
			 * K2 -> [(1,7),(2,6),(3,5)]
			 */
			JavaPairRDD<MyClustreamKernel,Tuple2<Integer,Double>> kernelCartesian = kernelsRDD.flatMapToPair(k -> {
				return k.streamOfCenterCoordinates()
				.map(tuple -> {
					return new scala.Tuple2<MyClustreamKernel, scala.Tuple2<Integer, Double>>(k, tuple);
				}).iterator();
			});
			
			//compute for each kernel, the distance relative to "instance"
			JavaRDD<Tuple2<MyClustreamKernel,Double>> distancesFromPointPairRDD = kernelCartesian.aggregateByKey(
					Double.valueOf(0), //zero value
					(Double d, scala.Tuple2<Integer, Double> pair) -> {return d + Double.valueOf(Math.pow(pair._2 - instance.toDoubleArray()[pair._1], 2)); }, //sequence function
					(Double d1, Double d2) -> { return Double.valueOf(d1.doubleValue() + d2.doubleValue());} //comb function
			).map(tuple -> new scala.Tuple2<>(tuple._1, Math.sqrt(tuple._2)));

			//compute the minimum distance
			//FIXME be careful: the int conversion may result in casting 0.00001 (hence k2 is farther than k1) into 0 (k2 and k1 are at the same distance from instance)
			Tuple2<MyClustreamKernel,Double> kernelAtMinimumDistance = distancesFromPointPairRDD.reduce((distanceOfKernel1, distanceOfKernel2) -> { 
				return (distanceOfKernel1._2.doubleValue() < distanceOfKernel2._2.doubleValue()) ? distanceOfKernel1 : distanceOfKernel2;
			});
			
			return new NearestKernel(kernelAtMinimumDistance._2, kernelAtMinimumDistance._1);
	}
	
	@Override
	public double computeMinDistanceKernels(MyClustreamKernel kernel, MyClustreamKernel[] kernels) {

		ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);
		//TODO in parallel
		List<Double> doubles = new ArrayList<>();
		for (int i=0; i<kernel.getCenter().length; i++) {
			doubles.add(Double.valueOf(i));
		}
//		JavaRDD<Double> dimensionIndexRDD = sparkContext.parallelize(doubles);
		JavaRDD<MyClustreamKernel> kernelsRDD = sparkContext.parallelize(readOnlyKernels);

		//we generate a RDD pair where each key is a kernel and each value is a pair associating each center coordinate of a given kernel with its value
		/* assuming K1=(5,4,3) and K2=(7,6,5) then:
		 * K1 -> [(1,5),(2,4),(3,3)]
		 * K2 -> [(1,7),(2,6),(3,5)]
		 */
		JavaPairRDD<MyClustreamKernel,Tuple2<Integer,Double>> kernelCartesian = kernelsRDD.flatMapToPair(k -> {
			return k.streamOfCenterCoordinates()
			.map(tuple -> {
				return new scala.Tuple2<MyClustreamKernel, scala.Tuple2<Integer, Double>>(k, tuple);
			}).iterator();
		});
		
		//compute for each kernel, the distance relative to "instance"
		JavaRDD<Double> distancesFromPointPairRDD = kernelCartesian.aggregateByKey(
				Double.valueOf(0), //zero value
				(Double d, scala.Tuple2<Integer, Double> pair) -> {
					return d + Double.valueOf(Math.pow(pair._2 - kernel.getCenter()[pair._1], 2)); 
				}, //sequence function
				(Double d1, Double d2) -> { 
					
					return Double.valueOf(d1.doubleValue() + d2.doubleValue());
				} //comb function
		).map(tuple -> Math.sqrt(tuple._2));

		//compute the minimum distance
		//FIXME be careful: the int conversion may result in casting 0.00001 (hence k2 is farther than k1) into 0 (k2 and k1 are at the same distance from instance)
		Double kernelAtMinimumDistance = distancesFromPointPairRDD.reduce((distanceOfKernel1, distanceOfKernel2) -> { 
			return (distanceOfKernel1.doubleValue() < distanceOfKernel2.doubleValue()) ? distanceOfKernel1 : distanceOfKernel2;
		});
		
		return kernelAtMinimumDistance;
	}
	
	@Override
	public NearesKernelsPositionsInArray foundPositionsNearestKernels(MyClustreamKernel[] kernels) {
		
		NearesKernelsPositionsInArray nearestKernel = new NearesKernelsPositionsInArray(-1, -1, Double.MAX_VALUE);

		ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);
		
		//TODO si potrebbe parallelizzare ancora
		for ( int i = 0; i < kernels.length; i++ ) {
			double[] centerA = kernels[i].getCenter();
			final int i2 = i;
			List<Double> doubles = new ArrayList<>();
			for (int k=0; k<centerA.length; k++) {
				doubles.add(Double.valueOf(k));
			}
//			JavaRDD<Double> dimensionIndexRDD = sparkContext.parallelize(doubles);
			JavaRDD<MyClustreamKernel> kernelsRDD = sparkContext.parallelize(readOnlyKernels);
			JavaPairRDD<MyClustreamKernel,Tuple2<Integer,Double>> kernelCartesian = kernelsRDD.flatMapToPair(k -> {
				return k.streamOfCenterCoordinates()
				.map(tuple -> {
					return new scala.Tuple2<MyClustreamKernel, scala.Tuple2<Integer, Double>>(k, tuple);
				}).iterator();
			});
			
			//compute for each kernel, the distance relative to "instance"
			JavaRDD<Tuple2<MyClustreamKernel,Double>> distancesFromPointPairRDD = kernelCartesian.aggregateByKey(
					Double.valueOf(0), //zero value
					(Double d, scala.Tuple2<Integer, Double> pair) -> {return d + Double.valueOf(Math.pow(pair._2 - centerA[pair._1], 2)); }, //sequence function
					(Double d1, Double d2) -> { return Double.valueOf(d1.doubleValue() + d2.doubleValue());} //comb function
			).map(tuple -> new scala.Tuple2<>(tuple._1, tuple._1.getPosition() == i2 ? Double.MAX_VALUE:Math.sqrt(tuple._2)));

			//compute the minimum distance
			//FIXME be careful: the int conversion may result in casting 0.00001 (hence k2 is farther than k1) into 0 (k2 and k1 are at the same distance from instance)
			
			Tuple2<MyClustreamKernel,Double> kernelAtMinimumDistance = distancesFromPointPairRDD.reduce((distanceOfKernel1, distanceOfKernel2) -> { 
				return (distanceOfKernel1._2.doubleValue() < distanceOfKernel2._2.doubleValue()) ? distanceOfKernel1 : distanceOfKernel2;
			});
			
			NearesKernelsPositionsInArray nearestKernel2 = new NearesKernelsPositionsInArray(kernelAtMinimumDistance._1.getPosition(), i2, kernelAtMinimumDistance._2);
			
			if ( nearestKernel2.getDistance() < nearestKernel.getDistance()) {
				nearestKernel = nearestKernel2;
			}
		}
		
		return nearestKernel;
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.distance_computer.IDistanceComputer#getOldestKernel(it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel[], long)
	 */
	@Override
	public KernelAge getOldestKernel(MyClustreamKernel[] kernels) {
		// 3.1 Oldest Kernel
		ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);
		
		 return sparkContext.parallelize(readOnlyKernels).map(k -> {
			 double age = k.getRelevanceStamp();
			  return new KernelAge(k, age);
		}).reduce((o1, o2) -> { 
			return (o1.getAge() < o2.getAge()) ? o2 : o1;
		});//min((o1,o2) -> Double.compare(o1.getAge(), o2.getAge()));

	}
}