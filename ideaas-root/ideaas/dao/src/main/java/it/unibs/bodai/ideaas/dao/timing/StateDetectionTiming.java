package it.unibs.bodai.ideaas.dao.timing;

import java.util.Date;

public class StateDetectionTiming {
	
	private String api_name;
	private Date start_test_date ;
	private Date end_test_date;
	private long duration;
	private long total_number_of_objects_in_collection;
	private long total_number_of_objects_scanned;
	private long total_number_of_objects_returned;
	
	public StateDetectionTiming() {}
	
	public StateDetectionTiming(String api_name, Date start_date_test, Date end_test_date, long duration, long total_number_of_objects_in_collection, long total_number_of_objects_scanned, long total_number_of_objects_returned) {
		this.setApi_name(api_name);
		this.setStart_test_date(start_date_test);
		this.setEnd_test_date(end_test_date);
		this.setDuration(duration);
		this.setTotal_number_of_objects_in_collection(total_number_of_objects_in_collection);
		this.setTotal_number_of_objects_scanned(total_number_of_objects_scanned);
		this.setTotal_number_of_objects_returned(total_number_of_objects_returned);
	}
	
	
	public String getApi_name() {
		return api_name;
	}
	public void setApi_name(String api_name) {
		this.api_name = api_name;
	}
	public Date getStart_test_date() {
		return start_test_date;
	}
	public void setStart_test_date(Date start_test_date) {
		this.start_test_date = start_test_date;
	}
	public Date getEnd_test_date() {
		return end_test_date;
	}
	public void setEnd_test_date(Date start_end_date) {
		this.end_test_date = start_end_date;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getTotal_number_of_objects_in_collection() {
		return total_number_of_objects_in_collection;
	}
	public void setTotal_number_of_objects_in_collection(long total_number_of_objects_in_collection) {
		this.total_number_of_objects_in_collection = total_number_of_objects_in_collection;
	}
	public long getTotal_number_of_objects_scanned() {
		return total_number_of_objects_scanned;
	}
	public void setTotal_number_of_objects_scanned(long total_number_of_objects_scanned) {
		this.total_number_of_objects_scanned = total_number_of_objects_scanned;
	}
	public long getTotal_number_of_objects_returned() {
		return total_number_of_objects_returned;
	}
	public void setTotal_number_of_objects_returned(long total_number_of_objects_returned) {
		this.total_number_of_objects_returned = total_number_of_objects_returned;
	}
	
}
