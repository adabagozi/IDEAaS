package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.anomalies.DataSummarisationAnomalies;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;
import it.unibs.bodai.ideaas.dao.timing.TimingDataSummarisation;
import utils.StaticVariables;

public class DataSummarisationAnomaliesMySQL extends DataSummarisationAnomalies {




	public static final String TABLE_NAME = "data_summarisation_anomalies"+StaticVariables.collectionNameExt;
	public static final String KEY_STABLE_ID = "stable_id";
	public static final String KEY_PREV_ID = "prev_id";
	public static final String KEY_SNAPSHOT_ID = "snapshot_id";
	public static final String KEY_START = "start_datetime";
	public static final String KEY_END = "end_datetime";
	public static final String KEY_ALGORITHM = "algorithm";
	public static final String KEY_DIST_CENTROID_STAB = "centroid_dist_stable";
	public static final String KEY_DIST_RADIUS_STAB = "radius_dist_stable";
	public static final String KEY_DIST_RADIUS_VAR_STAB = "radius_vers_stable";

	public static final String KEY_DIST_CENTROID_PREV = "centroid_dist_previous";
	public static final String KEY_DIST_RADIUS_PREV = "radius_dist_previous";
	public static final String KEY_DIST_RADIUS_VAR_PREV = "radius_vers_previous";

	public static final String KEY_DESCRIPTION = "description";


	public DataSummarisationAnomaliesMySQL(String stableId, String prevId, String snapshotId, String startDate, String endDate, int algorithm,
			double dist_centroids_stab, double dist_radius_stab, int dist_radius_var_stab, double dist_centroids_prev,
			double dist_radius_prev, int dist_radius_var_prev, String description) {
		super(stableId, prevId, snapshotId, startDate, endDate, algorithm, dist_centroids_stab, dist_radius_stab, dist_radius_var_stab, dist_centroids_prev,
				dist_radius_prev, dist_radius_var_prev, description);
		// TODO Auto-generated constructor stub
	}


	private void createTable(Connection connect) throws SQLException {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
				+ "  ("+ KEY_STABLE_ID + "   	TEXT,"
				+ "   "+ KEY_PREV_ID + "   		TEXT,"
				+ "   "+ KEY_SNAPSHOT_ID + "   		TEXT,"
				+ "   "+ KEY_START + "   	TEXT,"
	            + "   "+ KEY_END + "   		TEXT,"
	            + "   "+ KEY_ALGORITHM + "  TEXT,"
	            + "   "+ KEY_DIST_CENTROID_STAB + "   	TEXT,"
	            + "   "+ KEY_DIST_RADIUS_STAB + "   	TEXT,"
				+ "   "+ KEY_DIST_RADIUS_VAR_STAB + "   	TEXT,"
				+ "   "+ KEY_DIST_CENTROID_PREV + "   	TEXT,"
				+ "   "+ KEY_DIST_RADIUS_PREV + "   	TEXT,"
				+ "   "+ KEY_DIST_RADIUS_VAR_PREV + "   	TEXT,"
	            + "   "+ KEY_DESCRIPTION + "   TEXT)";

		try (PreparedStatement stmt = connect.prepareStatement(sqlCreate)) {
			stmt.execute(sqlCreate);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}
	/**
	 * Metodo per l'inserimento di un tempo di acquisizione misurato nel database
	 * @param connect connessione al database
	 * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
	 */
	
	public void insertMySQL(Connection connect){
		try {
			createTable(connect);
			
			String query = "INSERT INTO "+TABLE_NAME+" ("
					+ KEY_STABLE_ID + ", "
					+ KEY_PREV_ID + ", "
					+ KEY_SNAPSHOT_ID + ", "
					+ KEY_START + ", "
					+ KEY_END + ", " 
					+ KEY_ALGORITHM + ", " 
					+ KEY_DIST_CENTROID_STAB + ", " 
					+ KEY_DIST_RADIUS_STAB + ", " 
					+ KEY_DIST_RADIUS_VAR_STAB + ", "
					+ KEY_DIST_CENTROID_PREV + ", " 
					+ KEY_DIST_RADIUS_PREV + ", " 
					+ KEY_DIST_RADIUS_VAR_PREV + ", " 
					+ KEY_DESCRIPTION 
					+") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);";
			try (PreparedStatement stmt = connect.prepareStatement(query)) {
				stmt.setString(1, this.getStableId());
				stmt.setString(2, this.getPrevId());
				stmt.setString(3, this.getSnapshotId());
				stmt.setString(4, this.getStartDate());
				stmt.setString(5, this.getEndDate());
				stmt.setInt(6, this.getAlgorithm());
				stmt.setString(7, ""+this.getDist_centroids_stab());
				stmt.setString(8, ""+this.getDist_radius_stab());
				stmt.setString(9, ""+this.getDist_radius_var_stab());
				stmt.setString(10, ""+this.getDist_centroids_prev());
				stmt.setString(11, ""+this.getDist_radius_prev());
				stmt.setString(12, ""+this.getDist_radius_var_prev());
				stmt.setString(13, this.getDescription());
				stmt.executeUpdate();
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}

