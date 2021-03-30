package it.unibs.bodai.ideaas.data_summarisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Random;

import org.junit.Before;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecordMap;



/**
 * @author Daniele Comincini
 *
 */
public class TestParallelDataSummarisation {
	
	private IDAO dao;
	
	private double mean = 0; 
	private double std = 2;
	
	private int idTest = 5;
	//Config per test reali
//	private int numTests = 10;
//	private int[] numDimensions = {2,4,6,8,10,12,14,16,18,20};
//	private int[] numSyntheses = {1000,2000,3000,4000,5000};
	//Config per test di controllo corretto funzionamento
	int numTests = 5;
	int[] numDimensions = {2};
	int[] numSyntheses = {100};
	
	@Before
	public void setup() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Questo metodo calcola la sintesi più vicina in modo seriale
//	@Test
	public void computeNearestS_Serial() {
		
		String type = "serial";
		System.out.println(type);
		
		long start, end, duration;
		double distance;
		
		MultidimensionalRecord nearestSynthesis;
		double minDistance;

		for(int i=0; i<numDimensions.length; i++) {
			for(int j=0; j<numSyntheses.length; j++) {
				for(int k=0; k<numTests; k++) {
					FeatureSpace clusteringFeatureSpace = generateFeatureSpace(numDimensions[i]);
					Collection<MultidimensionalRecord> syntheses = generateMultiDimRecord(mean, std, numSyntheses[j], clusteringFeatureSpace);
					Collection<MultidimensionalRecord> newPointCollection = generateMultiDimRecord(mean, std, 1, clusteringFeatureSpace);
					Iterator<MultidimensionalRecord> itrNewPoint = newPointCollection.iterator();
					//Nuovo punto, rispetto a questo devo calcolare la sintesi più vicina
					MultidimensionalRecord newPoint = (MultidimensionalRecord) itrNewPoint.next();
					
					//Ciclo su tutte le sintesi
					Iterator<MultidimensionalRecord> itrSyntheses = syntheses.iterator();
					
					//Parte cronometro
					start = System.nanoTime();
					//inizializzo
					minDistance=-1;
					nearestSynthesis=null;
					
					while(itrSyntheses.hasNext()) {
						MultidimensionalRecord synthesis = itrSyntheses.next();
						Iterator<FeatureData> itrCentroidDim = synthesis.getDataRecords().iterator();
						distance=0;
						//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
						while(itrCentroidDim.hasNext()) {
							FeatureData fSynthesis = itrCentroidDim.next();
							
							Iterator<FeatureData> itrNewPointDim = newPoint.getDataRecords().iterator();
							//Ciclo sulle dimensioni del nuovo punto
							while(itrNewPointDim.hasNext()) {
								FeatureData fNewPoint = (FeatureData)itrNewPointDim.next();
								if(fNewPoint.getFeature().getId()==fSynthesis.getFeature().getId()) {
									//Stampe di verifica
//									System.out.println(fNewPoint.getFeature().getName()+" "+fNewPoint.getValue());
//									System.out.println(fSynthesis.getFeature().getName()+" "+fSynthesis.getValue());
//									System.out.println(fNewPoint.getValue()-fSynthesis.getValue());
//									System.out.println(Math.pow(fNewPoint.getValue()-fSynthesis.getValue(),2));
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
						
						//Stampe di verifica
//						System.out.println(synthesis.hashCode()+" "+distance);
//						System.out.println(nearestSynthesis.hashCode()+" "+minDistance);
//						System.out.println();
					}
					
					end = System.nanoTime();
					duration = end-start;
					
					//Stampo risultato test (durata in millisec)
					System.out.println(nearestSynthesis.hashCode()+" "+minDistance+" "+duration/1000000);
					
					//Inserimento risultato nel db
//					try {
//						dao.insertParallelDataSummarisationTiming(new ParallelDataSummarisationTiming(numDimensions[i],numSyntheses[j],duration,type,mean,std,idTest));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}
			}
		}
	}
	
	//Questo metodo calcola la sintesi più vicina in modo seriale utilizzando hashmap
//	@Test
	public void computeNearestS_Serial2() {
		
		String type = "serial_hashmap";
		System.out.println(type);
		
		long start, end, duration;
		double distance;
		 
		MultidimensionalRecordMap nearestSynthesis;
		double minDistance;

		for(int i=0; i<numDimensions.length; i++) {
			for(int j=0; j<numSyntheses.length; j++) {
				for(int k=0; k<numTests; k++) {
					FeatureSpace clusteringFeatureSpace = generateFeatureSpace(numDimensions[i]);
					//Creo dati per il test
					Collection<MultidimensionalRecordMap> syntheses = generateMultiDimRecordMap(mean, std, numSyntheses[j], clusteringFeatureSpace);
					Collection<MultidimensionalRecordMap> newPointCollection = generateMultiDimRecordMap(mean, std, 1, clusteringFeatureSpace);
					
					//Estraggo il nuovo punto, rispetto a questo devo calcolare la sintesi più vicina
					Iterator<MultidimensionalRecordMap> itrNewPoint = newPointCollection.iterator();
					MultidimensionalRecordMap newPoint = itrNewPoint.next();
					HashMap<Integer,FeatureData> newPointFeatures = newPoint.getDataRecords();
					
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
							//Stampe di verifica
//							System.out.println(newPointFeatures.get(feature.getKey()).getFeature().getName()+" "+newPointFeatures.get(feature.getKey()).getValue());
//							System.out.println(feature.getValue().getFeature().getName()+" "+feature.getValue().getValue());
//							System.out.println(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue());
//							System.out.println(Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2));
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
						
						//Stampe di verifica
//						System.out.println(synthesis.hashCode()+" "+distance);
//						System.out.println(nearestSynthesis.hashCode()+" "+minDistance);
//						System.out.println();
						
					}
					
					end = System.currentTimeMillis();
					duration = end-start;
					
					//Stampo risultato test (durata in millisec)
					System.out.println(nearestSynthesis.hashCode()+" "+minDistance+" "+duration);
					
					//Inserimento risultato nel db
//					try {
//						dao.insertParallelDataSummarisationTiming(
//								new ParallelDataSummarisationTiming(numDimensions[i],numSyntheses[j],duration,type,mean,std,idTest)
//								);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}
			}
		}
	}
	
	//Questo metodo calcola la sintesi più vicina in modo parallelo con stream java utilizzando hashmap
//	@Test
	public void computeNearestS_ParellelJava() {
		
		String type = "parallel_java";
		System.out.println(type);
		
		long start, end, duration;
		 
		MultidimensionalRecordMap nearestSynthesis;
		double minDistance;

		for(int i=0; i<numDimensions.length; i++) {
			for(int j=0; j<numSyntheses.length; j++) {
				for(int k=0; k<numTests; k++) {
					FeatureSpace clusteringFeatureSpace = generateFeatureSpace(numDimensions[i]);
					//Creo dati per il test
					Collection<MultidimensionalRecordMap> syntheses = generateMultiDimRecordMap(mean, std, numSyntheses[j], clusteringFeatureSpace);
					Collection<MultidimensionalRecordMap> newPointCollection = generateMultiDimRecordMap(mean, std, 1, clusteringFeatureSpace);
					
					//Estraggo il nuovo punto, rispetto a questo devo calcolare la sintesi più vicina
					Iterator<MultidimensionalRecordMap> itrNewPoint = newPointCollection.iterator();
					MultidimensionalRecordMap newPoint = itrNewPoint.next();
					HashMap<Integer,FeatureData> newPointFeatures = newPoint.getDataRecords();
					
					//Ciclo su tutte le sintesi
					Iterator<MultidimensionalRecordMap> itrSyntheses = syntheses.iterator();
					
					//Parte cronometro
					start = System.currentTimeMillis();
					//inizializzo
					minDistance=-1;
					nearestSynthesis=null;
					OptionalDouble minDist = syntheses.parallelStream().mapToDouble(s -> {
						
						double dist = s.getDataRecords().entrySet().parallelStream().mapToDouble(feature -> {
							return Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
						}).sum();
						//TODO creare altri due test, con combinazione di paralellizazione
//						for(Map.Entry<Integer, FeatureData> feature : synthesisFeatures.entrySet()) {
//							dist+=Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
//						}
						return Math.sqrt(dist);
					}).min();        
					if(minDist.isPresent())
						minDistance = minDist.getAsDouble();
//					while(itrSyntheses.hasNext()) {
//						MultidimensionalRecordMap synthesis = itrSyntheses.next();
//						HashMap<Integer,FeatureData> synthesisFeatures = synthesis.getDataRecords();
//						distance=0;
//						//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
////						for(Map.Entry<Integer, FeatureData> feature : synthesisFeatures.entrySet()) {
////							//Stampe di verifica
//////							System.out.println(newPointFeatures.get(feature.getKey()).getFeature().getName()+" "+newPointFeatures.get(feature.getKey()).getValue());
//////							System.out.println(feature.getValue().getFeature().getName()+" "+feature.getValue().getValue());
//////							System.out.println(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue());
//////							System.out.println(Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2));
////							distance+=Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
////						}
//						
//						
//						distance = Math.sqrt(distance);
//						
//						//Controllo se ho trovato una sintesi più vicina
//						if(minDistance==-1) {
//							minDistance = distance;
//							nearestSynthesis = synthesis;
//						}	
//						else if(distance < minDistance) {
//							minDistance = distance;
//							nearestSynthesis = synthesis;
//						}
//						
//						//Stampe di verifica
////						System.out.println(synthesis.hashCode()+" "+distance);
////						System.out.println(nearestSynthesis.hashCode()+" "+minDistance);
////						System.out.println();
//						
//					}
					
					end = System.currentTimeMillis();
					duration = end-start;
					
					//Stampo risultato test (durata in millisec)
					System.out.println(" "+minDistance+" "+duration);
					
					//Inserimento risultato nel db
//					try {
//						dao.insertParallelDataSummarisationTiming(
//								new ParallelDataSummarisationTiming(numDimensions[i],numSyntheses[j],duration,type,mean,std,idTest)
//								);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}
			}
		}
	}
	
	//Questo metodo calcola la sintesi più vicina in modo seriale
//	@Test
	public void computeNearestS_ParallelSynth() {
		
//		String type = "parallel_syntheses_broadcast";
//		System.out.println(type);
//		
//		long start, end, duration;
//		 
//		MultidimensionalRecordMap nearestSynthesis;
//		double minDistance;
//		
//		//Disabilito le stampe di log
//		Logger.getLogger("org").setLevel(Level.OFF);
//	    Logger.getLogger("akka").setLevel(Level.OFF);
//		
////	    SparkConf conf = new SparkConf()
////	    	.setAppName("SparkCluStream_parallelSynth")
////	    	.setMaster("local[*]")
////	    	.set("spark.driver.maxResultSize", "4g")
////            .set("spark.ui.showConsoleProgress", "false")
////            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
////            .set("spark.kryoserializer.buffer.max", "1g");
//	    
//		//Locale
//	    SparkConf conf = new SparkConf()
//		    	.setAppName("SparkCluStream_parallelSynth")
//		    	.setMaster("local[*]")
//	            .set("spark.ui.showConsoleProgress", "false");
//	    
//	    //Cluster
////	    SparkConf conf = new SparkConf()
////		    	.setAppName("SparkCluStream_parallelSynth")
////		    	.setMaster("spark://169.254.235.202:7077")
////	            .set("spark.ui.showConsoleProgress", "true");
//	    
//		for(int i=0; i<numDimensions.length; i++) {
//			for(int j=0; j<numSyntheses.length; j++) {
//				for(int k=0; k<numTests; k++) {
//					FeatureSpace clusteringFeatureSpace = generateFeatureSpace(numDimensions[i]);
//					//Creo dati per il test
//					Collection<MultidimensionalRecordMap> syntheses = generateMultiDimRecordMap(mean, std, numSyntheses[j], clusteringFeatureSpace);
//					Collection<MultidimensionalRecordMap> newPointCollection = generateMultiDimRecordMap(mean, std, 1, clusteringFeatureSpace);
//					
//					//Estraggo il nuovo punto, rispetto a questo devo calcolare la sintesi più vicina
//					Iterator<MultidimensionalRecordMap> itrNewPoint = newPointCollection.iterator();
//					MultidimensionalRecordMap newPoint = itrNewPoint.next();
//					HashMap<Integer,FeatureData> newPointFeatures = newPoint.getDataRecords();
//					
//					JavaSparkContext jsc = new JavaSparkContext(conf);
//					//TODO capire cocosa mettere nel jar
//					jsc.addJar("C:\\Users\\Daniele\\git\\ideaas-root\\ideaas\\data-summarisation\\target\\data-summarisation-0.0.1-SNAPSHOT-jar-with-dependencies.jar");
////					jsc.addJar("C:\\Users\\Daniele\\git\\ideaas-root\\ideaas\\data-summarisation\\target\\data-summarisation-0.0.1-SNAPSHOT.jar");
//					//Rendo il nuovo punto broadcast
//					Broadcast<HashMap<Integer,FeatureData>> newPointFeaturesSpark = jsc.broadcast(newPointFeatures);
//					
//					//Ciclo su tutte le sintesi
//					JavaRDD<MultidimensionalRecordMap> synthesesSpark = jsc.parallelize((List<MultidimensionalRecordMap>) syntheses);
//					System.out.println(synthesesSpark.getNumPartitions());
//					
//					//Parte cronometro
//					System.out.println("START");
//					start = System.nanoTime();
//					
//					//TODO l'utilizzo dell'oggetto tuple peggiora le performance?
//					//Calcolo la distanza del punt da ogni sintesi parallelizzando (coppia sintesi distanza)
////					Tuple2<MultidimensionalRecordMap,Double> result = synthesesSpark.map(v->{
////								HashMap<Integer,FeatureData> synthesisFeatures = v.getDataRecords();
////								double distanceTest=0;
////								//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
////								for(Map.Entry<Integer, FeatureData> feature : synthesisFeatures.entrySet()) {
////									//Uso var broadcast
//////									distanceTest+=Math.pow(newPointFeaturesSpark.getValue().get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
////									//Variabile passata "normalmente"
////									distanceTest+=Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
////								}
////								
////								return new Tuple2<MultidimensionalRecordMap,Double>(v,Math.sqrt(distanceTest));
////				    	    }).min(new DummyComparator());
////					
//////					System.out.println(result._2);
////					minDistance=result._2;
////					nearestSynthesis=result._1;
//					
//					
//					//Calcolo la distanza del punt da ogni sintesi parallelizzando (solo distanza)
//					minDistance = synthesesSpark.map(v->{
//								HashMap<Integer,FeatureData> synthesisFeatures = v.getDataRecords();
//								double distanceTest=0;
//								//Ciclo su tutte le dimensioni del centroide della sintesi per calcolare la distanza
//								for(Map.Entry<Integer, FeatureData> feature : synthesisFeatures.entrySet()) {
//									//Uso var broadcast
//									distanceTest+=Math.pow(newPointFeaturesSpark.getValue().get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
//									//Variabile passata "normalmente"
////									distanceTest+=Math.pow(newPointFeatures.get(feature.getKey()).getValue() - feature.getValue().getValue(),2);
//								}
//								
//								return Math.sqrt(distanceTest);
//				    	    }).min(Comparator.naturalOrder());
//					
//					System.out.println(minDistance);
//					
//					end = System.nanoTime();
//					System.out.println("STOP");
//					duration = end-start;
//					
//					//Stampo risultato test (durata in millisec)
////					System.out.println(nearestSynthesis.hashCode()+" "+minDistance+" "+duration/1000000);
//					System.out.println(minDistance+" "+duration/1000000);
//					
//					
//			  		jsc.close();
//				  		
//					//Inserimento risultato nel db
////					try {
////						dao.insertParallelDataSummarisationTiming(
////								new ParallelDataSummarisationTiming(numDimensions[i],numSyntheses[j],duration,type,mean,std,idTest)
////								);
////					} catch (Exception e) {
////						e.printStackTrace();
////					}
//				}
//			}
//		}
	}

	
	public FeatureSpace generateFeatureSpace(int numFeatures) {
		FeatureSpace fs = new FeatureSpace(0, "FS_"+numFeatures, new ArrayList<>());
		for(int i=0; i< numFeatures; i++) {
			Feature f1 = new Feature(i, "F_"+i);
			f1.setUpperBoundError(10000);
			f1.setLowerBoundError(-10000);
			fs.addFeature(f1);
		}
		return fs;
	}
	
	public Collection<MultidimensionalRecordMap> generateMultiDimRecordMap(double mean, double std, int numMeasures, FeatureSpace featureSpace) {
		
		Collection<MultidimensionalRecordMap> newRecords = new ArrayList<>();
		int separateNumber = numMeasures / 3;
		for( int i=0; i<numMeasures; i++){
			MultidimensionalRecordMap mr = new MultidimensionalRecordMap();
			String multidimRec = "";
			boolean first = true;

			for(Feature f: featureSpace.getFeatures()) {
				Random r = new Random();
				
				double randomValue = mean + std * r.nextGaussian();
				if(i > separateNumber*2) {
					 randomValue = (mean + 10) + std * r.nextGaussian();
				}else if (i > separateNumber && i <= separateNumber*2) {
					 randomValue = (mean - 10) + std * r.nextGaussian();
				}
				
				FeatureData featureData = new FeatureData(f, randomValue);
				mr.addFeatureData(featureData);
				if (first) {
					multidimRec += ""+randomValue;
					first = false;
				}else {
					multidimRec += " |----| "+randomValue;
				}
			}
//			System.out.println(multidimRec);
			
			newRecords.add(mr);
		}
		
		return newRecords;
	}
	
	public Collection<MultidimensionalRecord> generateMultiDimRecord(double mean, double std, int numMeasures, FeatureSpace featureSpace) {
		
		Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
		int separateNumber = numMeasures / 3;
		for( int i=0; i<numMeasures; i++){
			MultidimensionalRecord mr = new MultidimensionalRecord();
			String multidimRec = "";
			boolean first = true;

			for(Feature f: featureSpace.getFeatures()) {
				Random r = new Random();
				
				double randomValue = mean + std * r.nextGaussian();
				if(i > separateNumber*2) {
					 randomValue = (mean + 10) + std * r.nextGaussian();
				}else if (i > separateNumber && i <= separateNumber*2) {
					 randomValue = (mean - 10) + std * r.nextGaussian();
				}
				
				FeatureData featureData = new FeatureData(f, randomValue);
				mr.addFeatureData(featureData);
				if (first) {
					multidimRec += ""+randomValue;
					first = false;
				}else {
					multidimRec += " |----| "+randomValue;
				}
			}
//			System.out.println(multidimRec);
			
			newRecords.add(mr);
		}
		
		return newRecords;
	}
}