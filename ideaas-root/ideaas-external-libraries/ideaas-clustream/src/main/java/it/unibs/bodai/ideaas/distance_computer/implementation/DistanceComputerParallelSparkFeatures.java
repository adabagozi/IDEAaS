package it.unibs.bodai.ideaas.distance_computer.implementation;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;

import com.yahoo.labs.samoa.instances.Instance;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.distance_computer.IDistanceComputer;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;
import utils.CluStreamAlgorithmImplementation;
import utils.VectorsUtility;
import utils.lists.ReadOnlyArrayList;

public class DistanceComputerParallelSparkFeatures implements IDistanceComputer, Serializable, scala.Serializable {

	private static final long serialVersionUID = 1L;
	public final String identificator = "DistanceComputerSerial";
	JavaSparkContext sparkContext;
	public DistanceComputerParallelSparkFeatures(JavaSparkContext sparkContext) {
        this.sparkContext = sparkContext;
	}
	
	@Override
	public NearestKernel foundNearestKernel(Instance instance, MyClustreamKernel[] kernels) {
//		System.out.println("DistanceComputerParallelJavaFeatures -> foundPositionsNearestKernels");
		List<Integer> indexes = new ArrayList<>();
		double[] inputData = instance.toDoubleArray();
		for (int i=0; i< inputData.length; i++) {
			indexes.add(i);
		}
		return Stream.of(kernels).map(k -> {
			double distance = Math.sqrt(sparkContext.parallelize(indexes).mapToDouble(i->Math.pow(inputData[i] - k.getCenter()[i], 2)).reduce((a,b)->a+b));
			return new NearestKernel(distance, k);
		}).min((o1,o2) -> Double.compare(o1.getMinDistance(), o2.getMinDistance())).get();
	}
	
	@Override
	public double computeMinDistanceKernels(MyClustreamKernel kernel, MyClustreamKernel[] kernels) {
		List<Integer> indexes = new ArrayList<>();
		double[] baseKernel = kernel.getCenter();
		for (int i=0; i< baseKernel.length; i++) {
			indexes.add(i);
		}
//		System.out.println("DistanceComputerParallelJavaFeatures -> computeMinDistanceKernels");
		 return Stream.of(kernels).mapToDouble(k -> {
			 if(kernel.getPosition() == k.getPosition()) {
				 return Double.MAX_VALUE;
			 }
			 double distance = Math.sqrt(sparkContext.parallelize(indexes).mapToDouble(i->Math.pow(baseKernel[i] - k.getCenter()[i], 2)).reduce((a,b)->a+b));
			return distance;
		}).min().getAsDouble();
	}
	
	@Override
	public NearesKernelsPositionsInArray foundPositionsNearestKernels(MyClustreamKernel[] kernels) {

//		System.out.println("DistanceComputerParallelJavaFeatures -> foundPositionsNearestKernels");
		NearesKernelsPositionsInArray nearestKernel = new NearesKernelsPositionsInArray(0, 0, Double.MAX_VALUE);
		
		for ( int i = 0; i < kernels.length; i++ ) {
			MyClustreamKernel kernelA = kernels[i];
			List<Integer> indexes = new ArrayList<>();
			for (int k=0; k< kernelA.getCenter().length; k++) {
				indexes.add(k);
			}
			NearesKernelsPositionsInArray nearestKernel2 = Stream.of(kernels).map(k -> {
				 if(kernelA.getPosition() == k.getPosition()) {
					 return new NearesKernelsPositionsInArray(kernelA.getPosition(), k.getPosition(), Double.MAX_VALUE);
				 }
				 double distance = Math.sqrt(sparkContext.parallelize(indexes).mapToDouble(j->Math.pow(kernelA.getCenter()[j] - k.getCenter()[j], 2)).reduce((a,b)->a+b));
				return new NearesKernelsPositionsInArray(kernelA.getPosition(), k.getPosition(), distance);
			}).min((o1,o2) -> Double.compare(o1.getDistance(), o2.getDistance()))
					.get();
			
			
			
			if ( nearestKernel2.getDistance() < nearestKernel.getDistance() ) {
				nearestKernel = nearestKernel2;
			}
		}
		
		return nearestKernel;
	}
	
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.distance_computer.IDistanceComputer#getOldestKernel(it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel[])
	 */
	@Override
	public KernelAge getOldestKernel(MyClustreamKernel[] kernels) {
		KernelAge kA = new KernelAge(null, 0);
		for (MyClustreamKernel k: kernels) {
			if(kA.getOldestKernel() == null) {
				kA = new  KernelAge(k, k.getRelevanceStamp());
			}else {
				if (kA.getAge() > k.getRelevanceStamp()) {
					new  KernelAge(k, k.getRelevanceStamp());
				}
			}
			
		}
		return kA;
	}

}