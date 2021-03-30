package spark;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import utils.CSVHandler;

public class SparkTesting {

	private static int num_samples = 1000000;

	private static boolean set_master = false;
	private static boolean print = false;

	private static Logger logger = Logger.getLogger(SparkTesting.class);
	
	public static void main(String[] args) {

		SparkTesting.logger.setLevel(Level.WARN);
		try {
			SparkTesting.populateCommandLineOptions(args);
			SparkTesting.performPI();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void populateCommandLineOptions(String[] args) throws org.apache.commons.cli.ParseException {
		CommandLineParser parser = new GnuParser();

		// create CLI options
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		

		options.addOption("s", "samples", true, "Number of samples for test_pi; default = 1000000");

		options.addOption("m", "set_master", false, "uset this parameter if you want to set local master");

		options.addOption("p", "print", false, "Print i");
		// parse the command line arguments
		CommandLine line = parser.parse(options, args);

		if (line.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Data Acquisition Standalone software", options);
			System.exit(0);
		}




		if (line.hasOption("samples")) {
			SparkTesting.num_samples = Integer.parseInt(line.getOptionValue("samples"));
		}

		
		if (line.hasOption("set_master")) {
			SparkTesting.set_master = true;
		}else {

			SparkTesting.set_master = false;
		}
		
		if (line.hasOption("print")) {
			SparkTesting.print = true;
		}else {

			SparkTesting.print = false;
		}
	}
	
	
	private static void performPI() {
		LocalDateTime now = LocalDateTime.now();
		int samples = SparkTesting.num_samples;
		String path = "test_parallel_pi_"+now.getYear()+"_"+now.getMonthValue()+"_"+now.getDayOfMonth()+".csv";
		String datetime = now.toString();
		String[] header = new String[] {"num", "alg", "time", "datetime"};
		File csvFile = new File(path);
		try (CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath(), header)) {
			int cores = Runtime.getRuntime().availableProcessors();
			System.out.println("cores: "+cores); // 8
			
			List<Integer> l = new ArrayList<>();
			for (int i = 0; i < samples; i++) {
				l.add(i);
			}


			SparkConf conf = new SparkConf().setAppName("Perform PI");//.setMaster("spark://59bd3b8cdd1d:7077");//.setMaster("local*");
			if(SparkTesting.set_master) {
				conf = new SparkConf().setAppName("Perform PI").setMaster("local[7]");
			}
			try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
				sparkContext.setLogLevel("ERROR");
				System.out.println("\n******************* SPARK ******************\n");
				long current = System.currentTimeMillis();

				

				SparkTesting.logger.warn("Spark logger");
//				System.out.println(x);
				sparkContext.parallelize(l).map(i -> {
//					long c = System.currentTimeMillis();
//					if(SparkTesting.print) {
						SparkTesting.logger.warn(i+";");
//						System.out.print(i+"; ");
//					}
					double x = Math.random();
					double y = Math.random();
					double pow = x*x + y*y;

//					System.out.println(i + " -> end: " + System.currentTimeMillis());
					return pow < 1;
				}).count();
				csvHandler.printRow(samples+"", "SPARK", ""+ (System.currentTimeMillis() - current), datetime);

				
//				System.out.println("Pi is roughly " + 4.0 * count / samples);
				System.out.println("\n Spark Time (ms): " + (System.currentTimeMillis() - current));
			}


				System.out.println("\n******************* JAVA PARALLEL STREAM ******************\n");
				long current = System.currentTimeMillis();

				l.parallelStream().filter(i -> {
					if(SparkTesting.print) {
						System.out.print(i+"; ");
					}
					double x = Math.random();
					double y = Math.random();
					return x*x + y*y < 1;
				}).count();

				csvHandler.printRow(samples+"", "JAVA", ""+ (System.currentTimeMillis() - current), datetime);
//				System.out.println("Pi is roughly " + 4.0 * count2 / samples);
				System.out.println("\n Java Time (ms): " + (System.currentTimeMillis() - current) + "  \n\n");
			
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
