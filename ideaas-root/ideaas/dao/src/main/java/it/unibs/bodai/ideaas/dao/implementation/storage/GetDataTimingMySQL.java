package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import it.unibs.bodai.ideaas.dao.timing.GetDataTiming;

public class GetDataTimingMySQL extends GetDataTiming{

	public GetDataTimingMySQL(String api_name, Date start_date_test, Date end_test_date, long total_time,
			long extraction_time, long tranform_time, long xml_generation_time, String collection_name,
			long count_collection, int experiment_number) {
		super(api_name, start_date_test, end_test_date, total_time, extraction_time, tranform_time, xml_generation_time,
				collection_name, count_collection, experiment_number);
	}

	public void insertGetDataTest(Connection conn) {
		String query = "INSERT INTO sd_get_data_timing(api_name, start_date_test, end_date_test, extraction_time, transform_time, xml_generation, total_time, collection_name, num_files, experiment_number) VALUES (?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {			
			stmt.setString(1, this.getApi_name());
			stmt.setTimestamp(2, new java.sql.Timestamp(this.getStart_date_test().getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(this.getEnd_test_date().getTime()));
			stmt.setLong(4, this.getExtraction_time());
			stmt.setLong(5, this.getTransform_time());
			stmt.setLong(6, this.getXml_generation_time());
			stmt.setLong(7,this.getTotal_time());
			stmt.setString(8, this.getCollection_name());
			stmt.setLong(9, this.getCount_collection());
			stmt.setInt(10, this.getExperiment_number());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}
	
	
}
