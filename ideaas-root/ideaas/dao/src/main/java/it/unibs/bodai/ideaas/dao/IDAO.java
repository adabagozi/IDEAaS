package it.unibs.bodai.ideaas.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

import it.unibs.bodai.ideaas.dao.anomalies.DataSummarisationAnomalies;
import it.unibs.bodai.ideaas.dao.model.Context;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.SendAlert;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.dao.timing.DataAcquistionAnomalies;
import it.unibs.bodai.ideaas.dao.timing.GetDataTiming;
import it.unibs.bodai.ideaas.dao.timing.ParallelDataSummarisationTiming;
import it.unibs.bodai.ideaas.dao.timing.StateDetectionTiming;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;
import it.unibs.bodai.ideaas.dao.timing.TimingDataReading;
import it.unibs.bodai.ideaas.dao.timing.TimingDataSummarisation;

/**
 * Represents a layer allowing you to easily connect to every databases involved in the BODAI project<br>
 * 
 * 
 * Basically, BODAI project exploits <b>3 different databases</b>:
 * <ul>
 *  <li>one database concerning the metamodel</li>
 *  <li>one concerning the concrete model generated by the metamodel one</li>
 *  <li>one containing big data elements</li>
 * </ul>
 * 
 * Although concrete implementation of BODAI uses 3 databases, this interface does not directly imply it.
 * 
 * 
 * @author Ada Bagozi, Daniele Comincini, Gianmaria Micheli, Naim Xhani
 */
public interface IDAO {

	/**************************************************************************************
	 * READ METHODS
	 **************************************************************************************/

	/**
	 * @param collection
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
	public Collection<Measure> readMeasures(String collection, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, String intervalType, int intervalValue);

	/**
	 * Metodo per la lettura dei dati salvati nella collection di dati indicata applicando la query in input.
	 * @param collection Nome della collection nella quale leggere i dati
	 * @param whereQuery query da applicare ai dati per la lettura.
	 * @return @Optional<Iterable<Document>> ritorna un optional dell'iteratore ai documenti salvati nel DB.
	 */
	public Optional<Iterable<Document>> readMeasuresWithQuery(String collection, String step, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, String intervalType, int intervalValue);
	
	/**
	 * Metodo per la lettura dei dati salvati nella collection di dati attraverso l'applicazione di query sul time interval
	 * @param datetime
	 * @param intervalType
	 * @param intervalValue
	 * @return
	 */
	public Collection<Measure> readMeasuresWithQueryTimeInterval(String collection, LocalDateTime datetime, String intervalType, int intervalValue);

	/**
	 * TODO sistemare le query mettendo in input dimensionIntances
	 * @param datetime
	 * @param intervalType
	 * @param intervalValue
	 * @return
	 */
	public Collection<Measure> readMeasuresWithQueryFromTo(String collection, String macchinaID, String componenteID, LocalDateTime datetimeStart, LocalDateTime datetimeEnd);

	/**
	 * Metodo per la lettura dei dati salvati nella collection di dati indicata applicando la query in input.
	 * @param collection Nome della collection nella quale leggere i dati
	 * @param whereQuery query da applicare ai dati per la lettura.
	 * @return @Optional<Iterable<Document>> ritorna un optional dell'iteratore ai documenti salvati nel DB.
	 */
	public Optional<Iterable<Document>> readMeasures(String collection, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, LocalDateTime dateTimeTwo, boolean increase);

	public  int numMeasuresWithQuery(String collection, String step, String utensileID, String partProgram, String mode, String macchinaID, String componenteID, LocalDateTime datetime, String intervalType, int intervalValue);

	public Optional<Iterable<Document>> readDataPoints(String collection, int experimentNumber);
	
	public Optional<Iterable<Document>> readDocuments(String collection, BasicDBObject whereQuery);

	public DimensionInstance readDimensionInstance(String string, String string2);

	
	public DimensionInstance readDimensionInstance(Dimension dim, String string2);

	
	public DimensionInstance readDimensionInstanceWithParentId(String string, String string2, int parentInstanceId);

	/**
	 * @param collection
	 * @param featureSpace
	 * @param idMachine
	 * @param idSpindle
	 * @param idTool
	 * @param mode
	 * @param idPartProgram
	 * @param intervalType
	 * @param intervalValue
	 * @return
	 */
	public Optional<Iterable<Document>> readSummarisedDatas(String collection, FeatureSpace featureSpace, String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, String intervalType, int intervalValue, int experimentNumber);
	/**
	 * @param collection
	 * @param featureSpace
	 * @param idMachine
	 * @param idSpindle
	 * @param idTool
	 * @param mode
	 * @param idPartProgram
	 * @param intervalType
	 * @param intervalValue
	 * @return
	 */
	public Optional<Iterable<Document>> readSummarisedDatas(String collection, FeatureSpace featureSpace, String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, LocalDateTime datetime, LocalDateTime dateTimeTwo, boolean increase, int experimentNumber);

	/**
	 * Metodo per la lettura dei summarized data
	 * @param id
	 * @return
	 */
	public Optional<SummarisedData> readSummarisedDataById(String collection, String id);


	/**
	 * @param collection
	 * @param featureSpace
	 * @param idMachine
	 * @param idSpindle
	 * @param idTool
	 * @param mode
	 * @param idPartProgram
	 * @param stable
	 * @param intervalType
	 * @param intervalValue
	 * @return
	 */
	public Optional<SummarisedData> readSummarisedData(String collection, FeatureSpace featureSpace, String idMachine, String idSpindle, String idTool, String mode, String idPartProgram, Boolean stable,String intervalType, int intervalValue, int experimentNumber);


	public ArrayList<Dimension> readDimensions();
	/**
	 * Metodo per leggere una FeatureSpace dal database in base al suo identifiatore
	 * @param id identificatore della fueature space da leggere dal database
	 * @return la feature space presente nel database con l'id dato, se l'id non esiste ritorna optional null
	 */
	public Optional<FeatureSpace> readFeatureSpace(int featureSpaceID);


	public ArrayList<FeatureSpace> readFeatureSpaces();
	/**
	 * Metodo per leggere dal database una collection di feature che appartengono ad una data feature space
	 * @param featureSpaceId identificativo della feature space di cui si vogliono ottenere le feature
	 * @return insieme di Feature
	 */
	public Collection<Feature> readFeaturesByFeatureSpaceId(int featureSpaceID);

	/**
	 * Metodo per leggere dal database tutte le feature presenti
	 * @return collection di Feature
	 */
	public Collection<Feature> readFeatures();

	/**
	 * Metodo per leggere dal database una collection di feature space in cui ?? contenuta una feature
	 * @param featureId identificativo della feature di cui si vogliono ottenere le feature space
	 * @return insieme di @FeatureSpace
	 */
	public Collection<FeatureSpace> readFeatureSpacesByFeatureId(int featureId);

	public Collection<DimensionInstance> readDimensionInstances(String idMachine, String idSpindle);

	public Optional<Context> readContext(String idTool, String mode, String idPartProgram);


	/**************************************************************************************
	 * INSERT METHODS
	 **************************************************************************************/


	/**
	 * Metodo per l'inserimento di un file json nella collection di dati indicata,
	 * @param jsonToInsert	file @json da salvare sul database
	 * @param collectionName nome della collection nella quale salvare il file
	 * @return vero se il file ?? stato inserito correttamente, falso altrimenti
	 */
	public boolean insertJSON(JSONObject jsonToInsert, String collectionName);

	/**
	 * Metodo per l'inserimento di un file @Document nella collection di dati indicata,
	 * @param docuementToInsert	file @Document da salvare sul database
	 * @param collectionName nome della collection nella quale salvare il file
	 * @return vero se il file ?? stato inserito correttamente, falso altrimenti
	 */
	public boolean insertDocument(Document docuementToInsert, String collectionName);

	/**
	 * Metodo per l'inserimento di un file @Document nella collection di dati indicata,
	 * @param docuementToInsert	file @Document da salvare sul database
	 * @param collectionName nome della collection nella quale salvare il file
	 * @return vero se il file ?? stato inserito correttamente, falso altrimenti
	 */
	public ObjectId insertDocumentAndGetID(Document docuementToInsert, String collectionName);


	/**
	 * Metodo per l'inserimento di pi?? file json nel database
	 * @param documentsToInsert hashmap che ha come chiave il nome della collection nella quale salvare i file contenuti nel relativo valore, i file sono passati come una lista (List<Document>)
	 * @return vero se i file sono stati inseriti correttamente, falso altrimenti
	 
	public boolean insertDocumentList(HashMap<String, List<Document>> documentsToInsert);	
	 */
	
	/**
	 * Metodo per l'inserimento di pi?? oggetti misura nel database
	 * @param measuresToInsert hashmap che ha come chiave il nome della collection nella quale salvare le misure contenute nel relativo valore, le misure sono passate come una lista (List<Measures>)
	 * @return vero se le misure sono stati inseriti correttamente, falso altrimenti
	 */
	public boolean insertMeasuresList(HashMap<String, List<Measure>> measuresToInsert);	



	/**
	 * Inserimento di un tempo di acquisizione nel database
	 * @param dataAcquisitionTiming
	 * @return
	 */
	public boolean insertDataAcquisitionTiming(TimingDataAcquisition dataAcquisitionTiming);
	

	/**
	 * Inserimento di un tempo di lettura nel database
	 * @param dataReadingTime
	 * @return
	 */
	public boolean insertDataReadingTime(TimingDataReading dataReadingTime);
	



	/**
	 * Inserimento di un tempo di clustering nel database
	 * @param dataSummarisationTiming
	 * @return
	 */
	public boolean insertDataSummarisationTiming(TimingDataSummarisation dataSummarisationTiming);

	public Optional<Document> readDoc(String collection, BasicDBObject whereQuery);
	
	public Iterator<Document> getLastMeasures(LocalDateTime fromDateTime, int minuteInterval);
	
	/**
	 * Method to insert dimension instances in the dB
	 * @param dimInst
	 * @return
	 */
	public DimensionInstance insertDimensionInstance(DimensionInstance dimInst);
	
	/**************************************************************************************
	 * MODIFY METHODS
	 **************************************************************************************/
	
	/**************************************************************************************
	 * NAIMs - METHODS to be reviewed with Ada 
	 **************************************************************************************/
		
	public MongoDatabase getMongoConnection();

	public ArrayList<DimensionInstance> readDimensioninstaces();

	public ArrayList<DimensionInstance> readSystemDimensions();

	public ArrayList<FeatureSpace> readAllFeatureSpaces();

	public ArrayList<Feature> readAllAndOnlyFeatures();

	public String getLastMongoCollection();
	
	public long countCollection(String collection_name);
	
	
	public void insertStateDetectionTiming(StateDetectionTiming sdt);

	public StateDetectionTiming testGetAlertStatusTiming(String collection_name, BasicDBObject whereQuery);

	public void insertGetDataTiming(GetDataTiming gdt);

	public void addIndex(String[] collections, String[] indexes);

	public void getIndexes(String[] collections);

	public void dropAllIndexes(String[] collections);
	public void saveSendAlertStateMySQL(SendAlert sendAlert);

	public ArrayList<SendAlert> sendAlertObjectState();

	public ArrayList<FeatureSpace> sendAlertFeatureSpaceState();
	

	public boolean saveDocuments(String collection, List<Document> documents);
	public boolean saveDocument(String collection, Document doc);
	
	/**
	 * Daniele Comincini
	 *
	 */
	boolean insertParallelDataSummarisationTiming(ParallelDataSummarisationTiming parallelDataSummarisationTiming);

	public void saveSendAlertTest(SendAlert sendAlertTest);

	public Collection<Measure> readGetDataMeasures(String collection, ArrayList<DimensionInstance> dimensionInstances, LocalDateTime datetimeStart, LocalDateTime datetimeEnd);

	public Collection<Measure> readGetAlertStatusMeasures(String collection, ArrayList<DimensionInstance> dimensionInstances);

	
	public void insertDataAcquisitionAnomalies(DataAcquistionAnomalies dataAcquisitionAnomalies);
	public void insertDataSummarisationAnomalies(DataSummarisationAnomalies dataSummarisationAnomalies);

	Collection<Measure> sendAlertMeasures(String collection, Date a, Date t, Date b,
			ArrayList<DimensionInstance> dimensionInstances);

}
