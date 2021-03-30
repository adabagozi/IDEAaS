package it.unibs.bodai.ideaas.psb.implementation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Context;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.Parameter;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.dao.model.Synthesi;
import it.unibs.bodai.ideaas.data_summarisation.DataSummarisationFactory;
import it.unibs.bodai.ideaas.data_summarisation.IDataSummarisation;
import it.unibs.bodai.ideaas.psb.IPSBBusinessLogic;
import utils.DateUtils;
import utils.StaticVariables;

/**
 * @author Ada
 *
 */
public class PSBBusinessLogicImpl implements IPSBBusinessLogic {
	private static final Logger LOG = Logger.getLogger(PSBBusinessLogicImpl.class.getName());

	private static final int MAX_KERNELS = 1000;

	private DateUtils dateUtils = new DateUtils();
	private String summarisedDataCollectionName = "summarised_data";
	private String summarisedDataCollectionNameCioce = "summarised_data_cioce";

	private IDAO dao;

	private IDataSummarisation dataSummarisation;

	/**
	 * @param iDAO
	 */
	protected PSBBusinessLogicImpl(IDAO iDAO) {
		this.dao = iDAO;
	}

	public PSBBusinessLogicImpl() throws IOException {
		// this.dao = iDAO;
		this(DAOFactory.getDAOFactory().getDAO());
		this.dataSummarisation = DataSummarisationFactory.getDataSummarisationFactory().getDataSummarisation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#StableDataSummarisation(java.time
	 * .LocalDateTime, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, boolean)
	 */
	@Override
	public String stableDataSummarisation(LocalDateTime datetime, int featureSpaceID, String macchinaID,
			String componenteID, String utensileID, String mode, String partProgram, String intervalType,
			int intervalValue, boolean newSummarisation, boolean isStable, int experimentNumber) throws Exception {
		long timeTotal = System.currentTimeMillis();
		Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
		String resultHTML = "";

		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(featureSpaceID);

		if (fs.isPresent()) {
			dimensionInstances = this.dao.readDimensionInstances(macchinaID, componenteID);
			FeatureSpace clusteringFeatureSpace = fs.get();
			long timeExtraction = System.currentTimeMillis();

			Collection<MultidimensionalRecord> newRecords = this.getNewData(clusteringFeatureSpace, macchinaID,
					componenteID, utensileID, mode, partProgram, datetime, intervalType, intervalValue);
			resultHTML += "Number New Data: " + newRecords.size();
			timeExtraction = System.currentTimeMillis() - timeExtraction;

			String summarisatonDataCollection = newSummarisation ? this.summarisedDataCollectionName
					: this.summarisedDataCollectionNameCioce;

			long timeClustering = System.currentTimeMillis();
			LOG.severe(
					"**************************************************** IDEAAS ***************************************************************");
			SummarisedData summarisedDataResult = this.dataSummarisation.runClustering(null, newRecords,
					clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date());

			timeClustering = System.currentTimeMillis() - timeClustering;

			// TODO setParams
			summarisedDataResult.setStable(isStable);
			summarisedDataResult.setIntervalType(intervalType);
			summarisedDataResult.setIntervalValue(intervalValue);
			summarisedDataResult.setExperimentNumber(experimentNumber);
			summarisedDataResult.setDimensionIstances(dimensionInstances);
			summarisedDataResult.setCollectionName(summarisatonDataCollection);
			summarisedDataResult.setDimensionIstances(dimensionInstances);
			Optional<Context> readedContext = this.dao.readContext(utensileID, mode, partProgram);
			if (readedContext.isPresent()) {
				summarisedDataResult.setContext(readedContext.get());
			} else {
				summarisedDataResult.setContext(new Context());
			}

			timeTotal = System.currentTimeMillis() - timeTotal;
			summarisedDataResult.setExperimentNumber(experimentNumber);
//			summarisedDataResult.setTimeClustering(timeClustering);
//			summarisedDataResult.setTimeExtraction(timeExtraction);
//			summarisedDataResult.setTimeTotal(timeTotal);

			Document docSummarisedDataResult = summarisedDataResult.toDocument();
			this.dao.insertDocument(docSummarisedDataResult, summarisatonDataCollection);
			resultHTML += "<br><br>SummarisedData NumSynthesi: " + summarisedDataResult.getSynthesis().size();

		}
		return resultHTML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#StableDataSummarisation(java.time
	 * .LocalDateTime, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, boolean)
	 */
	@Override
	public String stableDataSummarisationTest(LocalDateTime datetime, int featureSpaceID, String macchinaID,
			String componenteID, String utensileID, String mode, String partProgram, String intervalType,
			int intervalValue, boolean newSummarisation, boolean isStable, int experimentNumber, int condition,
			Collection<MultidimensionalRecord> newRecords) throws Exception {
		long timeTotal = System.currentTimeMillis();
		Collection<DimensionInstance> dimensionInstances = new ArrayList<>();

		String resultHTML = "";

		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(featureSpaceID);

		if (fs.isPresent()) {
			dimensionInstances = this.dao.readDimensionInstances(macchinaID, componenteID);

			/****************/
			Feature f1 = new Feature(1, "X");
			f1.setUpperBoundError(10000);
			f1.setLowerBoundError(-10000);

			Feature f2 = new Feature(2, "Y");
			f2.setUpperBoundError(100000);
			f2.setLowerBoundError(-100000);

			FeatureSpace clusteringFeatureSpace = new FeatureSpace(1, "X-Y", new ArrayList<>());
			clusteringFeatureSpace.addFeature(f1);
			clusteringFeatureSpace.addFeature(f2);
			/****************/
			long timeExtraction = System.currentTimeMillis();

			timeExtraction = System.currentTimeMillis() - timeExtraction;

			String summarisatonDataCollection = newSummarisation ? this.summarisedDataCollectionName
					: this.summarisedDataCollectionNameCioce;

			long timeClustering = System.currentTimeMillis();

			LOG.severe(
					"**************************************************** IDEAAS ***************************************************************");
			SummarisedData summarisedDataResult = this.dataSummarisation.runClustering(null, newRecords,
					clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date());

			timeClustering = System.currentTimeMillis() - timeClustering;

			// TODO setParams
			summarisedDataResult.setStable(isStable);
			summarisedDataResult.setIntervalType(intervalType);
			summarisedDataResult.setIntervalValue(intervalValue);
			summarisedDataResult.setExperimentNumber(experimentNumber);
			summarisedDataResult.setDimensionIstances(dimensionInstances);
			summarisedDataResult.setCollectionName(summarisatonDataCollection);
			summarisedDataResult.setDimensionIstances(dimensionInstances);

			Optional<Context> readedContext = this.dao.readContext(utensileID, mode, partProgram);
			if (readedContext.isPresent()) {
				summarisedDataResult.setContext(readedContext.get());
			} else {
				summarisedDataResult.setContext(new Context());
			}
			timeTotal = System.currentTimeMillis() - timeTotal;
			summarisedDataResult.setExperimentNumber(experimentNumber);
//			summarisedDataResult.setTimeClustering(timeClustering);
//			summarisedDataResult.setTimeExtraction(timeExtraction);
//			summarisedDataResult.setTimeTotal(timeTotal);

			Document docSummarisedDataResult = summarisedDataResult.toDocument();
			this.dao.insertDocument(docSummarisedDataResult, summarisatonDataCollection);
			resultHTML += "<br><br>SummarisedData NumSynthesi: " + summarisedDataResult.getSynthesis().size();

		}
		return resultHTML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#dataSummarisation(java.time.
	 * LocalDateTime, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, boolean, boolean,
	 * int)
	 */
	@Override
	public String dataSummarisation(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID,
			String utensileID, String mode, String partProgram, String intervalType, int intervalValue,
			boolean newSummarisation, boolean isStable, int experimentNumber) throws Exception {

		long timeTotal = System.currentTimeMillis();
		Collection<DimensionInstance> dimensionInstances = new ArrayList<>();

		String resultHTML = "";

		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(featureSpaceID);
		if (fs.isPresent()) {
			dimensionInstances = this.dao.readDimensionInstances(macchinaID, componenteID);

			SummarisedData summarisedDataStable = null;
			SummarisedData summarisedDataPrev = null;

			FeatureSpace clusteringFeatureSpace = fs.get();

			String summarisatonDataCollection = newSummarisation ? this.summarisedDataCollectionName
					: this.summarisedDataCollectionNameCioce;

			long timeExtraction = System.currentTimeMillis();

			Optional<SummarisedData> getSummarisedDataStable = this.dao.readSummarisedData(summarisatonDataCollection,
					clusteringFeatureSpace, macchinaID, componenteID, utensileID, mode, partProgram, true, intervalType,
					intervalValue, experimentNumber);
			if (getSummarisedDataStable.isPresent()) {
				summarisedDataStable = getSummarisedDataStable.get();

				resultHTML += "<br>SummarisedData Stable ID: " + summarisedDataStable.getId() + "<br>";
			} else {
				resultHTML += "<br>no stable";
			}
			Optional<SummarisedData> getSummarisedDataPrev = this.dao.readSummarisedData(summarisatonDataCollection,
					clusteringFeatureSpace, macchinaID, componenteID, utensileID, mode, partProgram, false,
					intervalType, intervalValue, experimentNumber);

			HashMap<Integer, Synthesi> prevSynthesis = new HashMap<>();
			if (getSummarisedDataPrev.isPresent()) {
				summarisedDataPrev = getSummarisedDataPrev.get();
				summarisedDataPrev.setStableSummarisedData(summarisedDataStable);
				summarisedDataPrev.setDimensionIstances(dimensionInstances);
				prevSynthesis = summarisedDataPrev.getSynthesis();
				resultHTML += "<br>SummarisedData Prev ID: " + summarisedDataPrev.getId() + "<br> Num Sintese: "
						+ prevSynthesis.size() + "<br>";
			} else {
				resultHTML += "<br>no prev";
			}

			Collection<MultidimensionalRecord> newRecords = this.getNewData(clusteringFeatureSpace, macchinaID,
					componenteID, utensileID, mode, partProgram, datetime, intervalType, intervalValue);

			resultHTML += "Number New Data: " + newRecords.size();
			timeExtraction = System.currentTimeMillis() - timeExtraction;

			long timeClustering = System.currentTimeMillis();

			LOG.severe(
					"**************************************************** IDEAAS ***************************************************************");
			SummarisedData summarisedDataResult = this.dataSummarisation.runClustering(summarisedDataPrev, newRecords,
					clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date());

			timeClustering = System.currentTimeMillis() - timeClustering;
			// TODO setParams
			summarisedDataResult.setStable(false);
			summarisedDataResult.setIntervalType(intervalType);
			summarisedDataResult.setIntervalValue(intervalValue);
			summarisedDataResult.setDimensionIstances(dimensionInstances);
			summarisedDataResult.setCollectionName(summarisatonDataCollection);
			summarisedDataResult.setDimensionIstances(dimensionInstances);
			Optional<Context> readedContext = this.dao.readContext(utensileID, mode, partProgram);
			if (readedContext.isPresent()) {
				summarisedDataResult.setContext(readedContext.get());
			} else {
				summarisedDataResult.setContext(new Context());
			}

			timeTotal = System.currentTimeMillis() - timeTotal;
			summarisedDataResult.setExperimentNumber(experimentNumber);
//			summarisedDataResult.setTimeClustering(timeClustering);
//			summarisedDataResult.setTimeExtraction(timeExtraction);
//			summarisedDataResult.setTimeTotal(timeTotal);
			summarisedDataResult.setExperimentNumber(experimentNumber);

			Document docSummarisedDataResult = summarisedDataResult.toDocument();
			dao.insertDocument(docSummarisedDataResult, summarisatonDataCollection);
			resultHTML += "<br><br>SummarisedData NumSynthesi: " + summarisedDataResult.getSynthesis().size();
			// resultHTML += "<br>SummarisedData Ada:
			// "+docSummarisedDataAda.toJson().toString();

			return resultHTML;
		}
		return "Fine";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#getSummarisedData(java.time.
	 * LocalDateTime, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, boolean, boolean,
	 * int)
	 */
	@Override
	public JSONArray getSummarisedData(LocalDateTime datetime, int featureSpaceID, String macchinaID,
			String componenteID, String utensileID, String mode, String partProgram, String intervalType,
			int intervalValue, boolean newSummarisation, int experimentNumber) {

		JSONArray dataJSON = new JSONArray();
		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(featureSpaceID);
		if (fs.isPresent()) {
			FeatureSpace featureSpace = fs.get();
			String summarisatonDataCollection = newSummarisation ? this.summarisedDataCollectionName
					: this.summarisedDataCollectionNameCioce;

			Optional<Iterable<Document>> documents = dao.readSummarisedDatas(summarisatonDataCollection, featureSpace,
					macchinaID, componenteID, utensileID, mode, partProgram, intervalType, intervalValue,
					experimentNumber);
			if (documents.isPresent()) {
				for (Document doc : documents.get()) {
					SummarisedData summarisedData = new SummarisedData();
					summarisedData.setFromDocument(doc);
					dataJSON.put(summarisedData.toJSON());
				}
			} else {
				LOG.severe("======> getSummarisedData: No results");
			}
		} else {
			LOG.severe("======> getSummarisedData: feature space no present");
		}
		return dataJSON;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#getSummarisedData(java.time.
	 * LocalDateTime, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, boolean, boolean,
	 * int)
	 */
	@Override
	public JSONObject getSummarisedData(FeatureSpace feature_space, String dataset, String macchinaID,
			String componenteID, String utensileID, String mode, String partProgram, LocalDateTime datetime, LocalDateTime datetimeTwo, boolean increase, int experimentNumber) {
		String stableID = "";

		JSONObject jsonObject = new JSONObject();
		JSONArray dataJSON = new JSONArray();
		jsonObject.put("dataset", dataset);
		String summarisatonDataCollection = this.summarisedDataCollectionName;

		Optional<Iterable<Document>> documents = dao.readSummarisedDatas(summarisatonDataCollection, feature_space, macchinaID, componenteID, utensileID, mode, partProgram, datetime, datetimeTwo, increase, experimentNumber);
		if (documents.isPresent()) {
			for (Document doc : documents.get()) {

				//					System.out.println("JSON: "+doc); 
				if(stableID.equals("")) {
					stableID = doc.get("stable_summarised_data_id").toString();
				}
				//					SummarisedData summarisedData = new SummarisedData();
				//					summarisedData.setFromDocument(doc);
				dataJSON.put(doc);//.toJson());
			}
		} else {
			System.out.println("======> getSummarisedData: No results");
		}
		jsonObject.put("data", dataJSON);
		jsonObject.put("stable_id", stableID);
		return jsonObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#readAllDocument(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String readAllDocument(String collectionName, String dateStart) {
		String messageHTML = "";

		LocalDateTime dateTimeStable = dateUtils.getLocalDateTime(dateStart);
		// BasicDBObject whereQueryStab = this.buildQuery("-1","-1","-1","-1","-1","-1",
		// dateTimeStable, "a", 1);

		// TODO cambiare

		int countData = 0;
		Optional<Iterable<Document>> documents = this.dao.readMeasuresWithQuery(collectionName, "-1", "-1", "-1", "-1",
				"-1", "-1", dateTimeStable, "a", 1);
		if (documents.isPresent()) {
			// Iterator<Document> docs = documents.get();
			for (Document doc : documents.get()) {
				Collection<Document> features = (Collection<Document>) doc.get("features");
				countData++;
			}
		}
		messageHTML += countData;
		return messageHTML;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#getNumDocumentsMachineForData(
	 * java.lang.String, java.time.LocalDateTime)
	 */
	public int getNumDocumentsMachineForData(String machineID, LocalDateTime date) {
		String messageHTML = "";

		// LocalDateTime dateTimeStable = dateUtils.getLocalDateTime(dateStart);
		// BasicDBObject whereQueryStab = this.buildQuery("-1","-1","-1","-1","-1","-1",
		// dateTimeStable, "a", 1);

		// int countData = 0;
		String collectionName = String.format("%04d_%02d", date.getYear(), date.getMonthValue());

		// Optional<Iterable<Document>> documents =
		// dao.readMeasuresWithQuery(collectionName, "-1", "-1", "-1", "-1", machineID,
		// "-1", date, "a", 1);
		//
		//// Optional<Iterable<Document>> documents =
		// this.dao.readMeasuresWithQuery(collectionName, "-1", "-1", "-1", "-1", "-1",
		// "-1", date, "a", 1);
		// if (documents.isPresent()){
		// //Iterator<Document> docs = documents.get();
		//
		//// int countData = dao.numMeasuresWithQuery(collectionName, "-1", "-1", "-1",
		// "-1", machineID, "-1", date, "a", 1);
		// for(Document doc: documents.get()){
		// countData++;
		// }
		// }

		int countData = dao.numMeasuresWithQuery(collectionName, "-1", "-1", "-1", "-1", machineID, "-1", date, "a", 1);
		System.out.println("NUM: " + countData);
		return countData;

		// return dao.numMeasuresWithQuery(collectionName, "-1", "-1", "-1", "-1",
		// machineID, "-1", date, "a", 1);
	}

	private Collection<MultidimensionalRecord> getNewData(FeatureSpace featureSpace, String idMachine, String idSpindle,
			String idTool, String mode, String idPartProgram, LocalDateTime dateTime, String intervalType,
			int intervalValue) {
		Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
		String collectionName = String.format("%04d_%02d", dateTime.getYear(), dateTime.getMonthValue());

		Optional<Iterable<Document>> documents = dao.readMeasuresWithQuery(collectionName, "-1", idTool, idPartProgram,
				mode, idMachine, idSpindle, dateTime, intervalType, intervalValue);

		if (documents.isPresent()) {
			for (Document doc : documents.get()) {
				MultidimensionalRecord multiDimRecord = new MultidimensionalRecord();
				multiDimRecord.setFeaturesDataFromDocument(doc, featureSpace);
				newRecords.add(multiDimRecord);
				// LOG.info(multiDimRecord.toJSON().toString());
			}
		}
		return newRecords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#dataSummarisation(java.time.
	 * LocalDateTime, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, boolean, boolean,
	 * int)
	 */
	@Override
	public String dataSummarisationTest(LocalDateTime datetime, int featureSpaceID, String macchinaID,
			String componenteID, String utensileID, String mode, String partProgram, String intervalType,
			int intervalValue, boolean newSummarisation, boolean isStable, int experimentNumber, int condition,
			Collection<MultidimensionalRecord> newRecords) throws Exception {

		long timeTotal = System.currentTimeMillis();
		Collection<DimensionInstance> dimensionInstances = new ArrayList<>();

		String resultHTML = "";

		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(featureSpaceID);
		if (fs.isPresent()) {
			dimensionInstances = this.dao.readDimensionInstances(macchinaID, componenteID);
			SummarisedData summarisedDataStable = null;
			SummarisedData summarisedDataPrev = null;

			// FeatureSpace clusteringFeatureSpace = fs.get();
			/****************/
			Feature f1 = new Feature(1, "X");
			f1.setUpperBoundError(10000);
			f1.setLowerBoundError(-10000);

			Feature f2 = new Feature(2, "Y");
			f2.setUpperBoundError(100000);
			f2.setLowerBoundError(-100000);

			FeatureSpace clusteringFeatureSpace = new FeatureSpace(1, "X-Y", new ArrayList<>());
			clusteringFeatureSpace.addFeature(f1);
			clusteringFeatureSpace.addFeature(f2);
			/****************/

			String summarisatonDataCollection = newSummarisation ? this.summarisedDataCollectionName
					: this.summarisedDataCollectionNameCioce;

			long timeExtraction = System.currentTimeMillis();

			Optional<SummarisedData> getSummarisedDataStable = this.dao.readSummarisedData(summarisatonDataCollection,
					clusteringFeatureSpace, macchinaID, componenteID, utensileID, mode, partProgram, true, intervalType,
					intervalValue, experimentNumber);
			if (getSummarisedDataStable.isPresent()) {
				summarisedDataStable = getSummarisedDataStable.get();

				resultHTML += "<br>SummarisedData Stable ID: " + summarisedDataStable.getId() + "<br>";
			} else {
				resultHTML += "<br>no stable";
			}
			Optional<SummarisedData> getSummarisedDataPrev = this.dao.readSummarisedData(summarisatonDataCollection,
					clusteringFeatureSpace, macchinaID, componenteID, utensileID, mode, partProgram, false,
					intervalType, intervalValue, experimentNumber);

			HashMap<Integer, Synthesi> prevSynthesis = new HashMap<>();
			if (getSummarisedDataPrev.isPresent()) {
				summarisedDataPrev = getSummarisedDataPrev.get();
				summarisedDataPrev.setStableSummarisedData(summarisedDataStable);
				summarisedDataPrev.setDimensionIstances(dimensionInstances);
				prevSynthesis = summarisedDataPrev.getSynthesis();
				resultHTML += "<br>SummarisedData Prev ID: " + summarisedDataPrev.getId() + "<br> Num Sintese: "
						+ prevSynthesis.size() + "<br>";
			} else {
				resultHTML += "<br>no prev";
			}

			resultHTML += "Number New Data: " + newRecords.size();
			timeExtraction = System.currentTimeMillis() - timeExtraction;

			long timeClustering = System.currentTimeMillis();
			LOG.severe(
					"**************************************************** IDEAAS ***************************************************************");
			SummarisedData summarisedDataResult = this.dataSummarisation.runClustering(summarisedDataPrev, newRecords,
					clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date());

			timeClustering = System.currentTimeMillis() - timeClustering;
			// Optional<SummarisedData> result =
			// this.dataSummarisation.runClustering(summarisedData, newRecords,
			// Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date());
			// TODO setParams
			summarisedDataResult.setStable(false);
			summarisedDataResult.setIntervalType(intervalType);
			summarisedDataResult.setIntervalValue(intervalValue);
			summarisedDataResult.setDimensionIstances(dimensionInstances);
			summarisedDataResult.setCollectionName(summarisatonDataCollection);
			summarisedDataResult.setDimensionIstances(dimensionInstances);
			Optional<Context> readedContext = this.dao.readContext(utensileID, mode, partProgram);
			if (readedContext.isPresent()) {
				summarisedDataResult.setContext(readedContext.get());
			} else {
				summarisedDataResult.setContext(new Context());
			}

			timeTotal = System.currentTimeMillis() - timeTotal;
			summarisedDataResult.setExperimentNumber(experimentNumber);
//			summarisedDataResult.setTimeClustering(timeClustering);
//			summarisedDataResult.setTimeExtraction(timeExtraction);
//			summarisedDataResult.setTimeTotal(timeTotal);
			summarisedDataResult.setExperimentNumber(experimentNumber);

			Document docSummarisedDataResult = summarisedDataResult.toDocument();
			dao.insertDocument(docSummarisedDataResult, summarisatonDataCollection);
			resultHTML += "<br><br>SummarisedData NumSynthesi: " + summarisedDataResult.getSynthesis().size();
			// resultHTML += "<br>SummarisedData Ada:
			// "+docSummarisedDataAda.toJson().toString();

			return resultHTML;
		}
		return "Fine";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#getMeasures(java.time.
	 * LocalDateTime, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, boolean)
	 */
	@Override
	public JSONArray getMeasures(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID,
			String utensileID, String mode, String partProgram, String intervalType, int intervalValue)
					throws Exception {
		JSONArray jsonData = new JSONArray();
//		System.out.println("id FS: "+featureSpaceID);
		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(featureSpaceID);
		if (fs.isPresent()) {

			FeatureSpace clusteringFeatureSpace = fs.get();
			Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
			String collectionName = dateUtils.getCollectionName(datetime)+StaticVariables.collectionNameExt;//String.format("%04d_%02d", datetime.getYear(), datetime.getMonthValue());

			Optional<Iterable<Document>> documents = dao.readMeasuresWithQuery(collectionName, "-1", utensileID,
					partProgram, mode, macchinaID, componenteID, datetime, intervalType, intervalValue);

			if (documents.isPresent()) {

				JSONArray jsonElement = new JSONArray();
				for (Document doc : documents.get()) {
					MultidimensionalRecord multiDimRecord = new MultidimensionalRecord();
					multiDimRecord.setFeaturesDataFromDocument(doc, clusteringFeatureSpace);

//					for (FeatureData fd : multiDimRecord.getDataRecords()) {
//						if (fd.getFeature().getId() == 5) {
//							LOG.severe("Feature 5: " + fd.getFeature().getName() + " - Valore: " + fd.getValue());
//						}
//						if (fd.getFeature().getId() == 5 && fd.getValue() < -10000) {
//							LOG.severe("Feature " + fd.getFeature().getName() + " - Valore: " + fd.getValue());
//						}
//					}
					jsonElement.put(multiDimRecord.toJSON());
				}
				jsonData.put(jsonElement);
			}
		}

		return jsonData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#getDataPoints(int)
	 */
	@Override
	public JSONArray getDataPoints(int experimentNumber) throws Exception {
		JSONArray jsonData = new JSONArray();
		Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
		String collectionName = "random_data";
		/****************/
		Feature f1 = new Feature(1, "X");
		f1.setUpperBoundError(10000);
		f1.setLowerBoundError(-10000);

		Feature f2 = new Feature(2, "Y");
		f2.setUpperBoundError(100000);
		f2.setLowerBoundError(-100000);

		FeatureSpace clusteringFeatureSpace = new FeatureSpace(0, "X-Y", new ArrayList<>());
		clusteringFeatureSpace.addFeature(f1);
		clusteringFeatureSpace.addFeature(f2);
		/****************/

		Optional<Iterable<Document>> documents = this.dao.readDataPoints(collectionName, experimentNumber);
		// TODO
		if (documents.isPresent()) {
			for (Document doc : documents.get()) {
				// for (Document multidimDoc: (Collection<Document>) doc.get("datas")){
				// jsonData.put(multidimDoc);
				// }
				JSONArray jsonElement = new JSONArray();
				for (Document multiDimDocument : (Collection<Document>) doc.get("datas")) {
					MultidimensionalRecord multiDimRecord = new MultidimensionalRecord();
					multiDimRecord.setFromDocument(multiDimDocument);
					jsonElement.put(multiDimRecord.toJSON());
				}
				jsonData.put(jsonElement);
			}
		}
		return jsonData;
	}

	private Document newRecordsToDocument(int experiment_number, Collection<MultidimensionalRecord> multidimRecords) {
		JSONObject jsonInputDatas = new JSONObject();
		JSONArray jsonDatas = new JSONArray();

		for (MultidimensionalRecord mdr : multidimRecords) {
			jsonDatas.put(mdr.toJSON());
		}
		jsonInputDatas.put("datas", jsonDatas);

		Document doc = Document.parse(jsonInputDatas.toString());
		doc.append("experiment_number", experiment_number);

		return doc;
	}

	public Collection<MultidimensionalRecord> generateMultiDimRecord(Feature f1, Feature f2, int condizione,
			int experiment_number) {
		System.out.println("----------------------------------------------- generateMultiDimRecord " + experiment_number
				+ " ----------------------------------------------\n");

		Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
		int min = -10;
		int max = 10;
		int numData = 1000;

		Random r = new Random();
		int randomElements = (int) (5 * r.nextDouble());

		switch (randomElements) {
		case 0:
			min = -10;
			max = 10;
			break;
		case 1:
			min = -10;
			max = -5;
			break;
		case 2:
			min = -5;
			max = 0;
			break;
		case 3:
			min = 0;
			max = 5;
			break;
		default:
			min = 5;
			max = 10;
			break;
		}
		for (int i = 0; i < numData; i++) {

			double randomValueX = min + (max - min) * r.nextDouble();

			double randomValueY = (min + (max - min) * r.nextDouble());

			FeatureData fd1 = new FeatureData(f1, randomValueX);
			FeatureData fd2 = new FeatureData(f2, randomValueY);

			MultidimensionalRecord mr = new MultidimensionalRecord();
			mr.addFeatureData(fd1);
			mr.addFeatureData(fd2);
			newRecords.add(mr);
		}
		this.dao.insertDocument(this.newRecordsToDocument(experiment_number, newRecords), "random_data");
		return newRecords;
	}

	public Collection<MultidimensionalRecord> generateMultiDimRecordGaussian(Feature f1, Feature f2, int condizione,
			int experiment_number) {
		System.out.println("----------------------------------------------- generateMultiDimRecord " + experiment_number
				+ " ----------------------------------------------\n");

		Collection<MultidimensionalRecord> newRecords = new ArrayList<>();

		int numData = 1000;

		Random r = new Random();
		double mean = 0, std = 2.5;

		for (int i = 0; i < numData; i++) {
			double randomValueX = mean + std * r.nextGaussian();
			double randomValueY = mean + std * r.nextGaussian();
			FeatureData fd1 = new FeatureData(f1, randomValueX);
			FeatureData fd2 = new FeatureData(f2, randomValueY);

			MultidimensionalRecord mr = new MultidimensionalRecord();
			mr.addFeatureData(fd1);
			mr.addFeatureData(fd2);
			newRecords.add(mr);
		}
		this.dao.insertDocument(this.newRecordsToDocument(experiment_number, newRecords), "random_data");
		return newRecords;
	}

	/*************************************************************************************************************************************
	 * **************************************************
	 * ****************************************************************
	 * ************************************************** CluStream
	 * ****************************************************************
	 * **************************************************
	 * ****************************************************************
	 ***********************************************************************************************************************************/
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibs.bodai.ideaas.psb.IPSBBusinessLogic#generateCluStreamSummarisedData()
	 */
	@Override
	public JSONArray generateCluStreamSummarisedData() {
		JSONArray jsonResult = new JSONArray();
		Collection<DimensionInstance> dimensionInstances = new ArrayList<>();

		DimensionInstance machineDimInstance = new DimensionInstance("101143", "Macchina: 101143", true, null,
				new Dimension(3, "Macchina"));
		dimensionInstances.add(machineDimInstance);
		dimensionInstances.add(new DimensionInstance("Unit 1.0", "Componente : Unit 1.0", true, machineDimInstance,
				new Dimension(4, "Componente")));

		// String resultHTML = "";
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

		SummarisedData summarisedDataPrev = new SummarisedData("", new Date(), // TODO modificare
				new Date(), new Date(), null, null, dimensionInstances, new Context(), clusteringFeatureSpace);

		double mean = 0;
		double std = 2;
		int numMeasures = 1000;

		ArrayList<SummarisedData> summarisedDataGenerated = new ArrayList<>();

		try {
			System.out.println(
					"\n**************************************************** IDEAAS CluStream ***************************************************************");
			for (int i = 0; i < 10; i++) {
				Collection<MultidimensionalRecord> newRecords = generateMultiDimRecord(mean, std, numMeasures,
						clusteringFeatureSpace);
				SummarisedData summarisedDataResult = this.dataSummarisation.runClustering(summarisedDataPrev,
						newRecords, clusteringFeatureSpace, Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()),
						new Date());
				summarisedDataPrev = summarisedDataResult;
				summarisedDataResult.setInputData(newRecords);
				this.dao.insertDocument(summarisedDataResult.toDocument(), "summarised_data_cluStream");
				jsonResult.put(summarisedDataResult.toJSON());
				summarisedDataGenerated.add(summarisedDataResult);
			}

			//
			//// resultHTML += "\n\n\n*********** 2 *************";
			//
			// Collection<MultidimensionalRecord> newRecords1 =
			// generateMultiDimRecord(mean+0.1, std, numMeasures, clusteringFeatureSpace);
			//// resultHTML += "\nNum Records: "+newRecords1.size();
			// SummarisedData summarisedDataResult1 =
			// this.dataSummarisation.runClustering(summarisedDataResult, newRecords1,
			// clusteringFeatureSpace,
			// Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date());
			// summarisedDataResult1.setInputData(newRecords1);
			//// resultHTML += "\nNum Synthesis: "+
			// summarisedDataResult1.getSynthesis().size();
			// jsonResult.put(summarisedDataResult1.toJSON());
			//
			// Collection<MultidimensionalRecord> newRecords2 =
			// generateMultiDimRecord(mean+0.3, std, numMeasures, clusteringFeatureSpace);
			//// resultHTML += "\nNum Records: "+newRecords2.size();
			// SummarisedData summarisedDataResult2 =
			// this.dataSummarisation.runClustering(summarisedDataResult1, newRecords2,
			// clusteringFeatureSpace,
			// Date.from(datetime.atZone(ZoneId.of("Z")).toInstant()), new Date());
			// summarisedDataResult2.setInputData(newRecords2);
			//// resultHTML += "\nNum Synthesis: "+
			// summarisedDataResult2.getSynthesis().size();
			// jsonResult.put(summarisedDataResult2.toJSON());
			//

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// jsonResult.put(resultHTML);
		return jsonResult;
	}

	public Collection<MultidimensionalRecord> generateMultiDimRecord(double mean, double std, int numMeasures,
			FeatureSpace featureSpace) {
		System.out.println(
				"----------------------------------------------- START generateMultiDimRecord ----------------------------------------------\n");

		Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
		int separateNumber = numMeasures / 3;
		for (int i = 0; i < numMeasures; i++) {
			MultidimensionalRecord mr = new MultidimensionalRecord();
			// String multidimRec = "";
			// boolean first = true;

			for (Feature f : featureSpace.getFeatures()) {
				Random r = new Random();

				double randomValue = mean + std * r.nextGaussian();
				if (i > separateNumber * 2) {
					randomValue = (mean + 10) + std * r.nextGaussian();
				} else if (i > separateNumber && i <= separateNumber * 2) {
					randomValue = (mean - 10) + std * r.nextGaussian();
				}

				FeatureData featureData = new FeatureData(f, randomValue);
				mr.addFeatureData(featureData);
				// if (first) {
				// multidimRec += ""+randomValue;
				// first = false;
				// }else {
				// multidimRec += " |----| "+randomValue;
				// }
			}
			// System.out.println(multidimRec);

			newRecords.add(mr);
		}
		System.out.println(
				"\n ------------------------------------------------ FINISH generateMultiDimRecord ----------------------------------------------");

		return newRecords;
	}

	@Override
	public JSONObject getSumData(FeatureSpace feature_space, String start_datetime, String end_datetime, int experimentNumber, String machineID, String componentID, String modeID, String partProgramID) {
		JSONObject json = new JSONObject();
		JSONArray sumDataJSON = new JSONArray();

		try {
			IDAO dao = DAOFactory.getDAOFactory().getDAO();
			System.out.println("\ndal: "+start_datetime+ "   al: "+end_datetime);
			//		String format = "dd/MM/yyyy HH:mm:ss";
			LocalDateTime startDateTime = this.dateUtils.getDateTimeFromString(start_datetime);
			LocalDateTime endDateTime = this.dateUtils.getDateTimeFromString(end_datetime);

			//			String[] machines = {"101143", "101170", "101185"};
			//			String[] modes = {"0", "1"};

			String dataset =  "Macchina: "+machineID +"; Componente: "+componentID + "; Mode: "+modeID + "; Part Program: "+partProgramID;
			System.out.print("=");
			sumDataJSON.put(this.getSummarisedData(feature_space, dataset, machineID, componentID, "-1", modeID, partProgramID, startDateTime, endDateTime, true, experimentNumber));
			//							

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json.put("summarised_data", sumDataJSON);
		return json;
	}


}
