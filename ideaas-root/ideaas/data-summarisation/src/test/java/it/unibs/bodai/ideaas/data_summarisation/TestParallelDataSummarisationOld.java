package it.unibs.bodai.ideaas.data_summarisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;


import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.timing.ParallelDataSummarisationTiming;

/**
 * @author Daniele Comincini
 *
 */
public class TestParallelDataSummarisationOld {
	
	private IDAO dao;
	
	@Before
	public void setup() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void computeNearestS() {
		double mean = 0; 
		double std = 2;
		
		String type = "no_parallelization_NO_HASHMAP";
		//Config per test reali
//		int numTests = 10;
//		int[] numDimensions = {2,4,8,10};
//		int[] numSyntheses = {500,1000,2000,3000};
		//Config per test di controllo corretto funzionamento
		int numTests = 10;
		int[] numDimensions = {20};
		int[] numSyntheses = {3000};
		
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
//						dao.insertParallelDataSummarisationTiming(new ParallelDataSummarisationTiming(numDimensions[i],numSyntheses[j],duration,type,mean,std));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}
			}
		}
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