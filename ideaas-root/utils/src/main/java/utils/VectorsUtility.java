package utils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.spark.api.java.JavaSparkContext;

public class VectorsUtility implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Distance between two vectors. (Eucledian distance?)
	 * TODO controllare se effettivamente l'ordine degli elementi è uguale nei due blocchi
	 * @param pointA
	 * @param pointB
	 * @return dist
	 */
	public static double distance(double[] pointA, double [] pointB) {
		double distance = 0.0;
		for (int i = 0; i < pointA.length; i++) {
			double d = pointA[i] - pointB[i];
			distance += d * d;
		}
		return Math.sqrt(distance);
	}

	public static double distance(double[] pointA, double [] pointB, CluStreamAlgorithmImplementation cluStreamImpl) {
		switch (cluStreamImpl) {
		case JAVA_PARALLEL_FEATURES:
			return distanteParallelJava(pointA, pointB);
		case JAVA_PARALLEL_BOTH:
			return distanteParallelJava(pointA, pointB);
		case SPARK_PARALLEL_BOTH:
			return distanteParallelJava(pointA, pointB);
		case SPARK_PARALLEL_FEATURES:
			return distanteParallelJava(pointA, pointB);
		
		default:
			return distanteNoParallel(pointA, pointB);
		}
	}

	public static double distance(double[] pointA, double [] pointB, CluStreamAlgorithmImplementation cluStreamImpl, JavaSparkContext sparkContext) {
		switch (cluStreamImpl) {
		case SPARK_PARALLEL_FEATURES:
			return distanteParallelSpark(pointA, pointB, sparkContext);
		case SPARK_PARALLEL_BOTH:
			return distanteParallelSpark(pointA, pointB, sparkContext);
		default:
			return distanteNoParallel(pointA, pointB);
		}
	}
	private static double distanteNoParallel(double[] pointA, double [] pointB) {
//		System.out.println("Feature Parallel -> NoParallel");
//		assertEquals(pointA.length, pointB.length);
		return Math.sqrt(IntStream.range(0, pointA.length).mapToDouble(i->Math.pow(pointA[i] - pointB[i], 2)).sum());
	}
	

	private static double distanteParallelJava(double[] pointA, double [] pointB) {
	
		return Math.sqrt(IntStream.range(0, pointA.length).parallel().mapToDouble(i->Math.pow(pointA[i] - pointB[i], 2)).sum());
	}
	
	private static double distanteParallelSpark(double[] pointA, double [] pointB, JavaSparkContext sparkContext) {
		return Math.sqrt(sparkContext.parallelize((List<Integer>) IntStream.range(1, pointA.length)).map(i->Math.pow(pointA[i] - pointB[i], 2)).reduce((a,b)->a+b));
	}
	
}

