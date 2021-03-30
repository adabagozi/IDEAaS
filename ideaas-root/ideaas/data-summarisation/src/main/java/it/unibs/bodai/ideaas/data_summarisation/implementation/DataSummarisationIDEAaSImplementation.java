package it.unibs.bodai.ideaas.data_summarisation.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import it.unibs.bodai.ideaas.clustream.CluStreamAlgorithmFactory;
import it.unibs.bodai.ideaas.clustream.ICluStreamAlgorithm;
import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecordMap;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.dao.model.Synthesi;
import it.unibs.bodai.ideaas.dao.model.SynthesiElement;
import it.unibs.bodai.ideaas.data_summarisation.IDataSummarisation;
import utils.StaticVariables;



public class DataSummarisationIDEAaSImplementation implements IDataSummarisation{
	private static final Logger LOG = Logger.getLogger(DataSummarisationIDEAaSImplementation.class.getName());

	private ICluStreamAlgorithm cluStreamAlgorithm;

	
	
	/*
	 * 
	 * */
	public DataSummarisationIDEAaSImplementation() {
	 
		super();
		
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getCluStreamAlgorithm(StaticVariables.SUM_DATA_VERSION);
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.data_summarisation.IDataSummarisation#runClustering(it.unibs.bodai.ideaas.dao.model.SummarisedData, java.util.Collection, it.unibs.bodai.ideaas.dao.model.FeatureSpace, java.util.Date, java.util.Date)
	 */
	public SummarisedData runClustering(SummarisedData previousSummarisedData, Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end) throws Exception{
		int maxKernels = inputData.size() == 1 ? 1 : (int) Math.sqrt(inputData.size()/2);
		if (previousSummarisedData != null) {
			int numTot = (int) Math.sqrt((inputData.size() + previousSummarisedData.getKernels().length)/2);
			maxKernels = numTot >= maxKernels ? numTot : maxKernels;
		}
//		System.out.println("data");
//		int k = maxKernels > 5 ? 5: (maxKernels-1);
		
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getCluStreamAlgorithm(StaticVariables.SUM_DATA_VERSION);
		this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		
		if(previousSummarisedData != null && previousSummarisedData.getKernels().length>0) {
			this.cluStreamAlgorithm.resetLearningImpl(previousSummarisedData.getKernels(), previousSummarisedData.getTimestampValue());
		}else {
			this.cluStreamAlgorithm.resetLearningImpl();
		}
		
		
		if(this.cluStreamAlgorithm.getKernels() != null){
//			System.out.println("KernelLenght: "+this.cluStreamAlgorithm.getKernels().length);
		}else{
			System.out.println("Kernels Null: ");
		}
		
		//TODO, magari possiamo lavorare a livello di extend evitando di generare le istanze.
		Instances instances = this.generateInstances(inputData, featureSpace);

		for (int i = 0; i< instances.size(); i++) {
			Instance inst = instances.get(i);
			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
		}

//		System.out.println("Timestamp: "+this.cluStreamAlgorithm.getTimestamp());
//		System.out.println("-----------------");
		
		SummarisedData newSummarisedData =  new SummarisedData();//summarisedData;
		newSummarisedData.setTimestampValue(this.cluStreamAlgorithm.getTimestamp());
		newSummarisedData.setStableSummarisedData(previousSummarisedData != null ? previousSummarisedData.getStableSummarisedDataNoCheckSetted() : null);
		newSummarisedData.setPreviousSummarisedData(previousSummarisedData);
		newSummarisedData.setFeatureSpace(featureSpace); 

		newSummarisedData.setSynthesis(this.getOutputKernelsFromCluStream(this.cluStreamAlgorithm.getKernels(), featureSpace, inputData.size()));
		newSummarisedData.setKernels(this.cluStreamAlgorithm.getKernels());
		newSummarisedData.setTimestampInitial(start);
		newSummarisedData.setTimestampFinal(end);
		newSummarisedData.setTimestamp(new Date());

		newSummarisedData.setNumberOfNewData(inputData.size());
		int prevNumData = previousSummarisedData != null ? previousSummarisedData.getNumberOfData() : 0;
		newSummarisedData.setNumberOfData(prevNumData + inputData.size());


		return newSummarisedData;
	}
	
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.data_summarisation.IDataSummarisation#runClusteringExcludeZeros(it.unibs.bodai.ideaas.dao.model.SummarisedData, java.util.Collection, it.unibs.bodai.ideaas.dao.model.FeatureSpace, java.util.Date, java.util.Date)
	 */
	public SummarisedData runClusteringExcludeZeros(boolean excludeZeros, SummarisedData previousSummarisedData, Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end) throws Exception{
		int maxKernels = inputData.size() == 1 ? 1 : (int) Math.sqrt(inputData.size()/2);
		if (previousSummarisedData != null) {
			int numTot = (int) Math.sqrt((inputData.size() + previousSummarisedData.getKernels().length)/2);
			maxKernels = numTot >= maxKernels ? numTot : maxKernels;
		}
//		System.out.println("data");
//		int k = maxKernels > 5 ? 5: (maxKernels-1);
		
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getCluStreamAlgorithm(StaticVariables.SUM_DATA_VERSION);
		this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		
		if(previousSummarisedData != null && previousSummarisedData.getKernels().length>0) {
			this.cluStreamAlgorithm.resetLearningImpl(previousSummarisedData.getKernels(), previousSummarisedData.getTimestampValue());
		}else {
			this.cluStreamAlgorithm.resetLearningImpl();
		}
		
		
		if(this.cluStreamAlgorithm.getKernels() != null){
//			System.out.println("KernelLenght: "+this.cluStreamAlgorithm.getKernels().length);
		}else{
			System.out.println("Kernels Null: ");
		}
		
		//TODO, magari possiamo lavorare a livello di extend evitando di generare le istanze.
		Instances instances = null;
		if(excludeZeros) {
			instances =	this.generateInstancesExcludeZeros(inputData, featureSpace);
		}else {

			instances = this.generateInstances(inputData, featureSpace);
		}

		for (int i = 0; i< instances.size(); i++) {
			Instance inst = instances.get(i);
			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
		}

//		System.out.println("Timestamp: "+this.cluStreamAlgorithm.getTimestamp());
//		System.out.println("-----------------");
		
		SummarisedData newSummarisedData =  new SummarisedData();//summarisedData;
		newSummarisedData.setTimestampValue(this.cluStreamAlgorithm.getTimestamp());
		newSummarisedData.setStableSummarisedData(previousSummarisedData != null ? previousSummarisedData.getStableSummarisedDataNoCheckSetted() : null);
		newSummarisedData.setPreviousSummarisedData(previousSummarisedData);
		newSummarisedData.setFeatureSpace(featureSpace); 

		newSummarisedData.setSynthesis(this.getOutputKernelsFromCluStream(this.cluStreamAlgorithm.getKernels(), featureSpace, inputData.size()));
		newSummarisedData.setKernels(this.cluStreamAlgorithm.getKernels());
		newSummarisedData.setTimestampInitial(start);
		newSummarisedData.setTimestampFinal(end);
		newSummarisedData.setTimestamp(new Date());

		newSummarisedData.setNumberOfNewData(inputData.size());
		int prevNumData = previousSummarisedData != null ? previousSummarisedData.getNumberOfData() : 0;
		newSummarisedData.setNumberOfData(prevNumData + inputData.size());


		return newSummarisedData;
	}

	
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.data_summarisation.IDataSummarisation#runClusteringTests(it.unibs.bodai.ideaas.dao.model.SummarisedData, java.util.Collection, it.unibs.bodai.ideaas.dao.model.FeatureSpace, java.util.Date, java.util.Date, java.lang.String, java.lang.String)
	 */
	public SummarisedData runClusteringTests(SummarisedData previousSummarisedData, Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end, String cluStreamIdentificator, String distanceIdentificator) throws Exception{
		int maxKernels = (int) Math.sqrt(inputData.size()/2);
		maxKernels = (inputData.size()+previousSummarisedData.getSynthesis().size()) > maxKernels ? maxKernels : (inputData.size()+previousSummarisedData.getSynthesis().size());

//		int k = maxKernels > 5 ? 5: (maxKernels-1);
		
		this.cluStreamAlgorithm = CluStreamAlgorithmFactory.getCluStreamAlgorithmFactory().getCluStreamAlgorithm(StaticVariables.SUM_DATA_VERSION, distanceIdentificator);
		this.cluStreamAlgorithm.setNumMicroKernels(maxKernels);
		
		if(previousSummarisedData.getKernels().length>0) {
			this.cluStreamAlgorithm.resetLearningImpl(previousSummarisedData.getKernels(), previousSummarisedData.getTimestampValue());
		}else {
			this.cluStreamAlgorithm.resetLearningImpl();
		}
		
		
		if(this.cluStreamAlgorithm.getKernels() != null){
//			System.out.println("KernelLenght: "+this.cluStreamAlgorithm.getKernels().length);
		}else{
			System.out.println("Kernels Null: ");
		}
		
		//TODO, magari possiamo lavorare a livello di extend evitando di generare le istanze.
		Instances instances = this.generateInstances(inputData, featureSpace);

		//TODO Test elaborazione blocco di punti in parallelo
		for (int i = 0; i< instances.size(); i++) {
			Instance inst = instances.get(i);
			this.cluStreamAlgorithm.trainOnInstanceImpl(inst);
		}

//		System.out.println("Timestamp: "+this.cluStreamAlgorithm.getTimestamp());
		System.out.println("-----------------");
		
		SummarisedData newSummarisedData =  new SummarisedData();//summarisedData;
		newSummarisedData.setTimestampValue(this.cluStreamAlgorithm.getTimestamp());
		newSummarisedData.setStableSummarisedData(previousSummarisedData != null ? previousSummarisedData.getStableSummarisedDataNoCheckSetted() : null);
		newSummarisedData.setPreviousSummarisedData(previousSummarisedData);
		newSummarisedData.setFeatureSpace(featureSpace); 

		newSummarisedData.setSynthesis(this.getOutputKernelsFromCluStream(this.cluStreamAlgorithm.getKernels(), featureSpace, inputData.size()));

		newSummarisedData.setKernels(this.cluStreamAlgorithm.getKernels());

		newSummarisedData.setTimestampInitial(start);
		newSummarisedData.setTimestampFinal(end);
		newSummarisedData.setTimestamp(new Date());

		newSummarisedData.setNumberOfNewData(inputData.size());
		int prevNumData = previousSummarisedData != null ? previousSummarisedData.getNumberOfData() : 0;
		newSummarisedData.setNumberOfData(prevNumData + inputData.size());
		return newSummarisedData;
	}

	
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.data_summarisation.IDataSummarisation#runClusteringMOAImpl(it.unibs.bodai.ideaas.dao.model.SummarisedData, java.util.Collection, it.unibs.bodai.ideaas.dao.model.FeatureSpace, java.util.Date, java.util.Date)
	 */
	@Override
	public SummarisedData runClusteringMOAImpl(SummarisedData previousSummarisedData,
			Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end)
					throws Exception {
		// TODO Auto-generated method stub
		return null;
	}




	/**
	 * TODO trovare un modo per inserire le hashmap in Instances/Instance
	 * @param inputData
	 * @param featureSpace
	 * @return
	 */
	private Instances generateInstances(Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace){
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


	/**
	 * TODO trovare un modo per inserire le hashmap in Instances/Instance
	 * @param inputData
	 * @param featureSpace
	 * @return
	 */
	private Instances generateInstancesExcludeZeros(Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace){
		List<com.yahoo.labs.samoa.instances.Attribute> attributes = new ArrayList<Attribute>();

		for(int attr = 0; attr < featureSpace.getFeatures().size(); attr++)
			attributes.add( new Attribute("attr" + attr) );

		//		Instances instances = new Instances();

		Instances instances = new Instances("instances",attributes,0);
		// Nuovi dati in input
		

		for (MultidimensionalRecord record : inputData) {
			if(record.getDataRecords().size() == featureSpace.getFeatures().size()){
				double[] rec = record.dataRecordsToDoubleArray();
				boolean allZeros = true;
				for(double val: rec) {
					if(val != 0) {
						allZeros = false;
					}
				}
				if(!allZeros) {
					Instance inst = new DenseInstance(1, rec);

					inst.setWeight(1);
					inst.setDataset(instances);
					instances.add(inst);
				}
			}else {
				System.out.println("<br> Dimensione dati new record diverse da quelle della dimensione delle feature: "+record.toString());
			}
		}

		return instances;
	}


	/**
	 * @param kernels
	 * @param featureSpace
	 * @param totNumberNewData
	 * @return
	 */
	private HashMap<Integer, Synthesi> getOutputKernelsFromCluStream(MyClustreamKernel[] kernels, FeatureSpace featureSpace, int totNumberNewData){
		HashMap<Integer, Synthesi> synthesis = new HashMap<>();
//		int totalDataInvolved = 0;
//		for (MyClustreamKernel kernel : kernels) {
//			totalDataInvolved += (int) (totalDataInvolved + kernel.getN());
//		}
		for (MyClustreamKernel kernel : kernels) {
			Synthesi synthesi = new Synthesi(kernel.getIdMicroCluster(), kernel.getParentId(), kernel.getParentHistoryId());
			synthesi.setNumberData((long) kernel.getN());
			synthesi.setRadius(kernel.getRadius());
//			synthesi.setTotalData(totalDataInvolved);
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
			
//			double radius = synthesi.getRadius();
//			
//			for(Entry<Integer, SynthesiElement> synthesiElement : synthesi.getSynthesiElements().entrySet()) {
//				synthesiElement.getValue().setRadius(radius);
//			}
			synthesis.put(synthesi.getId(), synthesi);
		}
		return synthesis;
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
