package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import it.unibs.bodai.ideaas.dao.timing.StateDetectionTiming;

public class StateDetectionTimingMySQL extends StateDetectionTiming { 
	
	public StateDetectionTimingMySQL() {}
	
	public StateDetectionTimingMySQL(String api_name, Date start_date_test, Date end_test_date, long duration,
			long total_number_of_objects_in_collection, long total_number_of_objects_scanned,
			long total_number_of_objects_returned) {
		super(api_name, start_date_test, end_test_date, duration, total_number_of_objects_in_collection,
				total_number_of_objects_scanned, total_number_of_objects_returned);
	}

	public void insertTest(Connection conn) {
		String query = "INSERT INTO state_detection_timing(api_name, start_test_date, end_test_date, duration, objects_in_collection, objects_scanned, objects_returned) VALUES (?,?,?,?,?,?,?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {			
			stmt.setString(1, this.getApi_name());
			stmt.setTimestamp(2, new java.sql.Timestamp(this.getStart_test_date().getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(this.getEnd_test_date().getTime()));
			stmt.setLong(4, this.getDuration());
			stmt.setLong(5, this.getTotal_number_of_objects_in_collection());
			stmt.setLong(6, this.getTotal_number_of_objects_scanned());
			stmt.setLong(7, this.getTotal_number_of_objects_returned());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}
	
	
	
}
