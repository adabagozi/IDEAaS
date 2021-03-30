package it.unibs.bodai.ideaas.state_detection.webservice;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.sun.xml.fastinfoset.sax.Features;

import api_responses.GetAlertStatusResponse;
import api_responses.GetDataResponse;
import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.SendAlert;
import send_alert_objects.FspDataAndState;
import statedetection.IStateDetectionBusinessLogic;
import statedetection.StateDetectionFactory;
import utils.CheckBoundaries;
import utils.Consts;
import utils.DateTime;
import utils.DateUtils;
import utils.FeatureLimits;
import utils.FeatureSpacesUtils;
import xml_models.GetAlertStatusResponseXML;
import xml_models.GetDataInputFilter;
import xml_models.GetDataRespXML;
import xml_models.SendAlertResponseXML;

/**
 * @author Ada Bagozi, Naim Xhani
 *
 */
@Path("service")
public class StateDetection {
	//	private static final Logger LOG = Logger.getLogger(null);

	private IDAO dao;
	private DateUtils dateUtils;
	private IStateDetectionBusinessLogic stateDetection;
	protected StateDetection(IDAO dao) {
		this.dao = dao;

	}

	public StateDetection() throws IOException {
		this(DAOFactory.getDAOFactory().getDAO());
		this.dateUtils = new DateUtils();
		this.stateDetection = StateDetectionFactory.getStateDetectionFactory().getStateDetectionBusinessLogic();
	}

	@POST
	@Path("/sendAlert")
	@Produces(MediaType.APPLICATION_XML)	
	public String sendAlertResponse(@FormParam("interval_type") String interval_type, @FormParam("interval_length") String interval, @FormParam("end_date") String end_date) {
		//Get collection name from end date
		
		String[] input_date = end_date.split(" ");
		String date = input_date[0];
		String[] date_parts = date.split("/");

		FspDataAndState fspDataAndState = new FspDataAndState();

		ArrayList<DimensionInstance> systemDimensions = this.dao.readSystemDimensions();
		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
		ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();
		
		Date b = DateTime.dateTimePickerToISO(end_date);
		Date a = DateTime.sendAlertStartDate(interval_type, Integer.valueOf(interval), b);
		Date t = DateTime.addMinuteInterval(a);

		int i = 1;
		while(a.before(b)) {
			
			Collection<Measure> dimensions_measures_minute = this.stateDetection.updatedSendAlertQuery(date_parts[2] + "_" + date_parts[1], a, t, systemDimensions);
			CheckBoundaries.featureBoundaries(features_mysql, dimensions_measures_minute);			
			fspDataAndState.featureDataAndFeatureSpace(feature_spaces, dimensions_measures_minute, systemDimensions);
						
//			long start_send_alert_service = System.currentTimeMillis();
			a = t;
			t = DateTime.addMinuteInterval(a);
			
//			i++;
//			System.out.println("Interval number: " + i);
//			long end_send_alert_service = System.currentTimeMillis();
//			System.out.println("Total time seconds: " + TimeUnit.MILLISECONDS.toSeconds(end_send_alert_service - start_send_alert_service) + " Total time in milliseconds: " + TimeUnit.MILLISECONDS.toMillis(end_send_alert_service - start_send_alert_service));
		
		}

		return "Send Alert completed";

	}
	
	@POST
	@Path("/mandrinoAlert")
	@Produces(MediaType.APPLICATION_XML)
	public String getMandrinoAlert(InputStream input) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(input);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
		String response_string = "";
		String ln = "";
		while((ln = bufferedReader.readLine()) != null) {
			response_string += ln + "\n";
		}
		
		return response_string;
	}
	
	@POST
	@Path("/getData")
	@Produces(MediaType.APPLICATION_XML)
	public GetDataRespXML getDataResponse(GetDataInputFilter filter) throws IOException {
		String timestampStart = filter.getStartDate();
		String timestampEnd = filter.getEndDate();

		LocalDateTime startDateTime = this.dateUtils.getLocalDateTime(timestampStart);
		LocalDateTime endDateTime = this.dateUtils.getLocalDateTime(timestampEnd);
		
		ArrayList<DimensionInstance> dimensionInstances = filter.getMonitoredSystem().getDimensionInstances();
		
		String collectionName = this.dateUtils.getCollectionName(startDateTime);
		Collection<Measure> measures = this.dao.readGetDataMeasures(collectionName, dimensionInstances, startDateTime, endDateTime);
		
		ArrayList<FeatureSpace> featureSpaces = this.dao.readAllFeatureSpaces();
		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
		
		Collection<Measure> measures_checked = CheckBoundaries.featureBoundaries(features_mysql, measures);
		GetDataRespXML getDataLogicTest = GetDataResponse.get_xml(dimensionInstances, featureSpaces, features_mysql, measures_checked);	
		
		return getDataLogicTest;

	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/getAlertStatus")
	public GetAlertStatusResponseXML getAlertStatusResponse() throws IOException {
		ArrayList<DimensionInstance> componentInstances = this.dao.readDimensioninstaces();
		ArrayList<DimensionInstance> systemDimensions = this.dao.readSystemDimensions();
		
		ArrayList<FeatureSpace> featureSpaces = this.dao.readAllFeatureSpaces();
		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
		
		String collection = this.dao.getLastMongoCollection();
		Collection<Measure> readGetAlertStatusMeasures = this.dao.readGetAlertStatusMeasures(collection, systemDimensions);
		
		System.out.println("Numero di misure trovate: " + readGetAlertStatusMeasures.size());
		
//		Collection<Measure> measures = this.dao.readMeasuresSD(collection, systemDimensions, null, null, true);
//		Collection<Measure> measures_checked = CheckBoundaries.featureBoundaries(features_mysql, measures);

//		GetAlertStatusResponseXML getAlertStatusResponse = GetAlertStatusResponse.getXML(systemDimensions, featureSpaces, measures_checked);
//
//		return getAlertStatusResponse;
		
		return null;
		
	}


	/**
	 * **********************
	 * 		Tests 
	 * **********************
	 * @throws IOException
	 */
	//	public GetDataRespXML getDataResponseTest(GetDataInputFilter filter, int experiment_number) throws IOException {
	//		Date data_inizio_test = new Date(System.currentTimeMillis());
	//		long start_total_time = System.currentTimeMillis();
	//		String timestamp_start = filter.getStartDate();
	//		String collection_name = timestamp_start.substring(0, 4) + "_" + timestamp_start.substring(5, 7);
	//
	//		String timestamp_end = filter.getEndDate();
	//		ArrayList<DimensionInstance> dimensionInstances = filter.getMonitoredSystem().getDimensionInstances();
	//
	//		long start_extraction_time = System.currentTimeMillis();
	//
	//		Collection<org.bson.Document> docs = this.stateDetection.getDataMeasures(timestamp_start, timestamp_end, dimensionInstances);
	//		int num_files = docs.size();
	//		long start_transform_time = System.currentTimeMillis();
	//		ArrayList<Measure> measures = new ArrayList<>();
	//		for(org.bson.Document d : docs) {
	//			Measure m = new Measure();
	//			m.setFromDocument(d);
	//			measures.add(m);
	//		}
	//
	//		ArrayList<FeatureSpace> featureSpaces = this.dao.readAllFeatureSpaces();
	//		ArrayList<Feature> features_mysql = this.dao.readAllAndOnlyFeatures();
	//		Collection<Measure> measures_checked = CheckBoundaries.featureBoundaries(features_mysql, measures);
	//
	//		long start_xml_generation = System.currentTimeMillis();
	//		GetDataRespXML getDataLogicTest = GetDataResponse.get_xml(dimensionInstances, featureSpaces, features_mysql, measures_checked);
	//
	//		long end_total_time = System.currentTimeMillis();
	//		Date data_fine_test = new Date(System.currentTimeMillis());		
	//
	//		long count_collection = this.dao.countCollection(collection_name);
	//		if(count_collection!=0) {
	//			GetDataTiming getDataTiming = new GetDataTiming("", data_inizio_test, data_fine_test, end_total_time-start_total_time, start_transform_time-start_extraction_time, start_xml_generation-start_transform_time, end_total_time-start_xml_generation, collection_name, num_files, experiment_number);
	//			this.dao.insertGetDataTiming(getDataTiming);
	//		}
	//
	//		return getDataLogicTest;
	//
	//	}
	//	@GET
	//	@Path("/testCycleGetData/{day}/{experiment_number}")
	//	@Produces(MediaType.APPLICATION_XML)
	//	public void testCycleGetData(@PathParam("day") String day, @PathParam("experiment_number") String experiment_number) throws IOException {
	//
	//		String[] years = {"2016"};
	//		String[] month_days = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28"};
	//		ArrayList<GetDataInputFilter> filters = new ArrayList<>();
	//		for(int i = 0; i < years.length; i++) {
	//			//				for(int j = 6; j < 11; j++) {
	//			//for(int k = 0; k < 28; k++) {
	//			GetDataInputFilter getDataInputFilter = new GetDataInputFilter(years[i] + "-" + "10" + "-" + day + "T14:00:55Z", years[i] + "-" + "10" + "-" + day + "T15:00:55Z", new MonitoredSystem("Azienda 1", "Stabilimento 1", "101170", "Unit 1.0"));
	//			filters.add(getDataInputFilter);
	//			//}
	//			//				}
	//		}
	//
	//		int experiment = Integer.valueOf(experiment_number);
	//
	//		for(GetDataInputFilter filter : filters) {
	//			getDataResponseTest(filter, experiment);
	//		}
	//	}
	//
	//
	//

	//	@Path("/testGetAlertStatus")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	@GET
	//	public String testTimingGetAlertStatus() throws IOException {
	//		ArrayList<DimensionInstance> componentInstances = this.dao.readDimensioninstaces();
	//		ArrayList<BasicDBObject> whereQuery = this.stateDetection.testTimingGetAlertStatus(componentInstances);
	//		String collection_name = this.dao.getLastMongoCollection();
	//		StateDetectionTiming testGetAlertStatusTiming = this.dao.testGetAlertStatusTiming(collection_name, whereQuery);
	//		testGetAlertStatusTiming.setApi_name("GetAlertStatus");
	//		this.dao.insertStateDetectionTiming(testGetAlertStatusTiming);
	//		return "";
	//	}
	//	
	//	@Path("/testGetData")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	@GET
	//	public String testTimingGetData(GetDataInputFilter filter) {
	//		//@Context UriInfo info
	//		//String from = info.getQueryParameters().getFirst("from");
	//		//return "Get is called, from : " + from;
	//		String timestamp_start = filter.getStartDate();
	//		System.out.println("Timestamp start: " + timestamp_start);
	//		String timestamp_end = filter.getEndDate();
	//		ArrayList<DimensionInstance> dimensionInstances = filter.getMonitoredSystem().getDimensionInstances();
	//		StateDetectionTiming testGetDataTiming = this.stateDetection.testGetDataTiming(timestamp_start, timestamp_end, dimensionInstances);
	//		
	//		this.dao.insertStateDetectionTiming(testGetDataTiming);
	//		
	//		return "hello world";
	//	}
	//	
	//	@Path("/testSendAlert")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	@GET
	//	public String testTimingSendAlert(@Context UriInfo info) {
	//		String from = info.getQueryParameters().getFirst("from");
	//		return "Get is called, from : " + from;
	//	}
	//	
	//	@Path("/combinedAnnotations")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@GET
	//	public Person getAccount() {
	//		Person p = new Person("Biti", 2);
	//		return p;
	//	}
	//	
	//	@POST
	//	@Path("/testSendAlert")
	//	@Produces(MediaType.APPLICATION_XML)	public ArrayList<SendAlertResponseXML> sendAlertResponse(@FormParam("start_date") String start_date, @FormParam("end_date") String end_date) throws ParseException {
	//		System.out.println("Start date: " + start_date + " End date: " + end_date);
	//		String[] dateParts = start_date.split(" ");
	//		String date = dateParts[0];
	//		String[] date_splitted = date.split("/");
	//		String collection_name = date_splitted[2] + "_" + date_splitted[1];
	//		
	//		Date start_date_iso = DateTimePickerISO.dateTimePickerToISO(start_date);
	//		Date end_date_iso = DateTimePickerISO.dateTimePickerToISO(end_date);
	//		
	//		ArrayList<Feature> features = this.dao.readAllAndOnlyFeatures();
	//		ArrayList<FeatureSpace> feature_spaces = this.dao.readAllFeatureSpaces();
	//		Collection<Measure> measures = this.dao.sendAlertMeasures(collection_name, start_date_iso, end_date_iso);
	//
	//		System.out.println("Measures found: " + measures.size());
	//		return SendAlertResponse.build_send_alert_xml(measures);
	//
	//	}


	//String[] collections = {"2016_11"};
	//, "2016_07", "2016_06", "2016_05", "2016_04", "2016_03", "2016_02", "2016_01", "2015_12", "2015_11", "2015_10", "2015_09", "2015_08", "2015_07", "2015_06", "2015_05"

	String[] collections = {"2016_11", "2016_10", "2016_09", "2016_08", "2016_07", "2016_06", "2016_05", "2016_04", "2016_03", "2016_02", "2016_01", "2015_12", "2015_11", "2015_10", "2015_09", "2015_08", "2015_07", "2015_06", "2015_05"};
	String[] machines = {"101143", "101185", "101170"};
	String[] unit_instances = {"Unit 1.0", "Unit 2.0", "Unit 3.0"};
	String[] indexes = {"features"};
	//	String[] features = {"Carico mandrino", "Numero di giri mandrino", "Corrente asse X", "Corrente asse Y", "Corrente asse Z", "Velocità asse X", "Velocità asse Y", "Velocità asse Z"};
	//	String[] features = {"Carico", "NumeroGiri", "CorrenteX", "CorrenteY", "CorrenteZ", "VelocityX", "VelocityY", "VelocityZ"};
	String[] features = {"Carico", "NumeroGiri"};

//	@GET
//	@Path("/getFeatureLimits")
//	@Produces(MediaType.TEXT_PLAIN)	
//	public String getAllLimits() {
//		//	public void getAllLimits() {
//		MongoDatabase db = this.dao.getMongoConnection();
//		//		return FeatureLimits.getCollections(collections, machines, unit_instances,db).toString();
//		return FeatureLimits.getCollections(collections, machines, db).toString();
//	}
//
//
//	@GET
//	@Path("/getIndexes")
//	public void getIndexes() {
//		this.dao.addIndex(collections, indexes);
//	}
//
//	@GET
//	@Path("/deleteIndexes") 
//	public void dropIndexes() {
//		this.dao.dropAllIndexes(collections);
//	}
//
//	@GET
//	@Path("/addIndexes")
//	public void addIndexes(){
//		this.dao.addIndex(collections, indexes);
//	}
	

}
