package it.unibs.bodai.ideaas.distance_computer.implementation;

import java.io.Serializable;
import java.util.stream.IntStream;

import org.apache.spark.api.java.JavaSparkContext;

import com.yahoo.labs.samoa.instances.Instance;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.distance_computer.IDistanceComputer;
import it.unibs.bodai.ideaas.utils.NearesKernelsPositionsInArray;
import it.unibs.bodai.ideaas.utils.NearestKernel;
import it.unibs.ideaas.utils.KernelAge;
import utils.lists.ReadOnlyArrayList;

public class DistanceComputerParallelSparkSyntheses  implements IDistanceComputer, Serializable, scala.Serializable {

	private static final long serialVersionUID = 1L;
	public final String identificator = "DistanceComputerSerial";
	JavaSparkContext sparkContext;
	public DistanceComputerParallelSparkSyntheses(JavaSparkContext sparkContext) {
		this.sparkContext = sparkContext;
	}

	@Override
	public NearestKernel foundNearestKernel(Instance instance, MyClustreamKernel[] kernels) {
		double[] inputData = instance.toDoubleArray();

		ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);

		return sparkContext.parallelize(readOnlyKernels).map(k->{
			double distance = Math.sqrt(IntStream.range(0, inputData.length).mapToDouble(i->Math.pow(inputData[i] - k.getCenter()[i], 2)).sum());
			return new NearestKernel(distance, k);
		}).treeReduce((o1, o2) -> { 
			return (o1.getMinDistance() < o2.getMinDistance()) ? o1 : o2;
		});
	}

	@Override
	public double computeMinDistanceKernels(MyClustreamKernel kernel, MyClustreamKernel[] kernels) {
		double[] inputKernel = kernel.getCenter();
		ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);

		return sparkContext.parallelize(readOnlyKernels).mapToDouble(k->{
			if(kernel.getPosition() == k.getPosition()) {
				return Double.MAX_VALUE;
			}
			return Math.sqrt(IntStream.range(0, inputKernel.length).mapToDouble(i->Math.pow(inputKernel[i] - k.getCenter()[i], 2)).sum());
		}).min();
	}

	@Override
	public NearesKernelsPositionsInArray foundPositionsNearestKernels(MyClustreamKernel[] kernels) {
		NearesKernelsPositionsInArray nearestKernel = new NearesKernelsPositionsInArray(0, 0, Double.MAX_VALUE);
		ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);
		for ( int i = 0; i < kernels.length; i++ ) {
			MyClustreamKernel kernelA = kernels[i];

			NearesKernelsPositionsInArray nearestKernel2 = sparkContext.parallelize(readOnlyKernels).map(k->{
//				System.out.println("kernelA: "+kernelA.getPosition());
//				System.out.println("kernelB: "+k.getPosition());
				if(kernelA.getPosition() == k.getPosition()) {
					return new NearesKernelsPositionsInArray(kernelA.getPosition(), k.getPosition(), Double.MAX_VALUE);
				}
				double distance = Math.sqrt(IntStream.range(0, kernelA.getCenter().length).mapToDouble(j->Math.pow(kernelA.getCenter()[j] - k.getCenter()[j], 2)).sum());
				return new NearesKernelsPositionsInArray(kernelA.getPosition(), k.getPosition(), distance);
			}).treeReduce((o1, o2) -> { 
				return (o1.getDistance() < o2.getDistance()) ? o1 : o2;
			});

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
		// 3.1 Oldest Kernel
		ReadOnlyArrayList<MyClustreamKernel> readOnlyKernels = new ReadOnlyArrayList<>(kernels);

		return sparkContext.parallelize(readOnlyKernels).map(k -> {
			double age = k.getRelevanceStamp();
			return new KernelAge(k, age);
		}).treeReduce((o1, o2) -> { 
			return (o1.getAge() < o2.getAge()) ? o2 : o1;
		});
	}



}