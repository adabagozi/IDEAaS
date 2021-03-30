package it.unibs.bodai.ideaas.dao.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SendAlert {
	private String measure_id;
	private Timestamp timestamp;
	private String feature_space_name;
	private String feature_space_state;
	private int feature_space_id;
	
	//fields for tests
	private int objects;
	private long extraction;
	private long analysis;
	private long initialize;
	private long xml_submit;
	private Timestamp start_test;
	private Timestamp end_test;
	private String start_interval;
	private String end_interval;
	private String inteval_long;
	private String interval_type;
	private int feature_numbers;
	private int alert_numbers;
	private LocalDateTime begin;
	private LocalDateTime end;
	
	
	public SendAlert(String measure_id, Timestamp timestamp, String feature_space_name, String feature_space_state, int feature_space_id) {
		this.setMeasure_id(measure_id);
		this.setTimestamp(timestamp);
		this.setFeature_space_name(feature_space_name);
		this.setFeature_space_state(feature_space_state);
		this.setFeature_space_id(feature_space_id);
	}
	
	public SendAlert(String feature_space_name, String feature_space_state, int feature_space_id) {
		this.setFeature_space_name(feature_space_name);
		this.setFeature_space_state(feature_space_state);
		this.setFeature_space_id(feature_space_id);
	}
	
	//constructor for test
	public SendAlert(int feature_numbers, String interval_long, String interval_type, int objects, long extraction, long analysis, int alert_numbers, LocalDateTime begin, LocalDateTime end) {
//		this.setStart_interval(start_interval);
		this.feature_numbers = feature_numbers;
		this.inteval_long = interval_long;
		this.interval_type = interval_type;
		this.objects = objects;
		this.extraction = extraction;
		this.analysis = analysis;
		this.alert_numbers = alert_numbers;
		this.begin = begin;
		this.end = end;
	}
	
	public SendAlert(String start_interval, String end_interval, String interval_long, String interval_type, int objects) {
		this.setStart_interval(start_interval);
		this.setEnd_interval(end_interval);
		this.setInteval_long(interval_long);
		this.setInterval_type(interval_type);
		this.setObjects(objects);
	}

	public String getMeasure_id() {
		return measure_id;
	}

	public void setMeasure_id(String measure_id) {
		this.measure_id = measure_id;
	}

	public String getFeature_space_name() {
		return feature_space_name;
	}

	public void setFeature_space_name(String feature_space_name) {
		this.feature_space_name = feature_space_name;
	}

	public String getFeature_space_state() {
		return feature_space_state;
	}

	public void setFeature_space_state(String features_space_state) {
		this.feature_space_state = features_space_state;
	}

	public int getFeature_space_id() {
		return feature_space_id;
	}

	public void setFeature_space_id(int feature_space_id) {
		this.feature_space_id = feature_space_id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getObjects() {
		return objects;
	}

	public void setObjects(int objects) {
		this.objects = objects;
	}

	public long getExtraction() {
		return extraction;
	}

	public void setExtraction(long extraction) {
		this.extraction = extraction;
	}

	public long getAnalysis() {
		return analysis;
	}

	public void setAnalysis(long analysis) {
		this.analysis = analysis;
	}

	public long getXml_submit() {
		return xml_submit;
	}

	public void setXml_submit(long xml_submit) {
		this.xml_submit = xml_submit;
	}

	public Timestamp getStart_test() {
		return start_test;
	}

	public void setStart_test(Timestamp start_test) {
		this.start_test = start_test;
	}

	public Timestamp getEnd_test() {
		return end_test;
	}

	public void setEnd_test(Timestamp end_test) {
		this.end_test = end_test;
	}

	public String getStart_interval() {
		return start_interval;
	}

	public void setStart_interval(String start_interval) {
		this.start_interval = start_interval;
	}

	public String getEnd_interval() {
		return end_interval;
	}

	public void setEnd_interval(String end_interval) {
		this.end_interval = end_interval;
	}

	public String getInteval_long() {
		return inteval_long;
	}

	public void setInteval_long(String inteval_long) {
		this.inteval_long = inteval_long;
	}

	public String getInterval_type() {
		return interval_type;
	}

	public void setInterval_type(String interval_type) {
		this.interval_type = interval_type;
	}

	/**
	 * @return the initialize
	 */
	public long getInitialize() {
		return initialize;
	}

	/**
	 * @param initialize the initialize to set
	 */
	public void setInitialize(long initialize) {
		this.initialize = initialize;
	}

	/**
	 * @return the feature_numbers
	 */
	public int getFeature_numbers() {
		return feature_numbers;
	}

	/**
	 * @param feature_numbers the feature_numbers to set
	 */
	public void setFeature_numbers(int feature_numbers) {
		this.feature_numbers = feature_numbers;
	}

	/**
	 * @return the begin
	 */
	public LocalDateTime getBegin() {
		return begin;
	}

	/**
	 * @param begin the begin to set
	 */
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}

	/**
	 * @return the end
	 */
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	/**
	 * @return the alert_numbers
	 */
	public int getAlert_numbers() {
		return alert_numbers;
	}

	/**
	 * @param alert_numbers the alert_numbers to set
	 */
	public void setAlert_numbers(int alert_numbers) {
		this.alert_numbers = alert_numbers;
	}

	
	
}
