package it.unibs.bodai.ideaas.dao.model;

import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

/**
 * @author Ada Bagozi
 *
 */
public class SynthesiElement {

	private static final Logger LOG = Logger.getLogger(SynthesiElement.class.getName());

	private final static double EPSILON = 0.00005;
	public static final double MIN_VARIANCE = 1e-50;
	
	public static final String KEY_FEATURE_ID = "feature_id";
	public static final String KEY_FEATURE_NAME = "feature_name";
	public static final String KEY_VALUE = "feature_value";
	public static final String KEY_DEV_STD = "dev_std";
	public static final String KEY_LS = "ls";
	public static final String KEY_SS = "ss";
	public static final String KEY_STATUS_CENTER = "status_center";
	public static final String KEY_STATUS_RADIUS = "status_radius";
	public static final String KEY_FEATURE_STATUS = "feature_status";
	public static final String KEY_ERROR = "error";
	public static final String KEY_OK = "ok";
	public static final String KEY_UNCHANGED = "unchanged";
	public static final String KEY_WARNING = "warning";
	
//	"feature_id": "1",
//	"feature_name": "Carico mandrino",
//	"feature_value": "90",
//	"dev_std": "10",
//	"feature_status": "ok"
		
	private double ls;
	private double ss;
//	private double radius;
	private double recordNumbers;
	private Feature feature;
	private double centerValue;
	private String statusCenter = KEY_UNCHANGED;
	private String statusRadius = KEY_UNCHANGED;
	private String status = KEY_UNCHANGED;
	
	/**
	 * @param ls
	 * @param ss
	 * @param feature
	 * @param centerValue
	 */
	public SynthesiElement(double ls, double ss, Feature feature, long numberOfData) {
		super();
		this.ls = ls;
		this.ss = ss;
		this.feature = feature;
		
		this.centerValue = this.ls/numberOfData;
		this.recordNumbers = numberOfData;
	
	}
	public SynthesiElement() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SynthesiElement(Document elementDocument) {
		super();
		this.setFromDocument(elementDocument);
	}
	
	
	/**
	 * @return the ls
	 */
	public double getLs() {
		return ls;
	}
	/**
	 * @param ls the ls to set
	 */
	public void setLs(double ls) {
		this.ls = ls;
	}
	/**
	 * @return the ss
	 */
	public double getSs() {
		return ss;
	}
	/**
	 * @param ss the ss to set
	 */
	public void setSs(double ss) {
		this.ss = ss;
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
	 * @return the statusCenter
	 */
	public String getStatusCenter() {
		return this.getFeature().checkBoundary(centerValue);
	}
	/**
	 * @param statusCenter the statusCenter to set
	 */
	public void setStatusCenter(String statusCenter) {
		this.statusCenter = statusCenter;
	}
	
	/**
	 * @return the statusRadius
	 */
	public String getStatusRadius() {
		
		double valueUpper = centerValue+getDeviation();
		double valueLower = centerValue-getDeviation();
//		if (centerValue < 0) {
//			value = centerValue-radius;
//		}
		String statusUpper = this.getFeature().checkBoundary(valueUpper);
		String statusLower = this.getFeature().checkBoundary(valueLower);
		if (statusUpper.equalsIgnoreCase(KEY_ERROR) || statusLower.equalsIgnoreCase(KEY_ERROR)) {
			return KEY_ERROR;
		}
		if (statusUpper.equalsIgnoreCase(KEY_WARNING) || statusLower.equalsIgnoreCase(KEY_WARNING)) {
			return KEY_WARNING;
		}
		return KEY_UNCHANGED;
	}
	/**
	 * @param statusRadius the statusRadius to set
	 */
	public void setStatusRadius(String statusRadius) {
		this.statusRadius = statusRadius;
	}

	
//	private double getDeviation() {
//		return ;
//	}

//	/**
//	 * @return the radius
//	 */
//	public double getRadius() {
//		return radius;
//	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
//	/**
//	 * @param radius the radius to set
//	 */
//	public void setRadius(double radius) {
//		this.radius = radius;
//	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		if(this.status.equalsIgnoreCase(KEY_UNCHANGED)) {
			this.status = status;
		}else if (this.status.equalsIgnoreCase(KEY_OK) && (status.equalsIgnoreCase(KEY_WARNING) || status.equalsIgnoreCase(KEY_ERROR))) {
			this.status = status;
		}else if (this.status.equalsIgnoreCase(KEY_WARNING) && status.equalsIgnoreCase(KEY_ERROR)) {
			this.status = status;
		}
	}
	

	/**
	 * @return the recordNumbers
	 */
	public double getRecordNumbers() {
		return recordNumbers;
	}
	/**
	 * @param recordNumbers the recordNumbers to set
	 */
	public void setRecordNumbers(double recordNumbers) {
		this.recordNumbers = recordNumbers;
	}
	
	public double getDeviation() {
		double lsDivN = ls / this.getRecordNumbers();
		double lsDivNSquared = lsDivN * lsDivN;
		double ssDivN = ss / this.getRecordNumbers();
		double variance = ssDivN - lsDivNSquared;

		// Due to numerical errors, small negative values can occur.
		// We correct this by settings them to almost zero.
		if (variance <= 0.0) {
			if (variance > -EPSILON) {
				variance = MIN_VARIANCE;
			}
		}
		return Math.sqrt(variance);
	}
	
	public JSONObject toJSON() {
		JSONObject jsonSynthesiElementParams = new JSONObject();
		jsonSynthesiElementParams.put(KEY_FEATURE_ID, String.format("%s",  this.getFeature().getId()));
		jsonSynthesiElementParams.put(KEY_FEATURE_NAME, String.format("%s", this.getFeature().getName()));
		jsonSynthesiElementParams.put(KEY_VALUE, String.format("%s", this.getCenterValue()));
		jsonSynthesiElementParams.put(KEY_DEV_STD, String.format("%s", this.getDeviation()));
		jsonSynthesiElementParams.put(KEY_LS, String.format("%s", this.getLs()));
		jsonSynthesiElementParams.put(KEY_SS, String.format("%s", this.getSs()));
		jsonSynthesiElementParams.put(KEY_STATUS_CENTER, String.format("%s", this.getStatusCenter()));
		jsonSynthesiElementParams.put(KEY_STATUS_RADIUS, String.format("%s", this.getStatusRadius()));
		jsonSynthesiElementParams.put(KEY_FEATURE_STATUS, String.format("%s", this.getStatus()));
		
		return jsonSynthesiElementParams;
	}
	
	//TODO aggiornare
	public void setFromDocument(Document doc) {
		this.setFeature(new Feature(Integer.parseInt(doc.get(KEY_FEATURE_ID).toString()), doc.get(KEY_FEATURE_NAME).toString()));
		this.setCenterValue(Double.parseDouble(doc.get(KEY_VALUE).toString()));
		this.setLs(Double.parseDouble(doc.get(KEY_LS).toString()));
		this.setSs(Double.parseDouble(doc.get(KEY_SS).toString()));
		this.setStatusCenter(doc.get(KEY_STATUS_CENTER).toString());
		this.setStatusRadius(doc.get(KEY_STATUS_RADIUS).toString());
	}
}
