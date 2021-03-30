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
import it.unibs.bodai.ideaas.dao.timing.TimingDataReading;

public class TimingDataReadingMySQL extends TimingDataReading{
	
	public static final String TABLE_NAME = "timing_data_reading";
	public static final String KEY_ID = "id";
	public static final String KEY_NUM_FILES = "num_files";
	public static final String KEY_READ_TIME = "read_time";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_QUERY = "query";

	public TimingDataReadingMySQL() {
		super();
	}
	
	public TimingDataReadingMySQL(int numFiles, long readingTime, String description, String query) {
		super(numFiles, readingTime, description, query);
	}

	/**
	 * Metodo per l'inserimento di un tempo di acquisizione misurato nel database
	 * @param connect connessione al database
	 * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
	 */
	public boolean insertMySQL(Connection connect){
		if(this.checkValues()){
			String query = "INSERT INTO "+TABLE_NAME+" ("+KEY_NUM_FILES+", "+KEY_READ_TIME+", "+KEY_DESCRIPTION+", "+KEY_QUERY+") VALUES (?,?,?,?);";
			
			try (PreparedStatement stmt = connect.prepareStatement(query)) {
				stmt.setInt(1, this.getNumFiles());
				stmt.setInt(2, (int) this.getReadingTime());
				stmt.setString(3, this.getDescription());
				stmt.setString(4, this.getQuery());
				stmt.executeUpdate();
				return true;
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
			}
		}
		return false;
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

