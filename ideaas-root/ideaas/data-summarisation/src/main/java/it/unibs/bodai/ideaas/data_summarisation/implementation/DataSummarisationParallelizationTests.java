package it.unibs.bodai.ideaas.data_summarisation.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.MinSynthesisDistance;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecordMap;
import it.unibs.bodai.ideaas.dao.timing.ParallelDataSummarisationTiming;

public class DataSummarisationParallelizationTests {

	private static final boolean PRINT_EACH_DIST = false;
	private static final boolean DO_DB_INSERT = false;
	
	//TODO valori per inserimento nel db dei test (devono corrispondere a qulli settanti nello standalone)
	private static final double MEAN = 0;
	private static final double STD = 2;
	

	/**
	 * @author Daniele Comincini
	 *
	 */
	public static void runSparkParallelSynthFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures) {
//		String type = "parallel_spark_syntheses";
//		System.out.println(type);
//
//		long start, end, duration;
//		double meanTime = 0;
//		MinSynthesisDistance result;
//
//		SparkConf conf = new SparkConf()
//				.setAppName(type)
//				//		    	.setMaster("local[*]")
//				.setMaster("spark://ip-172-31-22-214.us-west-2.compute.internal:7077")
//				//		    	.setMaster("spark://169.254.235.202:7077")
//				.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//				.set("spark.ui.showConsoleProgress", "false");
//
//		Class[] classes = new Class[] {MultidimensionalRecordMap.class,FeatureData.class, Feature.class};
//		conf.registerKryoClasses(classes);
//
//		JavaSparkContext jsc = new JavaSparkContext(conf);
//		//		jsc.addJar("C:\\Users\\Daniele\\git\\ideaas-root\\ideaas\\data-summarisation\\target\\data-summarisation-0.0.1-SNAPSHOT-jar-with-dependencies.jar");
//
//		//Creo RDD Spark
//		JavaRDD<MultidimensionalRecordMap> synthesesSpark = jsc.parallelize((List<MultidimensionalRecordMap>) syntheses);
//
//		//Rendo il nuovo punto broadcast
//		Broadcast<HashMap<Integer,FeatureData>> newPointFeaturesSpark = jsc.broadcast(newPointFeatures);
//
//		for(int k=0; k<numTests; k++) {
//			//Parte cronometro
//			start = System.currentTimeMillis();
//
//			//Calcolo la distanza del punt da ogni sintesi parallelizzando
//			result = synthesesSpark.map(synth->{
//				HashMap<Integer,FeatureData> synthesisFeatures = synth.getDataRecords();
//				double distance=0;
//				//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
//				for(Map.Entry<Integer, FeatureData> feature : synthesisFeatures.entrySet()) {
//					//Uso var broadcast
//					distance+=Math.pow(newPointFeaturesSpark.getValue().get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
//					//Variabile passata "normalmente"
//					//TODO rimuovere?
//					//							distance+=Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
//				}
//				return new MinSynthesisDistance(synth,Math.sqrt(distance));
//			}).min(new MinSynthesisDistance());
//
//			end = System.currentTimeMillis();
//			duration = end-start;
//
//			//Stampo risultato test (durata in millisec)
//			//TODO ha senso stampare l'hash dell'oggeto dato che oggetti uguali ma distinti lo hanno diverso?
//			if(PRINT_EACH_DIST)
//				System.out.println(result+" "+duration);
//			meanTime+=duration;
//			//Inserimento risultato nel db
//			if(DO_DB_INSERT) {
//				try {
//					dao.insertParallelDataSummarisationTiming(
//							new ParallelDataSummarisationTiming(newPointFeatures.size(),syntheses.size(),duration,type,MEAN,STD,idTest)
//							);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		meanTime/=numTests;
//		System.out.println("Dim="+newPointFeatures.size()+" Synth="+syntheses.size()+" tempo medio="+meanTime);
//		newPointFeaturesSpark.destroy();
//		jsc.close();
	}



	public static void runParallelSynthFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures) {
		String type = "parallel_syntheses";
		System.out.println(type);

		long start, end, duration;
		double meanTime = 0;

		Optional<MinSynthesisDistance> result;

		for(int k=0; k<numTests; k++) {
			//Parte cronometro
			start = System.currentTimeMillis();

			//Calcolo la distanza del punt da ogni sintesi parallelizzando
			result = syntheses.parallelStream().map(synth->{
				HashMap<Integer,FeatureData> synthesisFeatures = synth.getDataRecords();
				double distance=0;
				//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
				for(Map.Entry<Integer, FeatureData> feature : synthesisFeatures.entrySet()) {
					distance+=Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
				}
				return new MinSynthesisDistance(synth,Math.sqrt(distance));
			}).min(new MinSynthesisDistance());

			end = System.currentTimeMillis();
			duration = end-start;

			if(result.isPresent()) {
				//Stampo risultato test (durata in millisec)
				if(PRINT_EACH_DIST)
					System.out.println(result.get()+" "+duration);
				meanTime+=duration;
				//Inserimento risultato nel db
				if(DO_DB_INSERT) {
					try {
						dao.insertParallelDataSummarisationTiming(
								new ParallelDataSummarisationTiming(newPointFeatures.size(),syntheses.size(),duration,type,MEAN,STD,idTest)
								);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		//TODO attenzione se dei test non vengono svolti? vedi controllo if(result.isPresent())
		meanTime/=numTests;
		System.out.println("Dim="+newPointFeatures.size()+" Synth="+syntheses.size()+" tempo medio="+meanTime);
	}



	public static void runSparkParallelDimFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures) {
//		String type = "parallel_spark_dimensions";
//		System.out.println(type);
//
//		long start, end, duration;
//
//		MultidimensionalRecordMap nearestSynthesis;
//		double distance, minDistance, meanTime = 0;
//
//		SparkConf conf = new SparkConf()
//				.setAppName("SparkCluStream_parallelDim")
//				.setMaster("local[*]")
//				//		    	.setMaster("spark://169.254.235.202:7077")
//				.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//				.set("spark.ui.showConsoleProgress", "false");
//
//		Class[] classes = new Class[] {MultidimensionalRecordMap.class,FeatureData.class, Feature.class};
//		conf.registerKryoClasses(classes);
//
//		JavaSparkContext jsc = new JavaSparkContext(conf);
//		//		jsc.addJar("C:\\Users\\Daniele\\git\\ideaas-root\\ideaas\\data-summarisation\\target\\data-summarisation-0.0.1-SNAPSHOT-jar-with-dependencies.jar");
//
//		//Rendo il nuovo punto broadcast
//		Broadcast<HashMap<Integer,FeatureData>> newPointFeaturesSpark = jsc.broadcast(newPointFeatures);
//
//		for(int k=0; k<numTests; k++) {
//			//Ciclo su tutte le sintesi
//			Iterator<MultidimensionalRecordMap> itrSyntheses = syntheses.iterator();
//
//			//inizializzo
//			minDistance=-1;
//			nearestSynthesis=null;
//
//			//Parte cronometro
//			start = System.currentTimeMillis();
//
//			while(itrSyntheses.hasNext()) {
//				MultidimensionalRecordMap synthesis = itrSyntheses.next();
//				HashMap<Integer,FeatureData> synthesisFeatures = synthesis.getDataRecords();
//				//TODO ciclo temporaneo fino a quando non trovo modo diretto per passare da hashmap a RDD
//				List<Tuple2<Integer, FeatureData>> list = new ArrayList<Tuple2<Integer, FeatureData>>();      
//				for(Map.Entry<Integer, FeatureData> entry : synthesisFeatures.entrySet()){
//					list.add(new Tuple2<Integer, FeatureData>(entry.getKey(),entry.getValue()));
//				}
//				JavaRDD<Tuple2<Integer,FeatureData>> feturesSpark = jsc.parallelize(list);
//
//				distance = feturesSpark.map(feature->{
//					//Var classica
//					//					return Math.pow(newPointFeatures.get(feature._1).getValue() - feature._2.getValue(),2);
//					//Var broadcast
//					return Math.pow(newPointFeaturesSpark.getValue().get(feature._1).getValue() - feature._2.getValue(),2);
//				}).reduce((a,b)->a+b);
//
//				distance = Math.sqrt(distance);
//				//Controllo se ho trovato una sintesi più vicina
//				if(minDistance==-1) {
//					minDistance = distance;
//					nearestSynthesis = synthesis;
//				}	
//				else if(distance < minDistance) {
//					minDistance = distance;
//					nearestSynthesis = synthesis;
//				}
//			}
//
//			end = System.currentTimeMillis();
//			duration = end-start;
//
//			//Stampo risultato test (durata in millisec)
//			if(PRINT_EACH_DIST)
//				System.out.println(nearestSynthesis.hashCode()+" "+minDistance+" "+duration);
//			meanTime+=duration;
//			//Inserimento risultato nel db
//			if(DO_DB_INSERT) {
//				try {
//					dao.insertParallelDataSummarisationTiming(
//							new ParallelDataSummarisationTiming(newPointFeatures.size(),syntheses.size(),duration,type,MEAN,STD,idTest)
//							);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		meanTime/=numTests;
//		System.out.println("Dim="+newPointFeatures.size()+" Synth="+syntheses.size()+" tempo medio="+meanTime);
//		newPointFeaturesSpark.destroy();
//		jsc.close();
	}


	public static void runParallelDimFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures) {
		String type = "parallel_dimensions";
		System.out.println(type);

		long start, end, duration;
		double distance, meanTime = 0;

		MultidimensionalRecordMap nearestSynthesis;
		double minDistance;

		for(int k=0; k<numTests; k++) {
			//Ciclo su tutte le sintesi
			Iterator<MultidimensionalRecordMap> itrSyntheses = syntheses.iterator();

			//inizializzo
			minDistance=-1;
			nearestSynthesis=null;

			//Parte cronometro
			start = System.currentTimeMillis();

			while(itrSyntheses.hasNext()) {
				MultidimensionalRecordMap synthesis = itrSyntheses.next();

				distance = synthesis.getDataRecords().entrySet().parallelStream().mapToDouble(feature -> {
					return Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
				}).sum();

				distance = Math.sqrt(distance);
				//Controllo se ho trovato una sintesi più vicina
				if(minDistance==-1) {
					minDistance = distance;
					nearestSynthesis = synthesis;
				}	
				else if(distance < minDistance) {
					minDistance = distance;
					nearestSynthesis = synthesis;
				}
			}

			end = System.currentTimeMillis();
			duration = end-start;

			//Stampo risultato test (durata in millisec)
			if(PRINT_EACH_DIST)
				System.out.println(nearestSynthesis.hashCode()+" "+minDistance+" "+duration);
			meanTime+=duration;
			//Inserimento risultato nel db
			if(DO_DB_INSERT) {
				try {
					dao.insertParallelDataSummarisationTiming(
							new ParallelDataSummarisationTiming(newPointFeatures.size(),syntheses.size(),duration,type,MEAN,STD,idTest)
							);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		meanTime/=numTests;
		System.out.println("Dim="+newPointFeatures.size()+" Synth="+syntheses.size()+" tempo medio="+meanTime);
	}

	public static void runParallelSynthDimFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures) {
		String type = "parallel_syntheses_dimensions";
		System.out.println(type);

		long start, end, duration;

		double meanTime=0;

		for(int k=0; k<numTests; k++) {
			//Parte cronometro
			start = System.currentTimeMillis();

			//Calcolo la distanza del punt da ogni sintesi parallelizzando
			Optional<MinSynthesisDistance> result = syntheses.parallelStream().map(synth->{
				double distance = synth.getDataRecords().entrySet().parallelStream().mapToDouble(feature -> {
					return Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
				}).sum();
				return new MinSynthesisDistance(synth,Math.sqrt(distance));
			}).min(new MinSynthesisDistance());

			end = System.currentTimeMillis();
			duration = end-start;

			if(result.isPresent()) {
				//Stampo risultato test (durata in millisec)
				if(PRINT_EACH_DIST)
					System.out.println(result.get()+" "+duration);

				meanTime+=duration; 
				//Inserimento risultato nel db
				if(DO_DB_INSERT) {
					try {
						dao.insertParallelDataSummarisationTiming(
								new ParallelDataSummarisationTiming(newPointFeatures.size(),syntheses.size(),duration,type,MEAN,STD,idTest)
								);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		meanTime/=numTests;
		System.out.println("Dim="+newPointFeatures.size()+" Synth="+syntheses.size()+" tempo medio="+meanTime);

	}



	public static void runSerialFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecord> syntheses, Collection<FeatureData> newPointFeatures) {
		String type = "serial";
		System.out.println(type);

		long start, end, duration;
		double distance, meanTime=0;

		MultidimensionalRecord nearestSynthesis;
		double minDistance;

		for(int k=0; k<numTests; k++) {			
			//Ciclo su tutte le sintesi
			Iterator<MultidimensionalRecord> itrSyntheses = syntheses.iterator();

			//Parte cronometro
			start = System.currentTimeMillis();
			//inizializzo
			minDistance=-1;
			nearestSynthesis=null;

			while(itrSyntheses.hasNext()) {
				MultidimensionalRecord synthesis = itrSyntheses.next();
				Iterator<FeatureData> itrSynthesisFeatures = synthesis.getDataRecords().iterator();
				distance=0;
				//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
				while(itrSynthesisFeatures.hasNext()) {
					FeatureData fSynthesis = itrSynthesisFeatures.next();

					Iterator<FeatureData> itrNewPointDim = newPointFeatures.iterator();
					//Ciclo sulle dimensioni del nuovo punto
					while(itrNewPointDim.hasNext()) {
						FeatureData fNewPoint = (FeatureData)itrNewPointDim.next();
						if(fNewPoint.getFeature().getId()==fSynthesis.getFeature().getId()) {
							distance+=Math.pow(fNewPoint.getValue()-fSynthesis.getValue(),2);
							break;
						}
					}
				}
				distance = Math.sqrt(distance);

				//Controllo se ho trovato una sintesi più vicina
				if(minDistance==-1) {
					minDistance = distance;
					nearestSynthesis = synthesis;
				}	
				else if(distance < minDistance) {
					minDistance = distance;
					nearestSynthesis = synthesis;
				}
			}

			end = System.currentTimeMillis();
			duration = end-start;

			//Stampo risultato test (durata in millisec)
			if(PRINT_EACH_DIST)
				System.out.println(nearestSynthesis.hashCode()+" "+minDistance+" "+duration);

			meanTime+=duration; 
			//Inserimento risultato nel db
			if(DO_DB_INSERT) {
				try {
					dao.insertParallelDataSummarisationTiming(new ParallelDataSummarisationTiming(newPointFeatures.size(),syntheses.size(),duration,type,MEAN,STD,idTest));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		meanTime/=numTests;
		System.out.println("Dim="+newPointFeatures.size()+" Synth="+syntheses.size()+" tempo medio="+meanTime);
	}


	public static void runSerialHashMapFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures) {
		String type = "serial_hashmap";
		System.out.println(type);

		long start, end, duration;
		double distance, meanTime = 0;

		MultidimensionalRecordMap nearestSynthesis;
		double minDistance;

		for(int k=0; k<numTests; k++) {
			//Ciclo su tutte le sintesi
			Iterator<MultidimensionalRecordMap> itrSyntheses = syntheses.iterator();

			//Parte cronometro
			start = System.currentTimeMillis();
			//inizializzo
			minDistance=-1;
			nearestSynthesis=null;

			while(itrSyntheses.hasNext()) {
				MultidimensionalRecordMap synthesis = itrSyntheses.next();
				HashMap<Integer,FeatureData> synthesisFeatures = synthesis.getDataRecords();
				distance=0;
				//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
				for(Map.Entry<Integer, FeatureData> feature : synthesisFeatures.entrySet()) {
					distance+=Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
				}

				distance = Math.sqrt(distance);

				//Controllo se ho trovato una sintesi più vicina
				if(minDistance==-1) {
					minDistance = distance;
					nearestSynthesis = synthesis;
				}	
				else if(distance < minDistance) {
					minDistance = distance;
					nearestSynthesis = synthesis;
				}
			}

			end = System.currentTimeMillis();
			duration = end-start;

			//Stampo risultato test (durata in millisec)
			if(PRINT_EACH_DIST)
				System.out.println(nearestSynthesis.hashCode()+" "+minDistance+" "+duration);
			meanTime+=duration;
			//Inserimento risultato nel db
			if(DO_DB_INSERT) {
				try {
					dao.insertParallelDataSummarisationTiming(
							new ParallelDataSummarisationTiming(newPointFeatures.size(),syntheses.size(),duration,type,MEAN,STD,idTest)
							);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		meanTime/=numTests;
		System.out.println("Dim="+newPointFeatures.size()+" Synth="+syntheses.size()+" tempo medio="+meanTime);
	}

}
