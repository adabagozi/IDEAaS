package clustream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.junit.Before;
import org.junit.Test;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import it.unibs.bodai.ideaas.clustream.CluStreamAlgorithmFactory;
import it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm;
import it.unibs.bodai.ideaas.clustream.implementation.MyParallelCluStreamAlgorithmBuffer;
import it.unibs.bodai.ideaas.clustream.implementation.MyParallelSparkCluStreamAlgorithmBuffer;
import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import utils.CluStreamAlgorithmImplementation;

public class TestMyCluStream {

	private ICluStreamAlgorithm cluStreamAlgorithm;

	@Before
	public void setup() {

		Logger.getRootLogger().setLevel(Level.ERROR);
	}

	//@Test
	public void noParallel() throws Exception {
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.NO_PARALLEL);
		int maxKernels = 5;
		this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		this.cluStreamAlgorithm.resetLearningImpl();

		Instances instances = this.generateInstances();
		assertEquals(10, instances.size());

		for (int i = 0; i< instances.size(); i++) {
			Instance inst = instances.get(i);
			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
		}
		MyClustreamKernel[] kernels = this.cluStreamAlgorithm.getKernels();
		assertEquals(maxKernels, kernels.length);

		this.printKernels(kernels);
		assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(11.0), kernels[2].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(-1.5), kernels[1].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
		System.out.println("CluStream NO_PARALLEL Finished");
	}

	//@Test
	public void javaParallelSynthesis() throws Exception {
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES);
		int maxKernels = 5;
		this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		this.cluStreamAlgorithm.resetLearningImpl();

		Instances instances = this.generateInstances();
		assertEquals(10, instances.size());
		for (int i = 0; i< instances.size(); i++) {
			Instance inst = instances.get(i);
			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
		}

		MyClustreamKernel[] kernels = this.cluStreamAlgorithm.getKernels();
		assertEquals(maxKernels, kernels.length);
		//		this.printKernels(kernels);
		assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(11.0), kernels[2].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(-1.5), kernels[1].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
		System.out.println("CluStream JAVA_PARALLEL_SYNTHESES Finished");
	}

	//@Test
	public void javaParallelFeatures() throws Exception {
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_FEATURES);
		int maxKernels = 5;
		this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		this.cluStreamAlgorithm.resetLearningImpl();

		Instances instances = this.generateInstances();
		assertEquals(10, instances.size());

		for (int i = 0; i< instances.size(); i++) {
			Instance inst = instances.get(i);
			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
		}
		MyClustreamKernel[] kernels = this.cluStreamAlgorithm.getKernels();
		assertEquals(maxKernels, kernels.length);

				this.printKernels(kernels);
		assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(11.0), kernels[2].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(-1.5), kernels[1].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
		System.out.println("CluStream JAVA_PARALLEL_FEATURES Finished");
	}

	//@Test
	public void javaParallelBoth() throws Exception {
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_BOTH);
		int maxKernels = 5;
		this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		this.cluStreamAlgorithm.resetLearningImpl();

		Instances instances = this.generateInstances();
		assertEquals(10, instances.size());

		for (int i = 0; i< instances.size(); i++) {
			Instance inst = instances.get(i);
			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
		}
		MyClustreamKernel[] kernels = this.cluStreamAlgorithm.getKernels();
		assertEquals(maxKernels, kernels.length);
		this.printKernels(kernels);
		
		assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(11.0), kernels[2].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(-1.5), kernels[1].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
		assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
		System.out.println("CluStream JAVA_PARALLEL_BOTH Finished");
	}

//	@Test
	public void sparkParallelSynthesis() throws Exception {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Word Count");

		try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
			sparkContext.setLogLevel("ERROR");
			this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamSpark(CluStreamAlgorithmImplementation.SPARK_PARALLEL_SYNTHESES, sparkContext);
			int maxKernels = 5;
			this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
			this.cluStreamAlgorithm.resetLearningImpl();

			Instances instances = this.generateInstances();
			assertEquals(10, instances.size());

			for (int i = 0; i< instances.size(); i++) {
				Instance inst = instances.get(i);
				this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
			}
			MyClustreamKernel[] kernels = this.cluStreamAlgorithm.getKernels();
			assertEquals(maxKernels, kernels.length);
			this.printKernels(kernels);
			assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(11.0), kernels[2].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(-1.5), kernels[1].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
			System.out.println("CluStream SPARK_PARALLEL_SYNTHESES Finished");
		}
	}



//	@Test
	public void sparkParallelFeatures() throws Exception {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Word Count");

		try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
			sparkContext.setLogLevel("ERROR");
			
			this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamSpark(CluStreamAlgorithmImplementation.SPARK_PARALLEL_FEATURES, sparkContext);
			int maxKernels = 5;
			this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
			this.cluStreamAlgorithm.resetLearningImpl();

			Instances instances = this.generateInstances();
			assertEquals(10, instances.size());

			for (int i = 0; i< instances.size(); i++) {
				Instance inst = instances.get(i);
				this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
			}
			MyClustreamKernel[] kernels = this.cluStreamAlgorithm.getKernels();
			assertEquals(maxKernels, kernels.length);
			this.printKernels(kernels);
			
			assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(11.0), kernels[2].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(-1.5), kernels[1].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
			System.out.println("CluStream SPARK_PARALLEL_FEATURES Finished");
		}
	}

//	@Test
	public void sparkParallelBoth() throws Exception {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Word Count");

		try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
			sparkContext.setLogLevel("ERROR");
			this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamSpark(CluStreamAlgorithmImplementation.SPARK_PARALLEL_BOTH, sparkContext);
			int maxKernels = 5;
			this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
			this.cluStreamAlgorithm.resetLearningImpl();

			Instances instances = this.generateInstances();
			assertEquals(10, instances.size());

			for (int i = 0; i< instances.size(); i++) {
				Instance inst = instances.get(i);
				this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
			}
			MyClustreamKernel[] kernels = this.cluStreamAlgorithm.getKernels();
			assertEquals(maxKernels, kernels.length);

			this.printKernels(kernels);
			assertArrayEquals(this.kernelCentroid(2.25), kernels[3].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(11.0), kernels[2].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(-1.5), kernels[1].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(19.5), kernels[4].getCenter(), 0);
			assertArrayEquals(this.kernelCentroid(30.0), kernels[0].getCenter(), 0);
			System.out.println("CluStream SPARK_PARALLEL_BOTH Finished");
		}
	}
	
	//@Test
	public void parallelJavaBuffer() throws Exception {
		MyParallelCluStreamAlgorithmBuffer cluStreamAlgorithm = new MyParallelCluStreamAlgorithmBuffer(CluStreamAlgorithmImplementation.NO_PARALLEL);
		int maxKernels = 5;
		cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		cluStreamAlgorithm.resetLearningImpl();

		Instances instances = this.generateInstances();
		assertEquals(10, instances.size());
		cluStreamAlgorithm.trainOnInstanceImpl(instances);
//		for (int i = 0; i< instances.size(); i++) {
//			Instance inst = instances.get(i);
//			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
//		}
		MyClustreamKernel[] kernels = cluStreamAlgorithm.getKernels();
		assertEquals(maxKernels, kernels.length);

		this.printKernels(kernels);
//		assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(11.0), kernels[1].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(-1.5), kernels[2].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
		System.out.println("CluStream PARALLE_JAVA_BUFFER Finished");
	}

	
	//@Test
	public void parallelSparkBuffer() throws Exception {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Word Count");
		// Create a Java version of the Spark Context
		try(SparkSession sc = SparkSession.builder().appName("Clustream").master("local").getOrCreate()){
		MyParallelSparkCluStreamAlgorithmBuffer cluStreamAlgorithm = new MyParallelSparkCluStreamAlgorithmBuffer(CluStreamAlgorithmImplementation.NO_PARALLEL, sc);
		int maxKernels = 5;
		cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		cluStreamAlgorithm.resetLearningImpl();

		Instances instances = this.generateInstances();
		assertEquals(10, instances.size());
		cluStreamAlgorithm.trainOnInstanceImpl(instances);
//		for (int i = 0; i< instances.size(); i++) {
//			Instance inst = instances.get(i);
//			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
//		}
		MyClustreamKernel[] kernels = cluStreamAlgorithm.getKernels();
		assertEquals(maxKernels, kernels.length);

		this.printKernels(kernels);
//		assertArrayEquals(this.kernelCentroid(2.25), kernels[0].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(11.0), kernels[1].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(-1.5), kernels[2].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(19.5), kernels[3].getCenter(), 0);
//		assertArrayEquals(this.kernelCentroid(30.0), kernels[4].getCenter(), 0);
		System.out.println("CluStream PARALLE_SPARK_BUFFER Finished");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			// TODO: handle exception
		}
	}
	
//	@Test
	public void streamPerformance() {
		//Create a SparkContext to initialize
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Word Count");
		// Create a Java version of the Spark Context
		JavaSparkContext sc = new JavaSparkContext(conf);
		int totaleven = 0;

		List<Integer> randomList = new ArrayList<>();
		Random rnd = new Random();
		for(int i = 0 ;i < 10000;i++) {
			int r = rnd.nextInt(100);
			randomList.add(r);
		}

		long s1 = System.currentTimeMillis();

		randomList.stream().mapToDouble(i->Math.pow(i, 2)).sum();

		long e1 = System.currentTimeMillis();
		System.out.println(e1 - s1);

		totaleven = 0;
		long s2 = System.currentTimeMillis();
		randomList.parallelStream().mapToDouble(i->Math.pow(i, 2)).sum();

		System.out.println("Even: "+totaleven);
		long e2 = System.currentTimeMillis();
		System.out.println(e2 - s2);

		long s3 = System.currentTimeMillis();
		sc.parallelize(randomList).mapToDouble(i->Math.pow(i, 2)).sum();
		System.out.println("Even: "+totaleven);
		long e3 = System.currentTimeMillis();
		System.out.println(e3 - s3);


	}

	public void printKernels(MyClustreamKernel[] kernels) {
		for (MyClustreamKernel myClustreamKernel : kernels) {
			System.out.println(myClustreamKernel);

		}
	}
	
	public void printKernels(List<MyClustreamKernel> kernels) {
		for (MyClustreamKernel myClustreamKernel : kernels) {
			System.out.println(myClustreamKernel);

		}
	}

	public double[] kernelCentroid(double value) {
		double[] k = new double[2];
		for (int i = 0; i < k.length; i++) {
			k[i] = value;
		}
		return k;
	}

	public Instances generateInstances() {
		List<com.yahoo.labs.samoa.instances.Attribute> attributes = new ArrayList<Attribute>();
		for(int attr = 0; attr < 2; attr++)
			attributes.add( new Attribute("attr" + attr) );

		Instances instances = new Instances("instances", attributes,0);

		double[] measures = {1, 5, 10, 19, 20, -1, 4, 30, 12, -1.5};
		for(double value: measures){
			double[] arrayElement = new double[2];
			arrayElement[0] = value; 
			arrayElement[1] = value;
			Instance inst = new DenseInstance(1, arrayElement);
			inst.setWeight(1);
			inst.setDataset(instances);
			instances.add(inst);
		}
		return instances;
	}
}
