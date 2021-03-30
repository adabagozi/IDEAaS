package it.unibs.bodai.ideaas.dao.implementation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;

import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.anomalies.DataSummarisationAnomalies;
import it.unibs.bodai.ideaas.dao.implementation.storage.TimingDataAcquisitionMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.TimingDataReadingMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.TimingDataSummarisationMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.DimensionInstanceMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.DimensionMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.FeatureMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.FeatureSpaceMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.GetDataTimingMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.MeasureCassandra;
import it.unibs.bodai.ideaas.dao.implementation.storage.MeasureMongoDB;
import it.unibs.bodai.ideaas.dao.implementation.storage.ParallelDataSummarisationTimingMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.SendAlertMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.StateDetectionTimingMySQL;
import it.unibs.bodai.ideaas.dao.implementation.storage.SummarisedDataMongoDB;
import it.unibs.bodai.ideaas.dao.model.Context;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.SendAlert;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;
import it.unibs.bodai.ideaas.dao.timing.TimingDataReading;
import it.unibs.bodai.ideaas.dao.timing.TimingDataSummarisation;
import it.unibs.bodai.ideaas.dao.timing.DataAcquistionAnomalies;
import it.unibs.bodai.ideaas.dao.timing.GetDataTiming;
import it.unibs.bodai.ideaas.dao.timing.ParallelDataSummarisationTiming;
import it.unibs.bodai.ideaas.dao.timing.StateDetectionTiming;
import it.unibs.bodai.ideaas.dao.utils.ManageDB;

/**
 * @author Ada Bagozi, Daniele Comincini, Gianmaria Micheli, Naim Xhani
 *
 */
public class DAOImplCassandra implements IDAO {
	//TODO iniziare a togliere i metodi superflui
	private static final Logger LOG = Logger.getLogger(DAOImplCassandra.class.getName());
	private ManageDB mDB;

	public DAOImplCassandra(String configFilename) throws IOException {
		mDB = new ManageDB(configFilename);
	}
	
	

	/**************************************************************************************
	 * READ METHODS
	 **************************************************************************************/
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readMeasures(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.time.LocalDateTime, java.time.LocalDateTime, boolean)
	 */
	public Optional<Iterable<Document>> readMeasures(String collection, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, LocalDateTime dateTimeTwo, boolean increase) {
		MeasureMongoDB measure = new MeasureMongoDB();
		BasicDBObject whereQuery = measure.buildGenericQuery(utensileID, partProgram, mode, macchinaID, componenteID, datetime, dateTimeTwo, increase);

		return this.readDocuments(collection, whereQuery); 
	}
	/* (non-Javadoc)
		 * @see it.unibs.bodai.ideaas.dao.IDAO#readMeasuresWithQuery(java.lang.String, com.mongodb.BasicDBObject)
		 */
		@Override
		
		public Collection<Measure> readMeasures(String collection, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, String intervalType, int intervalValue) {
			ManageDBCassandra dbCassandra = new ManageDBCassandra(mDB.getBigDataServer(), mDB.getBigDataDBName(), true);
			MeasureCassandra measureCassandra = new MeasureCassandra();
			measureCassandra.loadPartitions(mDB.getBigDataDBName(), dbCassandra.getSession());

			Collection<Measure> measures = new ArrayList<Measure>();
			int mID = Integer.parseInt(macchinaID);
			ArrayList<Integer> machineID = new ArrayList<Integer>();
			ArrayList<String> componentID = new ArrayList<String>();
			
			//se non ho la macchina, più partizioni
			if(mID == -1) {
				machineID.addAll(measureCassandra.getMachines());
			}else {
				machineID.add(mID);
			}
			
			//se non ho le componenti, più partizioni
			if(componenteID.equalsIgnoreCase("-1")) {
				componentID.addAll(measureCassandra.getComponents());
			} else {
				componentID.add(componenteID);
			}
			
			//controllo se tutto nello stesso giorno, altrimenti query multipla (più partizioni)
			LocalDateTime localDateTwo = measureCassandra.calcDateTwo(datetime, intervalType, intervalValue);
			
			ArrayList<LocalDateTime> datetimes = new ArrayList<LocalDateTime>();
			
			//se un giorno solo
			if((localDateTwo.getDayOfYear() == datetime.getDayOfYear()) ||
			   ((localDateTwo.getDayOfYear() == datetime.getDayOfYear()+1) &&
			    (localDateTwo.getHour() == 0) &&
			    (localDateTwo.getMinute() == 0)))
			{
				datetimes.add(datetime);
			}
			else
			{
				//primo giorno
				datetimes.add(datetime);
				
				//giorni intermedi e finale
				int differenceDays = localDateTwo.getDayOfYear() - datetime.getDayOfYear();
				
				LocalDateTime datetimeTemp = LocalDateTime.of(datetime.getYear(),
													 datetime.getMonth().getValue(), 
													 datetime.getDayOfMonth(),
													 0,
													 0);
				
				for(int i = 1; i <= differenceDays; i++)
				{
					LocalDateTime dayTemp = datetimeTemp.plusDays(i);
					
					Date dateTemp = Date.from(dayTemp.plusHours(10).atZone(ZoneId.systemDefault()).toInstant());
					if(measureCassandra.getDays().contains(dateTemp))
					{
						datetimes.add(dayTemp);
					}
				}
			}
			

			Statement query = measureCassandra.createOutputQuery(mDB.getBigDataDBName(), machineID, componentID, datetimes, intervalType, intervalValue);
			ResultSet result = dbCassandra.getSession().execute(query);
			
			while(!result.isExhausted())
			{
				measures.add(measureCassandra.fromRowToMeasure(result.one()));
			}
			return measures;
		}
		
	
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readMeasuresWithQuery(java.lang.String, com.mongodb.BasicDBObject)
	 */
	@Override
	public Optional<Iterable<Document>> readMeasuresWithQuery(String collection, String step, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, String intervalType, int intervalValue) {
		MeasureMongoDB measure = new MeasureMongoDB();
		BasicDBObject whereQuery = measure.buildQuery(step, utensileID, partProgram, mode, macchinaID, componenteID, datetime, intervalType, intervalValue);

		return this.readDocuments(collection, whereQuery); 
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#numMeasuresWithQuery(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.time.LocalDateTime, java.lang.String, int)
	 */
	public int numMeasuresWithQuery(String collection, String step, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, String intervalType, int intervalValue) {
		MeasureMongoDB measure = new MeasureMongoDB();
		BasicDBObject whereQuery = measure.buildQuery(step, utensileID, partProgram, mode, macchinaID, componenteID, datetime, intervalType, intervalValue);

		return this.numDocuments(collection, whereQuery); 
	}



	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readDataPoints(java.lang.String, int)
	 */
	@Override
	public Optional<Iterable<Document>> readDataPoints(String collection, int experimentNumber) {
		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put("experiment_number", experimentNumber);

		LOG.info("query " + whereQuery.toString());
		return this.readDocuments(collection, whereQuery);
	}



	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readSummarisedDatas(java.lang.String, it.unibs.bodai.ideaas.dao.model.FeatureSpace, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Optional<Iterable<Document>> readSummarisedDatas(String collection, FeatureSpace featureSpace,
			String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, String intervalType, int intervalValue, int experimentNumber) {
		SummarisedDataMongoDB summarisedData = new SummarisedDataMongoDB();
		BasicDBObject whereQuery = summarisedData.buildQuery(featureSpace, idMachine, idSpindle, idTool, mode, idPartProgram, false, intervalType, intervalValue, experimentNumber);
		LOG.severe("readSummarisedDatas: whereQuery: " + whereQuery.toJson());
		// TODO Auto-generated method stub
		return this.readDocuments(collection, whereQuery);
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readSummarisedDatas(java.lang.String, it.unibs.bodai.ideaas.dao.model.FeatureSpace, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Optional<Iterable<Document>> readSummarisedDatas(String collection, FeatureSpace featureSpace,
			String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, LocalDateTime datetime, LocalDateTime dateTimeTwo, boolean increase, int experimentNumber) {
		SummarisedDataMongoDB summarisedData = new SummarisedDataMongoDB();
		BasicDBObject whereQuery = summarisedData.buildGenericQuery(featureSpace, idMachine, idSpindle, idTool, mode, idPartProgram, datetime, dateTimeTwo, increase,  experimentNumber);
//		LOG.severe("readSummarisedDatas: whereQuery: " + whereQuery.toJson());
		// TODO Auto-generated method stub
		return this.readDocuments(collection, whereQuery);
	}

	public Optional<Iterable<Document>> readDocuments(String collection, BasicDBObject whereQuery) {
		return Optional.of(new Iterable<Document>() {

			@Override
			public Iterator<Document> iterator() {
				try {
					return new Iterator<Document>() {

						MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
						MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());
						Iterator<Document> docIterator = this.readDocumentWithQuery(database, collection, whereQuery);	
						@Override
						public boolean hasNext() {
							if (!docIterator.hasNext() && (client != null)) {
								client.close();
								//System.out.println("Connessione al client chiusa");
							}
							//LOG.info("readMeasuresWithQuery iterator has next is " + docIterator.hasNext());
							return docIterator.hasNext();
						}
						@Override
						public Document next() {
							return docIterator.next();
						}

						public Iterator<Document> readDocumentWithQuery(MongoDatabase db, String collection, BasicDBObject whereQuery){
							Collection <Document> objects = new ArrayList<>();
							FindIterable<Document> find = db.getCollection(collection).find(whereQuery);
							MongoCursor<Document> iterator = find.iterator();
							return iterator;
						}

					};
				} catch (Exception e) {
					e.printStackTrace();
					return new Iterator<Document>() {
						@Override
						public boolean hasNext() {
							return false;
						}
						@Override
						public Document next() {
							return null;
						}
					};
				} 
			}
		});
	}

	public int numDocuments(String collection, BasicDBObject whereQuery) {

		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());
		Collection <Document> objects = new ArrayList<>();
		return (int) database.getCollection(collection).count(whereQuery);

	}

	public Optional<Document> readDocument(String collection, BasicDBObject whereQuery) {
		String toPrint = "";
		try (MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort())){
			MongoDatabase database = client.getDatabase(mDB.getBigDataDBName()); 
			try {
				//				System.out.println("BDAOImpl -> readDocument -> "+database.getName());

				BasicDBObject sortObject = new BasicDBObject().append("_id", -1);

				Document first = database.getCollection(collection).find(Filters.eq("_id", new ObjectId("595945f90a9e3ea11ae6b22f"))).first();
				//						.find(whereQuery).sort(sortObject).limit(1);
				System.out.println(first.toString());
				return Optional.of(first);
				//				MongoCursor<Document> iterator = find.iterator();
				//				while (iterator.hasNext()) {
				//					Document object = iterator.next();
				//					System.out.println("BDAOImpl -> readDocument -> "+object.toString());
				//					return Optional.of(object);
				//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<Document> readDoc(String collection, BasicDBObject whereQuery) {
		try (MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort())){
			MongoDatabase database = client.getDatabase(mDB.getBigDataDBName()); 
			try {
				System.out.println("BDAOImpl -> readAllMeasures -> "+database.getName());

				BasicDBObject sortObject = new BasicDBObject().append("_id", -1);
				FindIterable<Document> find = database.getCollection(collection).find(whereQuery).sort(sortObject).limit(1);
				MongoCursor<Document> iterator = find.iterator();
				while (iterator.hasNext()) {
					Document object = iterator.next();
					return Optional.of(object);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readSummarisedDataById(java.lang.String)
	 */
	@Override
	public Optional<SummarisedData> readSummarisedDataById(String collection, String id) {
		SummarisedDataMongoDB summarisedData = new SummarisedDataMongoDB();
		BasicDBObject whereQuery = summarisedData.buildQueryID(id);
		System.out.println(whereQuery);

		Optional<Document> readDocument = this.readDocument(collection, whereQuery); 
		if(readDocument.isPresent()){
			System.out.println(readDocument.get());
			summarisedData.setFromDocument(readDocument.get());
			return Optional.of(summarisedData);
		}
		return Optional.empty();
	}



	@Override
	public ObjectId insertDocumentAndGetID(Document documentToInsert, String collectionName) {
		//TODO
		return null;
	}
	

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readSummarisedDataStable(java.lang.String, it.unibs.bodai.ideaas.dao.model.FeatureSpace, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<SummarisedData> readSummarisedData(String collection, FeatureSpace featureSpace, String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, Boolean stable, String intervalType, int intervalValue, int experimentNumber) {

		SummarisedDataMongoDB summarisedData = new SummarisedDataMongoDB();
		BasicDBObject whereQuery = summarisedData.buildQuery(featureSpace, idMachine, idSpindle, idTool, mode, idPartProgram, stable, intervalType, intervalValue, experimentNumber);
		LOG.severe("readSummarisedData: whereQuery: " + whereQuery.toJson().toString());

		Optional<Document> readedDoc = this.readDocument(collection, whereQuery); 
		if(readedDoc.isPresent()){
			//			LOG.severe("readSummarisedData: readedDoc: " + readedDoc.get().toJson());

			summarisedData.setFromDocument(readedDoc.get());
			return Optional.of(summarisedData);
		}else {
			LOG.severe("readSummarisedData: readedDoc: No document found");

		}
		return Optional.empty();
	}



	/* (non-Javadoc)
	 * @see it.unibs.bodai.bdao.BDAO#readFeatureSpace(int)
	 */
	@Override
	public Optional<FeatureSpace> readFeatureSpace(int featureSpaceID) {
		FeatureSpaceMySQL featureSpaceMySQL = new FeatureSpaceMySQL(featureSpaceID);
		try (Connection conn = mDB.connectMySqlModelDB()) {
			featureSpaceMySQL.fetchFeatureSpaceMySQL(conn, this);
			//            featureSpaceMySQL.stampaFeatureSpace();
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		FeatureSpace featureSpace = featureSpaceMySQL;
		return Optional.ofNullable(featureSpace);
	}

	/* (non-Javadoc)
	 * @see it.unibs.bodai.bdao.BDAO#readFeaturesByFeatureSpaceId(int)
	 */
	@Override
	public Collection<Feature> readFeaturesByFeatureSpaceId(int featureSpaceId) {
		Collection<Feature> features = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			features = FeatureMySQL.fetchFeaturesMySQLByFeatureSpace(conn, featureSpaceId);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return features;
	}




	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readFeatures()
	 */
	@Override
	public Collection<Feature> readFeatures() {
		Collection<Feature> features = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			features = FeatureMySQL.fetchFeaturesMySQL(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return features;
	}





	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readFeatureSpacesByFeatureId(int)
	 */
	@Override
	public Collection<FeatureSpace> readFeatureSpacesByFeatureId(int featureId) {
		Collection<FeatureSpace> featureSpaces = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			featureSpaces = FeatureSpaceMySQL.fetchFeatureSpaceMySQLWhithFeatureID(conn, this, featureId);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return featureSpaces;
	}


	

	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readDimensionInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public DimensionInstance readDimensionInstance(String dimensionName, String dimensionValue) {
		DimensionMySQL dimensionMySQL = new DimensionMySQL();
		DimensionInstanceMySQL dimInstanceMySQL = new DimensionInstanceMySQL();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			dimensionMySQL.fetchDimensionMySQLByName(conn, dimensionName);
			dimInstanceMySQL.fetchDimensionInstanceMySQLByValue(conn, dimensionValue, dimensionMySQL);
			return dimInstanceMySQL;
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#readDimensionInstanceWithParentId(java.lang.String, java.lang.String, java.lang.String)
	 */
	public DimensionInstance readDimensionInstanceWithParentId(String dimensionName, String dimensionValue, int parentInstanceId) {
		DimensionMySQL dimensionMySQL = new DimensionMySQL();
		DimensionInstanceMySQL dimInstanceMySQL = new DimensionInstanceMySQL();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			dimensionMySQL.fetchDimensionMySQLByName(conn, dimensionName);
			dimInstanceMySQL.fetchDimensionInstanceMySQLValueParentInstance(conn, dimensionValue, dimensionMySQL, parentInstanceId);
			
			return dimInstanceMySQL;
		} catch (Exception e) {
			Logger.getLogger(DAOImpl.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}



	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#getDimensions(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<DimensionInstance> readDimensionInstances(String idMachine, String idSpindle) {
		Collection<DimensionInstance> dimensions = new ArrayList<>();

		//		Dimension dimensionMachine = new Dimension(2);
		//		String idDimensionInstanceMachine = !idMachine.equalsIgnoreCase("-1") ? idMachine: "0";
		//		DimensionInstance instanceMachine = new DimensionInstance(idDimensionInstanceMachine, dimensionMachine);
		//		dimensionMachine.addDimensionInstances(instanceMachine);
		//		dimensions.add(dimensionMachine);
		//
		//		Dimension dimensionSpindle = new Dimension(3);
		//		String idDimensionInstanceSpindle = !idSpindle.equalsIgnoreCase("-1") ? idSpindle: "0";
		//		DimensionInstance instanceSpindle = new DimensionInstance(idDimensionInstanceSpindle, dimensionSpindle);
		//		dimensionSpindle.addDimensionInstances(instanceSpindle);
		//		dimensions.add(dimensionSpindle);



		return dimensions;
	}



	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#getDimensions(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<Context> readContext(String idTool, String mode, String idPartProgram) {
		//		Collection<DimensionInstance> dimensions = new ArrayList<>();
		//
		//		Dimension dimensionMachine = new Dimension(2);
		//		String idDimensionInstanceMachine = !idMachine.equalsIgnoreCase("-1") ? idMachine: "0";
		//		DimensionInstance instanceMachine = new DimensionInstance(idDimensionInstanceMachine, dimensionMachine);
		//		dimensionMachine.addDimensionInstances(instanceMachine);
		//		dimensions.add(dimensionMachine);
		//
		//		Dimension dimensionSpindle = new Dimension(3);
		//		String idDimensionInstanceSpindle = !idSpindle.equalsIgnoreCase("-1") ? idSpindle: "0";
		//		DimensionInstance instanceSpindle = new DimensionInstance(idDimensionInstanceSpindle, dimensionSpindle);
		//		dimensionSpindle.addDimensionInstances(instanceSpindle);
		//		dimensions.add(dimensionSpindle);
		//
		//
		//		Dimension dimensionTool = new Dimension(10);
		//		String idDimensionInstanceTool = !idTool.equalsIgnoreCase("-1") ? idTool: "0";
		//		DimensionInstance instanceTool = new DimensionInstance(idDimensionInstanceTool, dimensionTool);
		//		dimensionTool.addDimensionInstances(instanceTool);
		//		dimensions.add(dimensionTool);
		//
		//
		//		Dimension dimensionPartProgram = new Dimension(11);
		//		String idDimensionInstancePartProgram = !idPartProgram.equalsIgnoreCase("-1") ? idPartProgram: "0";
		//		DimensionInstance instancePartProgram = new DimensionInstance(idDimensionInstancePartProgram, dimensionPartProgram);
		//		dimensionPartProgram.addDimensionInstances(instancePartProgram);
		//		dimensions.add(dimensionPartProgram);
		//
		//		Dimension dimensionMode = new Dimension(12);
		//		String dimensionInstanceMode = !mode.equalsIgnoreCase("-1") ? mode: "0";
		//		DimensionInstance instanceMode = new DimensionInstance(dimensionInstanceMode, dimensionMode);
		//		dimensionMode.addDimensionInstances(instanceMode);
		//		dimensions.add(dimensionMode);

		return Optional.ofNullable(new Context(1)) ;
	}



	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#getLastMeasures(java.time.LocalDateTime, int)
	 */
	@Override
	public Iterator<Document> getLastMeasures(LocalDateTime fromDateTime, int minuteInterval) {
		// TODO L'ideale sarebbe omologare al resto delle chiamate al resto delle cose
		return null;
	}



	/**************************************************************************************
	 * INSERT METHODS
	 **************************************************************************************/
	/**
	@Override
	public boolean insertDocumentList(HashMap<String, List<Document>> documentsToInsert) {
		try (MongoClient client = new MongoClient(mDB.getMongoServer(), mDB.getMongoPort())){
			MongoDatabase database = client.getDatabase(mDB.getMongoDBName()); 
			try {
				boolean allInsert = true;
				Iterator it = documentsToInsert.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, List<Document>> pair = (Map.Entry) it.next();
					if (!MeasureMongoDB.insertDocuments(database, pair.getKey(), pair.getValue())){
						allInsert = false;
						LOG.severe("Non è stato possibile inserire i file della collection : "+pair.getKey());
					}
					//			        System.out.println(pair.getKey() + " = " + pair.getValue());
					it.remove(); // avoids a ConcurrentModificationException
				}
				return allInsert;
				//studiare l'inserimento multiplo di file 
				//return MeasureMongoDB.insertJSON(database, collectionName, jsonToInsert);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	*/

	@Override
	public boolean insertMeasuresList(HashMap<String, List<Measure>> measuresToInsert) {
		//TODO è necessario chiudere le connessioni o dopo un po' va in overflow... ma così non è per nulla efficente. piuttosto che closeConnection chiudere solo session
		//e usare sempre lo stesso ManageDBCassandra creandolo all'inizio, aprendo e chiudendo sessione con openSession e closeSession
		ManageDBCassandra dbCassandra = new ManageDBCassandra(mDB.getBigDataServer(), mDB.getBigDataDBName(), true);
		
		//Per Cassandra
		
		try {
			boolean allInsert = true;
			Iterator it = measuresToInsert.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, List<Measure>> pair = (Map.Entry) it.next();
				MeasureCassandra measureCassandra = new MeasureCassandra();
				measureCassandra.createPreparedStatementInsertion(dbCassandra.getSession());
				
				measureCassandra.createQueryToInsertMeasures(pair.getValue(), mDB.getBigDataDBName(), dbCassandra.getCluster(), dbCassandra.getSession());
				//TODO controllare che l'inserimento sia andato a buon fine
				measureCassandra.executeInsertMeasures(mDB.getBigDataDBName(), dbCassandra.getSession());
				
				it.remove(); // avoids a ConcurrentModificationException
				
				dbCassandra.closeConnection();
				
			}
			return allInsert;
		} catch (Exception e) {
			e.printStackTrace();
			dbCassandra.closeConnection();
		}
		return false;
	}

	@Override
	public boolean insertDocument(Document documentToInsert, String collectionName) {
		try (MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort())){
			MongoDatabase database = client.getDatabase(mDB.getBigDataDBName()); 
			try {
				MongoCollection<Document> collezione = database.getCollection(collectionName);
				try {
					collezione.insertOne(documentToInsert);
					return true;
				} catch (NullPointerException e) {
					LOG.info(e.getMessage());
					e.getStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}


	@Override
	public boolean insertJSON(JSONObject jsonToInsert, String collectionName) {
		// TODO Auto-generated method stub
		return false;
	}



	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#insertDataAcquisitionTiming(it.unibs.bodai.ideaas.dao.timing.DataAcquisitionTiming)
	 */
	@Override
	public boolean insertDataAcquisitionTiming(TimingDataAcquisition dataAcquisitionTiming)  {
		//		System.out.println("insertDataAcquisitionTiming()");
		try (Connection conn = mDB.connectMySqlModelDB()) {
			TimingDataAcquisitionMySQL dataAcqTiming = new TimingDataAcquisitionMySQL(dataAcquisitionTiming.getNumFiles(), dataAcquisitionTiming.getTransformTime(), dataAcquisitionTiming.getInsertTime(), dataAcquisitionTiming.getDescription(), dataAcquisitionTiming.getAnomalyPerc());
			return dataAcqTiming.insertMySQL(conn);
			//SynthesisContextMySQL s = new SynthesisContextMySQL(0, null, data.getTime(), 2, 1, 2);
			//s.insertMySQL(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}   
		return false;
	}
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#insertDataReadingTime(it.unibs.bodai.ideaas.dao.timing.TimingDataReading)
	 */
	public boolean insertDataReadingTime(TimingDataReading dataReadingTiming)  {
		//		System.out.println("insertDataAcquisitionTiming()");
		try (Connection conn = mDB.connectMySqlModelDB()) {
			TimingDataReadingMySQL dataAcqTiming = new TimingDataReadingMySQL(dataReadingTiming.getNumFiles(), dataReadingTiming.getReadingTime(), dataReadingTiming.getDescription(), dataReadingTiming.getQuery());
			return dataAcqTiming.insertMySQL(conn);
			//SynthesisContextMySQL s = new SynthesisContextMySQL(0, null, data.getTime(), 2, 1, 2);
			//s.insertMySQL(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}   
		return false;
	}
	
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#insertDataSummarisationTiming(it.unibs.bodai.ideaas.dao.timing.TimingDataSummarisation)
	 */
	@Override
	public boolean insertDataSummarisationTiming(TimingDataSummarisation dataSummarisationTiming) {
		// TODO Auto-generated method stub
		try (Connection conn = mDB.connectMySqlModelDB()) {
			TimingDataSummarisationMySQL dataSumTiming = new TimingDataSummarisationMySQL(dataSummarisationTiming.getNumData(), dataSummarisationTiming.getNumNewData(), dataSummarisationTiming.getNumSyntheses(), dataSummarisationTiming.getNumNewSyntheses(), dataSummarisationTiming.getTotalTime(), dataSummarisationTiming.getInsertTime(), dataSummarisationTiming.getSummarisationTime(), dataSummarisationTiming.getExtractTime(), dataSummarisationTiming.getRelevanceEvaluationTime(), dataSummarisationTiming.getDescription());
			return dataSumTiming.insertMySQL(conn);
			//SynthesisContextMySQL s = new SynthesisContextMySQL(0, null, data.getTime(), 2, 1, 2);
			//s.insertMySQL(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}   
		return false;
	}


	/**
	 ******************************
	 *****  @author Naim
	 *******************************/

	@Override
	public MongoDatabase getMongoConnection() {
		return mDB.connectToMongo();
	}

	@Override
	public ArrayList<DimensionInstance> readDimensioninstaces() {
		ArrayList<DimensionInstance> activeComponentsInstances = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			DimensionInstanceMySQL dI = new DimensionInstanceMySQL();
			activeComponentsInstances = dI.getActiveComponents(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return activeComponentsInstances;
	}

	@Override
	public ArrayList<DimensionInstance> readSystemDimensions() {
		ArrayList<DimensionInstance> activeSystemDimensions = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			DimensionInstanceMySQL dI = new DimensionInstanceMySQL();
			activeSystemDimensions = dI.systemDimensions(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return activeSystemDimensions;
	}

	@Override
	public ArrayList<FeatureSpace> readAllFeatureSpaces(){
		ArrayList<FeatureSpace> feature_spaces_mysql = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			feature_spaces_mysql = FeatureSpaceMySQL.getFeatureSpaces(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		} 

		return feature_spaces_mysql;
	}

	@Override 
	public ArrayList<Feature> readAllAndOnlyFeatures(){
		ArrayList<Feature> features = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			features = FeatureMySQL.getOnlyFeatures(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}

		return features;
	}


	/**
	 * Used in getAlertStatus API - StateDetection
	 */
	public String getLastMongoCollection() {
		//		Document first = database.getCollection("2016_11").find().modifiers(new Document("$explain", true)).first();
		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());
		MongoIterable<String> listCollectionNames = database.listCollectionNames();

		SimpleDateFormat date_format = new SimpleDateFormat("yyyy_MM");
		String collection_date_regex = "(\\d{4}_\\d{2})";
		ArrayList<Date> collections_date = new ArrayList<Date>();
		for(String collection_name : listCollectionNames) {
			Matcher m = Pattern.compile(collection_date_regex).matcher(collection_name);
			if (m.find()) {
				try {
					Date collection_date = date_format.parse(collection_name);
					collections_date.add(collection_date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		Collections.sort(collections_date);
		String collection_name = date_format.format(collections_date.get(collections_date.size() - 1));

		return collection_name;

	}

	@Override
	public void insertStateDetectionTiming(StateDetectionTiming sdt) {
		try (Connection conn = mDB.connectMySqlModelDB()) {
			StateDetectionTimingMySQL stateDetectionTimingTest = new StateDetectionTimingMySQL(sdt.getApi_name(), sdt.getStart_test_date(), sdt.getEnd_test_date(), sdt.getDuration(), sdt.getTotal_number_of_objects_in_collection(), sdt.getTotal_number_of_objects_scanned(), sdt.getTotal_number_of_objects_returned());
			stateDetectionTimingTest.insertTest(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}  
	}

	@Override
	public void insertGetDataTiming(GetDataTiming gdt) {
		try (Connection conn = mDB.connectMySqlModelDB()) {
			GetDataTimingMySQL getDataTiming = new GetDataTimingMySQL(gdt.getApi_name(), gdt.getStart_date_test(), gdt.getEnd_test_date(), gdt.getTotal_time(), gdt.getExtraction_time(), gdt.getTransform_time(), gdt.getXml_generation_time(), gdt.getCollection_name(), gdt.getCount_collection(), gdt.getExperiment_number()); 
			getDataTiming.insertGetDataTest(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}  
	}

	@Override
	public StateDetectionTiming testGetAlertStatusTiming(String collection_name, BasicDBObject whereQuery) {
		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());

		Date start_test_date = new Date(System.currentTimeMillis()); 
		long start_time = System.currentTimeMillis();
		FindIterable<Document> sort = database.getCollection(collection_name).find(whereQuery).modifiers(new Document("$explain", true)).limit(1).sort(new BasicDBObject("timestamp" , -1));
		long end_time = System.currentTimeMillis() - start_time;
		Date end_test_date = new Date(System.currentTimeMillis());
		long objects_in_collection = database.getCollection(collection_name).count();

		StateDetectionTiming stateDetectionTiming = new StateDetectionTiming("", start_test_date, end_test_date, end_time, objects_in_collection, 0, 0);

		for(Document d : sort) {
			System.out.println("Document: " + d.toJson());
		}
		return stateDetectionTiming;	
	}

	@Override
	public long countCollection(String collection_name) {
		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());
		return database.getCollection(collection_name).count();
	}

	@Override
	public Collection<Measure> sendAlertMeasures(String collection, Date a, Date t, Date b, ArrayList<DimensionInstance> dimensionInstances) {
		Collection<Measure> sendAlertMeasures = new ArrayList<>();

		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());

		BasicDBObject whereQuery = MeasureMongoDB.sendAlertMeasuresQuery(a, t, dimensionInstances);
		FindIterable<Document> sendAlertDocuments = database.getCollection(collection).find(whereQuery);
		System.err.println(sendAlertDocuments.toString());

		return sendAlertMeasures;
	}

	@Override()
	public void addIndex(String[] collections, String[] indexes) {
		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());
		for(String c : collections) {
			for(String i : indexes) {
				database.getCollection(c).createIndex(Indexes.ascending(i));
				System.out.println("Created index: " + i + " in Collection: " + c);
			}
		}
	}

	@Override
	public void getIndexes(String[] collections){
		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());
		for(String c : collections) {
			ListIndexesIterable<Document> listIndexes = database.getCollection(c).listIndexes();
			for(Document d : listIndexes) {
				//System.out.println(d.toJson());
				System.out.println(d.get("key"));

			}
		}
	}

	@Override
	public void dropAllIndexes(String[] collections) {
		MongoClient client = new MongoClient(mDB.getBigDataServer(), mDB.getBigDataPort());
		MongoDatabase database = client.getDatabase(mDB.getBigDataDBName());
		for(String c : collections) {
			//			for(String i : indexes) {
			database.getCollection(c).dropIndexes();
			System.out.println("Indexes removed from Collection: " + c);
			//			}
		}
	}

	@Override
	public void saveSendAlertStateMySQL(SendAlert sendAlert) {
		try (Connection conn = mDB.connectMySqlModelDB()) {
			SendAlertMySQL sendAlertMySQL = new SendAlertMySQL(sendAlert.getMeasure_id(), sendAlert.getTimestamp(), sendAlert.getFeature_space_name(), sendAlert.getFeature_space_state(), sendAlert.getFeature_space_id());
			sendAlertMySQL.saveFeaturesState(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	public ArrayList<SendAlert> sendAlertObjectState() {
		ArrayList<SendAlert> sendAlertObjects = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			sendAlertObjects = SendAlertMySQL.getLastFeatureSpaceState(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return sendAlertObjects;
	}
	
	@Override
	public ArrayList<FeatureSpace> sendAlertFeatureSpaceState() {
		ArrayList<FeatureSpace> feature_spaces = new ArrayList<>();
		try (Connection conn = mDB.connectMySqlModelDB()) {
			feature_spaces = SendAlertMySQL.sendAlertFspState(conn);
		} catch (Exception e) {
			Logger.getLogger(DAOImplCassandra.class.getName()).log(Level.SEVERE, null, e);
		}
		return feature_spaces;
	}
	
	/**
	 * @author Daniele Comincini
	 *
	 */
	
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#insertParallelDataSummarisationTiming(it.unibs.bodai.ideaas.dao.timing.ParallelDataSummarisationTiming)
	 */
	@Override
	public boolean insertParallelDataSummarisationTiming(ParallelDataSummarisationTiming parallelDataSummarisationTiming)  {
		//		System.out.println("insertParallelDataSummarisationTiming()");
		try (Connection conn = mDB.connectMySqlModelDB()) {
			ParallelDataSummarisationTimingMySQL parallelDataSumTiming = new ParallelDataSummarisationTimingMySQL(
					parallelDataSummarisationTiming.getNumDimensions(),
					parallelDataSummarisationTiming.getNumSyntheses(),
					parallelDataSummarisationTiming.getTime(),
					parallelDataSummarisationTiming.getType(),
					parallelDataSummarisationTiming.getMean(),
					parallelDataSummarisationTiming.getStd(),
					parallelDataSummarisationTiming.getIdTest());
			return parallelDataSumTiming.insertMySQL(conn);
			//SynthesisContextMySQL s = new SynthesisContextMySQL(0, null, data.getTime(), 2, 1, 2);
			//s.insertMySQL(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}   
		return false;
	}

	@Override
	public DimensionInstance insertDimensionInstance(DimensionInstance dimInst) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertDataAcquisitionAnomalies(DataAcquistionAnomalies dataAcquisitionAnomalies) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see it.unibs.bodai.ideaas.dao.IDAO#insertDataSummarisationAnomalies(it.unibs.bodai.ideaas.dao.anomalies.DataSummarisationAnomalies)
	 */
	@Override
	public void insertDataSummarisationAnomalies(DataSummarisationAnomalies dataSummarisationAnomalies) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Measure> readMeasuresWithQueryTimeInterval(String collection, LocalDateTime datetime,
			String intervalType, int intervalValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Measure> readMeasuresWithQueryFromTo(String collection, String macchinaID, String componenteID,
			LocalDateTime datetimeStart, LocalDateTime datetimeEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<FeatureSpace> readFeatureSpaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveSendAlertTest(SendAlert sendAlertTest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Measure> readGetDataMeasures(String collection, ArrayList<DimensionInstance> dimensionInstances,
			LocalDateTime datetimeStart, LocalDateTime datetimeEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Measure> readGetAlertStatusMeasures(String collection,
			ArrayList<DimensionInstance> dimensionInstances) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveDocuments(String collection, List<Document> documents) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean saveDocument(String collection, Document doc) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public ArrayList<Dimension> readDimensions() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public DimensionInstance readDimensionInstance(Dimension dim, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

}
