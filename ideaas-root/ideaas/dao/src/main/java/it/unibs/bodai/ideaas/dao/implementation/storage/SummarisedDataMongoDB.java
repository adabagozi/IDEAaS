package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import it.unibs.bodai.ideaas.dao.model.Cluster;
import it.unibs.bodai.ideaas.dao.model.Context;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.dao.model.Synthesi;

/**
 * @author Ada Bagozi
 *
 */
public class SummarisedDataMongoDB extends SummarisedData{

	/**
	 * @param id
	 * @param timestamp
	 * @param timestampInitial
	 * @param timestampFinal
	 * @param stableSummarisedData
	 * @param previousSummarisedData
	 * @param dimensions
	 * @param parameters
	 * @param synthesis
	 * @param clusters
	 */
	public SummarisedDataMongoDB(String id, Date timestamp, Date timestampInitial, Date timestampFinal,
			SummarisedData stableSummarisedData, SummarisedData previousSummarisedData,
			Collection<DimensionInstance> dimensionInstances, Context context,  HashMap<Integer, Synthesi> synthesis,
			Collection<Cluster> clusters, FeatureSpace featureSpace) {
		super(id, timestamp, timestampInitial, timestampFinal, stableSummarisedData, previousSummarisedData, dimensionInstances, context, synthesis, clusters, featureSpace);
		// TODO Auto-generated constructor stub
	}

	public SummarisedDataMongoDB() {
		super();
		// TODO Auto-generated constructor stub
	}



	public Iterator<Document> readSummarisedData(MongoDatabase db, String collection, BasicDBObject whereQuery){
		String string = "";
		Collection <Document> objects = new ArrayList<>();
		FindIterable<Document> find = db.getCollection(collection).find(whereQuery);
		MongoCursor<Document> iterator = find.iterator();
		return iterator;
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
	public BasicDBObject buildQueryID(String id){ 
		 System.out.println(id);
		 BasicDBObject whereQuery = new BasicDBObject();
		 whereQuery.put(KEY_ID, new ObjectId(id));

		return whereQuery;
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
	public BasicDBObject buildQuery(FeatureSpace featureSpace, String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, Boolean stable, String intervalType, int intervalValue, int experimentNumber){
		String dimensionKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_DIMENSION_ID;
		String instanceKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_INSTANCE_ID;

		String featureSpaceID = KEY_FEATURE_SPACE + ".id";
		
		
		BasicDBObject whereQuery = new BasicDBObject();
		ArrayList<BasicDBObject> queries = new ArrayList<>() ;

		String idDimensionInstanceMachine = !idMachine.equalsIgnoreCase("-1") ? idMachine: "0";
		String idDimensionInstanceSpindle = !idSpindle.equalsIgnoreCase("-1") ? idSpindle: "0";
		String idDimensionInstanceTool = !idTool.equalsIgnoreCase("-1") ? idTool: "0";
		String idDimensionInstancePartProgram = !idPartProgram.equalsIgnoreCase("-1") ? idPartProgram: "0";
		String dimensionInstanceMode = !mode.equalsIgnoreCase("-1") ? mode: "0";

		BasicDBObject query;

		query= new BasicDBObject();
		query.put(featureSpaceID, String.format("%s", featureSpace.getId()));
		queries.add(query);
		if(stable){
			query= new BasicDBObject();
			BasicDBObject equalBoolean  = new BasicDBObject().append("$eq",  String.format("%s", stable));
			query.put(KEY_IS_STABLE, equalBoolean);
			queries.add(query);
		}
		
		if(experimentNumber>=0){
			query= new BasicDBObject();
			query.put(KEY_EXPERIMENT_NUMBER, String.format("%s", experimentNumber));
			queries.add(query);
		}
		
		query= new BasicDBObject();
		query.put(KEY_INTERVAL_TYPE, intervalType);
		queries.add(query);
		
		query= new BasicDBObject();
		query.put(KEY_INTERVAL_VALUE,  String.format("%s", intervalValue));
		queries.add(query);
		
		query = new BasicDBObject();
		query.put(dimensionKeyID, "3");
		query.put(instanceKeyID, String.format("%s", idDimensionInstanceMachine));
		queries.add(query);

		query= new BasicDBObject();
		query.put(dimensionKeyID, "4");
		query.put(instanceKeyID, String.format("%s", idDimensionInstanceSpindle));
		queries.add(query);

		if(!idTool.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "10");
			query.put(instanceKeyID, String.format("%s", idDimensionInstanceTool));
			queries.add(query);
		}
		if(!idPartProgram.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "11");
			query.put(instanceKeyID, String.format("%s", idDimensionInstancePartProgram));
			queries.add(query);
		}
		if(!mode.equalsIgnoreCase("-1")) {
			query= new BasicDBObject();
			query.put(dimensionKeyID, "12");
			query.put(instanceKeyID, String.format("%s", dimensionInstanceMode));
			queries.add(query);
		}

		whereQuery.put("$and", queries);


		return whereQuery;
	}
	
	//TODO add query on context or context parameters
		public BasicDBObject buildGenericQuery(FeatureSpace featureSpace, String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, LocalDateTime datetime, LocalDateTime dateTimeTwo, boolean increase,int experimentNumber){
			String dimensionKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_DIMENSION_ID;
			String instanceKeyID = KEY_DIMENSIONS + "." + DimensionInstance.KEY_INSTANCE_ID;

			String featureSpaceID = KEY_FEATURE_SPACE + ".id";
			
			
			BasicDBObject whereQuery = new BasicDBObject();
			ArrayList<BasicDBObject> queries = new ArrayList<>() ;

			String idDimensionInstanceMachine = !idMachine.equalsIgnoreCase("-1") ? idMachine: "0";
			String idDimensionInstanceSpindle = !idSpindle.equalsIgnoreCase("-1") ? idSpindle: "0";
			String idDimensionInstanceTool = !idTool.equalsIgnoreCase("-1") ? idTool: "0";
			String idDimensionInstancePartProgram = !idPartProgram.equalsIgnoreCase("-1") ? idPartProgram: "0";
			String dimensionInstanceMode = !mode.equalsIgnoreCase("-1") ? mode: "0";

			BasicDBObject query;

			query= new BasicDBObject();
			query.put(featureSpaceID, String.format("%s", featureSpace.getId()));
			queries.add(query);
//			if(stable){
//				query= new BasicDBObject();
//				BasicDBObject equalBoolean  = new BasicDBObject().append("$eq",  String.format("%s", stable));
//				query.put(KEY_IS_STABLE, equalBoolean);
//				queries.add(query);
//			}
//			
			if(experimentNumber>=0){
				query= new BasicDBObject();
				query.put(KEY_EXPERIMENT_NUMBER, String.format("%s", experimentNumber));
				queries.add(query);
			}
			
			if(!idMachine.equalsIgnoreCase("-1")) {
				query= new BasicDBObject();
				query.put(dimensionKeyID, "3");
				query.put(instanceKeyID, String.format("%s", idMachine));
				queries.add(query);
			}

			if(!idSpindle.equalsIgnoreCase("-1")) {
				query= new BasicDBObject();
				query.put(dimensionKeyID, "4");
				query.put(instanceKeyID, String.format("%s", idSpindle));
				queries.add(query);

			}
			if(!idTool.equalsIgnoreCase("-1")) {
				query= new BasicDBObject();
				query.put(dimensionKeyID, "6");
				query.put(instanceKeyID, String.format("%s", idTool));
				queries.add(query);
			}

			if(!idPartProgram.equalsIgnoreCase("-1")) {
				query= new BasicDBObject();
				query.put(dimensionKeyID, "5");
				query.put(instanceKeyID, idPartProgram);
				queries.add(query);
			}

			if(!mode.equalsIgnoreCase("-1")) {
				query= new BasicDBObject();
				query.put(dimensionKeyID, "7");
				query.put(instanceKeyID, String.format("%s", mode));
				queries.add(query);
			}
			
//			query = new BasicDBObject();
//			query.put(dimensionKeyID, "3");
//			query.put(instanceKeyID, String.format("%s", idDimensionInstanceMachine));
//			queries.add(query);
//
//			query= new BasicDBObject();
//			query.put(dimensionKeyID, "4");
//			query.put(instanceKeyID, String.format("%s", idDimensionInstanceSpindle));
//			queries.add(query);

//			query= new BasicDBObject();
//			query.put(dimensionKeyID, "6");
//			query.put(instanceKeyID, String.format("%s", idDimensionInstanceTool));
//			queries.add(query);
//
//			query= new BasicDBObject();
//			query.put(dimensionKeyID, "5");
//			query.put(instanceKeyID, String.format("%s", idDimensionInstancePartProgram));
//			queries.add(query);
//
//			query= new BasicDBObject();
//			query.put(dimensionKeyID, "7");
//			query.put(instanceKeyID, String.format("%s", dimensionInstanceMode));
		
			queries.add(query);
			
			query= new BasicDBObject();
			if(datetime != null) {
				Instant instant =  datetime.atZone(ZoneId.of("Z")).toInstant();
				Date date = Date.from(instant);
				
				BasicDBObject dateQuery= new BasicDBObject();
				
				if (increase) {
					dateQuery.put("$gte", date);
				}else {

					dateQuery.put("$lt", date);
				}

				query.put(KEY_TIMESTAMP_INITIAL, dateQuery);
				queries.add(query);
			}
			
			query = new BasicDBObject();
			if (dateTimeTwo != null) {
				Instant instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
				Date dateTwo = Date.from(instantTwo);
				BasicDBObject dateQuery= new BasicDBObject();
				
				if (increase) {
					dateQuery.put("$lt", dateTwo);
				}else {
					dateQuery.put("$gte", dateTwo);
				}
				
				query.put(KEY_TIMESTAMP_FINAL, dateQuery);
				queries.add(query);
			}
			
			whereQuery.put("$and", queries);


			return whereQuery;
		}

}
