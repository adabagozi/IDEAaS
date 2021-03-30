package utils;

/**
 * @author ada
 *
 */
public class StaticVariables {
	
	public static final String URL_MAIL = "http://bagozi.it/sendMail.php";
	
	public static final boolean normalize = true;
	public static final boolean density = true;
	public static final int experiment_numebr = 1;
	public static final String no_indexes = "_no_indexes";
	public static final String indexes_dimensions = "_indexes_dimensions";
	public static final String indexes_all = "_indexes_all";
	public static final String anomalies = "_anomalies"; //anomalies are introduced in a collection with collection indexes on timestamp and dimensions
	public static final boolean introduce_anomalies = true;

	public static final String anomalies_soft = "_soft_2";
	public static final String anomalies_strong = "";
	
	
	public static final String HOURS = "hours"; 
	public static final String MINUTES = "minutes"; 
	public static final String SECONDS = "seconds";
	public static final String DAYS = "days";

	public static final boolean CHECK_PERC = true;	
	public static final int PERCENT_DATA_THRESHOLD = 1/100;
	public static final boolean CHECK_DENSITY = true;	
	
	public static final boolean SAVE_DATA_IN_SYNTHESES = false;
	
	public static final int KERNEL_RADIUS_FACTOR = 2;
	public static final double EPSILON = 0.000001;

	

	public static int TIME_WINDOW = 500;
	
	/**
	 * 0 - remove based on density
	 * 1 - remove based on age
	 * 2 - remove based on density and num data
	 * 3 - remove disabled
	 * 3 - remove more dense
	 */
	public static int SUM_DATA_VERSION = 0;

	public static String collectionNameExt = anomalies_strong;
}
