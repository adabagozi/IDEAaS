package it.unibs.bodai.ideaas.clustream;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import it.unibs.bodai.ideaas.clustream.implementation.CombinedParallelCluStreamAlgorithm;
import it.unibs.bodai.ideaas.clustream.implementation.CombinedParallelCluStreamAlgorithmBuffer;
import it.unibs.bodai.ideaas.clustream.implementation.MyCluStreamAlgorithm;
import it.unibs.bodai.ideaas.clustream.implementation.MyCluStreamAlgorithmNoDensity;
import it.unibs.bodai.ideaas.clustream.implementation.MyCluStreamAlgorithmNoRemove;
import it.unibs.bodai.ideaas.clustream.implementation.MyCluStreamAlgorithmNumData;
import it.unibs.bodai.ideaas.clustream.implementation.MyCluStreamAlgorithmRemoveDense;
import it.unibs.bodai.ideaas.clustream.implementation.MyParallelCluStreamAlgorithm;
import it.unibs.bodai.ideaas.clustream.implementation.MyParallelCluStreamAlgorithmBuffer;
import it.unibs.bodai.ideaas.clustream.implementation.MyParallelSparkCluStreamAlgorithmBuffer;
import utils.CluStreamAlgorithmImplementation;
import utils.StaticVariables;

public class CluStreamAlgorithmFactory {
private static final CluStreamAlgorithmFactory factory = new CluStreamAlgorithmFactory();
    
	private CluStreamAlgorithmFactory() {
		
    }
    
    public static final CluStreamAlgorithmFactory getCluStreamAlgorithmFactory(){
    		return factory;
    }
    
//    public ICluStreamAlgorithm getCluStreamAlgorithm() {
//    	//TODO magari e' piu' saggio scegliere un altro tipo di definizione del default
//    		return this.getCluStreamAlgorithm(0, "default");
//    }
//    
    public ICluStreamAlgorithm getCluStreamAlgorithm(int version) {
    		return this.getCluStreamAlgorithm(version, "default");
    }
    
    public ICluStreamAlgorithm getCluStreamAlgorithm(int version, String identificatorDistance) {
        switch (version) {
		case 0:
			System.out.println("Remove based on DENSITY and timewindow: "+StaticVariables.TIME_WINDOW);
			return new MyCluStreamAlgorithm(identificatorDistance);
		
		case 1:
			System.out.println("Remove based on AGE and timewindow: "+StaticVariables.TIME_WINDOW);
			return new MyCluStreamAlgorithmNoDensity(identificatorDistance);
			
		case 2:
			System.out.println("Remove based on NUM_DATA, DENSITY and timewindow: "+StaticVariables.TIME_WINDOW);
			return new MyCluStreamAlgorithmNumData(identificatorDistance);
			
		case 3:

			System.out.println("DON'T REMOVE");
			return new MyCluStreamAlgorithmNoRemove(identificatorDistance);
		
		case 4:

			System.out.println("Remove Dense");
			return new MyCluStreamAlgorithmRemoveDense(identificatorDistance);
			
		default:
			return new MyCluStreamAlgorithm(identificatorDistance);
			
		}
    }
    
    
//    public iclustreamalgorithm getparallelclustreamalgorithm(int version) {
//		return this.getclustreamalgorithm(version, "default");
//	}
	
	public ICluStreamAlgorithm getParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation cluStreamImpl) throws Exception {
	    switch (cluStreamImpl) {
		case NO_PARALLEL:
//			System.out.println("Remove based on DENSITY and timewindow: NO_PARALLEL");
			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.NO_PARALLEL);
		
		case JAVA_PARALLEL_SYNTHESES:
//			System.out.println("JAVA_PARALLEL_SYNTHESES");
			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_SYNTHESES);
			
		case JAVA_PARALLEL_FEATURES:
//			System.out.println("JAVA_PARALLEL_FEATURES");
			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_FEATURES);
			
		case JAVA_PARALLEL_BOTH:
	
//			System.out.println("JAVA_PARALLEL_BOTH");
			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.JAVA_PARALLEL_BOTH);
		case JAVA_PARALLEL_BUFFER:
			return new MyParallelCluStreamAlgorithmBuffer(CluStreamAlgorithmImplementation.NO_PARALLEL);
//		case SPARK_PARALLEL_SYNTHESES:
////			System.out.println("SPARK_PARALLEL_SYNTHESES");
//			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.SPARK_PARALLEL_SYNTHESES);
//			
//		case SPARK_PARALLEL_FEATURES:
////			System.out.println("SPARK_PARALLEL_SYNTHESES");
//			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.SPARK_PARALLEL_SYNTHESES);
//			
//		case SPARK_PARALLEL_BOTH:
////			System.out.println("SPARK_PARALLEL_BOTH");
//			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.SPARK_PARALLEL_BOTH);
		default:
			throw new Exception("No Parallel Clustream algorithm initialized");
			
		}
	}
	
	
	public ICluStreamAlgorithm getParallelCluStreamSpark(CluStreamAlgorithmImplementation cluStreamImpl, JavaSparkContext sparkContext) throws Exception {
	    switch (cluStreamImpl) {
		
		case SPARK_PARALLEL_SYNTHESES:
//			System.out.println("SPARK_PARALLEL_SYNTHESES");
			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.SPARK_PARALLEL_SYNTHESES, sparkContext);
			
		case SPARK_PARALLEL_FEATURES:
//			System.out.println("SPARK_PARALLEL_SYNTHESES");
			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.SPARK_PARALLEL_FEATURES, sparkContext);
			
		case SPARK_PARALLEL_BOTH:
//			System.out.println("SPARK_PARALLEL_BOTH");
			return new MyParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation.SPARK_PARALLEL_BOTH, sparkContext);
		default:
			throw new Exception("No Parallel Clustream algorithm initialized");
			
		}
	}
	
	public ICluStreamAlgorithm getParallelBufferCluStreamAlgorithm(CluStreamAlgorithmImplementation cluStreamImpl, CluStreamAlgorithmImplementation cluStreamDistImpl) throws Exception {
	    switch (cluStreamImpl) {
		case JAVA_PARALLEL_BUFFER:
			return new MyParallelCluStreamAlgorithmBuffer(cluStreamDistImpl);
			
		default:
			throw new Exception("No Parallel Clustream algorithm initialized");
			
		}
	}

	public ICluStreamAlgorithm getParallelSparkBufferCluStreamAlgorithm(CluStreamAlgorithmImplementation cluStreamImpl, CluStreamAlgorithmImplementation cluStreamDistImpl, SparkSession sparkSession) throws Exception {
	    switch (cluStreamImpl) {
		case SPARK_PARALLEL_BUFFER:
			return new MyParallelSparkCluStreamAlgorithmBuffer(cluStreamDistImpl, sparkSession);
			
		default:
			throw new Exception("No Parallel Clustream algorithm initialized");
			
		}
	}
	
	public ICluStreamAlgorithm getCombinedParallelCluStreamAlgorithm(CluStreamAlgorithmImplementation cluStreamImpl, CluStreamAlgorithmImplementation algNearest, CluStreamAlgorithmImplementation algOldMicro, CluStreamAlgorithmImplementation algMerge) throws Exception {
	    switch (cluStreamImpl) {
		case JAVA_PARALLEL_BUFFER:
			return new CombinedParallelCluStreamAlgorithmBuffer(algNearest, algOldMicro, algMerge);

		case JAVA_PARALLEL_SYNTHESES:
		case JAVA_PARALLEL_FEATURES:
		case JAVA_PARALLEL_BOTH:
		case NO_PARALLEL:
//			System.out.println("JAVA_PARALLEL_SYNTHESES; JAVA_PARALLEL_FEATURES; JAVA_PARALLEL_BOTH; NO_PARALLEL");
			return new CombinedParallelCluStreamAlgorithm(algNearest, algOldMicro, algMerge);
		default:
			throw new Exception("No Parallel Clustream algorithm initialized");
			
		}
	}
	
}
