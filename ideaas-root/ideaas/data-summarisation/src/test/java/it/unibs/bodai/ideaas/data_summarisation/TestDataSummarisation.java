package it.unibs.bodai.ideaas.data_summarisation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.model.Context;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.distance_computer.DistanceComputerTypes;
import utils.DateUtils;

public class TestDataSummarisation {

	private IDataSummarisation dataSummarisation;

	private DateUtils dateUtils;


	@Before
	public void setup() {
		try {
			this.dataSummarisation = DataSummarisationFactory.getDataSummarisationFactory().getDataSummarisation();

			this.dateUtils = new DateUtils();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


//	@Test
	public void generateCluStreamSummarisedData() {
		String[] cluStreamIdentificators = {"default"};
		String[] cluStreamDistanceIdentificators = {DistanceComputerTypes.DISTANCE_COMPUTER_SERIAL};//, DistanceComputerTypes.DISTANCE_COMPUTER_PARALLEL_JAVA};
		
		JSONArray jsonResult = new JSONArray();
		Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
		dimensionInstances.add(new DimensionInstance("101143", new Dimension(1, "Machine 101143", new Dimension(3))));
		dimensionInstances.add(new DimensionInstance("101170", new Dimension(1, "Machine 101170", new Dimension(3))));
		dimensionInstances.add(new DimensionInstance("101185", new Dimension(1, "Machine 101185", new Dimension(3))));



		//		String resultHTML = "";
		//		
		String date = "2016-08-01T00:00:00.000Z";
		LocalDateTime datetime = this.dateUtils.getLocalDateTime(date);


		Feature f1 = new Feature(1, "X");
		f1.setUpperBoundError(10000);
		f1.setLowerBoundError(-10000);

		Feature f2 = new Feature(2, "Y");
		f2.setUpperBoundError(100000);
		f2.setLowerBoundError(-100000);

		FeatureSpace clusteringFeatureSpace = new FeatureSpace(0, "X-Y", new ArrayList<>());
		clusteringFeatureSpace.addFeature(f1);
		clusteringFeatureSpace.addFeature(f2);
		
		double mean = 0; double std = 2; int numMeasures = 20;

//		System.out.println("NewRecords step 1: ");
		Collection<MultidimensionalRecord> newRecords = generateMultiDimRecord(mean, std, numMeasures, clusteringFeatureSpace);
		
//		System.out.println("\nNewRecords step 2: ");
		Collection<MultidimensionalRecord> newRecords1 = generateMultiDimRecord(mean+0.1, std, numMeasures, clusteringFeatureSpace);
//		System.out.println("\nNewRecords step 3: ");
		Collection<MultidimensionalRecord> newRecords3 = generateMultiDimRecord(mean+0.1, std, numMeasures, clusteringFeatureSpace);
		
		for(String cluStreamIdentificator: cluStreamIdentificators) {
			for(String cluStreamDistanceIdentificator: cluStreamDistanceIdentificators) {
				System.out.println("\n**************************************************** "+cluStreamIdentificator+" -> "+cluStreamDistanceIdentificator+" ***************************************************************");
				

				SummarisedData summarisedDataPrev = new SummarisedData(
						"", 
						new Date(), //TODO modificare
						new Date(), 
						new Date(), 
						null, 
						null,
						dimensionInstances,
						new Context(),
						clusteringFeatureSpace
						);

				


				try {
//					System.out.println("\n**************************************************** IDEAAS CluStream 1 ***************************************************************");
					long timinig = System.currentTimeMillis();
					SummarisedData summarisedDataResult = this.dataSummarisation.runClusteringTests(summarisedDataPrev, newRecords, clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date(), cluStreamIdentificator, cluStreamDistanceIdentificator);
					summarisedDataResult.printSynthesis();
//					System.out.println("tempo esecuzione: " + (System.currentTimeMillis() - timinig));
//					summarisedDataResult.setInputData(newRecords);
//					jsonResult.put(summarisedDataResult.toJSON());
					

//					System.out.println("\n**************************************************** IDEAAS CluStream 2 ***************************************************************");

					timinig = System.currentTimeMillis();
					SummarisedData summarisedDataResult1 = this.dataSummarisation.runClusteringTests(summarisedDataResult, newRecords1, clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date(), cluStreamIdentificator, cluStreamDistanceIdentificator);
					summarisedDataResult1.printSynthesis();
//					System.out.println("tempo esecuzione: " + (System.currentTimeMillis() - timinig));
//					summarisedDataResult1.setInputData(newRecords1);
//
//					jsonResult.put(summarisedDataResult1.toJSON());

					timinig = System.currentTimeMillis();
					SummarisedData summarisedDataResult2 = this.dataSummarisation.runClusteringTests(summarisedDataResult1, newRecords3, clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date(), cluStreamIdentificator, cluStreamDistanceIdentificator);
					summarisedDataResult2.printSynthesis();
					//					System.out.println("tempo esecuzione: " + (System.currentTimeMillis() - timinig));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	public Collection<MultidimensionalRecord> generateMultiDimRecord(double mean, double std, int numMeasures, FeatureSpace featureSpace){
		//		System.out.println("----------------------------------------------- START generateMultiDimRecord ----------------------------------------------\n");

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
					multidimRec += String.format("(%.2f", randomValue);
					first = false;
				}else {

					multidimRec += "; "+String.format("%.2f", randomValue);
				}
			}
//			System.out.println(multidimRec+")");

			newRecords.add(mr);
		}

		return newRecords;
	}
}
