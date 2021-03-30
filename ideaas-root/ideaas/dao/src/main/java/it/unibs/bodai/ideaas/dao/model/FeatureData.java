package it.unibs.bodai.ideaas.dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Ada Bagozi
 *
 */
/**
 * @author ada
 *
 */
public class FeatureData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(FeatureData.class.getName());
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_VALUE = "value";
	public static final String KEY_STATUS = "status";
	public static final String KEY_CONTEXT_STATUS = "contexts_status";

	private Feature feature;
	
	private double value;
	private String status;
	private Date timestamp; //TODO penso sia molto più corretto metterlo sopra.
	private Collection<ContextStatus> contextsStatus = new ArrayList<>();

	/**
	 * @param feature
	 * @param value
	 */
	public FeatureData(Feature feature, double value) {
		super();
		this.feature = feature;
		this.value = value;
	}

	public FeatureData(Feature feature, double value, String status, Collection<ContextStatus> contextsStatus) {
		super();
		this.feature = feature;
		this.value = value;
		this.status = status;
		this.contextsStatus = contextsStatus;
	}

	/**
	* 
	*/
	public FeatureData() {
		this(null, 0);
	}

	/**
	* 
	*/
	public FeatureData(Document featureDataDocument) {
		super();
		this.setFromDocument(featureDataDocument);
	}

	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature
	 *            the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double value) {
		this.value = value;
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

	/**
	 * @return the contextStatus
	 */
	public Collection<ContextStatus> getContextsStatus() {
		return this.contextsStatus;
	}

	/**
	 * @param contextsStatus
	 */
	public void setContextsStatus(Collection<ContextStatus> contextsStatus) {
		this.contextsStatus = contextsStatus;
	}

	/**
	 * @param contextStatus
	 *            the contextStatus to set
	 */
	public void addContextStatus(ContextStatus contextStatus) {
		this.contextsStatus.add(contextStatus);
	}

	public JSONObject toJSON() {
		JSONObject jsonFeature = new JSONObject();
		jsonFeature.put(KEY_ID, this.getFeature().getId());
		jsonFeature.put(KEY_NAME, this.getFeature().getName());
		jsonFeature.put(KEY_VALUE, this.getValue());
		jsonFeature.put(KEY_STATUS, this.getStatus());

		JSONArray jsonContextsStatus = new JSONArray();
		for (ContextStatus contextStatus : this.getContextsStatus()) {
			jsonContextsStatus.put(contextStatus.toJSON());
		}
		jsonFeature.put(KEY_CONTEXT_STATUS, jsonContextsStatus);

		return jsonFeature;
	}

	public void setFromDocument(Document doc) {
		//TODO magari è meglio recuperare la feature da mysql
		this.setFeature(new Feature(Integer.parseInt(doc.get(KEY_ID).toString()), doc.get(KEY_NAME).toString()));
		this.setValue(Double.parseDouble(doc.get(KEY_VALUE).toString()));
//		this.setStatus(doc.get(KEY_STATUS).toString());
		Collection<Document> contextsStatusDocument = (Collection<Document>) doc.get(KEY_CONTEXT_STATUS);
		for (Document contextStatusDocument : contextsStatusDocument) {
			this.addContextStatus(new ContextStatus(contextStatusDocument));
		}
	}

	/**
	 * @author Naim
	 */
	public FeatureData(Feature feature, double value, Date timestamp, String status) {
		super();
		this.feature = feature;
		this.value = value;
		this.setTimestamp(timestamp);
		this.status = status;
	}
	
	public FeatureData(Feature feature, double value, Date timestamp) {
		super();
		this.feature = feature;
		this.value = value;
		this.setTimestamp(timestamp);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
