package testing_parallelization;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import data_generator.DataGenerator;
import it.unibs.bodai.ideaas.clustream.CluStreamAlgorithmFactory;
import it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm;
import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.dao.model.Synthesi;
import it.unibs.bodai.ideaas.dao.model.SynthesiElement;
import it.unibs.bodai.ideaas.dao.utils.BestMetch;
import utils.CSVHandler;
import utils.CluStreamAlgorithmImplementation;

public class DataSummarisationTestingParallelization {
	/**
	 * RUN: nohup java -jar ideaas-data-summarisation-testing-0.0.1-SNAPSHOT-jar-with-dependencies.jar &
	 * see status: tail -f nohup.out
	 * @param args
	 */

	private static String directory="";
	private static String test;
	private static boolean set_master = false;
	private static int num_samples = 1000000;
	private static List<Integer> features = new ArrayList<>();
	private static List<Integer> syntheses = new ArrayList<>();
	private static List<String> algorithms = new ArrayList<>();

	private static CluStreamAlgorithmImplementation algNearest;
	private static CluStreamAlgorithmImplementation algOldest;
	private static CluStreamAlgorithmImplementation algMerge;

	private static List<Integer> buffer_size_variable = new ArrayList<>();
	private static int buffer_size = 500;

	// *********************************

	// *********************************

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.ERROR);
		buffer_size_variable.add(500);
		features.add(2);
		syntheses.add(1000);
		algorithms.add("no_parallel");

		try {
//			DataSummarisationTestingParallelization.populateCommandLineOptionsCombined(args);
			DataSummarisationTestingParallelization.populateCommandLineOptions(args);
					switch (DataSummarisationTestingParallelization.test) {
						case "test_pi":
							System.out.println("****************************** Testing PI SPARK vs JAVA STREAM *****************************************");
							DataSummarisationTestingParallelization.testSark();
							break;
						case "test_parallel": 
							DataSummarisationTestingParallelization.testParallel();
							break;
							
						case "test_combined": 
							DataSummarisationTestingParallelization.testPerformanceCombined();
							break;
			
						case "test_feasibility":
							System.out.println("****************************** Testing Feasibility *****************************************");
							DataSummarisationTestingParallelization.testBufferClusteringFeasibility();
							break;
						default:
			
							throw new Exception(String.format("Invalid \"%s\" value. Choose the test to execute. -help to see all possible options", DataSummarisationTestingParallelization.test));
						}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testParallel() throws Exception {
		boolean overBuffer = false;
		for (String alg : algorithms) {
			if(alg.equalsIgnoreCase("java_buffer") || alg.equalsIgnoreCase("spark_buffer") || alg.equalsIgnoreCase("no_parallel_buffer")) {
				overBuffer = true;
			}
			System.out.println(alg);
		}
		if(overBuffer) {
			System.out.println("****************************** Parallel Buffer Size *****************************************");
			DataSummarisationTestingParallelization.testPerformanceOverBuffer();

		}else {
			System.out.println("****************************** Testing Performance *****************************************");
			DataSummarisationTestingParallelization.testPerformance();
		}
	}

	private static void populateCommandLineOptionsCombined(String[] args) throws Exception {
		CommandLineParser parser = new GnuParser();

		// create CLI options
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addOption("d", "directory", true,
				"The directory we will analyze recursively in order to acquire data");

		options.addOption("a", "algorithm", true, "Chose the algorithm to test: \n"
				+ "no_parallel - No Parallel \n"
				+ "buffer_parallel - Parallel Buffer \n"
				+ "java_parallel - Parallel Java \n"
				+ "To combine more than one algortithm, separate by ; es: no_parallel;buffer_parallel;java_parallel");

		options.addOption("n", "nearest", true, "Chose the algorithm to run for elaborating nearest distances: \n"
				+ "no_parallel - No Parallel \n"
				+ "java_syntheses - Parallel Java Syntheses \n"
				+ "java_features - Parallel Java Features \n"
				+ "java_both - Parallel Java Both \n"
				+ "To combine more than one algortithm, separate by ; es: no_parallel;java_syntheses;java_features");//First

		options.addOption("o", "old", true, "Chose the algorithm to run for elaborating old syntheses: \n"
				+ "no_parallel - No Parallel \n"
				+ "java_syntheses - Parallel Java Syntheses \n"
				+ "To combine more than one algortithm, separate by ; es: no_parallel;java_syntheses;java_features");//Second

		options.addOption("m", "merge", true, "Chose the algorithm to run for elaborating merge clusters: \n"
				+ "no_parallel - No Parallel \n"
				+ "java_syntheses - Parallel Java Syntheses \n"
				+ "java_features - Parallel Java Features \n"
				+ "java_both - Parallel Java Both \n"
				+ "To combine more than one algortithm, separate by ; es: no_parallel;java_syntheses;java_features");//Third

		options.addOption("f", "features", true, "Number of features to test. Default = 2. \nIf more than one use ;. es: 2;10;20;30");
		options.addOption("q", "syntheses", true, "Number of syntheses to test. Default = 1000 \nIf more than one use ;. es: 100;200;500;1000");
		options.addOption("v", "variable_buffer_size", true, "Number of records to test. Default = 500 \nIf more than one use ;. es: 100;200;500;1000");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("Data Acquisition Standalone software", options);
				System.exit(0);
			}
			if (line.hasOption("directory")) {
				DataSummarisationTestingParallelization.directory = line.getOptionValue("directory");
			}

			
			if (line.hasOption("algorithm")) {
				DataSummarisationTestingParallelization.algorithms = new ArrayList<String>();
				String[] stringAlg = line.getOptionValue("algorithm").split(";");
				for (String alg : stringAlg) {
					DataSummarisationTestingParallelization.algorithms.add(alg);
				}
			}
			
			if (line.hasOption("nearest")) {
				String stringAlg = line.getOptionValue("nearest");
				DataSummarisationTestingParallelization.algNearest = DataSummarisationTestingParallelization.getAlgorithmType(stringAlg);
			}


			if (line.hasOption("old")) {
				String stringAlg = line.getOptionValue("old");
				DataSummarisationTestingParallelization.algOldest = DataSummarisationTestingParallelization.getAlgorithmType(stringAlg);
			}
			if (line.hasOption("merge")) {
				String stringAlg = line.getOptionValue("merge");
				DataSummarisationTestingParallelization.algMerge = DataSummarisationTestingParallelization.getAlgorithmType(stringAlg);
			}


			if (line.hasOption("features")) {
				DataSummarisationTestingParallelization.features = new ArrayList<Integer>();
				String[] stringFeatures = line.getOptionValue("features").split(";");
				for (String feature : stringFeatures) {
					DataSummarisationTestingParallelization.features.add(Integer.parseInt(feature));
				}
			}
			if (line.hasOption("syntheses")) {
				DataSummarisationTestingParallelization.syntheses = new ArrayList<Integer>();
				String[] stringSyntheses = line.getOptionValue("syntheses").split(";");
				for (String synthesis : stringSyntheses) {
					DataSummarisationTestingParallelization.syntheses.add(Integer.parseInt(synthesis));
				}
			}
			if (line.hasOption("variable_buffer_size")) {
				DataSummarisationTestingParallelization.buffer_size_variable = new ArrayList<Integer>();
				String[] stringRecords = line.getOptionValue("variable_buffer_size").split(";");
				for (String records : stringRecords) {
					//					System.out.println(records);
					DataSummarisationTestingParallelization.buffer_size_variable.add(Integer.parseInt(records));
				}
			}
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			System.exit(0);
		}
	}


	private static void populateCommandLineOptions(String[] args) {
		CommandLineParser parser = new GnuParser();

		// create CLI options
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addOption("d", "directory", true,
				"The directory we will analyze recursively in order to acquire data");


		options.addOption("t", "test", true, 
				"chose the algoritm to run: \n"
						+ "test_pi - Test Spark PI \n"
						+ "test_parallel - Test performance Parallel \n"
						+ "test_combined - Test performance combined algorithms"
						+ "test_feasibility - Test Feasibility");

		options.addOption("a", "algorithm", true, "Chose the algorithm to test: \n"
				+ "no_parallel - No Parallel \n"
				+ "java_syntheses - Parallel Java Syntheses \n"
				+ "java_features - Parallel Java Features \n"
				+ "java_both - Parallel Java Both \n"
				+ "spark_syntheses - Parallel Spark Syntheses \n"
				+ "spark_features - Parallel Spark Features \n"
				+ "spark_both - Parallel Spark Both \n"
				+ "java_buffer - Parallel Buffer With Java \n"
				+ "spark_buffer - Parallel Buffer With Spark\n"
				+ "no_parallel_buffer - No Buffer\n\n"
				+ "To combine more than one algortithm, separate by ; es: no_parallel;java_syntheses;java_features");

		options.addOption("s", "samples", true, "Number of samples for test_pi; default = 1000000");
		options.addOption("b", "buffer_size", true, "Number of new records to summarise; default = 500");

		options.addOption("f", "features", true, "Number of features to test. Default = 2. \nIf more than one use ;. es: 2;10;20;30");
		options.addOption("q", "syntheses", true, "Number of syntheses to test. Default = 1000 \nIf more than one use ;. es: 100;200;500;1000");
		options.addOption("v", "variable_buffer_size", true, "Number of records to test. Default = 500 \nIf more than one use ;. es: 100;200;500;1000");

		options.addOption("m", "set_master", false, "uset this parameter if you want to set local master");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("Data Acquisition Standalone software", options);
				System.exit(0);
			}
			if (line.hasOption("directory")) {
				DataSummarisationTestingParallelization.directory = line.getOptionValue("directory");
			}


			if (line.hasOption("test")) {
				System.out.println("test: "+line.getOptionValue("test"));
				DataSummarisationTestingParallelization.test = line.getOptionValue("test");
			}



			if (line.hasOption("samples")) {
				DataSummarisationTestingParallelization.num_samples = Integer.parseInt(line.getOptionValue("samples"));
			}

			if (line.hasOption("buffer_size")) {
				DataSummarisationTestingParallelization.buffer_size = Integer.parseInt(line.getOptionValue("buffer_size"));
			}

			//			if (line.hasOption("algorithm")) {
			//				DataSummarisationTestingParallelization.algorithm = line.getOptionValue("algorithm");
			//			}

			if (line.hasOption("algorithm")) {
				DataSummarisationTestingParallelization.algorithms = new ArrayList<String>();
				String[] stringAlg = line.getOptionValue("algorithm").split(";");
				//				DataSummarisationTestingParallelization.algorithms = new ReadOnlyArrayList<>(stringAlg);
				for (String alg : stringAlg) {
					DataSummarisationTestingParallelization.algorithms.add(alg);
				}
			}

			if (line.hasOption("features")) {
				DataSummarisationTestingParallelization.features = new ArrayList<Integer>();
				String[] stringFeatures = line.getOptionValue("features").split(";");
				for (String feature : stringFeatures) {
					DataSummarisationTestingParallelization.features.add(Integer.parseInt(feature));
				}
			}
			if (line.hasOption("syntheses")) {
				DataSummarisationTestingParallelization.syntheses = new ArrayList<Integer>();
				String[] stringSyntheses = line.getOptionValue("syntheses").split(";");
				for (String synthesis : stringSyntheses) {
					DataSummarisationTestingParallelization.syntheses.add(Integer.parseInt(synthesis));
				}
			}
			if (line.hasOption("variable_buffer_size")) {
				DataSummarisationTestingParallelization.buffer_size_variable = new ArrayList<Integer>();
				String[] stringRecords = line.getOptionValue("variable_buffer_size").split(";");
				for (String records : stringRecords) {
					System.out.println(records);
					DataSummarisationTestingParallelization.buffer_size_variable.add(Integer.parseInt(records));
				}
			}
			if (line.hasOption("set_master")) {
				DataSummarisationTestingParallelization.set_master = true;
			}else {

				DataSummarisationTestingParallelization.set_master = false;
			}

		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			System.exit(0);
		}
	}



	private static void testPerformanceOverBuffer() throws Exception {

		LocalDateTime now = LocalDateTime.now();
		String path = "buffer_parallel_clustering_"+now.getYear()+"_"+now.getMonth()+"_"+now.getDayOfMonth()+"_"+now.getHour()+"_"+now.getMinute()+"_parallel_syntheses"+".csv";
		String datetime = now.toString();

		String[] header = new String[] {"algorithm", "num_data", "num_syntheses", "num_features", "num_iterations", "mean_time (ms)", "mean_single_point_time (ms)", "datetime"};


		try {
			int num_iterations = 1;
			File csvFile = new File(DataSummarisationTestingParallelization.directory+path);
			try (CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath(), header)) {
				for (Integer num_records : DataSummarisationTestingParallelization.buffer_size_variable) {
					for(int num_feature : DataSummarisationTestingParallelization.features ) {
						FeatureSpace fs = DataGenerator.generateFeatureSpace(num_feature);
						for(int num_syntheses : DataSummarisationTestingParallelization.syntheses) {
							Instances initializeSynthesis = DataGenerator.generateInstances(10, 1, num_syntheses, fs);
							Instances newData = DataGenerator.generateInstances(10, 1, num_records, fs);
							//								System.out.println("Finised data generation..."+ "   initializeSynthesis: "+initializeSynthesis.size()+ "   newData: "+newData.size());
							System.out.println("\n");
							for(String alg: DataSummarisationTestingParallelization.algorithms) {
								String algorithm = DataSummarisationTestingParallelization.getAlgorithmbuffer(alg);

								switch (algorithm) {
								case "JAVA_PARALLEL_BUFFER":
									//									MyParallelCluStreamAlgorithmBuffer cluStreamAlgorithmBuffer = new MyParallelCluStreamAlgorithmBuffer();
									ICluStreamAlgorithm cluStreamAlgorithmBuffer = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelBufferCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_BUFFER, CluStreamAlgorithmImplementation.NO_PARALLEL);
									cluStreamAlgorithmBuffer.setNumMicroKernels(num_syntheses);
									cluStreamAlgorithmBuffer.setTimeHorizont(num_records/2);
									cluStreamAlgorithmBuffer.resetLearningImpl();
									cluStreamAlgorithmBuffer.trainOnInstanceImpl(initializeSynthesis);
									//									System.out.println("****************** Initialized *******************");
									//									DataSummarisationTestingParallelization.initializeMicroClusters(CluStreamAlgorithmImplementation.NO_PARALLEL, initializeSynthesis, cluStreamAlgorithm, num_syntheses);
									long time = 0;long single_measure_time = 0;

									long current = System.currentTimeMillis();
									//									for(int i = 0; i<num_iterations; i++) {
									//										single_measure_time = single_measure_time + DataSummarisationTestingParallelization.runClusteringBuffer(newData, cluStreamAlgorithm, num_syntheses, algorithm);
									//										time = time + (System.currentTimeMillis() - current);
									//									}

									cluStreamAlgorithmBuffer.trainOnInstanceImpl(newData);
									time = System.currentTimeMillis() - current;
									if(newData.size()>0)
										single_measure_time = time/newData.size();
									//									System.out.println("num data "+num_records+";  q: " + num_syntheses + "  feature: "+ num_feature+" time:"+ time);
									System.out.println("******** " + algorithm + " Finished " + " N -> " + num_records + "   q -> " + num_syntheses + "   f -> " + num_feature + " time -> "+ time);
									csvHandler.printRow(""+algorithm, ""+num_records, ""+num_syntheses, ""+num_feature, ""+num_iterations, ""+(time/num_iterations), ""+(single_measure_time/num_iterations), datetime);
									break;

								case "SPARK_PARALLEL_BUFFER":
//								
									SparkConf conf = new SparkConf().setAppName("IDEAaS");
									if(DataSummarisationTestingParallelization.set_master) {
										conf = new SparkConf().setAppName("IDEAaS").setMaster("local");
									}
//									try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
									try(SparkSession sparkSession = SparkSession.builder().appName("IDEAaS").getOrCreate()){
										ICluStreamAlgorithm cluStreamSparkAlgorithmBuffer = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelSparkBufferCluStreamAlgorithm(CluStreamAlgorithmImplementation.SPARK_PARALLEL_BUFFER, CluStreamAlgorithmImplementation.NO_PARALLEL, sparkSession);//getParallelBufferCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_BUFFER, CluStreamAlgorithmImplementation.NO_PARALLEL);
										sparkSession.sparkContext().setLogLevel(Level.FATAL.toString());
										cluStreamSparkAlgorithmBuffer.setNumMicroKernels(num_syntheses);
										cluStreamSparkAlgorithmBuffer.setTimeHorizont(num_records/2);
										cluStreamSparkAlgorithmBuffer.resetLearningImpl();
										cluStreamSparkAlgorithmBuffer.trainOnInstanceImpl(initializeSynthesis);
										//									System.out.println("****************** Initialized *******************");
										//									DataSummarisationTestingParallelization.initializeMicroClusters(CluStreamAlgorithmImplementation.NO_PARALLEL, initializeSynthesis, cluStreamAlgorithm, num_syntheses);
										time = 0; single_measure_time = 0;
	
										current = System.currentTimeMillis();
										//									for(int i = 0; i<num_iterations; i++) {
										//										single_measure_time = single_measure_time + DataSummarisationTestingParallelization.runClusteringBuffer(newData, cluStreamAlgorithm, num_syntheses, algorithm);
										//										time = time + (System.currentTimeMillis() - current);
										//									}
	
										cluStreamSparkAlgorithmBuffer.trainOnInstanceImpl(newData);
										time = System.currentTimeMillis() - current;
										if(newData.size()>0)
											single_measure_time = time/newData.size();
										//									System.out.println("num data "+num_records+";  q: " + num_syntheses + "  feature: "+ num_feature+" time:"+ time);
										System.out.println("******** " + algorithm + " Finished " + " N -> " + num_records + "   q -> " + num_syntheses + "   f -> " + num_feature + " time -> "+ time);
										csvHandler.printRow(""+algorithm, ""+num_records, ""+num_syntheses, ""+num_feature, ""+num_iterations, ""+(time/num_iterations), ""+(single_measure_time/num_iterations), datetime);
									
									} catch (Exception e) {
										System.out.println(e.getMessage());
										// TODO: handle exception
									}
									break;

								
								case "NO_PARALLEL_BUFFER":
									ICluStreamAlgorithm cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.NO_PARALLEL);

									cluStreamAlgorithm.setNumMicroKernels(num_syntheses);
									cluStreamAlgorithm.setTimeHorizont(num_records/2);
									DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm, num_syntheses);
									time = 0;single_measure_time = 0;
									for(int i = 0; i<num_iterations; i++) {
										current = System.currentTimeMillis();
										single_measure_time = single_measure_time + DataSummarisationTestingParallelization.runClusteringBuffer(newData, cluStreamAlgorithm, num_syntheses, algorithm);
										time = time + (System.currentTimeMillis() - current);
									}
									//									System.out.println("num data "+num_records+";  q: " + num_syntheses + "  feature: "+ num_feature+" time:"+ time);
									//									System.out.println("********************** Finished:  "+algorithm + "  ***********************\n\n");
									System.out.println("******** " + algorithm + " Finished " + "   N -> " + num_records + "   q -> " + num_syntheses + "   f -> " + num_feature + " time -> "+ time);

									csvHandler.printRow(""+algorithm, ""+num_records, ""+num_syntheses, ""+num_feature, ""+num_iterations, ""+(time/num_iterations), ""+(single_measure_time/num_iterations), datetime);
									break;
								default:
									System.out.println("No alg: "+algorithm); 
								}
							}
						}
					}

				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("fine");
	} 

	private static void testSark() {
		LocalDateTime now = LocalDateTime.now();
		int samples = DataSummarisationTestingParallelization.num_samples;
		String path = "test_parallel_pi_"+now.getYear()+"_"+now.getMonthValue()+"_"+now.getDayOfMonth()+".csv";
		String datetime = now.toString();
		String[] header = new String[] {"num", "alg", "time", "datetime"};
		File csvFile = new File(DataSummarisationTestingParallelization.directory+path);
		try (CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath(), header)) {
			List<Integer> l = new ArrayList<>();
			for (int i = 0; i < samples; i++) {
				l.add(i);
			}


			SparkConf conf = new SparkConf().setAppName("IDEAaS");//.setMaster("spark://59bd3b8cdd1d:7077");//.setMaster("local*");
			if(DataSummarisationTestingParallelization.set_master) {
				conf = new SparkConf().setAppName("IDEAaS").setMaster("local");
			}
			try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
				sparkContext.setLogLevel("ERROR");
				System.out.println("\n******************* SPARK ******************\n");
				long current = System.currentTimeMillis();

				long count = sparkContext.parallelize(l).filter(i -> {
					double x = Math.random();
					double y = Math.random();
					return x*x + y*y < 1;
				}).count();
				csvHandler.printRow(samples+"", "SPARK", ""+ (System.currentTimeMillis() - current), datetime);

				//				System.out.println("Pi is roughly " + 4.0 * count / samples);
				System.out.println("Time (ms): " + (System.currentTimeMillis() - current)/1000);
			}


			System.out.println("\n******************* JAVA PARALLEL STREAM ******************\n");
			long current = System.currentTimeMillis();


			long count2 = l.parallelStream().filter(i -> {
				double x = Math.random();
				double y = Math.random();
				return x*x + y*y < 1;
			}).count();

			csvHandler.printRow(samples+"", "JAVA", ""+ (System.currentTimeMillis() - current), datetime);
			//				System.out.println("Pi is roughly " + 4.0 * count2 / samples);
			System.out.println("Time (ms): " + (System.currentTimeMillis() - current)/1000);

		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static void testBufferClusteringFeasibility() throws Exception {
		int num_feature = 2;
		FeatureSpace fs = DataGenerator.generateFeatureSpace(num_feature);

		CluStreamAlgorithmImplementation algorithm = CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES;
//		int num_records = DataSummarisationTestingParallelization.buffer_size;


		int[] types = {1, 2, 4};
		String[] description = {"no variations", "begin", "end", "midle", "sparse", "parallel_buffer"};

		LocalDateTime now = LocalDateTime.now();
		String path = "src/main/resources/feasibility_parallel_clustering"+now.getYear()+"_"+now.getMonth()+"_"+now.getDayOfMonth()+".csv";
		String datetime = now.toString();

		String[] header = new String[] {"algorithm", "num_data", "num_syntheses", "num_features", "detected_anomaly", "description", "type", datetime};
		File csvFile = new File(DataSummarisationTestingParallelization.directory+path);
		List<com.yahoo.labs.samoa.instances.Attribute> attributes = new ArrayList<Attribute>();

		for(int attr = 0; attr < fs.getFeatures().size(); attr++)
			attributes.add( new Attribute("attr" + attr) );

		try (CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath(), header)) {
			int q = 1000;
			int[] bufferSizes = {2100};
			for (int num_records : bufferSizes) {

				System.out.println("\nNum_sysnteses: "+q);
				Instances initializeSynthesis = DataGenerator.generateInstances(10,  0.2, q, fs);
				Instances newData = DataGenerator.generateInstances(10, 0.2, num_records, fs);
				ICluStreamAlgorithm cluStreamAlgorithmBase = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(algorithm);

				cluStreamAlgorithmBase.setNumMicroKernels(q);
				cluStreamAlgorithmBase.setTimeHorizont(num_records+1);

				DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithmBase,q);
				DataSummarisationTestingParallelization.runClustering(newData, cluStreamAlgorithmBase, q);
				SummarisedData prevSumm =  new SummarisedData();
				prevSumm.setSynthesis(DataSummarisationTestingParallelization.getOutputKernelsFromCluStream(cluStreamAlgorithmBase.getKernels(), fs));

				Instances newDataBase = DataGenerator.generateInstances(10,  0.2, num_records*80/100, fs);
				Instances newDataOther = DataGenerator.generateInstances(20,  0.2, num_records*20/100, fs);
				System.out.println("\nstart foreachTypes: "+types.length);

				for (int type : types) {
					System.out.println("\nType: "+type + " -> desc: "+description[type]);

					switch (type) {
//					case 0:
//						Instances newDataEqual = DataGenerator.generateInstances(10,  0.2, num_records*20/100, fs);
//						ICluStreamAlgorithm cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(algorithm);
//						cluStreamAlgorithm.setNumMicroKernels(q);
//						cluStreamAlgorithmBase.setTimeHorizont(num_records+1000);
//						DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm, q);
//						DataSummarisationTestingParallelization.runClustering(newDataBase, cluStreamAlgorithm, q);
//						DataSummarisationTestingParallelization.runClustering(newDataEqual, cluStreamAlgorithm, q);
//						SummarisedData sumData =  new SummarisedData();
//						sumData.setSynthesis(DataSummarisationTestingParallelization.getOutputKernelsFromCluStream(cluStreamAlgorithm.getKernels(), fs));
//
//						BestMetch bestMetch =  sumData.synthesesBestMetch(prevSumm);
//						csvHandler.printRow(""+algorithm, ""+num_records, ""+q, ""+num_feature, bestMetch.getDiffCentroids()+"", description[type], type+"", datetime);
//
//						break;

					case 1:

						ICluStreamAlgorithm cluStreamAlgorithm1 = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(algorithm);
						cluStreamAlgorithm1.setNumMicroKernels(q);
						cluStreamAlgorithm1.setTimeHorizont(num_records+1000);
						cluStreamAlgorithm1.resetLearningImpl();
						DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm1, q);
						DataSummarisationTestingParallelization.runClustering(newDataOther, cluStreamAlgorithm1, q);
						DataSummarisationTestingParallelization.runClustering(newDataBase, cluStreamAlgorithm1, q);
						SummarisedData sumData1 =  new SummarisedData();
						sumData1.setSynthesis(DataSummarisationTestingParallelization.getOutputKernelsFromCluStream(cluStreamAlgorithm1.getKernels(), fs));

						BestMetch bestMetch1 =  sumData1.synthesesBestMetch(prevSumm);
						csvHandler.printRow(""+algorithm, ""+num_records, ""+q, ""+num_feature, bestMetch1.getDiffCentroids()+"", description[type], type+"", datetime);

						break;


					case 2:
						ICluStreamAlgorithm cluStreamAlgorithm2 = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(algorithm);
						cluStreamAlgorithm2.setNumMicroKernels(q);
						cluStreamAlgorithm2.setTimeHorizont(num_records+1000);
						cluStreamAlgorithm2.resetLearningImpl();
						DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm2, q);
						DataSummarisationTestingParallelization.runClustering(newDataBase, cluStreamAlgorithm2, q);
						DataSummarisationTestingParallelization.runClustering(newDataOther, cluStreamAlgorithm2, q);
						SummarisedData sumData2 =  new SummarisedData();
						sumData2.setSynthesis(DataSummarisationTestingParallelization.getOutputKernelsFromCluStream(cluStreamAlgorithm2.getKernels(), fs));

						BestMetch bestMetch2 =  sumData2.synthesesBestMetch(prevSumm);
						csvHandler.printRow(""+algorithm, ""+num_records, ""+q, ""+num_feature, bestMetch2.getDiffCentroids()+"", description[type], type+"", datetime);

						break;

//					case 3:
//
//
//						Instances values1 = new Instances("instances",attributes,0);
//						for (int k = 0; k< newDataBase.size(); k++) {
//							values1.add(newDataBase.get(k));
//							if(k==200) {
//								for (int j = 0; j < newDataOther.size(); j++) {
//									values1.add(newDataOther.get(j));
//
//								}
//							}
//						}
//
//						ICluStreamAlgorithm cluStreamAlgorithm3 = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(algorithm);
//						cluStreamAlgorithm3.setNumMicroKernels(q);
//						cluStreamAlgorithm3.setTimeHorizont(num_records+1000);
//						DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm3, q);
//						DataSummarisationTestingParallelization.runClustering(values1, cluStreamAlgorithm3, q);
//						SummarisedData sumData3 =  new SummarisedData();
//						sumData3.setSynthesis(DataSummarisationTestingParallelization.getOutputKernelsFromCluStream(cluStreamAlgorithm3.getKernels(), fs));
//
//						BestMetch bestMetch3 =  sumData3.synthesesBestMetch(prevSumm);
//						csvHandler.printRow(""+algorithm, ""+num_records, ""+q, ""+num_feature, bestMetch3.getDiffCentroids()+"", description[type], type+"", datetime);
//
//						break;

					case 4:

						Instances values2 = new Instances("instances",attributes,0);
						for (int k = 0; k< newDataBase.size(); k++) {

							values2.add(newDataBase.get(k));
							if (k == 0) {
								for (int j = 0; j < newDataOther.size()/4; j++) {
									values2.add(newDataOther.get(j));

								}
							}
							if(k==(long)(newDataBase.size()/4)) {
								for (int j = newDataOther.size()/4; j < newDataOther.size()/2; j++) {
									values2.add(newDataOther.get(j));

								}
							}
							if(k==(long)(newDataBase.size()/2)) {
								for (int j = newDataOther.size()/2; j < newDataOther.size()*3/4; j++) {
									values2.add(newDataOther.get(j));

								}
							}

							if(k==(long)(newDataBase.size()*(3/4))){
								for (int j = newDataOther.size()*3/4; j < newDataOther.size(); j++) {
									values2.add(newDataOther.get(j));

								}
							}
						}

						ICluStreamAlgorithm cluStreamAlgorithm4 = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(algorithm);
						cluStreamAlgorithm4.setNumMicroKernels(q);
						cluStreamAlgorithm4.setTimeHorizont(num_records+1000);
						cluStreamAlgorithm4.resetLearningImpl();
						DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm4, q);
						DataSummarisationTestingParallelization.runClustering(values2, cluStreamAlgorithm4, q);
						SummarisedData sumData4 =  new SummarisedData();
						sumData4.setSynthesis(DataSummarisationTestingParallelization.getOutputKernelsFromCluStream(cluStreamAlgorithm4.getKernels(), fs));

						BestMetch bestMetch4 =  sumData4.synthesesBestMetch(prevSumm);
						csvHandler.printRow(""+algorithm, ""+num_records, ""+q, ""+num_feature, bestMetch4.getDiffCentroids()+"", description[type], type+"", datetime);

						break;
					case 5:
						ICluStreamAlgorithm cluStreamAlgorithmBuffer = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_BUFFER);

						//						MyParallelCluStreamAlgorithmBuffer cluStreamAlgorithmBuffer = new MyParallelCluStreamAlgorithmBuffer();
						//						DataSummarisationTestingParallelization.initializeMicroClusters(algorithm, initializeSynthesis, cluStreamAlgorithm1, q);
						cluStreamAlgorithmBuffer.setNumMicroKernels(q);
						cluStreamAlgorithmBuffer.setTimeHorizont(num_records+1);
						cluStreamAlgorithmBuffer.trainOnInstanceImpl(initializeSynthesis);
						Instances values3 = new Instances("instances",attributes,0);
						for (int k = 0; k< newDataBase.size(); k++) {

							values3.add(newDataBase.get(k));
							if (k == 0) {
								for (int j = 0; j < newDataOther.size()/4; j++) {
									values3.add(newDataOther.get(j));

								}
							}
							if(k==100) {
								for (int j = newDataOther.size()/4; j < newDataOther.size()/2; j++) {
									values3.add(newDataOther.get(j));

								}
							}
							if(k==200) {
								for (int j = newDataOther.size()/2; j < newDataOther.size()*3/4; j++) {
									values3.add(newDataOther.get(j));

								}
							}

							if(k == 300) {
								for (int j = newDataOther.size()*3/4; j < newDataOther.size(); j++) {
									values3.add(newDataOther.get(j));

								}
							}
						}
						cluStreamAlgorithmBuffer.trainOnInstanceImpl(values3);
						//						DataSummarisationTestingParallelization.runClustering(newDataOther, cluStreamAlgorithm1, q);
						//						DataSummarisationTestingParallelization.runClustering(newDataBase, cluStreamAlgorithm1, q);
						SummarisedData sumDataBuffer =  new SummarisedData();
						sumDataBuffer.setSynthesis(DataSummarisationTestingParallelization.getOutputKernelsFromCluStream(cluStreamAlgorithmBuffer.getKernels(), fs));

						BestMetch bestMetchBuffer =  sumDataBuffer.synthesesBestMetch(prevSumm);
						csvHandler.printRow(""+algorithm, ""+num_records, ""+q, ""+num_feature, bestMetchBuffer.getDiffCentroids()+"", description[type], type+"", datetime);

						break;

					default:
						break;
					}
				}
			}

		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("\nfine");

	}

	private static void testPerformanceCombined() throws Exception {

		LocalDateTime now = LocalDateTime.now();
//		String path = "combined_parallel_clustering_"+now.getYear()+"_"+now.getMonth()+"_"+now.getDayOfMonth()+"_"+now.getHour()+"_"+now.getMinute()+"_parallel_syntheses"+".csv";

		String path = "combined_parallel_clustering_"+now.getYear()+"_"+now.getMonth()+"_"+now.getDayOfMonth()+"_parallel_syntheses"+".csv";
		String[] header = new String[] {"algorithm", "num_data", "num_syntheses", "num_features", "num_iterations", "mean_time (ms)", "mean_single_point_time (ms)", "datetime"};


		try {
			File csvFile = new File(DataSummarisationTestingParallelization.directory+path);
			try (CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath(), header)) {
				for (Integer num_records : DataSummarisationTestingParallelization.buffer_size_variable) {
					for(int num_feature : DataSummarisationTestingParallelization.features ) {
						FeatureSpace fs = DataGenerator.generateFeatureSpace(num_feature);
						for(int num_syntheses : DataSummarisationTestingParallelization.syntheses) {
							Instances initializeSynthesis = DataGenerator.generateInstances(10, 1, num_syntheses, fs);
							Instances newData = DataGenerator.generateInstances(10, 1, num_records, fs);
							System.out.println("\n");
							for (String alg : DataSummarisationTestingParallelization.algorithms) {
								switch (alg) {
//								+ "no_parallel - No Parallel \n"
//										+ "buffer_parallel - Parallel Buffer \n"
//										+ "java_parallel - Parallel Java \n"
								case "buffer_parallel":
									DataSummarisationTestingParallelization.bufferCombined(csvHandler, initializeSynthesis, newData, num_records, num_syntheses, num_feature);
									break;
								case "java_parallel":
									DataSummarisationTestingParallelization.javaCombined(csvHandler, initializeSynthesis, newData, num_records, num_syntheses, num_feature, "COMBINED_PARALLEL_JAVA", DataSummarisationTestingParallelization.algNearest, DataSummarisationTestingParallelization.algOldest, DataSummarisationTestingParallelization.algMerge);
									break;
								case "no_parallel":
									DataSummarisationTestingParallelization.javaCombined(csvHandler, initializeSynthesis, newData, num_records, num_syntheses, num_feature, "NO_PARALLEL", CluStreamAlgorithmImplementation.NO_PARALLEL, CluStreamAlgorithmImplementation.NO_PARALLEL, CluStreamAlgorithmImplementation.NO_PARALLEL);
									break;
								default:
									break;
								}
							}
							
//							DataSummarisationTestingParallelization.javaCombined(csvHandler, initializeSynthesis, newData, num_records, num_syntheses, num_feature, "COMBINED_PARALLEL_JAVA", DataSummarisationTestingParallelization.algNearest, DataSummarisationTestingParallelization.algOldest, DataSummarisationTestingParallelization.algMerge);
//							DataSummarisationTestingParallelization.javaCombined(csvHandler, initializeSynthesis, newData, num_records, num_syntheses, num_feature, "NO_PARALLEL", CluStreamAlgorithmImplementation.NO_PARALLEL, CluStreamAlgorithmImplementation.NO_PARALLEL, CluStreamAlgorithmImplementation.NO_PARALLEL);
							
//						}
					}
				}

			}
		}
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	System.out.println("fine");
} 
private static void bufferCombined(CSVHandler csvHandler, Instances initializeSynthesis, Instances newData, int num_records, int num_syntheses, int num_feature) throws Exception {
	LocalDateTime now = LocalDateTime.now();
	String datetime = now.toString();

	ICluStreamAlgorithm cluStreamAlgorithmBuffer = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getCombinedParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_BUFFER, algNearest, algOldest, algMerge);

	cluStreamAlgorithmBuffer.setNumMicroKernels(num_syntheses);
	cluStreamAlgorithmBuffer.setTimeHorizont(num_records/2);
	cluStreamAlgorithmBuffer.resetLearningImpl();
	cluStreamAlgorithmBuffer.trainOnInstanceImpl(initializeSynthesis);
	long time = 0;
	long current = System.currentTimeMillis();
	cluStreamAlgorithmBuffer.trainOnInstanceImpl(newData);
	time = System.currentTimeMillis() - current;
	System.out.println("\n******** COMBINED_PARALLEL_BUFFER" + " -> "+algNearest+" -> "+algOldest+" -> "+algMerge + " Finished " + "    N -> " + num_records + "   q -> " + num_syntheses + "   f -> " + num_feature + " time -> "+ time);
	csvHandler.printRow("COMBINED_PARALLEL_BUFFER"+" -> "+algNearest+" -> "+algOldest+" -> "+algMerge, ""+num_records, ""+num_syntheses, ""+num_feature, "1", ""+time, "", datetime);
	
}

private static void javaCombined(CSVHandler csvHandler, Instances initializeSynthesis, Instances newData, int num_records, int num_syntheses, int num_feature, String desc, CluStreamAlgorithmImplementation algNearest, CluStreamAlgorithmImplementation algOldest, CluStreamAlgorithmImplementation algMerge) throws Exception {
	LocalDateTime now = LocalDateTime.now();
	String datetime = now.toString();
	ICluStreamAlgorithm cluStreamAlgorithmCombined = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getCombinedParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.NO_PARALLEL, algNearest, algOldest, algMerge);

	cluStreamAlgorithmCombined.setNumMicroKernels(num_syntheses);
	cluStreamAlgorithmCombined.setTimeHorizont(num_records/2);
	DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithmCombined, num_syntheses);
	long current = System.currentTimeMillis();
	DataSummarisationTestingParallelization.runClustering(newData, cluStreamAlgorithmCombined, num_syntheses);//(newData, cluStreamAlgorithmCombined, num_syntheses, algorithm);
	long time = System.currentTimeMillis() - current;
	System.out.println("\n******** "+ desc +" -> "+algNearest+" -> "+algOldest+" -> "+algMerge + " Finished " + "           N -> " + num_records + "   q -> " + num_syntheses + "   f -> " + num_feature + " time -> "+ time);

	csvHandler.printRow(desc+" -> "+algNearest+" -> "+algOldest+" -> "+algMerge, ""+num_records, ""+num_syntheses, ""+num_feature, "1", ""+time, "", datetime);
}
private static void testPerformance() {
	LocalDateTime now = LocalDateTime.now();

	String path = "algorithm_parallel_clustering_"+now.getYear()+"_"+now.getMonth()+"_"+now.getDayOfMonth()+"_"+now.getHour()+"_"+now.getMinute()+".csv";
	String datetime = now.toString();
	String[] header = new String[] {"algorithm", "num_data", "num_syntheses", "num_features", "num_iterations", "mean_time (ms)", "mean_single_point_time (ms)", "datetime"};
	File csvFile = new File(directory+path);
	try (CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath(), header)) {

		try {

			int num_records = DataSummarisationTestingParallelization.buffer_size;
			int num_iterations = 1;
			for (String alg : DataSummarisationTestingParallelization.algorithms) {

				CluStreamAlgorithmImplementation algorithm = DataSummarisationTestingParallelization.getAlgorithmType(alg);

				for(int num_feature : DataSummarisationTestingParallelization.features ) {
					FeatureSpace fs = DataGenerator.generateFeatureSpace(num_feature);
					for(int num_syntheses : DataSummarisationTestingParallelization.syntheses) {
						System.out.println("Generating Data...");
						Instances initializeSynthesis = DataGenerator.generateInstances(10, 1, num_syntheses, fs);
						Instances newData = DataGenerator.generateInstances(10, 1, num_records, fs);

						ICluStreamAlgorithm cluStreamAlgorithm;
						System.out.println("Running: "+algorithm+"\nFeatures: "+num_feature+"\nsyntheses: "+num_syntheses+"\nData: "+num_records );
						if(algorithm == CluStreamAlgorithmImplementation.SPARK_PARALLEL_BOTH || 
								algorithm == CluStreamAlgorithmImplementation.SPARK_PARALLEL_FEATURES ||
								algorithm == CluStreamAlgorithmImplementation.SPARK_PARALLEL_SYNTHESES) {
							SparkConf conf = new SparkConf().setAppName("IDEAaS");
							if(DataSummarisationTestingParallelization.set_master) {
								conf = new SparkConf().setAppName("IDEAaS").setMaster("local");
							}
							try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
								sparkContext.setLogLevel(Level.FATAL.toString());

								cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamSpark(algorithm, sparkContext);
								cluStreamAlgorithm.setNumMicroKernels(num_syntheses);
								cluStreamAlgorithm.setTimeHorizont(num_records+1);
								DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm, num_syntheses);
								long time = 0;long single_measure_time = 0;
								for(int i = 0; i<num_iterations; i++) {
									long current = System.currentTimeMillis();
									single_measure_time = single_measure_time + DataSummarisationTestingParallelization.runClustering(newData, cluStreamAlgorithm, num_syntheses);
									time = time + (System.currentTimeMillis() - current);
								}

								System.out.println(algorithm + " \nFinished \n n "+num_records+";  \n k: " + num_syntheses + "   \n feature: "+ num_feature+" time:"+ single_measure_time/num_iterations);
								csvHandler.printRow(""+algorithm, ""+num_records, ""+num_syntheses, ""+num_feature, ""+num_iterations, ""+(time/num_iterations), ""+(single_measure_time/num_iterations), datetime);

							}
						}else {
							cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getParallelCluStreamAlgorithm(algorithm);
							cluStreamAlgorithm.setNumMicroKernels(num_syntheses);
							cluStreamAlgorithm.setTimeHorizont(num_records+1);
							DataSummarisationTestingParallelization.initializeMicroClusters(initializeSynthesis, cluStreamAlgorithm, num_syntheses);
							long time = 0;long single_measure_time = 0;
							for(int i = 0; i<num_iterations; i++) {
								long current = System.currentTimeMillis();
								single_measure_time = single_measure_time + DataSummarisationTestingParallelization.runClustering(newData, cluStreamAlgorithm, num_syntheses);
								time = time + (System.currentTimeMillis() - current);
							}

							System.out.println(algorithm + " \nFinished \n n "+num_records+";  \n k: " + num_syntheses + "   \n feature: "+ num_feature+" time:"+ single_measure_time/num_iterations);
							csvHandler.printRow(""+algorithm, ""+num_records, ""+num_syntheses, ""+num_feature, ""+num_iterations, ""+(time/num_iterations), ""+(single_measure_time/num_iterations), datetime);

						}

					}




				}
				System.out.println("");
			}

			System.out.println("\n************** fine *********************");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
}

private static CluStreamAlgorithmImplementation getAlgorithmType(String alg) throws Exception {
	//		+ "no_parallel - No Parallel \n"
	//				+ "java_syntheses - Parallel Java Syntheses \n"
	//				+ "java_features - Parallel Java Features \n"
	//				+ "java_both - Parallel Java Both \n"
	//				+ "spark_syntheses - Parallel Spark Syntheses \n"
	//				+ "spark_features - Parallel Spark Features \n"
	//				+ "spark_both - Parallel Spark Both \n"
	switch (alg) {
	case "no_parallel":
		return CluStreamAlgorithmImplementation.NO_PARALLEL;
	case "java_syntheses":
		return CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES;
	case "java_features":
		return CluStreamAlgorithmImplementation.JAVA_PARALLEL_FEATURES;
	case "java_both":
		return CluStreamAlgorithmImplementation.JAVA_PARALLEL_BOTH;
	case "spark_syntheses":
		return CluStreamAlgorithmImplementation.SPARK_PARALLEL_SYNTHESES;
	case "spark_features":
		return CluStreamAlgorithmImplementation.SPARK_PARALLEL_FEATURES;
	case "spark_both":
		return CluStreamAlgorithmImplementation.SPARK_PARALLEL_BOTH;
	default:
		throw new Exception("Ivalid algorithm selected");
	}
}


private static String getAlgorithmbuffer(String alg) throws Exception {
	switch (alg) {
	case "java_buffer":
		return "JAVA_PARALLEL_BUFFER";
	case "spark_buffer":
		return "SPARK_PARALLEL_BUFFER";
	case "no_parallel_buffer":
		return "NO_PARALLEL_BUFFER";

		//			String[] parallel_type = new String[] {"SPARK_PARALLEL_BUFFER", "JAVA_PARALLEL_BUFFER"};

	default:
		throw new Exception("Ivalid algorithm selected: "+alg);
	}
}

private static long runClustering(Instances instances, ICluStreamAlgorithm cluStreamAlgorithm, int num_syntheses) {
	//		cluStreamAlgorithm.resetLearningImpl();
	long sum_time = 0;

	System.out.println("\n");
	for (int i = 0; i< instances.size(); i++) {

		System.out.print("=");
		long current = System.nanoTime();
		Instance inst = instances.get(i);
		cluStreamAlgorithm.trainOnInstanceImpl(inst);
		sum_time = sum_time + (System.nanoTime()-current);
	}

	return sum_time/instances.size();
}

private static long runClusteringBuffer(Instances instances, ICluStreamAlgorithm cluStreamAlgorithm, int num_syntheses, String parallel_type) {
	//		cluStreamAlgorithm.resetLearningImpl();
	List<Integer> indexes = new ArrayList<>();
	for(int i = 0; i < instances.size(); i++) {
		indexes.add(i);
	}

	switch (parallel_type) {

	case "SPARK_PARALLEL_BUFFER":
		SparkConf conf = new SparkConf().setAppName("IDEAaS");
		if(DataSummarisationTestingParallelization.set_master) {
			conf = new SparkConf().setAppName("IDEAaS").setMaster("local");
		}
		try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {


			return sparkContext.parallelize(indexes).map(i-> {
				long current = System.nanoTime();
				Instance inst = instances.get(i);
				cluStreamAlgorithm.trainOnInstanceImpl(inst);
				return System.nanoTime()-current;
			}).reduce((a,b)->a+b)/instances.size();
		}
		//			for (int i = 0; i< instances.size(); i++) {
		//
		//				System.out.print("=");
		//				long current = System.nanoTime();
		//				Instance inst = instances.get(i);
		//				cluStreamAlgorithm.trainOnInstanceImpl(inst);
		//				sum_time = sum_time + ();
		//			}
		//
		//			return sum_time/instances.size();
	case "JAVA_PARALLEL_BUFFER":
		System.out.println(instances.size());
		return indexes.stream().mapToLong(i-> {
			long current = System.nanoTime();
			Instance inst = instances.get(i);

			System.out.println(inst.toDoubleArray().toString());
			cluStreamAlgorithm.trainOnInstanceImpl(inst);
			return System.nanoTime()-current;
		}).sum()/instances.size();

	case "NO_PARALLEL_BUFFER":
		//			System.out.println(parallel_type+"  -> instances: "+instances.size());
		//			System.out.println(parallel_type+"  -> indexes: "+indexes.size());
		System.out.println("\n");
		return indexes.stream().mapToLong(i-> {
			System.out.print("=");

			long current = System.nanoTime();
			Instance inst = instances.get(i);
			cluStreamAlgorithm.trainOnInstanceImpl(inst);
			return System.nanoTime()-current;
		}).sum()/instances.size();


	default:
		return 0;
	}

}

private static void initializeMicroClusters(Instances instances, ICluStreamAlgorithm cluStreamAlgorithm, int num_syntheses) {
	cluStreamAlgorithm.resetLearningImpl();
	for (int i = 0; i< instances.size(); i++) {
		Instance inst = instances.get(i);
		cluStreamAlgorithm.trainOnInstanceImpl(inst);

	}
}



/**
 * @param kernels
 * @param featureSpace
 * @param totNumberNewData
 * @return
 */
private static HashMap<Integer, Synthesi> getOutputKernelsFromCluStream(MyClustreamKernel[] kernels, FeatureSpace featureSpace){
	HashMap<Integer, Synthesi> synthesis = new HashMap<>();
	for (MyClustreamKernel kernel : kernels) {
		Synthesi synthesi = new Synthesi(kernel.getIdMicroCluster(), kernel.getParentId(), kernel.getParentHistoryId());
		synthesi.setNumberData((long) kernel.getN());
		synthesi.setRadius(kernel.getRadius());
		synthesi.instances = kernel.instances;
		for(int j=0; j < kernel.LS.length; j++){
			int i=0;
			for (Feature feature: featureSpace.getFeatures()) {
				if(i==j) {
					synthesi.addSynthesiElements(new SynthesiElement(kernel.LS[j], kernel.SS[j], feature, synthesi.getNumberData()));
				}
				i++;
			}
		}
		synthesis.put(synthesi.getId(), synthesi);
	}
	return synthesis;
}

}
