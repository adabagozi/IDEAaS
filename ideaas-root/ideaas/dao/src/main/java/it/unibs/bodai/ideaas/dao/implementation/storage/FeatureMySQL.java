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
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;

/**
 * Classe che rappresenta la tabella MYSQL contenente le feature.
 * Questa classe estende le funzioni della classe Feature con l'aggiunta metodi strettamente connessi al tipo di database utilizzato.
 * @author Ada
 */
public class FeatureMySQL extends Feature{
	public static final String TABLE_NAME = "features";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_INPUT_POSITION = "input_position";
	public static final String KEY_WARNING_LOWER = "warning_lower_bound";
	public static final String KEY_WARNING_UPPER = "warning_upper_bound";
	public static final String KEY_ERROR_LOWER = "error_lower_bound";
	public static final String KEY_ERROR_UPPER = "error_upper_bound";
	public static final String KEY_UNIT = "measure_unit";

    /**
     * Costruttore della classe FeatureMySQL, il costruttore eredita tutto dalla superclasse
     * @param id
	 * @param name
	 * @param lowerBoundWarning
	 * @param lowerBoundError
	 * @param upperBoundWarning
	 * @param upperBoundError
	 * @param unitaMisura
	 * @param featureSpace
	 */
	public FeatureMySQL(int id,  String name, double lowerBoundWarning, double lowerBoundError,
			double upperBoundWarning, double upperBoundError, String unitaMisura, Collection<FeatureSpace> featureSpace) {
		super(id, name, lowerBoundWarning, lowerBoundError, upperBoundWarning, upperBoundError, unitaMisura,
				featureSpace);
		// TODO Auto-generated constructor stub
	}
    
             
    /**
     * Costruttore vuoto
     */
    public FeatureMySQL(){
        super();
    }
    
    

	/**
     * Costruttore che ha origine con solo l'id impostato
     * @param id identificativo della classe
     */
    public FeatureMySQL(int id){
        super(id);
    }
    
    /**
     * Metodo per la lettura della feature dal database, 
     * questo metodo imposta i dati della feature in base a quelli che vengono estratti dal database.
     * @param connect connessione al database dal quale recuperare la feature
     */
    public void fetchFeatureMySQL(Connection connect){
        String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+" WHERE "+TABLE_NAME+"."+KEY_ID+"=?;";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.setValues(rs);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
    
    /**
     * Mettodo per la lettura di tutte le feature dal database
     * @param connect connessione al database dal quale recuperare le features
     * @return insieme di features presenti nel database
     */
    public static Collection<Feature> fetchFeaturesMySQL(Connection connect){
        Collection<Feature> featureList = new ArrayList<>();
        String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+";";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FeatureMySQL temp = new FeatureMySQL();
                temp.setValues(rs);
                
                featureList.add(temp);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return featureList;
    }
    
    /**
     * Mettodo per la lettura di tutte le feature dal database
     * @param connect connessione al database dal quale recuperare le features
     * @return insieme di features presenti nel database
     */
    public static Collection<Feature> fetchFeaturesMySQLByFeatureSpace(Connection connect, int featureSpaceId){
        Collection<Feature> featureList = new ArrayList<>();
        String query = "SELECT features.*, feature_spaces_has_feature.feature_space_id FROM features INNER JOIN feature_spaces_has_feature ON features.id=feature_spaces_has_feature.feature_id WHERE feature_spaces_has_feature.feature_space_id=?;";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, featureSpaceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FeatureMySQL temp = new FeatureMySQL();
                temp.setValues(rs);
                
                featureList.add(temp);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return featureList;
    }
    
    /**
     * Metodo per l'inserimento di una feature nel database
     * @param connect connessione al database
     * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
     */
    public boolean insertMySQL(Connection connect){
    	if(this.checkValues()){
	        String query = "INSERT INTO "+TABLE_NAME+" ("+KEY_NAME+", "+KEY_WARNING_LOWER+", "+KEY_WARNING_UPPER+", "+KEY_ERROR_LOWER+", "+KEY_ERROR_UPPER+") VALUES (?,?,?,?,?);";
	        try (PreparedStatement stmt = connect.prepareStatement(query)) {
	        	stmt.setString(1, this.getName());
	            stmt.setDouble(2, this.getLowerBoundWarning());
	            stmt.setDouble(3, this.getUpperBoundWarning());
	            stmt.setDouble(4, this.getLowerBoundError());
	            stmt.setDouble(5, this.getUpperBoundError());
	            stmt.executeUpdate();
	            return true;
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }
    	}
        return false;
    }

    
    /**
     * Metodo per l'aggiornamento di una feature nel database.
     * @param connect connessione al database
     * @return il risultato della query, se l'aggiornamento &egrave; avvenuto con successo ritorna true, altrimenti false
     */
    public boolean updateMySQL(Connection connect){
    	if(this.checkValues()){
	   	    String query = "UPDATE features SET name=?, lower_bound_warning=?, upper_bound_warning=?, lower_bound_error=?, upper_bound_error=? WHERE id=?;";
	        try (PreparedStatement stmt = connect.prepareStatement(query)) {
	            stmt.setString(1, this.getName());
	            stmt.setDouble(2, this.getLowerBoundWarning());
	            stmt.setDouble(3, this.getUpperBoundWarning());
	            stmt.setDouble(4, this.getLowerBoundError());
	            stmt.setDouble(5, this.getUpperBoundError());
	            stmt.setInt(7, this.getId());
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
            this.setPositionInInputFile(rsRow.getInt(KEY_INPUT_POSITION));
            this.setMeasureUnit(rsRow.getString(KEY_UNIT));
            this.setMin(rsRow.getDouble(KEY_MIN));
            this.setMax(rsRow.getDouble(KEY_MAX));

/*            this.setLowerBoundWarning(this.normalizeData(rsRow.getDouble("warning_lower_bound")));
            this.setLowerBoundError(this.normalizeData(rsRow.getDouble("error_lower_bound")));
            this.setUpperBoundWarning(this.normalizeData(rsRow.getDouble("warning_upper_bound")));
            this.setUpperBoundError(this.normalizeData(rsRow.getDouble("error_upper_bound")));*/
            
//            int idFeatureSpace = rsRow.getInt("feature_spaces_has_feature.feature_space_id");
//            if(this.getFeatureSpace()==null){
//            	FeatureSpace fS = new FeatureSpace(idFeatureSpace);
//                this.setFeatureSpace(fS);
//            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FeatureMySQL.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException Result: " + ex.getMessage());
        }
    }
    
    /**
     * Metodo per il controllo dei dati della feature prima di operare sul database
     * @return il risultato del controllo sui dati, true se tutti i dati corretti, false altrimenti
     */
    //TODO valutare se può andare nel modello
    private boolean checkValues(){
        if(this.getName().equalsIgnoreCase("")){
            System.out.println("Il name della feature non puo' essere vuoto.");
            return false;
        }
        return true;
    }
    
	public static ArrayList<Feature> getOnlyFeatures(Connection connect) {
		ArrayList<Feature> features = new ArrayList<>();
		String sql_query = "SELECT id, features.name AS feature_name, warning_lower_bound, warning_upper_bound, error_lower_bound, error_upper_bound, measure_unit FROM features";
		try(PreparedStatement stmt = connect.prepareStatement(sql_query)) {
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				int feature_id = rs.getInt("id");
				String feature_name = rs.getString("feature_name");
				String feature_unit_measure = rs.getString("measure_unit");
				//System.out.println("Feature unit measure: " + feature_unit_measure);
				double warning_lower_bound = rs.getDouble("warning_lower_bound");
				//System.out.println(warning_lower_bound);
				double warning_upper_bound = rs.getDouble("warning_upper_bound");
				//System.out.println(warning_upper_bound);
				double error_lower_bound = rs.getDouble("error_lower_bound");
				//System.out.println(error_lower_bound);
				double error_upper_bound = rs.getDouble("error_upper_bound");
				//System.out.println(error_upper_bound);
				Feature feature = new Feature(feature_id, feature_name, warning_lower_bound, error_lower_bound, warning_upper_bound, error_upper_bound, feature_unit_measure);
				features.add(feature);
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
		return features;
	} 

}
