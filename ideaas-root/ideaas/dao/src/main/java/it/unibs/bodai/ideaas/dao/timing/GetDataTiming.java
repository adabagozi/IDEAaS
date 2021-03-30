package it.unibs.bodai.ideaas.dao.timing;

import java.util.Date;

public class GetDataTiming {
	private String api_name;
	private Date start_date_test;
	private Date end_test_date;
	private long total_time;
	private long extraction_time;
	private long transform_time;
	private long xml_generation_time;
	private String collection_name;
	private long count_collection;
	private int experiment_number;
	
	public GetDataTiming(String api_name, Date start_date_test, Date end_test_date, long total_time, long extraction_time, long tranform_time, long xml_generation_time, String collection_name, long count_collection, int experiment_number) {
		this.setApi_name(api_name);
		this.setStart_date_test(start_date_test);
		this.setEnd_test_date(end_test_date);
		this.setExtraction_time(extraction_time);
		this.setTransform_time(tranform_time);
		this.setXml_generation_time(xml_generation_time);
		this.setTotal_time(total_time);
		this.setCollection_name(collection_name);
		this.setCount_collection(count_collection);
		this.setExperiment_number(experiment_number);
	}
	
	public String getApi_name() {
		return api_name;
	}
	public void setApi_name(String api_name) {
		this.api_name = api_name;
	}
	public Date getStart_date_test() {
		return start_date_test;
	}
	public void setStart_date_test(Date start_date_test) {
		this.start_date_test = start_date_test;
	}
	public Date getEnd_test_date() {
		return end_test_date;
	}
	public void setEnd_test_date(Date end_test_date) {
		this.end_test_date = end_test_date;
	}
	public long getExtraction_time() {
		return extraction_time;
	}
	public void setExtraction_time(long extraction_time) {
		this.extraction_time = extraction_time;
	}
	public long getTransform_time() {
		return transform_time;
	}
	public void setTransform_time(long transform_time) {
		this.transform_time = transform_time;
	}
	public long getXml_generation_time() {
		return xml_generation_time;
	}
	public void setXml_generation_time(long xml_generation_time) {
		this.xml_generation_time = xml_generation_time;
	}

	public long getTotal_time() {
		return total_time;
	}

	public void setTotal_time(long total_time) {
		this.total_time = total_time;
	}

	public String getCollection_name() {
		return collection_name;
	}

	public void setCollection_name(String collection_name) {
		this.collection_name = collection_name;
	}

	public long getCount_collection() {
		return count_collection;
	}

	public void setCount_collection(long count_collection) {
		this.count_collection = count_collection;
	}

	public int getExperiment_number() {
		return experiment_number;
	}

	public void setExperiment_number(int experiment_number) {
		this.experiment_number = experiment_number;
	}
}
