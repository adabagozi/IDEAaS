package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;

public class TimingDataAcquisitionMySQL extends TimingDataAcquisition{
	
	public static final String TABLE_NAME = "timing_data_acquisition";
	public static final String KEY_ID = "id";
	public static final String KEY_NUM_FILES = "num_files";
	public static final String KEY_TRANSFORM_TIME = "transform_time";
	public static final String KEY_INSERT_TIME = "insert_time";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_ANOMALY = "anomaly_perc";

	public TimingDataAcquisitionMySQL() {
		super();
	}
	
	public TimingDataAcquisitionMySQL(int id, int numFiles, long transformTime, long insertTime) {
		super(id, numFiles, transformTime, insertTime);
	}
	
	public TimingDataAcquisitionMySQL(int numFiles, long transformTime, long insertTime, String description, double anomalyPerc) {
		super(numFiles, transformTime, insertTime, description, anomalyPerc);
	}


	/**
	 * Mettodo per la lettura di tutti i tempi di acquisizione delle misure dal database
	 * @param connect connessione al database dal quale recuperare i tempi
	 * @return insieme di tempi di acquisizione presenti nel database
	 */
	public static Collection<TimingDataAcquisition> fetchAcquisitionTimes(Connection connect){
		Collection<TimingDataAcquisition> dataAcquisitionTimings = new ArrayList<>();
		String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+";";
		try (PreparedStatement stmt = connect.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TimingDataAcquisitionMySQL temp = new TimingDataAcquisitionMySQL();
				temp.setValues(rs);

				dataAcquisitionTimings.add(temp);
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
		return dataAcquisitionTimings;
	}

	/**
	 * Metodo per l'inserimento di un tempo di acquisizione misurato nel database
	 * @param connect connessione al database
	 * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
	 */
	public boolean insertMySQL(Connection connect){
		if(this.checkValues()){
			String query = "INSERT INTO "+TABLE_NAME+" ("+KEY_NUM_FILES+", "+KEY_TRANSFORM_TIME+", "+KEY_INSERT_TIME+", "+KEY_DESCRIPTION+", "+KEY_ANOMALY+") VALUES (?,?,?,?,?);";
			try (PreparedStatement stmt = connect.prepareStatement(query)) {
				stmt.setInt(1, this.getNumFiles());
				stmt.setInt(2, (int) this.getTransformTime());
				stmt.setInt(3, (int) this.getInsertTime());
				stmt.setString(4, this.getDescription());
				stmt.setDouble(5, this.getAnomalyPerc());
				stmt.executeUpdate();
				return true;
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
			}
		}
		return false;
	}



	/**
	 * Metodo per impostare i parametri della classe in base al risultato ottenuto dal database
	 * @param rsRow la riga di risultato dal database
	 */
	private void setValues(ResultSet rsRow){
		try {
			//System.out.println(rsRow.toString());
			this.setId(rsRow.getInt(KEY_ID));
			this.setNumFiles(rsRow.getInt(KEY_NUM_FILES));
			this.setTransformTime(rsRow.getInt(KEY_TRANSFORM_TIME));
			this.setInsertTime(rsRow.getInt(KEY_INSERT_TIME));
			this.setDescription(rsRow.getString(KEY_DESCRIPTION));
			this.setAnomalyPerc(rsRow.getDouble(KEY_ANOMALY));
			

		} catch (SQLException ex) {
			Logger.getLogger(FeatureMySQL.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("SQLException Result: " + ex.getMessage());
		}
	}

	/**
	 * Metodo per il controllo dei dati della feature prima di operare sul database
	 * @return il risultato del controllo sui dati, true se tutti i dati corretti, false altrimenti
	 */
	private boolean checkValues(){
//		if(this.getName().equalsIgnoreCase("")){
//			System.out.println("Il name della feature non puo' essere vuoto.");
//			return false;
//		}
		return true;
	}

}

