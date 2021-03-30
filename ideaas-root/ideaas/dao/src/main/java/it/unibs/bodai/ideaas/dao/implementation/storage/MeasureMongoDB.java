package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.ParameterInstance;

/**
 * @author Ada Bagozi
 *
 */
public class MeasureMongoDB extends Measure{
	private static final Logger LOG = Logger.getLogger(MeasureMongoDB.class.getName());

	private static final String HOURS = "hours";
	private static final String MINUTES = "minutes";
	private static final String SECONDS = "seconds";
// <<<<<<< HEAD
//
// =======
//
// >>>>>>> ideaas
	public static final int COMPONENT_DIMENSION_ID = 4;
	public static final int MACHINE_DIMENSION_ID = 3;


	/**
	 * @param id
	 * @param timestamp
	 * @param state
	 * @param featuresMeasure
	 * @param dimensions
	 * @param parameters
	 */
	public MeasureMongoDB(String id, Date timestamp, String status,  HashMap<Integer, FeatureData> dataRecords, Collection<DimensionInstance> dimensionInstances, Collection<ParameterInstance> parameterInstances) {
		super(id, timestamp, status, dataRecords, dimensionInstances, parameterInstances);
		// TODO Auto-generated constructor stub
	}

	public MeasureMongoDB() {
		super();
		// TODO Auto-generated constructor stub
	}



	/**
	 * Function to build the query
	 * @param step
	 * @param utensileID
	 * @param partProgram
	 * @param mode
	 * @param macchinaID
	 * @param componenteID
	 * @param datetime
	 * @param intervalType
	 * @param intervalValue
	 * @return
	 */

	//TODO add query on context or context parameters
	public BasicDBObject buildGenericQuery(String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, LocalDateTime dateTimeTwo, boolean increase){
		BasicDBObject whereQuery = new BasicDBObject();
		ArrayList<BasicDBObject> queries = new ArrayList<>() ;

		BasicDBObject query;

		String dimensionKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_DIMENSION_ID;
		String instanceKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_INSTANCE_ID;

		//TODO rendere geberuca questa query passando un'insieme di istanze

		if(!macchinaID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "3");
			query.put(instanceKeyID, String.format("%s", macchinaID));
			queries.add(query);
		}

		if(!componenteID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "4");
			query.put(instanceKeyID, String.format("%s", componenteID));
			queries.add(query);

		}
		if(!utensileID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "6");
			query.put(instanceKeyID, String.format("%s", utensileID));
			queries.add(query);
		}

		if(!partProgram.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "5");
			query.put(instanceKeyID, partProgram);
			queries.add(query);
		}

		if(!mode.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "7");
			query.put(instanceKeyID, String.format("%s", mode));
			queries.add(query);
		}

		query= new BasicDBObject();

		if(datetime != null) {

			Instant instant =  datetime.atZone(ZoneId.of("Z")).toInstant();

			Date date = Date.from(instant);

			BasicDBObject dateQuery = new BasicDBObject();
			if (increase) {
				dateQuery.put("$gte", date);
			}else {

				dateQuery.put("$lt", date);
			}
			if (dateTimeTwo != null) {
				Instant instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
				Date dateTwo = Date.from(instantTwo);
				if (increase) {
					dateQuery.put("$lt", dateTwo);
				}else {
					dateQuery.put("$gte", dateTwo);
				}
			}
			query.put(KEY_TIMESTAMP, dateQuery);
			queries.add(query);
		}
		whereQuery.put("$and", queries);
//		LOG.info("query " + whereQuery.toString());

		return whereQuery;
	}


	//TODO add query on context or context parameters
	public BasicDBObject buildQuery(String step, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, String intervalType, int intervalValue){
		BasicDBObject whereQuery = new BasicDBObject();
		ArrayList<BasicDBObject> queries = new ArrayList<>() ;

		BasicDBObject query;

		String dimensionKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_DIMENSION_ID;
		String instanceKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_INSTANCE_ID;

		if(!macchinaID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "3");
			query.put(instanceKeyID, String.format("%s", macchinaID));
			queries.add(query);
		}

		if(!componenteID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "4");
			query.put(instanceKeyID, String.format("%s", componenteID));
			queries.add(query);

		}
		if(!utensileID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "6");
			query.put(instanceKeyID, String.format("%s", utensileID));
			queries.add(query);
		}

		if(!partProgram.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "5");
			query.put(instanceKeyID, partProgram);
			queries.add(query);
		}

		if(!mode.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "7");
			query.put(instanceKeyID, String.format("%s", mode));
			queries.add(query);
		}


		if(datetime != null) {
			query= new BasicDBObject();

			LocalDateTime localDateTwo = null;
			Instant instant = null;
			Instant instantTwo = null;
			if(intervalType.equalsIgnoreCase(HOURS)){
				localDateTwo = datetime.plusHours(intervalValue);
				instant = datetime.atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.atZone(ZoneId.of("Z")).toInstant();

			} else if(intervalType.equalsIgnoreCase(MINUTES)){
				localDateTwo = datetime.plusMinutes(intervalValue);

				LOG.severe("DATETIME: "+datetime.toString());
				LOG.severe("localDateTime: "+localDateTwo.toString());

				LocalDateTime localDateTime = LocalDateTime.of(datetime.getYear(), datetime.getMonth(), datetime.getDayOfMonth(), datetime.getHour(), datetime.getMinute(), 0, 0);
				instant = localDateTime.atZone(ZoneId.of("Z")).toInstant();

				LocalDateTime localDateTimeTwo = LocalDateTime.of(localDateTwo.getYear(), localDateTwo.getMonth(), localDateTwo.getDayOfMonth(), localDateTwo.getHour(), localDateTwo.getMinute(), 0, 0);
				instantTwo = localDateTimeTwo.atZone(ZoneId.of("Z")).toInstant();

				instant = datetime.atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.atZone(ZoneId.of("Z")).toInstant();

			} else if(intervalType.equalsIgnoreCase(SECONDS)){
				localDateTwo = datetime.plusSeconds(intervalValue);
				instant = datetime.atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.atZone(ZoneId.of("Z")).toInstant();

			}else {
				localDateTwo = datetime.plusDays(1);
				instant = datetime.toLocalDate().atStartOfDay().atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.toLocalDate().atStartOfDay().atZone(ZoneId.of("Z")).toInstant();
			}

			Date date = Date.from(instant);
			Date dateTwo = Date.from(instantTwo);

			BasicDBObject dateQuery = new BasicDBObject();
			dateQuery.put("$gte", date);
			dateQuery.put("$lt", dateTwo);
			query.put(KEY_TIMESTAMP, dateQuery);
			queries.add(query);
		}
		whereQuery.put("$and", queries);
//		LOG.info("query " + whereQuery.toString());

		return whereQuery;
	}

	//TODO add query on context or context parameters
	public BasicDBObject buildQueryTime(LocalDateTime datetime, String intervalType, int intervalValue){
		BasicDBObject whereQuery = new BasicDBObject();
		ArrayList<BasicDBObject> queries = new ArrayList<>() ;

		BasicDBObject query;

		if(datetime != null) {
			query= new BasicDBObject();

			LocalDateTime localDateTwo = null;
			Instant instant = null;
			Instant instantTwo = null;
			if(intervalType.equalsIgnoreCase(HOURS)){
				localDateTwo = datetime.minusHours(intervalValue);
				instant = datetime.atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.atZone(ZoneId.of("Z")).toInstant();

			} else if(intervalType.equalsIgnoreCase(MINUTES)){
				localDateTwo = datetime.minusMinutes(intervalValue);
				instant = datetime.atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.atZone(ZoneId.of("Z")).toInstant();

			} else if(intervalType.equalsIgnoreCase(SECONDS)){
				localDateTwo = datetime.minusSeconds(intervalValue);
				instant = datetime.atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.atZone(ZoneId.of("Z")).toInstant();

			}else {
				localDateTwo = datetime.minusDays(1);
				instant = datetime.toLocalDate().atStartOfDay().atZone(ZoneId.of("Z")).toInstant();
				instantTwo = localDateTwo.toLocalDate().atStartOfDay().atZone(ZoneId.of("Z")).toInstant();
			}

			Date date = Date.from(instant);
			Date dateTwo = Date.from(instantTwo);

			BasicDBObject dateQuery = new BasicDBObject();
			dateQuery.put("$gte", dateTwo);
			dateQuery.put("$lt", date);
			query.put(KEY_TIMESTAMP, dateQuery);
			queries.add(query);
		}
		whereQuery.put("$and", queries);
		//			LOG.info("query " + whereQuery.toString());

		return whereQuery;
	}

	public static boolean insertDocuments(MongoDatabase database, String collection, List<Document> documents) {
		MongoCollection<Document> collezione = database.getCollection(collection);
		try {
			collezione.insertMany(documents);
			return true;
		} catch (NullPointerException e) {
			LOG.severe(e.getMessage());
			e.getStackTrace();
		}
		return false;
	}
	public static boolean insertDocument(MongoDatabase database, String collection, Document document) {
		MongoCollection<Document> collezione = database.getCollection(collection);
		try {
			collezione.insertOne(document);//.insertMany(documents);
			return true;
		} catch (NullPointerException e) {
			LOG.severe(e.getMessage());
			e.getStackTrace();
		}
		return false;
	}
	public static boolean insertMeasures(MongoDatabase database, String collection, List<Measure> measures) {
		MongoCollection<Document> collezione = database.getCollection(collection);
		List<Document> documents = new ArrayList<>();

		//TODO Valutare se parallelStream è conveniente
		//TODO @Ada parallelStream non crea documents correttamente, map non funziona e forEach spesso inserisce dei null
		measures.stream().forEach(m -> {
			if(m !=null ) {
				Document mDoc = m.toDocument();
				if (mDoc != null) {
					documents.add(m.toDocument());
				}	else {

					System.out.println("MEasure DOC null");
				}
			}else {
				System.out.println("MEasure null");
			}
		});
		try {
			collezione.insertMany(documents);
			return true;
		} catch (Exception e) {
//			System.out.println(documents.toString());
			LOG.severe(e.getMessage());
			e.getStackTrace();
		}
		return false;
	}

	
	public static boolean insertData(MongoDatabase database, String collection, List<Document> documents) {
		MongoCollection<Document> collezione = database.getCollection(collection);
//		List<Document> documents = new ArrayList<>();

		//TODO Valutare se parallelStream è conveniente
		//TODO @Ada parallelStream non crea documents correttamente, map non funziona e forEach spesso inserisce dei null
//		measures.stream().forEach(m -> {
//			if(m !=null ) {
//				Document mDoc = m.toDocument();
//				if (mDoc != null) {
//					documents.add(m.toDocument());
//				}	else {
//
//					System.out.println("MEasure DOC null");
//				}
//			}else {
//				System.out.println("MEasure null");
//			}
//		});
		try {
			collezione.insertMany(documents);
			return true;
		} catch (Exception e) {
//			System.out.println(documents.toString());
			LOG.severe(e.getMessage());
			e.getStackTrace();
		}
		return false;
	}
	
	
	/**
	 * @author Naim
	 */
	public BasicDBObject buildQueryID(String id){
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id", new ObjectId(id));
		return whereQuery;
	}

	public BasicDBObject buildQueryFromTo(String macchinaID, String componenteID,
			LocalDateTime datetimeStart, LocalDateTime datetimeEnd) {
		BasicDBObject whereQuery = new BasicDBObject();
		ArrayList<BasicDBObject> queries = new ArrayList<>() ;

		BasicDBObject query;

		String dimensionKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_DIMENSION_ID;
		String instanceKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_INSTANCE_ID;

		if(!macchinaID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "3");
			query.put(instanceKeyID, String.format("%s", macchinaID));
			queries.add(query);
		}

		if(!componenteID.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "4");
			query.put(instanceKeyID, String.format("%s", componenteID));
			queries.add(query);

		}

		if(datetimeStart != null && datetimeEnd != null) {
			query= new BasicDBObject();
			Instant instant = datetimeStart.atZone(ZoneId.of("Z")).toInstant();
			Instant instantTwo = datetimeEnd.atZone(ZoneId.of("Z")).toInstant();

			Date date = Date.from(instant);
			Date dateTwo = Date.from(instantTwo);

			BasicDBObject dateQuery = new BasicDBObject();
			dateQuery.put("$gte", date);
			dateQuery.put("$lt", dateTwo);
			query.put(KEY_TIMESTAMP, dateQuery);
			queries.add(query);
		}
		whereQuery.put("$and", queries);

		//		LOG.info("query " + whereQuery.toString());

		return whereQuery;
	}

	public BasicDBObject buildQueryGetData(ArrayList<DimensionInstance> dimensionInsances, LocalDateTime datetimeStart, LocalDateTime datetimeEnd) {
		BasicDBObject whereQuery = new BasicDBObject();
		ArrayList<BasicDBObject> queries = new ArrayList<>() ;

		BasicDBObject query;

		String dimensionKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_DIMENSION_ID;
		String instanceKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_INSTANCE_ID;

		for(DimensionInstance dimensionInstance : dimensionInsances) {
			query= new BasicDBObject();
			if(dimensionInstance.getDimension().getId() == 3) {
				query.put(dimensionKeyID, "3");
				query.put(instanceKeyID, String.format("%s", dimensionInstance.getInstanceId()));
				queries.add(query);
			} else if (dimensionInstance.getDimension().getId() == 4) {
				query.put(dimensionKeyID, "4");
				query.put(instanceKeyID, String.format("%s", dimensionInstance.getInstanceId()));
				queries.add(query);
			}
		}

		if(datetimeStart != null && datetimeEnd != null) {
			query= new BasicDBObject();
			Instant instant = datetimeStart.atZone(ZoneId.of("Z")).toInstant();
			Instant instantTwo = datetimeEnd.atZone(ZoneId.of("Z")).toInstant();

			Date dateStart = Date.from(instant);
			Date dateEnd = Date.from(instantTwo);

			BasicDBObject dateQuery = new BasicDBObject();
			dateQuery.put("$gte", dateStart);
			dateQuery.put("$lt", dateEnd);
			query.put(KEY_TIMESTAMP, dateQuery);
			queries.add(query);
		}

		whereQuery.put("$and", queries);

		//		LOG.info("query " + whereQuery.toString());

		return whereQuery;

	}

	public BasicDBObject buildQueryGetAlertStatus(ArrayList<DimensionInstance> dimensionInsances) {
		BasicDBObject whereQuery = new BasicDBObject();

		for(DimensionInstance dimensionInstance : dimensionInsances) {
			String componentInstanceValue = dimensionInstance.getInstanceId();
			String manchineInstanceValue = dimensionInstance.getParentInstance().getInstanceId();

			BasicDBObject componentQuery = new BasicDBObject("dimensions.instance_id", componentInstanceValue);
			BasicDBObject machineQuery = new BasicDBObject("dimensions.instance_id", manchineInstanceValue);
			BasicDBList and = new BasicDBList();
			and.add(componentQuery);
			and.add(machineQuery);
			whereQuery = new BasicDBObject("$and", and);

		}

		LOG.info(whereQuery.toJson());

		return whereQuery;

	}

	public static BasicDBObject sendAlertMeasuresQuery(Date a, Date t,
			ArrayList<DimensionInstance> dimensionInstances) {
		// TODO Auto-generated method stub
		return null;
	}

}
