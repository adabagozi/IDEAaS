package it.unibs.bodai.ideaas.distance_computer.implementation;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.yahoo.labs.samoa.instances.Instance;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;
import utils.CluStreamAlgorithmImplementation;
import utils.VectorsUtility;

public class DistanceComputer {
	
	public NearestKernel foundNearestKernel(Instance instance, MyClustreamKernel[] kernels) {
//		System.out.println("DistanceComputerSerial -> foundNearestKernel");
		
		return  IntStream.range(0, kernels.length).mapToObj(j -> {
			double distance = VectorsUtility.distance(instance.toDoubleArray(), kernels[j].getCenter(), CluStreamAlgorithmImplementation.NO_PARALLEL);
			return new NearestKernel(distance, kernels[j]);
		}).min((o1,o2) -> Double.compare(o1.getMinDistance(), o2.getMinDistance())).get();
	}
	
	public double computeMinDistanceKernels(MyClustreamKernel kernel, MyClustreamKernel[] kernels) {

//		System.out.println("DistanceComputerSerial -> computeMinDistanceKernels");
		 return IntStream.range(0, kernels.length).mapToDouble(j -> {
			 if(kernel.getPosition() == kernels[j].getPosition()) {
				 return Double.MAX_VALUE;
			 }
			  return VectorsUtility.distance(kernels[j].getCenter(), kernel.getCenter(), CluStreamAlgorithmImplementation.NO_PARALLEL);
		}).min().getAsDouble();
	}
	
	
	
	public NearesKernelsPositionsInArray foundPositionsNearestKernels(MyClustreamKernel[] kernels) {
//		System.out.println("DistanceComputerSerial -> NearesKernelsPositionsInArray");
		
		NearesKernelsPositionsInArray nearestKernel = new NearesKernelsPositionsInArray(0, 0, Double.MAX_VALUE);
		for ( int i = 0; i < kernels.length; i++ ) {
			double[] centerA = kernels[i].getCenter();
			final int i2 = i;
			NearesKernelsPositionsInArray nearestKernel2 = IntStream
					.range(0, kernels.length)
					.filter(j -> j != i2)
					.mapToObj(j -> {
						return new NearesKernelsPositionsInArray(i2, j, VectorsUtility.distance(centerA, kernels[j].getCenter(), CluStreamAlgorithmImplementation.NO_PARALLEL));
					})
					.min((o1,o2) -> Double.compare(o1.getDistance(), o2.getDistance()))
					.get();
			
			if ( nearestKernel2.getDistance() < nearestKernel.getDistance() ) {
				nearestKernel = nearestKernel2;
			}
		}
		
		return nearestKernel;
	}
	

	public KernelAge getOldestKernel(List<MyClustreamKernel> kernels) {
		 return kernels.parallelStream().map(k -> {
			 double age = k.getRelevanceStamp();
			 return new KernelAge(k, age);
		}).min((o1,o2) -> Double.compare(o1.getAge(), o2.getAge())).get();
	}

}
