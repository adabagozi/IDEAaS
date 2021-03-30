package it.unibs.bodai.ideaas.dao.implementation.storage;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.core.schemabuilder.SchemaStatement;

import it.unibs.bodai.ideaas.dao.model.Context;
import it.unibs.bodai.ideaas.dao.model.ContextStatus;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.Measure;

/**
 * @author Gianmaria Micheli, Ada Bagozi
 *
 */
public class MeasureCassandra extends Measure {
	
	//parametri di creazione e configurazione del keyspace (database in cassandra)
	private final String CLASS = "class";
	private final String STRATEGY = "SimpleStrategy";
	private final String REPLICATION_FACTOR = "replication_factor";
	private final int REPLICATION_FACTOR_VALUE = 1;
	
	//nomi colonne della tabella delle misure
	private final String TABLE_MEASURE = "measures_by_dimensions";
	private final String COL_MACCHINA = "machine";
	private final String COL_COMPONENTE = "component";
	private final String COL_GIORNO = "day";
	private final String COL_TIMESTAMP = "timestamp";
	private final String COL_UUID = "uuid";
	private final String COL_DIMENSIONI = "dimensions";
	private final String COL_FEATURE = "features";
	private final String COL_STATUS = "status";
	
	//nomi colonne tabella del tipo feature
	private final String TABLE_FEATURE = "feature";
	private final String COL_ID = "id";
	private final String COL_NOME = "name";
	private final String COL_VALORE = "value";
	private final String COL_CONTEXT_STATUS = "context_status";
	
	//nomi colonne tabella del tipo dimensioni
	private final String TABLE_DIMENSIONS = "dimension";
	private final String COL_INSTANCE_ID = "instance_id";
	
	//TODO in mySQL identificare in qualche modo le dimensioni su cui partizionare con cassandra.
	//TODO togliere embedded code
	private final int MACCHINA = 3;
	private final int COMPONENTE = 4;
	
	private final String HOURS = "hours";
	private final String MINUTES = "minutes";
	private final String SECONDS = "seconds";
	
//	private Cluster cluster;
//	private Session session;
	
	//query pre-elaborata per l'inserimento delle misure nel db
	private PreparedStatement insertStatement;

	//lista delle partizioni presenti nel db
	private ResultSet partitions;
	//lista delle macchine distinte che definiscono una partizione
	private ArrayList<Integer> machines = new ArrayList<Integer>();
	//lista dei componenti distinti che definiscono una partizione
	private ArrayList<String>  components = new ArrayList<String>();
	//lista dei giorni distinti che definiscono una partizione
	private ArrayList<Date>  days = new ArrayList<Date>();
	
	//blocchi di batch per l'inserimento dei dati (uno per componente)
	private BatchStatement[] toInsertMeasures = new BatchStatement[3];
	
//	public MeasureCassandra(Cluster cluster, Session session)
//	{
//		this.cluster = cluster;
//		this.session = session;
//	}
//	
//	public MeasureCassandra(Cluster cluster, Session session, PreparedStatement insertStatement)
//	{
//		this.cluster = cluster;
//		this.session = session;
//		this.insertStatement = insertStatement;
//	}
	

	/**
	 * crea la query per il corretto inserimento della Measure in cassandra
	 * @param measure la measure da inserire nel db
	 * @param dbName nome del db
	 * @param cluster cluster a cui connettersi
	 * @param session sessione di connessione con il cluster
	 * @return la query di inserimento
	 */
	public BoundStatement createInsertQuery(Measure measure, String dbName, Cluster cluster, Session session)
	{
		BoundStatement boundStatement = new BoundStatement(insertStatement);
		int macchina = -1;
		String componente = null;
		
		//SI ESTRAE LE DIMENSIONI

		//UDT che rappresenta il tipo feature
		UserType dimensionType = cluster.getMetadata()
	    	     .getKeyspace(dbName)
	    	     .getUserType(this.TABLE_DIMENSIONS);
		
		//lista da inserire nel db
		List<UDTValue> dimensions = new ArrayList<UDTValue>();
		
		//collection di dimensioni nella measure
		Collection<DimensionInstance> dimensioniMeasure = measure.getDimensionInstances();
//		LinkedHashMap<String, String> dimensions = new LinkedHashMap<String, String>();
		Iterator<DimensionInstance> iteratorDimensions = dimensioniMeasure.iterator();
		while (iteratorDimensions.hasNext())
		{
			DimensionInstance dimensionTemp = (DimensionInstance) iteratorDimensions.next();
//			System.out.println(dim.getName() + " + " + dim.getDimension().getId() + " + " + dim.getInstanceId());
			switch (dimensionTemp.getDimension().getId())
			{
			case MACCHINA: //PARTITION KEY
				macchina = Integer.parseInt(dimensionTemp.getInstanceId());
				break;

			case COMPONENTE: //PARTITION KEY
				componente = dimensionTemp.getInstanceId();
				break;
				
			default:
				dimensions.add(dimensionType.newValue()
							   .setInt(this.COL_ID, dimensionTemp.getDimension().getId())
							   .setString(this.COL_NOME, dimensionTemp.getName())
							   .setString(this.COL_INSTANCE_ID, dimensionTemp.getInstanceId()));
				break;
			}
		}
		
		//INSERIMENTO DELLE FEATURE
		
		//UDT che rappresenta il tipo feature
		UserType featureType = cluster.getMetadata()
	    	     .getKeyspace(dbName)
	    	     .getUserType(this.TABLE_FEATURE);
		
		//lista da inserire nel db
		List<UDTValue> features = new ArrayList<UDTValue>();
		
		//collection di feature nella measure
		HashMap<Integer, FeatureData> featureData = measure.getFeaturesDataHashMap();
		Iterator<FeatureData> iteratorFeature = featureData.values().iterator();
		while(iteratorFeature.hasNext())
		{
			//feature corrente
			FeatureData featureTemp = iteratorFeature.next();

			//map di contesti da inserire nel db
			LinkedHashMap<Integer, String> contextMap = new LinkedHashMap<Integer,String>();
			
			//context_status della feature corrente
			Collection<ContextStatus> contextStatus = featureTemp.getContextsStatus();
			if(contextStatus.size() > 0) //se non ha context_status, skip
			{
				Iterator<ContextStatus> iteratorContext = contextStatus.iterator();
				while(iteratorContext.hasNext())
				{
					//contesto corrente
					ContextStatus contextTemp = iteratorContext.next();
					contextMap.put(contextTemp.getContext().getId(), contextTemp.getStatus());
				}
				
				//inserimento della feature nella lista
				features.add(featureType.newValue()
							.setInt(this.COL_ID, featureTemp.getFeature().getId())
							.setString(this.COL_NOME, featureTemp.getFeature().getName())
							.setDouble(this.COL_VALORE, featureTemp.getValue())
							.setMap(this.COL_CONTEXT_STATUS, contextMap)
							.setString(this.COL_STATUS, featureTemp.getStatus()));
			}
			else
			{
				features.add(featureType.newValue()
						.setInt(this.COL_ID, featureTemp.getFeature().getId())
						.setString(this.COL_NOME, featureTemp.getFeature().getName())
						.setDouble(this.COL_VALORE, featureTemp.getValue())
						.setString(this.COL_STATUS, featureTemp.getStatus()));
			}
			
		}
		
		//colonna Timestamp (CLUSTERING KEY)
		Date timestamp = measure.getTimestamp();
		//colonna Day (PARTITION KEY)
		Date dayTimestamp = null;
		if(timestamp != null)
		{
			Calendar day = Calendar.getInstance();
			day.setTime(timestamp);
			day.set(Calendar.HOUR_OF_DAY, 10);
			day.set(Calendar.MINUTE, 0);
			day.set(Calendar.SECOND, 0);
			day.set(Calendar.MILLISECOND, 0);
			dayTimestamp = day.getTime();
		}
		
		//Colonna Uuid (CLUSTERING KEY)
		UUID uuid = UUID.randomUUID();
		
		//Colonna Status
		String status = measure.getStatus();
		
		
		//inserisce i dati elaborati all'interno della query, nell'ordine di definizione nella query generica
		if(macchina != -1)
		{
			boundStatement.setInt(0, macchina);
		}
		if(componente != null)
		{
			boundStatement.setString(1, componente);
		}
		if(dayTimestamp != null)
		{
			boundStatement.setTimestamp(2, dayTimestamp);
		}
		if(timestamp != null)
		{
			boundStatement.setTimestamp(3, timestamp);
		}
		if(uuid != null)
		{
			boundStatement.setUUID(4, uuid);
		}
		if(dimensions.size() > 0)
		{
			boundStatement.setList(5, dimensions);
		}
		if(features.size() > 0)
		{
			boundStatement.setList(6, features);
		}
		if(status != null)
		{
			boundStatement.setString(7, status);
		}
		
		return boundStatement;
	}
	

	/**
	 * crea la query di estrazione delle misure dal db
	 * @param dbName nome del db
	 * @param machineID lista di id delle macchine coinvolte
	 * @param componentID lista di id dei componenti coinvolti
	 * @param datetimes lista di giorni coinvolti
	 * @param intervalType tipo di intervallo considerato (ore, minuti, secondi)
	 * @param intervalValue dimensione dell'intervallo
	 * @return la query di interrogazione del db
	 */
	public Statement createOutputQuery(String dbName, ArrayList<Integer> machineID, ArrayList<String> componentID, ArrayList<LocalDateTime> datetimes, String intervalType, int intervalValue)
	{	
		Statement query = null;
		
		//la query è costruita diversamente a seconda che siano indicati 1 o più valori delle chiavi di partizione.
		//si distingue la creazione in base a quanti valori sono coinvolti nella query
		//nel caso in cui una chiave indichi 2 o più valori, si fa uso della clausola IN
		//TODO vedere se è possibile gestire la cosa in modo più elegante, senza ridondare la creazione della query (improbabile date le librerie rigide)
		
		//un solo giorno coinvolto
		if(datetimes.size() == 1)
		{
			LocalDateTime datetime = datetimes.get(0);
			
			Calendar day = Calendar.getInstance();
			day.set(Calendar.YEAR, datetime.getYear());
			day.set(Calendar.MONTH, datetime.getMonth().getValue()-1);
			day.set(Calendar.DAY_OF_MONTH, datetime.getDayOfMonth());
			day.set(Calendar.HOUR_OF_DAY, 10);
			day.set(Calendar.MINUTE, 0);
			day.set(Calendar.SECOND, 0);
			day.set(Calendar.MILLISECOND, 0);
			
			LocalDateTime localDateTwo = this.calcDateTwo(datetime, intervalType, intervalValue);
			
			//una sola macchina coinvolta
			if(machineID.size()==1)
			{
				//un solo componente coinvolto
				if(componentID.size()==1)
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.eq(this.COL_MACCHINA	, machineID.get(0)))
							  .and(QueryBuilder.eq(this.COL_COMPONENTE, componentID.get(0)))
							  .and(QueryBuilder.eq(this.COL_GIORNO, day.getTimeInMillis()))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetime.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
				else //più componenti coinvolti
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.eq(this.COL_MACCHINA	, machineID.get(0)))
							  .and(QueryBuilder.in(this.COL_COMPONENTE, componentID))
							  .and(QueryBuilder.eq(this.COL_GIORNO, day.getTimeInMillis()))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetime.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
			}
			else //più macchine coinvolte
			{
				//un solo componente coinvolto
				if(componentID.size()==1)
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.in(this.COL_MACCHINA	, machineID))
							  .and(QueryBuilder.eq(this.COL_COMPONENTE, componentID.get(0)))
							  .and(QueryBuilder.eq(this.COL_GIORNO, day.getTimeInMillis()))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetime.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
				else //più componenti coinvolti
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.in(this.COL_MACCHINA	, machineID))
							  .and(QueryBuilder.in(this.COL_COMPONENTE, componentID))
							  .and(QueryBuilder.eq(this.COL_GIORNO, day.getTimeInMillis()))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetime.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
			}
		}
		else //più giorni coinvolti
		{
			ArrayList<Long> days = new ArrayList<Long>();
			
			for(int i = 0; i < datetimes.size(); i++)
			{
				Calendar day = Calendar.getInstance();
				day.set(Calendar.YEAR, datetimes.get(i).getYear());
				day.set(Calendar.MONTH, datetimes.get(i).getMonth().getValue()-1);
				day.set(Calendar.DAY_OF_MONTH, datetimes.get(i).getDayOfMonth());
				day.set(Calendar.HOUR_OF_DAY, 10);
				day.set(Calendar.MINUTE, 0);
				day.set(Calendar.SECOND, 0);
				day.set(Calendar.MILLISECOND, 0);
				days.add(day.getTimeInMillis());
			}
			
			LocalDateTime localDateTwo = this.calcDateTwo(datetimes.get(0), intervalType, intervalValue);
			
			//una sola macchina coinvolta
			if(machineID.size()==1)
			{
				//un solo componente coinvolto
				if(componentID.size()==1)
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.eq(this.COL_MACCHINA	, machineID.get(0)))
							  .and(QueryBuilder.eq(this.COL_COMPONENTE, componentID.get(0)))
							  .and(QueryBuilder.in(this.COL_GIORNO, days))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetimes.get(0).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
				else //più componenti coinvolti
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.eq(this.COL_MACCHINA	, machineID.get(0)))
							  .and(QueryBuilder.in(this.COL_COMPONENTE, componentID))
							  .and(QueryBuilder.in(this.COL_GIORNO, days))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetimes.get(0).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
			}
			else //più macchine coinvolte
			{
				//un solo componente coinvolto
				if(componentID.size()==1)
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.in(this.COL_MACCHINA	, machineID))
							  .and(QueryBuilder.eq(this.COL_COMPONENTE, componentID.get(0)))
							  .and(QueryBuilder.in(this.COL_GIORNO, days))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetimes.get(0).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
				else //più componenti coinvolti
				{
					query = QueryBuilder.select().all()
							  .from(dbName, this.TABLE_MEASURE)
							  .where(QueryBuilder.in(this.COL_MACCHINA	, machineID))
							  .and(QueryBuilder.in(this.COL_COMPONENTE, componentID))
							  .and(QueryBuilder.in(this.COL_GIORNO, days))
							  .and(QueryBuilder.gte(this.COL_TIMESTAMP, 
									  				datetimes.get(0).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()))
							  .and(QueryBuilder.lte(this.COL_TIMESTAMP, 
									  				localDateTwo.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()));
				}
			}
		}
		
		return query;
	}
	
	//TODO spostare da qualche altra parte; controllare anche MeasureMongo
	public LocalDateTime calcDateTwo(LocalDateTime datetime, String intervalType, int intervalValue)
	{
		LocalDateTime localDateTwo = null;
		
		if(intervalType.equalsIgnoreCase(HOURS)){
			localDateTwo = datetime.plusHours(intervalValue);
			
		} else if(intervalType.equalsIgnoreCase(MINUTES)){
			localDateTwo = datetime.plusMinutes(intervalValue);
			
		} else if(intervalType.equalsIgnoreCase(SECONDS)){
			localDateTwo = datetime.plusSeconds(intervalValue);
			
		}else {
			localDateTwo = datetime.plusDays(1);
		}
		return localDateTwo;
	}

	
	/**
	 * crea lo statement di base della query di inserimento
	 * @param session sessione di connessione con cassandra, necessaria per la preparazione della query
	 */
	public void createPreparedStatementInsertion(Session session){
		String stmnt = "INSERT INTO " + this.TABLE_MEASURE
						+ " ( " 
						+ this.COL_MACCHINA + ", "
						+ this.COL_COMPONENTE + ", "
						+ this.COL_GIORNO + ", "
						+ this.COL_TIMESTAMP + ", "
						+ this.COL_UUID + ", "
						+ this.COL_DIMENSIONI + ", "
						+ this.COL_FEATURE + ", "
						+ this.COL_STATUS
						+ " ) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		
	    insertStatement = session.prepare(stmnt);
	}
	

	/**
	 * creazione dei blocchi di query per gli inserimenti in batch delle misure
	 * @param measures lista delle misure da inserire
	 * @param dbName nome del db
	 * @param cluster cluster a cui connettersi
	 * @param session sessione di connessione con il cluster
	 */
	public void createQueryToInsertMeasures(List<Measure> measures, String dbName, Cluster cluster, Session session){
		//inizializzazione dei batch
		this.toInsertMeasures[0] = new BatchStatement();
		this.toInsertMeasures[1] = new BatchStatement();
		this.toInsertMeasures[2] = new BatchStatement();
		int component = -1;
	    
		//per ogni measure, viene inserita in uno dei tre blocchi a seconda del componente. questo per tenere limitata
		//la dimensione del batch. Nel caso generico, pensare di suddividere in blocchi di dimensione fissa.
	    for(int i = 0; i < measures.size(); i++)
	    {
	    	Iterator<DimensionInstance> it = measures.get(i).getDimensionInstances().iterator();
	    	while(it.hasNext())
	    	{
	    		DimensionInstance dim = it.next();
	    		if(dim.getDimension().getId() == 4)
	    		{
	    			switch (dim.getInstanceId())
					{
						case "Unit 1.0":
							component = 1;
							break;
						case "Unit 2.0":
							component = 2;
							break;
						case "Unit 3.0":
							component = 3;
							break;
						default:
							break;
					}
	    		}
	    	}
	    	
	    	this.toInsertMeasures[component-1].add(this.createInsertQuery(measures.get(i), dbName, cluster, session));
	    }
	}
	

	/**
	 * esegue l'inserimento dei dati convertiti precedentemente in batch query
	 * @param dbName nome del db
	 * @param session sessione di connessione con il cluster
	 */
	public void executeInsertMeasures(String dbName, Session session)
	{
		// In try-catch per problemi di batch troppo grandi
		//se oltre i limiti, viene richiamato il metodo insertMeasuresBatchTooLarge che suddivide il blocco
		//magari si potrebbe sfruttare tale metodo per effettuare una suddivisione generica in base alla dimensione del batch
		try {
			session.execute(this.toInsertMeasures[0]);
		} catch(InvalidQueryException e) {
			this.insertMeasuresBatchTooLarge(this.toInsertMeasures[0], 5, session);
		}
		try {
			session.execute(this.toInsertMeasures[1]);
			
		}catch(InvalidQueryException e) {
			this.insertMeasuresBatchTooLarge(this.toInsertMeasures[1], 5, session);
		}
		try 
		{
			session.execute(this.toInsertMeasures[2]);
			
		}
		catch(InvalidQueryException e)
		{
			this.insertMeasuresBatchTooLarge(this.toInsertMeasures[2], 5, session);
		}
		

	}
	
	/**
	 * carica in memoria le partizioni presenti all'interno del db cassandra
	 * @param dbName nome del db
	 * @param session sessione di connessione con il cluster
	 */
	public void loadPartitions(String dbName, Session session)
	{
		//da modificare nel caso in cui si modifichi lo schema dei dati
		Statement query = QueryBuilder.select().distinct()
				  .column(this.COL_MACCHINA)
				  .column(this.COL_COMPONENTE)
				  .column(this.COL_GIORNO)
				  .from(dbName, this.TABLE_MEASURE);
		this.partitions = session.execute(query);
		
		//i dati delle partizioni vengono inoltre suddivisi sulle singole colonne coinvolte
		while(!this.partitions.isExhausted())
		{
			Row row = this.partitions.one();
//			System.out.println(row);
			machines.add(row.getInt(this.COL_MACCHINA));
			components.add(row.getString(this.COL_COMPONENTE));
			days.add(row.getTimestamp(this.COL_GIORNO));
		}
		
		Set<Integer> machineSet = new HashSet<Integer>();
		Set<String> componentSet = new HashSet<String>();
		Set<Date> daySet = new HashSet<Date>();
		
		machineSet.addAll(machines);
		componentSet.addAll(components);
		daySet.addAll(days);
		
		machines.clear();
		components.clear();
		days.clear();
		
		machines.addAll(machineSet);
		components.addAll(componentSet);
		days.addAll(daySet);
	}
	

	/**
	 * metodo che converte la riga di output nell'oggetto Measure
	 * @param row riga di output dalle query
	 * @return la Measure estratta
	 */
	public Measure fromRowToMeasure(Row row) {
		Measure measure = new Measure();
		
		measure.setId(row.getUUID(this.COL_UUID).toString());
		measure.setTimestamp(row.getTimestamp(this.COL_TIMESTAMP));
		measure.setStatus(row.getString(this.COL_STATUS));
		
		//INSERIMENTO DELLE DIMENSIONI
		Collection<DimensionInstance> dimensions = new ArrayList<DimensionInstance>();
		
		//le dimensioni di partizione vengono "inserite a mano", a parte il valore di instance_id
		DimensionInstance machine = new DimensionInstance();
		Dimension machineDimension = new Dimension(this.MACCHINA);
		machine.setDimension(machineDimension);
		machine.setName("Macchina");
		machine.setInstanceId(String.valueOf(row.getInt(this.COL_MACCHINA)));
		dimensions.add(machine);
		
		DimensionInstance component = new DimensionInstance();
		Dimension componentDimension = new Dimension(this.COMPONENTE);
		component.setDimension(componentDimension);
		component.setName("Componente");
		component.setInstanceId(row.getString(this.COL_COMPONENTE));
		dimensions.add(component);
		
		//le altre dimensioni vengono invece inserite estraendo le informazioni dal db
		Set<UDTValue> dimensionsRow = row.getSet(this.COL_DIMENSIONI, UDTValue.class);
		Iterator<UDTValue> dimensionsRowIterator = dimensionsRow.iterator();
		while(dimensionsRowIterator.hasNext()) {
			UDTValue udtTemp = dimensionsRowIterator.next();
			DimensionInstance dimensionInstanceTemp = new DimensionInstance();
			Dimension dimensionTemp = new Dimension(udtTemp.getInt(this.COL_ID));
			dimensionInstanceTemp.setDimension(dimensionTemp);
			dimensionInstanceTemp.setName(udtTemp.getString(this.COL_NOME));
			dimensionInstanceTemp.setInstanceId(udtTemp.getString(this.COL_INSTANCE_ID));
			dimensions.add(dimensionInstanceTemp);
		}
		measure.setDimensionInstances(dimensions);
		
		//INSERIMENTO DELLE FEATURE
		HashMap<Integer, FeatureData> featuresData =  new HashMap<>();
		
		Set<UDTValue> featuresRow = row.getSet(this.COL_FEATURE, UDTValue.class);
		Iterator<UDTValue> featuresRowIterator = featuresRow.iterator();
		while(featuresRowIterator.hasNext())
		{
			UDTValue udtTemp = featuresRowIterator.next();
			FeatureData featureDataTemp = new FeatureData();
			Feature featureTemp = new Feature();
			featureTemp.setId(udtTemp.getInt(this.COL_ID));
			featureTemp.setName(udtTemp.getString(this.COL_NOME));
			featureDataTemp.setFeature(featureTemp);
			featureDataTemp.setValue(udtTemp.getDouble(this.COL_VALORE));
			featureDataTemp.setStatus(udtTemp.getString(this.COL_STATUS));
			
			Map<Integer, String> contextStatusRow = udtTemp.getMap(this.COL_CONTEXT_STATUS,
					Integer.class, String.class);
			if(!contextStatusRow.isEmpty())
			{
				Collection<ContextStatus> contextsStatus = new ArrayList<ContextStatus>();
				Set<Entry<Integer,String>> setContextStatusRow = contextStatusRow.entrySet();
				Iterator<Entry<Integer, String>> contextStatusSetIterator = setContextStatusRow.iterator();
				while(contextStatusSetIterator.hasNext())
				{
					Entry<Integer, String> contextStatusEntry = contextStatusSetIterator.next();
					Context contextTemp = new Context(contextStatusEntry.getKey());
					ContextStatus contextStatusTemp = new ContextStatus(contextTemp, contextStatusEntry.getValue());
					contextsStatus.add(contextStatusTemp);
				}
				featureDataTemp.setContextsStatus(contextsStatus);
			}
			
			featuresData.put(featureDataTemp.getFeature().getId(), featureDataTemp);
		}
		measure.setFeaturesDataHashMap(featuresData);
		
		
		return measure;
	}
	
	
	
	
	
	
	/**
	 * separa l'esecuzione del batchstatement in sotto-batch
	 * @param indexBatch
	 * @param parts in quante parti suddividere
	 * @param session sessione di connessione con il cluster
	 */
	private void insertMeasuresBatchTooLarge(BatchStatement toInsert, int parts, Session session) {
		Collection<Statement> statements = toInsert.getStatements();
		BatchStatement [] toInsertSmaller = new BatchStatement[parts];
		for(int i = 0; i < parts; i++) {
			toInsertSmaller[i] = new BatchStatement();
		}
		
		Iterator<Statement> iteratorStatements = statements.iterator();
		int inserted = 0;
		int indexSmallBatch = 0;
		int limitDimensionStatements = statements.size()/parts;
		
		while(iteratorStatements.hasNext()) {
			Statement stmnt = (Statement) iteratorStatements.next();
			if(inserted == limitDimensionStatements) {
				inserted = 0;
				toInsertSmaller[indexSmallBatch].add(stmnt);
				indexSmallBatch++;
			} else {
				toInsertSmaller[indexSmallBatch].add(stmnt);
			}
			
			inserted++;
		}
		
		for(int i = 0; i < parts; i++) {
			try  {
				session.execute(toInsertSmaller[i]);
			} catch(InvalidQueryException e) {
				this.insertMeasuresBatchTooLarge(toInsertSmaller[i], 2, session);
			}
		}
	}


	/* CREAZIONE DELLO SCHEMA */
	
	/**
	 * metodo per la creazione dello schema dei dati in cassandra
	 * @param dbName nome del db
	 * @param cluster cluster a cui connettersi
	 * @param session sessione di connessione con il cluster
	 */
	public void schemaCreation(String dbName, Cluster cluster, Session session) {
		
		if(cluster != null && session != null && cluster.getMetadata().getKeyspace(dbName) != null) {
			if(cluster.getMetadata().getKeyspace(dbName) == null) {
				
				//creazione del keyspace
				SchemaStatement keyspaceCreation = this.createKeyspace(dbName);
				session.execute(keyspaceCreation);
				
				session = cluster.connect(dbName);
				
				//creazione del tipo Feature
				SchemaStatement udtFeatureTypeCreation = this.createFeatureUDT();
				session.execute(udtFeatureTypeCreation);
				
				//creazione del tipo Dimension
				SchemaStatement udtDimensionTypeCreation = this.createDimensionUDT();
				session.execute(udtDimensionTypeCreation);
				
				//estrazione del riferimento ai tipi appena creati
				UserType featureType = cluster.getMetadata()
	    	    	     .getKeyspace(dbName)
	    	    	     .getUserType(this.TABLE_FEATURE);
				
				UserType dimensionType = cluster.getMetadata()
	    	    	     .getKeyspace(dbName)
	    	    	     .getUserType(this.TABLE_DIMENSIONS);
				
				//creazione della tabella dei dati
				SchemaStatement tableMeasureCreation = this.createMeasureTable(featureType, dimensionType);
				session.execute(tableMeasureCreation);
			}
		}
	}
	

	
	
	/**
	 * crea la query di creazione del keyspace
	 * @param name nome del db
	 * @return la query di creazione
	 */
	private SchemaStatement createKeyspace(String name) {
	    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
	    map.put(CLASS, STRATEGY);
	    map.put(REPLICATION_FACTOR, REPLICATION_FACTOR_VALUE);
	    
	    SchemaStatement stmnt = SchemaBuilder.createKeyspace(name)
	            .with()
	            .replication(map);  
	    
	    return stmnt;
	}
	
	/**
	 * crea la query di creazione del UDT feature (User Defined Type)
	 * @return la query di creazione
	 */
	private SchemaStatement createFeatureUDT() {
	    SchemaStatement udt = SchemaBuilder.createType(TABLE_FEATURE)
	    .addColumn(COL_ID, DataType.cint())
		.addColumn(COL_NOME, DataType.text())
		.addColumn(COL_VALORE, DataType.cdouble())
		.addColumn(COL_CONTEXT_STATUS, DataType.map(DataType.cint(), DataType.text()))
		.addColumn(COL_STATUS, DataType.text());
	    
	    return udt;
	}
	
	/**
	 * crea la query di creazione del UDT dimension (User Defined Type)
	 * @return la query di creazione
	 */
	private SchemaStatement createDimensionUDT() {
	    SchemaStatement udt = SchemaBuilder.createType(TABLE_DIMENSIONS)
	    .addColumn(COL_ID, DataType.cint())
		.addColumn(COL_NOME, DataType.text())
		.addColumn(COL_INSTANCE_ID, DataType.text());
	    
	    return udt;
	}
	
	/**
	 * crea la query di creazione della tabella delle Measure
	 * @param featureType riferimento al tipo UDT feature
	 * @param dimensionType riferimento al tipo UDT dimension
	 * @return
	 */
	private SchemaStatement createMeasureTable(UserType featureType, UserType dimensionType) {		
	    SchemaStatement stmnt = SchemaBuilder.createTable(TABLE_MEASURE)
		.addPartitionKey(COL_MACCHINA, DataType.cint())
		.addPartitionKey(COL_COMPONENTE, DataType.text())
		.addPartitionKey(COL_GIORNO, DataType.timestamp())
		.addClusteringColumn(COL_TIMESTAMP, DataType.timestamp())
		.addClusteringColumn(COL_UUID, DataType.uuid())
		.addColumn(COL_DIMENSIONI, DataType.set(dimensionType, true))
		.addColumn(COL_FEATURE, DataType.set(featureType, true))
		.addColumn(COL_STATUS, DataType.text())
		.withOptions().clusteringOrder(COL_TIMESTAMP, SchemaBuilder.Direction.DESC)
					  .clusteringOrder(COL_UUID, SchemaBuilder.Direction.ASC);
	    
	    return stmnt;
	}


	public ArrayList<Integer> getMachines() {
		return machines;
	}


	public ArrayList<String> getComponents() {
		return components;
	}


	public ArrayList<Date> getDays() {
		return days;
	}


	
	
}
