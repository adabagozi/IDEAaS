package utils;

public class Consts {
	
	/**
	 * Mongo DB collection
	 */
//	public static final String COLLECTION_NAME = "2016_11";
	
	
	/**
	 * API's name
 	 */
	public static final String 	GET_ALERT_STATUS = "getAlertStatus";
	public static final String 	GET_DATA = "getData";
	public static final String 	SEND_ALERT = "sendAlert";
	
	/**
	 * Feature states
	 */
	public static final String STATE_ERROR_1 = "error_1";
	public static final String STATE_WARNING_1 = "warning_1";
	public static final String STATE_WARNING_2 = "warning_2";
	public static final String STATE_ERROR_2 = "error_2";
	public static final String STATE_OK = "ok";
	public static final String STATE_WARNING = "warning";
	public static final String STATE_ERROR = "error";
	
	/**
	 * Table's names
	 */
	public static final String COMPONENSTS_TABLE = "dim_components";
	public static final String MACHINES_TABLE = "dim_machines";
	public static final String PLANTS_TABLE = "dim_plants";
	public static final String ENTERPRISES_TABLE = "dim_enterprises";
	
	/**
	 * Joins
	 */
	public static final String components_join_machines = "`dim_components`.`id_parent_instance` = `dim_machines`.`id`";
	
	/**
	 * Select(ions)
	 */
	public static final String select_components_and_machine_id_names = "`dim_components`.`id` as `component_id`, `dim_components`.`name` as `component_name`, `dim_machines`.`id` as `machine_id`, `dim_machines`.`name` as `machine_name`";
	public static final String select_machines_id_name_value_id_parent = "`dim_machines`.`id`, `dim_machines`.`name`, `dim_machines`.`value`, `dim_machines`.`id_parent_instance`";
	
	
	/**
	 * Dimensions names and IDs
	 */
	public static final int COMPONENT_DIMENSION_ID = 4;
	public static final String COMPONENT_DIMENSION_NAME = "Component";
	
	public static final int MACHINE_DIMENSION_ID = 3;
	public static final String MACHINE_DIMENSION_NAME = "Machine";
	
	public static final int PLANT_DIMENSION_ID = 2;
	public static final String PLANT_DIMENSION_NAME = "Plant";
	
	public static final int ENTERPRISE_DIMENSION_ID = 1;
	public static final String ENTERPRISE_DIMENSION_NAME = "Enterprise";
	
	/**
	 * Send alert interval types
	 */
	public static final String MINUTES_INTERVAL = "minutes";
	public static final String HOURS_INTERVEL = "hours";
	public static final String DAY_INTERVAL = "days";
	
	/**
	 * Send Alert External API to call for xml
	 */
	public static final String LOCAL_API = "http://localhost:8080/state-detection/requests/service/mandrinoAlert";
	public static final String HYLASOFT_API = "http://85.18.110.75/api/MandrinoAlert";
	
	
}
