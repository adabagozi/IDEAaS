package it.unibs.bodai.ideaas.dao.model;

import java.util.Collection;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

/**
 * @author Ada
 *
 */
public class ClusterElement {

	private static final Logger LOG = Logger.getLogger(ClusterElement.class.getName());
	public static final String KEY_FEATURE_ID = "feature_id";
	public static final String KEY_FEATURE_NAME = "feature_name";
	public static final String KEY_VALUE = "value";
	public static final String KEY_STATUS_CENTER = "status_center";
	public static final String KEY_STATUS_RADIUS = "status_radius";
	

	private Feature feature;
	private double centerValue;
	private String statusCenter;
	private String statusRadius;
	/**
	 * @param feature
	 * @param centerValue
	 */
	public ClusterElement(Feature feature, double centerValue) {
		super();
		this.feature = feature;
		this.centerValue = centerValue;
	}
	
	public ClusterElement(Feature feature, double centerValue, String statusCenter, String statusRadius) {
		super();
		this.feature = feature;
		this.centerValue = centerValue;
		this.setStatusCenter(statusCenter);
		this.setStatusRadius(statusRadius);
	}
	
	public ClusterElement() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ClusterElement(Document clusterElementDocument) {
		super();
		this.setFromDocument(clusterElementDocument);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}
	/**
	 * @param feature the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	/**
	 * @return the centerValue
	 */
	public double getCenterValue() {
		return centerValue;
	}
	/**
	 * @param centerValue the centerValue to set
	 */
	public void setCenterValue(double centerValue) {
		this.centerValue = centerValue;
	}	
	

	/**
	 * @return
	 */
	public String getStatusRadius() {
		return statusRadius;
	}
	
	/**
	 * @param statusRadius
	 */
	public void setStatusRadius(String statusRadius) {
		this.statusRadius = statusRadius;
	}

	/**
	 * @return the statusCenter
	 */
	public String getStatusCenter() {
		return statusCenter;
	}

	/**
	 * @param statusCenter the statusCenter to set
	 */
	public void setStatusCenter(String statusCenter) {
		this.statusCenter = statusCenter;
	}
	

	public JSONObject toJSON() {
		JSONObject jsonClusterElementParams = new JSONObject();
		jsonClusterElementParams.put(KEY_FEATURE_ID, String.format("%s",  this.getFeature().getId()));
		jsonClusterElementParams.put(KEY_FEATURE_NAME, String.format("%s", this.getFeature().getName()));
		jsonClusterElementParams.put(KEY_VALUE, String.format("%s", this.getCenterValue()));
		jsonClusterElementParams.put(KEY_STATUS_CENTER, String.format("%s", this.getStatusCenter()));
		jsonClusterElementParams.put(KEY_STATUS_RADIUS, String.format("%s", this.getStatusRadius()));
		
		return jsonClusterElementParams;
	}
	
	public void setFromDocument(Document doc) {
		this.setFeature(new Feature(Integer.parseInt(doc.get(KEY_FEATURE_ID).toString()), doc.get(KEY_FEATURE_NAME).toString()));
		this.setCenterValue(Double.parseDouble(doc.get(KEY_VALUE).toString()));
		this.setStatusCenter(doc.get(KEY_STATUS_CENTER).toString());
		this.setStatusRadius(doc.get(KEY_STATUS_RADIUS).toString());
	}

	
}
