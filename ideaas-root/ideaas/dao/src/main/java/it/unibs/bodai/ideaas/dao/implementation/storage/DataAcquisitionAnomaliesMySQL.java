package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.timing.DataAcquistionAnomalies;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;

public class DataAcquisitionAnomaliesMySQL extends DataAcquistionAnomalies{

	public DataAcquisitionAnomaliesMySQL(String date, double anomalyPerc, String featureId) {
		super(date, anomalyPerc, featureId);
	}

	public static final String TABLE_NAME = "data_acquisition_anomalies";
	public static final String KEY_DATE = "date";
	public static final String KEY_ANOMALY = "anomaly_perc";
	public static final String KEY_FEATURE_ID = "feature_id";


	private void createTable(Connection connect) throws SQLException {
	    String sqlCreate = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
	            + "  ("+ KEY_DATE + "   	TEXT,"
				+ "  "+ KEY_FEATURE_ID + "   	TEXT,"
				+ "  "+ KEY_ANOMALY + "   		TEXT)";

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
	public boolean insertMySQL(Connection connect){
		
		try {
			this.createTable(connect);
			String query = "INSERT INTO "+TABLE_NAME+" ("+KEY_DATE+", "+KEY_ANOMALY+",  "+KEY_FEATURE_ID+") VALUES (?,?,?);";
			try (PreparedStatement stmt = connect.prepareStatement(query)) {
				stmt.setString(1, this.getDate());
				stmt.setDouble(2, this.getAnomalyPerc());
				stmt.setString(3, this.getFeatureId());
				stmt.executeUpdate();
				return true;
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
}

