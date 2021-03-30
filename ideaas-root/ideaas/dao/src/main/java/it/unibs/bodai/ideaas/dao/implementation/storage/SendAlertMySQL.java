package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

// <<<<<<< HEAD
// =======
import it.unibs.bodai.ideaas.dao.model.Feature;
// >>>>>>> ideaas
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.SendAlert;

public class SendAlertMySQL extends SendAlert {


	public SendAlertMySQL(String measure_id, Timestamp timestamp, String feature_space_name, String feature_space_state, int feature_space_id) {
		super(measure_id, timestamp, feature_space_name, feature_space_state, feature_space_id);
	}

	/**
	 * @param feature_space_name
	 * @param feature_space_state
	 * @param feature_space_id
	 */
	public SendAlertMySQL(String feature_space_name, String feature_space_state, int feature_space_id) {
		super(feature_space_name, feature_space_state, feature_space_id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param feature_numbers
	 * @param interval_long
	 * @param interval_type
	 * @param objects
	 * @param extraction
	 * @param analysis
	 * @param xml_submit
	 * @param start_test
	 * @param end_test
	 */
	public SendAlertMySQL(int feature_numbers, String interval_long, String interval_type, int objects,
			long extraction, long analysis, int alert_numbers, LocalDateTime begin, LocalDateTime end) {
		super(feature_numbers, interval_long, interval_type, objects, extraction, analysis, alert_numbers, begin, end);
		// TODO Auto-generated constructor stub
	}

	public SendAlertMySQL(String start_interval, String end_interval, String interval_long, String interval_type, int objects) {
		super(start_interval, end_interval, interval_long, interval_type, objects);
	}


	public void saveFeaturesState(Connection conn) {
		String query = "INSERT INTO send_alert(measure_id, timestamp, feature_space_name, feature_space_state, feature_space_id) VALUES (?,?,?,?,?)";
//		System.out.println(query);
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, this.getMeasure_id());
			stmt.setTimestamp(2, this.getTimestamp());
			stmt.setString(3, this.getFeature_space_name());
			stmt.setString(4, this.getFeature_space_state());
			stmt.setInt(5, this.getFeature_space_id());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			ex.printStackTrace();
		}
	}


	public static ArrayList<SendAlert> getLastFeatureSpaceState(Connection connect) {
		ArrayList<SendAlert> send_alert_objects = new ArrayList<>();
		String query = "SELECT DISTINCT sa.* FROM send_alert sa INNER JOIN (SELECT feature_space_name, MAX(timestamp) AS MaxTimestamp FROM send_alert GROUP BY feature_space_name) groupedsa ON sa.feature_space_name = groupedsa.feature_space_name AND sa.timestamp = groupedsa.MaxTimestamp";
		try(PreparedStatement stmt = connect.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int feature_space_id = rs.getInt("feature_space_id");
				String feature_space_name = rs.getString("feature_space_name");
				String feature_space_state = rs.getString("feature_space_state");
				Timestamp timestamp = rs.getTimestamp("timestamp");
				String measure_id = rs.getString("measure_id");
//				System.out.println("Feature space ID: " + feature_space_id + " Feature Space name: " + feature_space_name + " Feature space state: " + feature_space_state);
				SendAlert sa_obj = new SendAlert(measure_id, timestamp, feature_space_name, feature_space_state, feature_space_id);
				send_alert_objects.add(sa_obj);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return send_alert_objects;
	}

	public static ArrayList<FeatureSpace> sendAlertFspState(Connection connect) {
		ArrayList<FeatureSpace> fsp_states = new ArrayList<>();
		String query = "SELECT DISTINCT sa.* FROM send_alert sa INNER JOIN (SELECT feature_space_name, MAX(timestamp) AS MaxTimestamp FROM send_alert GROUP BY feature_space_name) groupedsa ON sa.feature_space_name = groupedsa.feature_space_name AND sa.timestamp = groupedsa.MaxTimestamp";
		try(PreparedStatement stmt = connect.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				int feature_space_id = rs.getInt("feature_space_id");
				String feature_space_name = rs.getString("feature_space_name");
				String feature_space_state = rs.getString("feature_space_state");
				Timestamp timestamp = rs.getTimestamp("timestamp");
				String measure_id = rs.getString("measure_id");
//				System.out.println("Feature space ID: " + feature_space_id + " Feature Space name: " + feature_space_name + " Feature space state: " + feature_space_state);
				//int id, String name, String state, boolean isActive
				FeatureSpace fsp = new FeatureSpace(feature_space_id, feature_space_name, feature_space_state, true);
				fsp_states.add(fsp);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return fsp_states;
	}

	public void saveSendAlertTestTimes(Connection conn) {
//		int feature_numbers, String interval_long, String interval_type, int objects, long extraction, long analysis, long initialize
		String query = "INSERT INTO send_alert_timing(feature_numbers, interval_value, interval_type, objects, extraction, analysis, alert_number, begin, end) VALUES (?,?,?,?,?,?,?,?,?)";
//		System.out.println(this.getAnalysis());
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, this.getFeature_numbers());
			stmt.setString(2, this.getInteval_long());
			stmt.setString(3, this.getInterval_type());
			stmt.setInt(4, this.getObjects());
			stmt.setLong(5, this.getExtraction());
			stmt.setLong(6, this.getAnalysis());
			stmt.setInt(7, this.getAlert_numbers());
			stmt.setString(8, this.getBegin().toString());
			stmt.setString(9, this.getEnd().toString());
			stmt.executeUpdate();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
//		String query = "INSERT INTO send_alert_timing(start_interval, end_interval, interval_long, interval_type, objects, extraction, analysis, xml_submit, start_test, f) VALUES (?,?,?,?,?,?,?,?,?,?)";
//		try (PreparedStatement stmt = conn.prepareStatement(query)) {
//			stmt.setString(1, this.getStart_interval());
//			stmt.setString(2, this.getEnd_interval());
//			stmt.setString(3, this.getInteval_long());
//			stmt.setString(4, this.getInterval_type());
//			stmt.setInt(5, this.getObjects());
//			stmt.setLong(6, this.getExtraction());
//			stmt.setLong(7, this.getAnalysis());
//			stmt.setLong(8, this.getXml_submit());
//			stmt.setTimestamp(9, this.getStart_test());
//			stmt.setTimestamp(10, this.getEnd_test());
//			stmt.executeUpdate();
//		} catch (SQLException ex) {
//			System.out.println("SQLException: " + ex.getMessage());
//		}
	}

}
