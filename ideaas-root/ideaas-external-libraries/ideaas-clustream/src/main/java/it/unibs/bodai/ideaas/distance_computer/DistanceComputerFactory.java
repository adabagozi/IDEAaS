package it.unibs.bodai.ideaas.distance_computer;

import java.io.IOException;
import java.util.Optional;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;

import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputerParallelJava;
import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputerParallelJavaFeatures;
import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputerParallelJavaSyntheses;
import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputerParallelSpark;
import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputerParallelSparkFeatures;
import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputerParallelSparkSyntheses;
import it.unibs.bodai.ideaas.distance_computer.implementation.DistanceComputerSerial;
import utils.CluStreamAlgorithmImplementation;

public class DistanceComputerFactory {
    private static final DistanceComputerFactory factory = new DistanceComputerFactory();
    
	private DistanceComputerFactory() {
		
    }
    
    public static final DistanceComputerFactory getDAOFactory(){
    		return factory;
    }
    
    public IDistanceComputer getDistanceComputer() {
    		return this.getDistanceComputer("default");
    }
    
    public IDistanceComputer getDistanceComputer(String identificator) {
        switch (identificator) {
		case DistanceComputerTypes.DISTANCE_COMPUTER_PARALLEL_JAVA:
			return new DistanceComputerParallelJava();
			
//		case DistanceComputerTypes.DISTANCE_COMPUTER_PARALLEL_SPARK:
//			return new DistanceComputerParallelSpark();
		
		default:
			return new DistanceComputerSerial();
		}
    }
    
    public IDistanceComputer getDistanceComputer(CluStreamAlgorithmImplementation cluStreamImpl) throws Exception {
        switch (cluStreamImpl) {
        case NO_PARALLEL: 
        	return new DistanceComputerSerial();
		case JAVA_PARALLEL_SYNTHESES:
			return new DistanceComputerParallelJavaSyntheses();
			
		case JAVA_PARALLEL_FEATURES:
			return new DistanceComputerParallelJavaFeatures();
			
		case JAVA_PARALLEL_BOTH:
			return new DistanceComputerParallelJava();
			
		default:
			throw new Exception("CluStream distance implementation not valid");
		}
    }
    
    public IDistanceComputer getDistanceComputer(CluStreamAlgorithmImplementation cluStreamImpl, JavaSparkContext sparkContext) throws Exception {
        switch (cluStreamImpl) {
      		
		case SPARK_PARALLEL_SYNTHESES:
			return new DistanceComputerParallelSparkSyntheses(sparkContext);
			
		case SPARK_PARALLEL_FEATURES:
			return new DistanceComputerParallelSparkFeatures(sparkContext);
			
		case SPARK_PARALLEL_BOTH:
			return new DistanceComputerParallelSpark(sparkContext);
		
		default:
			throw new Exception("CluStream distance implementation not valid");
		}
    }
    
//    public IDistanceComputer getDistanceComputer(CluStreamAlgorithmImplementation cluStreamImpl, JavaSparkContext sparkContext) throws Exception {
//        switch (cluStreamImpl) {
//        
//			
//		case SPARK_PARALLEL_SYNTHESES:
//			return new DistanceComputerParallelSpark(sparkContext);
//			
//		case SPARK_PARALLEL_FEATURES:
//			return new DistanceComputerParallelSpark(sparkContext);
//			
//		case SPARK_PARALLEL_BOTH:
//			return new DistanceComputerParallelSpark(sparkContext);
//		
//		default:
//			throw new Exception("CluStream distance implementation not valid");
//		}
//    }
}
