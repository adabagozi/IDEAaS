package it.unibs.bodai.ideaas.distance_computer.implementation;

import java.util.AbstractMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.yahoo.labs.samoa.instances.Instance;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.distance_computer.IDistanceComputer;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;
import utils.CluStreamAlgorithmImplementation;
import utils.VectorsUtility;

public class DistanceComputerParallelJavaSyntheses  implements IDistanceComputer {
	
	@Override
	public NearestKernel foundNearestKernel(Instance instance, MyClustreamKernel[] kernels) {
//		System.out.println("DistanceComputerParallelJavaSyntheses -> foundNearestKernel");
		
		return Stream.of(kernels).parallel().map(k -> {
			double distance = VectorsUtility.distance(instance.toDoubleArray(), k.getCenter(), CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES);// VectorsUtility.distance(instance.toDoubleArray(), k.getCenter());
			return new NearestKernel(distance, k);
		}).min((o1,o2) -> Double.compare(o1.getMinDistance(), o2.getMinDistance())).get();
	}
	
	@Override
	public double computeMinDistanceKernels(MyClustreamKernel kernel, MyClustreamKernel[] kernels) {
//		System.out.println("DistanceComputerParallelJavaSyntheses -> computeMinDistanceKernels");
		
		 return Stream.of(kernels).parallel().mapToDouble(k -> {
			 if(kernel.getPosition() == k.getPosition()) {
					return Double.MAX_VALUE;
				}
				
			  return VectorsUtility.distance(k.getCenter(), kernel.getCenter(), CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES);
		}).min().getAsDouble();
	}
	
	@Override
	public NearesKernelsPositionsInArray foundPositionsNearestKernels(MyClustreamKernel[] kernels) {
//		System.out.println("DistanceComputerParallelJavaSyntheses -> foundPositionsNearestKernels");
		
		NearesKernelsPositionsInArray nearestKernel = new NearesKernelsPositionsInArray(0, 0, Double.MAX_VALUE);
		for ( int i = 0; i < kernels.length; i++ ) {
			double[] centerA = kernels[i].getCenter();
			final int i2 = i;
			NearesKernelsPositionsInArray nearestKernel2 = IntStream
					.range(0, kernels.length)
					.parallel()
					.filter(j -> j != i2)
					.mapToObj(j -> {
						return new NearesKernelsPositionsInArray(i2, j, VectorsUtility.distance(centerA, kernels[j].getCenter(), CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES));
					})
					.min((o1,o2) -> Double.compare(o1.getDistance(), o2.getDistance()))
					.get();
			
		
			//}).min((o1, o2) -> { return Double.compare(o1.getDistance(), o2.getDistance());}).get();
			//}).min(new NearesKernelsPositionsInArray(0, 0, Double.MAX_VALUE)).get();
			
			if ( nearestKernel2.getDistance() < nearestKernel.getDistance()) {
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
		 return Stream.of(kernels).parallel().map(k -> {
			 double age = k.getRelevanceStamp();
			 return new KernelAge(k, age);
		}).min((o1,o2) -> Double.compare(o1.getAge(), o2.getAge())).get();
	}

}