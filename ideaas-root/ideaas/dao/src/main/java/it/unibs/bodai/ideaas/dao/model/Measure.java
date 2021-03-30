package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Ada Bagozi
 *
 */
public class Measure {
	private static final Logger LOG = Logger.getLogger(Measure.class.getName());

	private static final String KEY_ID = "_id";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_DIMENSIONS = "dimensions";
	public static final String KEY_CONTEXT_PARAMETERS = "context_parameter";
	public static final String KEY_FEATURES = "features";
	public static final String KEY_STATUS = "status";

	private String id;
	private Date timestamp;
	private String status;
	private HashMap<Integer, FeatureData> featuresDataHashMap;
	// private Collection<FeatureData> featuresData; //TODO cercare di togliere
	// ovunque in quanto è preferibile usare le hashmap in questo caso
	private ArrayList<FeatureData> ftData;// TODO A che serve e perchè è stato creato?
	private FeatureData featureData;// TODO A che serve e perchè è stato creato?
	private Collection<DimensionInstance> dimensionInstances;
	private ArrayList<DimensionInstance> dimInstances; // TODO A che serve e perchè è stato creato?
	private Collection<ParameterInstance> parameterInstances;// TODO Sparirà nel futuro

	// /**
	// * @param id
	// * @param timestamp
	// * @param status
	// * @param featuresData
	// * @param dimensionInstances
	// * @param parameterInstances
	// */
	// public Measure(String id, Date timestamp, String status,
	// Collection<FeatureData> featuresData, Collection<DimensionInstance>
	// dimensionInstances, Collection<ParameterInstance> parameterInstances) {
	// super();
	// this.id = id;
	// this.timestamp = timestamp;
	// this.status = status;
	// this.featuresData = featuresData;
	// this.dimensionInstances = dimensionInstances;
	// this.parameterInstances = parameterInstances;
	// }
	/**
	 * @param id
	 * @param timestamp
	 * @param status
	 * @param featuresData
	 * @param dimensionInstances
	 * @param parameterInstances
	 */
	public Measure(String id, Date timestamp, String status, HashMap<Integer, FeatureData> dataRecords,
			Collection<DimensionInstance> dimensionInstances, Collection<ParameterInstance> parameterInstances) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.status = status;
		this.featuresDataHashMap = dataRecords;
		this.dimensionInstances = dimensionInstances;
		this.parameterInstances = parameterInstances;
	}

	/**
	 * 
	 */
	public Measure() {
		super();
	}

	/**
	 * 
	 */
	public Measure(Date timestamp, DimensionInstance machine, DimensionInstance partProgram) {
		super();
		this.timestamp = timestamp;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	// /**
	// * @return the featuresData
	// */
	// public Collection<FeatureData> getFeaturesData() {
	// return featuresData;
	// }
	//
	//
	// /**
	// * @param featuresData the featuresData to set
	// */
	// public void setFeaturesData(Collection<FeatureData> featuresData) {
	// this.featuresData = featuresData;
	// }
	//
	// /**
	// * @param featureData
	// */
	// public void addFeatureData(FeatureData featureData) {
	// if(this.featuresData == null) {
	// this.featuresData = new ArrayList<>();
	// }
	// this.featuresData.add(featureData);
	// }

	public HashMap<Integer, FeatureData> getFeaturesDataHashMap() {
		return featuresDataHashMap;
	}

	public void setFeaturesDataHashMap(HashMap<Integer, FeatureData> featuresDataHashMap) {
		this.featuresDataHashMap = featuresDataHashMap;
	}

	/**
	 * @param featureData
	 */
	public void addFeatureDataHashMap(FeatureData featureData) {
		if (this.featuresDataHashMap == null) {
			this.featuresDataHashMap =  new HashMap<>();
		}
		this.featuresDataHashMap.put(featureData.getFeature().getId(), featureData); //.add(featureData);
	}

	/**
	 * @return the dimensionInstances
	 */
	public Collection<DimensionInstance> getDimensionInstances() {
		return dimensionInstances;
	}

	/**
	 * @param dimensionInstances
	 *            the dimensionInstances to set
	 */
	public void setDimensionInstances(Collection<DimensionInstance> dimensionInstances) {
		this.dimensionInstances = dimensionInstances;
	}

	/**
	 * @param dimensionInstance
	 */
	public void addDimensionInstance(DimensionInstance dimensionInstance) {
		if (this.dimensionInstances == null) {
			this.dimensionInstances = new ArrayList<>();
		}
		this.dimensionInstances.add(dimensionInstance);
	}

	/**
	 * @return the parameterInstances
	 */
	public Collection<ParameterInstance> getParameterInstances() {
		return parameterInstances;
	}

	/**
	 * @param parameterInstances
	 *            the parameterInstances to set
	 */
	public void setParameterInstances(Collection<ParameterInstance> parameterInstances) {
		this.parameterInstances = parameterInstances;
	}

	/**
	 * @param parameterInstance
	 */
	public void addParameterInstance(ParameterInstance parameterInstance) {
		if (this.parameterInstances == null) {
			this.parameterInstances = new ArrayList<>();
		}
		this.parameterInstances.add(parameterInstance);
	}

	/**
	 * Metodo per trasformare la prima parte di data summarisation in json
	 * 
	 * @return JSONObject first part of data summarisation json
	 */
	public JSONObject toJSONCommon() {

		JSONObject jsonMeasure = new JSONObject();
		JSONArray jsonFeaturesData = new JSONArray();
		JSONArray jsonDimensionInstances = new JSONArray();
		JSONArray jsonParameters = new JSONArray();

		jsonMeasure.put(KEY_STATUS, this.getStatus());

//		for (FeatureData featureData : this.getFeaturesData()) {
//			jsonFeaturesData.put(featureData.toJSON());
//		}
		Iterator<Entry<Integer, FeatureData>> it = this.getFeaturesDataHashMap().entrySet().iterator();
	    while (it.hasNext()) {

			 Map.Entry<Integer, FeatureData> pair = (Map.Entry) it.next();
			 jsonFeaturesData.put(pair.getValue().toJSON());
//	        System.out.println(pair.getKey() + " = " + pair.getValue());
//	        it.remove(); // avoids a ConcurrentModificationException
	    }
		jsonMeasure.put(KEY_FEATURES, jsonFeaturesData);

		for (DimensionInstance dimensionInstance : this.getDimensionInstances()) {
			jsonDimensionInstances.put(dimensionInstance.toJSON());
		}
		jsonMeasure.put(KEY_DIMENSIONS, jsonDimensionInstances);

		/*
		 * for (ParameterInstance parameterInstance : this.getParameterInstances()) {
		 * jsonPasrameters.put(parameterInstance.toJSON()); }
		 * jsonMeasure.put(KEY_CONTEXT_PARAMETERS, jsonParameters);
		 */
		return jsonMeasure;
	}

	/**
	 * @return
	 */
	public JSONObject toJSON() {
		JSONObject jsonMeasure = this.toJSONCommon();
		jsonMeasure.put(KEY_TIMESTAMP, this.getTimestamp());
		return jsonMeasure;
	}

	/**
	 * Metodo per la trasformazione di un oggetto Measure in un elemento di
	 * tipo @Document
	 * 
	 * @return document documento generato dalla misura
	 */
	public Document toDocument() {
		Document doc = Document.parse(this.toJSONCommon().toString());
		doc.append("timestamp", this.getTimestamp());
		return doc;
	}

	public void setFromDocument(Document doc) throws NumberFormatException {
		this.setId(doc.get(KEY_ID).toString());
		this.setTimestamp((Date) doc.get(KEY_TIMESTAMP));

		Collection<Document> dimensionInstancesDocument = (Collection<Document>) doc.get(KEY_DIMENSIONS);
		for (Document dimensionInstanceDocument : dimensionInstancesDocument) {
			this.addDimensionInstance(new DimensionInstance(dimensionInstanceDocument));
		}

		// Collection<Document> parameterInstancesDocument = (Collection<Document>)
		// doc.get(KEY_CONTEXT_PARAMETERS);
		// for (Document parameterInstanceDocument: parameterInstancesDocument) {
		// this.addParameterInstance(new ParameterInstance(parameterInstanceDocument));
		// }

		Collection<Document> featuresDataDocument = (Collection<Document>) doc.get(KEY_FEATURES);
		for (Document featureDataDocument : featuresDataDocument) {
			this.addFeatureDataHashMap(new FeatureData(featureDataDocument));
		}
	}

	/**
	 * @author Naim
	 */
	public Measure(String object_id, Date timestamp, String status, FeatureData featureData) {
		super();
		this.id = object_id;
		this.timestamp = timestamp;
		this.status = status;
		this.setFeatureData(featureData);
	}

	public Measure(String object_id, Date timestamp, ArrayList<DimensionInstance> dimensionsInstances) {
		super();
		this.id = object_id;
		this.timestamp = timestamp;
		this.dimensionInstances = dimensionsInstances;
	}

	public Measure(String id, Date timestamp, String status, ArrayList<FeatureData> featuresData,
			ArrayList<DimensionInstance> dimensionInstances) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.status = status;
		this.setFtData(featuresData);
		this.setDimInstances(dimensionInstances);
	}

	public Measure(String object_id) {
		super();
		this.id = object_id;
	}

	public FeatureData getFeatureData() {
		return featureData;
	}

	public void setFeatureData(FeatureData featureData) {
		this.featureData = featureData;
	}

	public ArrayList<FeatureData> getFtData() {
		return ftData;
	}

	public void setFtData(ArrayList<FeatureData> ftData) {
		this.ftData = ftData;
	}

	public ArrayList<DimensionInstance> getDimInstances() {
		return dimInstances;
	}

	public void setDimInstances(ArrayList<DimensionInstance> dimInstances) {
		this.dimInstances = dimInstances;
	}

}
