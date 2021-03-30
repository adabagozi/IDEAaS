package storage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.timing.StateDetectionTiming;
import utils.Consts;

public class MeasuresMongoDB {

	private IDAO dao;
	private MongoDatabase db;

	protected MeasuresMongoDB(IDAO dao) {
		this.dao = dao;
		db = dao.getMongoConnection();
	}

	public MeasuresMongoDB() throws IOException {
		this(DAOFactory.getDAOFactory().getDAO());
	}

	/**
	 * Get Alert Status Measures
	 * @param collection
	 * @param activeComponentsInstances
	 * @return
	 */
	public ArrayList<Document> currentSystemMeasures(ArrayList<DimensionInstance> activeComponentsInstances) {
		String collection_name = this.dao.getLastMongoCollection();
		ArrayList<Document> componentMeasures = new ArrayList<>();

		for(DimensionInstance componentInstance : activeComponentsInstances) {
			String componentInstanceValue = componentInstance.getInstanceId();
			String manchineInstanceValue = componentInstance.getParentInstance().getInstanceId();

			BasicDBObject componentQuery = new BasicDBObject("dimensions.instance_id", componentInstanceValue);
			BasicDBObject machineQuery = new BasicDBObject("dimensions.instance_id", manchineInstanceValue);
			BasicDBList and = new BasicDBList();
			and.add(componentQuery);
			and.add(machineQuery);
			BasicDBObject whereQuery = new BasicDBObject("$and", and);
			System.out.println("JSON: " + whereQuery.toJson());
			FindIterable<Document> lastMeasureDocument = db.getCollection(collection_name).find(whereQuery).limit(1).sort(new BasicDBObject("timestamp" , -1));
			MongoCursor<Document> iterator = lastMeasureDocument.iterator();
			while(iterator.hasNext()) {
				componentMeasures.add(iterator.next());
			}
		}
		return componentMeasures;
	}

	public Collection<Measure> updatedSendAlertMeasures(String collection_name, Date a, Date t, ArrayList<DimensionInstance> dims){

		Collection<Measure> dimensions_measures_in_minute = new ArrayList<>();
		Collection<Document> documents = new ArrayList<Document>();

		for(DimensionInstance d : dims) {
			BasicDBList and = new BasicDBList();

			BasicDBObject dateQuery = new BasicDBObject();
			dateQuery.put("$gte", a);
			dateQuery.put("$lt", t);

			BasicDBObject timestampQuery = new BasicDBObject();
			timestampQuery.put("timestamp", dateQuery);
			and.add(timestampQuery);

//			System.out.println("Component Instance ID: " + component_instance + " Machine Instance ID: " + machine_instance);
			BasicDBObject componentQuery = new BasicDBObject("dimensions.instance_id", d.getInstanceId());
			BasicDBObject machineQuery = new BasicDBObject("dimensions.instance_id", d.getParentInstance().getInstanceId());
			and.add(componentQuery);
			and.add(machineQuery);

			BasicDBObject whereQuery = new BasicDBObject("$and", and);
//			System.out.println(whereQuery.toJson());
			try (MongoCursor<Document> cursor = db.getCollection(collection_name).find(whereQuery).iterator()) {
				while (cursor.hasNext()) {
					documents.add(cursor.next());
				}
			}
		}
		
//		System.out.println("#Documents found: " + documents.size());
		
		for(Document d : documents) {
			Measure m = new Measure();
			m.setFromDocument(d);
//			System.out.println("Adding obj: " + m.getId() + " with timestamp: " + m.getTimestamp());
			dimensions_measures_in_minute.add(m);
		}
		return dimensions_measures_in_minute;
	}
	
	/**
	 * Test Timing Get Alert Status 
	 * @param collection
	 * @param activeComponentsInstances
	 * @return
	 */
	public ArrayList<BasicDBObject> testTimingGetAlertStatus(ArrayList<DimensionInstance> activeComponentsInstances) {
		ArrayList<BasicDBObject> whereQueries = new ArrayList<>();

		for(DimensionInstance componentInstance : activeComponentsInstances) {
			String componentInstanceValue = componentInstance.getInstanceId();
			String manchineInstanceValue = componentInstance.getParentInstance().getInstanceId();
			BasicDBObject componentQuery = new BasicDBObject("dimensions.instance_id", componentInstanceValue);
			BasicDBObject machineQuery = new BasicDBObject("dimensions.instance_id", manchineInstanceValue);
			BasicDBList and = new BasicDBList();
			and.add(componentQuery);
			and.add(machineQuery);
			BasicDBObject whereQuery = new BasicDBObject("$and", and);
			whereQueries.add(whereQuery);
		}
		return whereQueries;
	}

	public StateDetectionTiming testGetDataTiming(String start_date, String end_date, ArrayList<DimensionInstance> dimensionInstances) {
		SimpleDateFormat collection_year = new SimpleDateFormat("yyyy_MM");
		String collection_name = collection_year.format(start_date);
		//		System.out.println("Collection name: " + collection_name);

		BasicDBList and = new BasicDBList();
		BasicDBObject dateQuery = new BasicDBObject();
		dateQuery.put("$gte", start_date);
		dateQuery.put("$lte", end_date);
		BasicDBObject timestampQuery = new BasicDBObject();
		timestampQuery.put("timestamp", dateQuery);

		and.add(timestampQuery);

		for(DimensionInstance dim : dimensionInstances) {
			if(dim.getDimension().getId() == Consts.COMPONENT_DIMENSION_ID) {
				String component_instance = dim.getInstanceId();
				BasicDBObject componentQuery = new BasicDBObject("dimensions.instance_id", component_instance);
				and.add(componentQuery);
			} else if (dim.getDimension().getId() == Consts.MACHINE_DIMENSION_ID) {
				String machine_instance = dim.getInstanceId();
				BasicDBObject machineQuery = new BasicDBObject("dimensions.instance_id", machine_instance);
				and.add(machineQuery);
			}
		}

		if(!and.isEmpty()) {
			BasicDBObject whereQuery = new BasicDBObject("$and", and);

			FindIterable<Document> lastMeasureDocument = db.getCollection(collection_name).find(whereQuery);
			StateDetectionTiming stateDetectionTiming = new StateDetectionTiming("test Get Data", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 3000, 40000, 50000, 60000);
			return stateDetectionTiming;
		} else {
			System.err.println("No measures found");
			return null;
		}
	}

}
