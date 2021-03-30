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
import it.unibs.bodai.ideaas.dao.timing.TimingDataSummarisation;

public class TimingDataSummarisationMySQL extends TimingDataSummarisation {
	
	public static final String TABLE_NAME = "timing_data_summarisation";
	public static final String KEY_ID = "id";
	public static final String KEY_NUM_DATA = "num_data";
	public static final String KEY_NUM_NEW_DATA = "num_new_data";
	public static final String KEY_NUM_SYNTHESES = "num_syntheses";
	public static final String KEY_NUM_NEW_SYNTHESES = "num_new_syntheses";
	public static final String KEY_TOTAL_TIME = "total_time";
	public static final String KEY_INSERT_TIME = "insert_time";
	public static final String KEY_SUMMARISE_TIME = "summarise_time";
	public static final String KEY_EXTRACT_TIME = "extract_time";
	public static final String KEY_RELEVANCE_EVALUATION_TIME = "relevance_evaluation_time";
	public static final String KEY_DESCRIPTION = "description";


	
//	/**
//	 * Mettodo per la lettura di tutti i tempi di acquisizione delle misure dal database
//	 * @param connect connessione al database dal quale recuperare i tempi
//	 * @return insieme di tempi di acquisizione presenti nel database
//	 */
//	public static Collection<TimingDataSummarisation> fetchAcquisitionTimes(Connection connect){
//		Collection<TimingDataAcquisition> dataAcquisitionTimings = new ArrayList<>();
//		String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+";";
//		try (PreparedStatement stmt = connect.prepareStatement(query)) {
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				TimingDataSummarisationMySQL temp = new TimingDataSummarisationMySQL();
//				temp.setValues(rs);
//
//				dataAcquisitionTimings.add(temp);
//			}
//		} catch (SQLException ex) {
//			System.out.println("SQLException: " + ex.getMessage());
//		}
//		return dataAcquisitionTimings;
//	}

	public TimingDataSummarisationMySQL() {
		super();
		// TODO Auto-generated constructor stub
	}



	public TimingDataSummarisationMySQL(int num_data, int num_new_data, int num_syntheses, int num_new_syntheses,
			long totalTime, long insertTime, long summarisationTime, long extractTime, long relevanceEvaluationTime, String description) {
		super(num_data, num_new_data, num_syntheses, num_new_syntheses, totalTime, insertTime, summarisationTime,
				extractTime, relevanceEvaluationTime, description);
		// TODO Auto-generated constructor stub
	}



	/**
	 * Metodo per l'inserimento di un tempo di acquisizione misurato nel database
	 * @param connect connessione al database
	 * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
	 */
	public boolean insertMySQL(Connection connect){
		if(this.checkValues()){
			String query = "INSERT INTO "+TABLE_NAME+" (" + KEY_NUM_DATA + ", " + KEY_NUM_NEW_DATA + ", " + KEY_NUM_SYNTHESES + ", " + KEY_NUM_NEW_SYNTHESES + ", " + KEY_TOTAL_TIME + ", " + KEY_INSERT_TIME + ", " + KEY_SUMMARISE_TIME + ", " + KEY_EXTRACT_TIME + ", " + KEY_DESCRIPTION+ ", " + KEY_RELEVANCE_EVALUATION_TIME +") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			try (PreparedStatement stmt = connect.prepareStatement(query)) {
				stmt.setInt(1, this.getNumData());
				stmt.setInt(2, this.getNumNewData());
				stmt.setInt(3, this.getNumSyntheses());
				stmt.setInt(4, this.getNumNewSyntheses());
				stmt.setLong(5, this.getTotalTime());
				stmt.setLong(6, this.getInsertTime());
				stmt.setLong(7, this.getSummarisationTime());
				stmt.setLong(8, this.getExtractTime());
				stmt.setString(9, this.getDescription());
				stmt.setLong(10, this.getRelevanceEvaluationTime());
				stmt.executeUpdate();
				return true;
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
			}
		}
		return false;
	}



//	/**
//	 * Metodo per impostare i parametri della classe in base al risultato ottenuto dal database
//	 * @param rsRow la riga di risultato dal database
//	 */
//	private void setValues(ResultSet rsRow){
//		try {
//			//System.out.println(rsRow.toString());
//			this.setId(rsRow.getInt(KEY_ID));
//			this.setNumFiles(rsRow.getInt(KEY_NUM_FILES));
//			this.setTransformTime(rsRow.getInt(KEY_TRANSFORM_TIME));
//			this.setInsertTime(rsRow.getInt(KEY_INSERT_TIME));
//			this.setDescription(rsRow.getString(KEY_DESCRIPTION));
//			
//
//		} catch (SQLException ex) {
//			Logger.getLogger(FeatureMySQL.class.getName()).log(Level.SEVERE, null, ex);
//			System.out.println("SQLException Result: " + ex.getMessage());
//		}
//	}

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

