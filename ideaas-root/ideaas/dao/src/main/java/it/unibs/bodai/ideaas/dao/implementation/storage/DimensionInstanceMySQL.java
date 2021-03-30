package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;

public class DimensionInstanceMySQL extends DimensionInstance{
	
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_VALUE = "value";
	public static final String KEY_PARENT_INSTANCE_ID = "id_parent_instance";

	
	/**
	 * 
	 */
	public DimensionInstanceMySQL() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param identificator
	 * @param dimension
	 */
	public DimensionInstanceMySQL(String identificator, Dimension dimension) {
		super(identificator, dimension);
		// TODO Auto-generated constructor stub
	}

	
    /**
     * 
     */

    public void fetchDimensionInstanceMySQLValueParentInstance(Connection connect, String value, Dimension dimension, int parentInstanceId){
        String query = "SELECT " + dimension.getTableName() + ".* FROM " + dimension.getTableName() + " WHERE " + dimension.getTableName() + "." + KEY_VALUE + "=?"+ " AND " + dimension.getTableName() + "." + KEY_PARENT_INSTANCE_ID + "=?;";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, value);
            stmt.setInt(2, parentInstanceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.setValues(rs);
            }
            this.setDimension(dimension);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
   
    /**
     * 
     */
    public void fetchDimensionInstanceMySQLByValue(Connection connect, String value, Dimension dimension){
        String query = "SELECT " + dimension.getTableName() + ".* FROM " + dimension.getTableName() + " WHERE " + dimension.getTableName() + "." + KEY_VALUE + "=?;";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.setValues(rs);
            }
            this.setDimension(dimension);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
   
    
    
   /** Metodo per impostare i parametri della classe in base al risultato ottenuto dal database
    * @param rsRow la riga di risultato dal database
    */
   private void setValues(ResultSet rsRow){
       try {
           //System.out.println(rsRow.toString());
           this.setId(rsRow.getInt(KEY_ID));
           this.setName(rsRow.getString(KEY_NAME));
           this.setInstanceId(rsRow.getString(KEY_VALUE));
           //TODO altri parametri dimension
           
       } catch (SQLException ex) {
           Logger.getLogger(FeatureMySQL.class.getName()).log(Level.SEVERE, null, ex);
           System.out.println("SQLException Result: " + ex.getMessage());
       }
   }
	
   /**
    * Metodo per l'inserimento di una feature nel database
    * @param connect connessione al database
    * @return risultato della query, se l'inserimento &egrave; avvenuto con successo ritorna true altriemnti false
    */
   public boolean insertDimensionInstance(Connection connect, Dimension dimension){
        String query = "INSERT INTO " + dimension.getTableName() + " ("+KEY_NAME+", "+KEY_VALUE+") VALUES (?,?);";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
        		stmt.setString(1, this.getName());
            stmt.setString(2, this.getInstanceId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
       return false;
   }
	
	
	/**
	 * Used in get alert status - state detection
	 * @param connection
	 * @return
	 */
	public ArrayList<DimensionInstance> getActiveComponents(Connection connection) {
		ArrayList<DimensionInstance> activeComponentsInstances = new ArrayList<>();
		//dim_components.name as component_name, dim_components.id_parent_dimension as component_parent_instance,
		String components_selection = "dim_components.name as component_name, dim_components.id as component_id, dim_components.value as component_value, dim_components.id_parent_instance as component_parent_instance ";
		//		String machines_selection = " dim_machines.value as machine_value, dim_machines.id_parent_instance as machine_parent_instance_id ";
		String machines_selection = "dim_machines.value as machine_value, dim_machines.id_parent_instance as machine_parent_instance_id, dim_machines.id_parent_instance as machine_parent_instance_id ";
		String sql = "SELECT " + components_selection + "," + machines_selection + "FROM dim_components " + "JOIN dim_machines " + "ON dim_components.id_parent_instance = dim_machines.id " + "WHERE dim_components.is_active = TRUE";

		try(PreparedStatement stmt = connection.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String component_name = rs.getString("component_name");
				String component_value = rs.getString("component_value");
				String machine_value = rs.getString("machine_value");
				int machine_parent_instance_id = rs.getInt("machine_parent_instance_id");
				//System.out.println("Component name: " + component_name +  " Component instance value: " + component_value + " Parent machine instance value: " + machine_value + " Machine's Plant parent instance ID: " + machine_parent_instance_id);
				DimensionInstance componentInstance = new DimensionInstance(component_value, component_name, new DimensionInstance(machine_value, new Dimension(3)));
				activeComponentsInstances.add(componentInstance);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return activeComponentsInstances;
	}
	
	/**
	 * Used in get alert status and send alert - state detection
	 * @param connection
	 * @return
	 */
	public ArrayList<DimensionInstance> systemDimensions(Connection connection) {
		ArrayList<DimensionInstance> activeSystemDimensions = new ArrayList<>();
		String sql = "SELECT `dim_components`.`id` AS `component_id`, `dim_components`.`name` AS `component_name`, `dim_components`.`value` AS `component_value`, `dim_machines`.`id` AS `machine_id`, `dim_machines`.`name` AS `machine_name`,`dim_machines`.`value` AS `machine_value`, `dim_plants`.`value` AS `plant_value`, `dim_plants`.`name` AS `plant_name`, `dim_enterprises`.`value` AS `enterprise_value`, `dim_enterprises`.`name` AS `enterprise_name` FROM `dim_components` JOIN `dim_machines` ON `dim_components`.`id_parent_instance` = `dim_machines`.`id` JOIN `dim_plants` ON `dim_machines`.`id_parent_instance` = `dim_plants`.`id` JOIN `dim_enterprises` ON `dim_plants`.`id_parent_instance` = `dim_enterprises`.`id`";
		try(PreparedStatement stmt = connection.prepareStatement(sql)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				//enterprise
				String enterprise_value = rs.getString("enterprise_value");
				String enterprise_name = rs.getString("enterprise_name");
				//plant
				String plant_value = rs.getString("plant_value");
				String plant_name = rs.getString("plant_name");
				//machine
				String machine_value = rs.getString("machine_value");
				String machine_name = rs.getString("machine_name");
				//component
				String component_value = rs.getString("component_value");
				String component_name = rs.getString("component_name");
//				System.out.println("Enterprise value: " + enterprise_value + " Enterprise name: " + enterprise_name + " | Plant value: " + plant_value + " Plant name: " + plant_name + " | Machine value " + machine_value + " Machine name: " + machine_name + " | Component value: " + component_value + " Component name: " + component_name); 
				DimensionInstance enterprise_instance = new DimensionInstance(enterprise_value, enterprise_name, null, new Dimension(1));
				DimensionInstance plant_instance = new DimensionInstance(plant_value, plant_name, enterprise_instance, new Dimension(2));
				DimensionInstance machine_instance = new DimensionInstance(machine_value, machine_name, plant_instance, new Dimension(3));
				DimensionInstance component_instance = new DimensionInstance(component_value, component_name, machine_instance, new Dimension(4));
				activeSystemDimensions.add(component_instance);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return activeSystemDimensions;
	}
}
