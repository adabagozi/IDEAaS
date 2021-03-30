/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;

/**
 *
 * @author Ada
 */
public class FeatureSpaceMySQL extends FeatureSpace{
	public static final String TABLE_NAME = "feature_spaces";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_IS_ACTIVE = "is_active";
	public static final String TABLE_NAME_JOIN_FEATURE = "feature_spaces_has_feature";
	public static final String KEY_JOIN_FEATURE_ID_FS = "feature_space_id";
	public static final String KEY_JOIN_FEATURE_ID_F = "feature_id";

	/**
	 * 
	 */
	public FeatureSpaceMySQL() {
		super();
	}

	/**
	 * @param id
	 * @param nome
	 * @param features
	 */
	public FeatureSpaceMySQL(int id, String nome, Collection<Feature> features) {
		super(id, nome, features);
	}

	/**
	 * @param id
	 */
	public FeatureSpaceMySQL(int id) {
		super(id);
	}
	
	/**
     * Metodo per la lettura della feature space dal database, 
     * questo metodo imposta i dati della feature space in base a quelli che vengono estratti dal database.
     * @param connect connessione al database dal quale recuperare la feature
     */
    public void fetchFeatureSpaceMySQL(Connection connect, IDAO dao){
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID+"=?;";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.setValues(rs);
                this.setFeatures(dao.readFeaturesByFeatureSpaceId(this.getId()));
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    /**
     * Mettodo per la lettura di tutte le feature space dal database
     * @param connect connessione al database dal quale recuperare le features
     * @return insieme di features presenti nel database
     */
	public static ArrayList<FeatureSpace> fetchAllFeatureSpaceMySQL(Connection connect, IDAO iDAO){
		ArrayList<FeatureSpace> featureSpaceList = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_NAME+";";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FeatureSpaceMySQL temp = new FeatureSpaceMySQL();
                temp.setValues(rs);
                temp.setFeatures(iDAO.readFeaturesByFeatureSpaceId(temp.getId()));
                featureSpaceList.add(temp);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return featureSpaceList;
    }
    
    /**
     * Mettodo per la lettura di tutte le feature space dal database
     * @param connect connessione al database dal quale recuperare le features
     * @return insieme di features presenti nel database
     */
    public static Collection<FeatureSpace> fetchFeatureSpaceMySQLWhithFeatureID(Connection connect, IDAO iDAO, int featureId){
        Collection<FeatureSpace> featureSpaceList = new ArrayList<>();
        String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+" INNER JOIN "+TABLE_NAME_JOIN_FEATURE+" ON "+TABLE_NAME+"."+KEY_ID+" = "+TABLE_NAME_JOIN_FEATURE+"."+KEY_JOIN_FEATURE_ID_FS+" WHERE "+TABLE_NAME_JOIN_FEATURE+"."+KEY_JOIN_FEATURE_ID_F+"=?;";
//        System.out.println(query);
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
         	stmt.setInt(1, featureId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FeatureSpaceMySQL temp = new FeatureSpaceMySQL();
                temp.setValues(rs);
                temp.setFeatures(iDAO.readFeaturesByFeatureSpaceId(temp.getId()));
                featureSpaceList.add(temp);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return featureSpaceList;
    }
    /**
     * Metodo per la lettura di tutte le feature space da osservare
     * @param connect connessione al database
     * @param isActive parametro per la condizione sulla lettura delle feature space. Se observe &egrave; true si leggono le feature space osservabili, se false quelle non osservabili.
     * @return le feature space a seconda del paramtro di ingresso observe. 
     */
    public static Collection<FeatureSpace> fetchFeatureSpaceObserve(Connection connect, boolean isActive, IDAO dao){
    	Collection<FeatureSpace> featureSpace = new ArrayList<>();
    	int osserva = isActive ? 1 : 0;
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_IS_ACTIVE+"=?;";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
        	stmt.setInt(1, osserva);
        	//System.out.println(stmt.toString());
        	
        	ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FeatureSpaceMySQL temp = new FeatureSpaceMySQL();
                temp.setValues(rs);
                temp.setFeatures(dao.readFeaturesByFeatureSpaceId(temp.getId()));
                featureSpace.add(temp);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return featureSpace;
    }
    
    
    /**
     * Metodo per l'inserimento di una feature space nel database
     * @param connect connessione al database
     * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
     */
    public boolean insertMySQL(Connection connect){
    	//TODO potrebbe essere interessante eseguire in questa fase anche l'inserimento delle feature della feature space (attenzione ricordarsi query transictions)
    	if(this.checkValues()){
	        String query = "INSERT INTO "+TABLE_NAME+" ("+KEY_NAME+") VALUES (?);";
	        try (PreparedStatement stmt = connect.prepareStatement(query)) {
	        	stmt.setString(1, this.getName());
	            stmt.executeUpdate();
	            return true;
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }
    	}
        return false;
    }

    
    /**
     * Metodo per l'aggiornamento di una feature space nel database.
     * @param connect connessione al database
     * @return il risultato della query, se l'aggiornamento &egrave; avvenuto con successo ritorna true, altrimenti false
     */
    public boolean updateMySQL(Connection connect){
    	if(this.checkValues()){
	   	    String query = "UPDATE "+TABLE_NAME+" SET "+KEY_NAME+"=? WHERE "+KEY_ID+"=?;";
	        try (PreparedStatement stmt = connect.prepareStatement(query)) {
	            stmt.setString(1, this.getName());
	            stmt.setInt(2, this.getId());
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
            this.setName(rsRow.getString(KEY_NAME));
            boolean isActive = rsRow.getInt(KEY_IS_ACTIVE)==1?true:false;
            this.setActive(isActive);

            
            /**
             * TODO in futuro si potrebbe rendere questa variabile optional 
             * e aggiungere alla classe feature una nuova variabile unicamente allo scopo di salvare l'id
             */
            
        } catch (SQLException ex) {
            Logger.getLogger(FeatureSpaceMySQL.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException Result: " + ex.getMessage());
        }
    }
    
    /**
     * Metodo per il controllo dei dati della feature space prima di operare sul database
     * @return il risultato del controllo sui dati, true se tutti i dati corretti, false altrimenti
     */
    private boolean checkValues(){
        if(this.getName().equalsIgnoreCase("")){
            System.out.println("Il nome della feature space non puo' essere vuoto.");
            return false;
        }
        return true;
    }
    
	public static ArrayList<FeatureSpace> getFeatureSpaces(Connection connect) {
		ArrayList<FeatureSpace> feature_spaces_mysql = new ArrayList<>();
		String sql_query = "SELECT feature_space_id, feature_spaces.name, feature_id FROM feature_spaces_has_feature JOIN feature_spaces ON feature_spaces_has_feature.feature_space_id = feature_spaces.id";
		try(PreparedStatement stmt = connect.prepareStatement(sql_query)) {
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int feature_space_id = rs.getInt("feature_space_id");
				String feature_space_name = rs.getString("name");
				int feature_id = rs.getInt("feature_id");
				Feature f = new Feature(feature_id, new FeatureSpace(feature_space_id));
				Collection <Feature> features = new ArrayList<>();
				features.add(f);
				FeatureSpace feature_space = new FeatureSpace(feature_space_id, feature_space_name, features);
				feature_space.setState("");
				feature_spaces_mysql.add(feature_space);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connect.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		}
		return feature_spaces_mysql;
	}
	
	
    
}
