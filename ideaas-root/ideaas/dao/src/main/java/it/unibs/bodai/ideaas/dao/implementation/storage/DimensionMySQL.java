package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.model.Dimension;


/**
 * Classe che rappresenta la tabella MYSQL contenente le dimensioni.
 * Questa classe estende le funzioni della classe Dimension con l'aggiunta metodi strettamente connessi al tipo di database utilizzato.
 * @author Ada
 */
public class DimensionMySQL extends Dimension {
	
		public static final String TABLE_NAME = "dimensions";
		public static final String KEY_ID = "id";
		public static final String KEY_ID_PARENT = "id_parent_dimension";
		public static final String KEY_NAME = "name";
		public static final String KEY_TABLE_NAME = "table_name";
		public static final String KEY_INPUT_POSITION = "input_position";

	    
	    /**
	     * Costruttore vuoto
	     */
	    public DimensionMySQL(){
	        super();
	    }
	    
	    

		/**
	     * Costruttore che ha origine con solo l'id impostato
	     * @param id identificativo della classe
	     */
	    public DimensionMySQL(int id){
	        super(id);
	    }
	    
	    /**
	     * 
	     */
	    public void fetchDimensionMySQL(Connection connect){
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
	     *
	     */
	    public static ArrayList<Dimension> fetchDimensionsMySQL(Connection connect){
	    	ArrayList<Dimension> dimensionList = new ArrayList<>();
	        String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+";";
	        try (PreparedStatement stmt = connect.prepareStatement(query)) {
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                DimensionMySQL temp = new DimensionMySQL();
	                temp.setValues(rs);
	                
	                dimensionList.add(temp);
	            }
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }
	        return dimensionList;
	    }
	    
	    

	    /**
	     * 
	     */
	    public void fetchDimensionMySQLByName(Connection connect, String name){
	        String query = "SELECT "+TABLE_NAME+".* FROM "+TABLE_NAME+" WHERE "+TABLE_NAME+"."+KEY_NAME+"=?;";
	        try (PreparedStatement stmt = connect.prepareStatement(query)) {
	            stmt.setString(1, name);
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                this.setValues(rs);
	            }
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }
	    }
	    //TODO insert and update
	    
	    
	    /**
	     * Metodo per impostare i parametri della classe in base al risultato ottenuto dal database
	     * @param rsRow la riga di risultato dal database
	     */
	    private void setValues(ResultSet rsRow){
	        try {
	            //System.out.println(rsRow.toString());
	            this.setId(rsRow.getInt(KEY_ID));
	            this.setName(rsRow.getString(KEY_NAME));
	            this.setTableName(rsRow.getString(KEY_TABLE_NAME));
	            this.setInputPosition(rsRow.getInt(KEY_INPUT_POSITION));
	            //TODO altri parametri dimension
	            
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
	        if(this.getName().equalsIgnoreCase("")){
	            System.out.println("Il name della dimensione non puo' essere vuoto.");
	            return false;
	        }
	        return true;
	    }
	    
	}
