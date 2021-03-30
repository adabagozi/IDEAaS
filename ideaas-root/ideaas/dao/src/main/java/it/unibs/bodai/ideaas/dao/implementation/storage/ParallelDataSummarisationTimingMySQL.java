package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.timing.ParallelDataSummarisationTiming;

/**
 * @author Daniele Comincini
 *
 */
public class ParallelDataSummarisationTimingMySQL extends ParallelDataSummarisationTiming{
	
	public static final String TABLE_NAME = "timing_compute_nearest_s";
	public static final String KEY_ID = "id";
	public static final String KEY_NUM_DIMENSIONS = "numDimensions";
	public static final String KEY_NUM_SYNTHESES = "numSyntheses";
	public static final String KEY_TIME = "time";
	public static final String KEY_TYPE = "type";
	public static final String KEY_MEAN = "mean";
	public static final String KEY_STD = "std";
	public static final String KEY_ID_TEST = "idTest";

	public ParallelDataSummarisationTimingMySQL() {
		super();
	}
	
	public ParallelDataSummarisationTimingMySQL(int id, int numDimensions, int numSyntheses, long time, String type, double mean, double std, int idTest) {
		super(id, numDimensions, numSyntheses, time, type, mean, std, idTest);
	}
	
	public ParallelDataSummarisationTimingMySQL(int numDimensions, int numSyntheses, long time, String type, double mean, double std, int idTest) {
		super(numDimensions, numSyntheses, time, type, mean, std, idTest);
	}


	/**
	 * Mettodo per la lettura di tutti i tempi di acquisizione delle misure dal database
	 * @param connect connessione al database dal quale recuperare i tempi
	 * @return insieme di tempi di acquisizione presenti nel database
	 */
	public static Collection<ParallelDataSummarisationTiming> fetchAcquisitionTimes(Connection connect){
		Collection<ParallelDataSummarisationTiming> parallelDataSummarisationTimings = new ArrayList<>();
		String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+";";
		try (PreparedStatement stmt = connect.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ParallelDataSummarisationTimingMySQL temp = new ParallelDataSummarisationTimingMySQL();
				temp.setValues(rs);

				parallelDataSummarisationTimings.add(temp);
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
		return parallelDataSummarisationTimings;
	}
	
	/**
	 * Metodo per l'inserimento di un tempo di calcolo sintesi più vicina misurato nel database
	 * @param connect connessione al database
	 * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
	 */
	public boolean insertMySQL(Connection connect){
		if(this.checkValues()){
			String query = "INSERT INTO "+TABLE_NAME+" ("+KEY_NUM_DIMENSIONS+", "+KEY_NUM_SYNTHESES+", "+KEY_TIME+", "+KEY_TYPE+", "+KEY_MEAN+", "+KEY_STD+", "+KEY_ID_TEST+") VALUES (?,?,?,?,?,?,?);";
			try (PreparedStatement stmt = connect.prepareStatement(query)) {
				stmt.setInt(1, this.getNumDimensions());
				stmt.setInt(2, this.getNumSyntheses());
				stmt.setLong(3, this.getTime());
				stmt.setString(4, this.getType());
				stmt.setDouble(5, this.getMean());
				stmt.setDouble(6, this.getStd());
				stmt.setInt(7, this.getIdTest());
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
			this.setId(rsRow.getInt(KEY_ID));
			this.setNumDimensions(rsRow.getInt(KEY_NUM_DIMENSIONS));
			this.setNumSyntheses(rsRow.getInt(KEY_NUM_SYNTHESES));
			this.setTime(rsRow.getInt(KEY_TIME));
			this.setType(rsRow.getString(KEY_TYPE));
			
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

