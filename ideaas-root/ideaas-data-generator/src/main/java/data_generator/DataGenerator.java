package data_generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecordMap;

public class DataGenerator {
	static public FeatureSpace generateFeatureSpace(int numFeature) {
		FeatureSpace featureSpace = new FeatureSpace();
		for (int i=1; i<=numFeature; i++) {
			featureSpace.addFeature(new Feature(i, i+" feature"));
		}
		return featureSpace;
		
	}
	
	static public Collection<MultidimensionalRecordMap> generateMultiDimRecordMap(double mean, double std, int numMeasures, FeatureSpace featureSpace) {

		Collection<MultidimensionalRecordMap> newRecords = new ArrayList<>();
		int separateNumber = numMeasures / 3;
		for( int i=0; i<numMeasures; i++){
			MultidimensionalRecordMap mr = new MultidimensionalRecordMap();
//			String multidimRec = "";
//			boolean first = true;

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
//				if (first) {
//					multidimRec += ""+randomValue;
//					first = false;
//				}else {
//					multidimRec += " |----| "+randomValue;
//				}
			}
//			System.out.println(multidimRec);

			newRecords.add(mr);
		}

		return newRecords;
	}
	
	static public Instances generateInstances(double mean, double std, int numMeasures, FeatureSpace featureSpace) {
		List<com.yahoo.labs.samoa.instances.Attribute> attributes = new ArrayList<Attribute>();

		for(int attr = 0; attr < featureSpace.getFeatures().size(); attr++)
			attributes.add( new Attribute("attr" + attr) );

		//		Instances instances = new Instances();

		Instances instances = new Instances("instances",attributes,0);
		
		int separateNumber = numMeasures / 3;
		for( int i=0; i<numMeasures; i++){
			MultidimensionalRecord mr = new MultidimensionalRecord();

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
			}
			Instance inst = new DenseInstance(1, mr.dataRecordsToDoubleArray());

			inst.setWeight(1);
			inst.setDataset(instances);
			instances.add(inst);
			
		}

		return instances;
	}
	
	static public List<Instance> generateInstancesList(double mean, double std, int numMeasures, FeatureSpace featureSpace) {
		List<Instance> instences = new ArrayList<>();
		List<com.yahoo.labs.samoa.instances.Attribute> attributes = new ArrayList<Attribute>();

		for(int attr = 0; attr < featureSpace.getFeatures().size(); attr++)
			attributes.add( new Attribute("attr" + attr) );

		//		Instances instances = new Instances();

		Instances instancesEmpty = new Instances("instances",attributes,0);
		
		int separateNumber = numMeasures / 3;
		for( int i=0; i<numMeasures; i++){
			MultidimensionalRecord mr = new MultidimensionalRecord();

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
			}
			Instance inst = new DenseInstance(1, mr.dataRecordsToDoubleArray());

			inst.setWeight(1);
			inst.setDataset(instancesEmpty);
			instences.add(inst);
			
		}

		return instences;
	}
	
	
	static public Collection<MultidimensionalRecord> generateTestMultidimData() {

		FeatureSpace featureSpace = DataGenerator.generateFeatureSpace(2);
		Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
		double[] measures = {1, 5, 10, 19, 20, -1, 4, 30, 12, -1.5};
		for(double value: measures){
			MultidimensionalRecord mr = new MultidimensionalRecord();
			for(Feature f: featureSpace.getFeatures()) {
			
				FeatureData featureData = new FeatureData(f, value);
				mr.addFeatureData(featureData);

			}
			newRecords.add(mr);
		}

		return newRecords;
	}
	/**
	 * TODO trovare un modo per inserire le hashmap in Instances/Instance
	 * @param inputData
	 * @param featureSpace
	 * @return
	 */
	static public Instances generateInstances(Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace){
		List<com.yahoo.labs.samoa.instances.Attribute> attributes = new ArrayList<Attribute>();

		for(int attr = 0; attr < featureSpace.getFeatures().size(); attr++)
			attributes.add( new Attribute("attr" + attr) );

		//		Instances instances = new Instances();

		Instances instances = new Instances("instances",attributes,0);
		// Nuovi dati in input
		

		for (MultidimensionalRecord record : inputData) {
			if(record.getDataRecords().size() == featureSpace.getFeatures().size()){
				Instance inst = new DenseInstance(1, record.dataRecordsToDoubleArray());

				inst.setWeight(1);
				inst.setDataset(instances);
				instances.add(inst);
			}else {
				System.out.println("<br> Dimensione dati new record diverse da quelle della dimensione delle feature: "+record.toString());
			}
		}

		return instances;
	}
	
	static public Instances generateInstances() {
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
