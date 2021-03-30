//package dao;
//
//import java.sql.Date;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//
//import org.junit.Test;
//
//import com.datastax.driver.core.BoundStatement;
//import com.datastax.driver.core.ResultSet;
//import com.datastax.driver.core.Row;
//import com.datastax.driver.core.Session;
//import com.datastax.driver.core.UserType;
//import com.datastax.driver.core.schemabuilder.SchemaBuilder;
//import com.datastax.driver.core.schemabuilder.SchemaStatement;
//
//import it.unibs.bodai.ideaas.dao.implementation.ManageDBCassandra;
//import it.unibs.bodai.ideaas.dao.implementation.storage.MeasureCassandra;
//import it.unibs.bodai.ideaas.dao.implementation.storage.SchemaDefinitionCassandra;
//import it.unibs.bodai.ideaas.dao.model.Context;
//import it.unibs.bodai.ideaas.dao.model.ContextStatus;
//import it.unibs.bodai.ideaas.dao.model.Dimension;
//import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
//import it.unibs.bodai.ideaas.dao.model.Feature;
//import it.unibs.bodai.ideaas.dao.model.FeatureData;
//import it.unibs.bodai.ideaas.dao.model.Measure;
//
//public class TestCassandra
//{
//	private ManageDBCassandra db;
//	private Session session;
//	
//	@Test
//	public void useCassandra()
//	{   
//	    //creazione del keyspace
////	    createKeyspace();
//		
//		//connessione al sistema con keyspace
//		connectDB(true);
//		
//		//STAMPA CREAZIONE SCHEMA DB
////		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
////	    map.put(SchemaDefinitionCassandra.CLASS, SchemaDefinitionCassandra.STRATEGY);
////	    map.put(SchemaDefinitionCassandra.REPLICATION_FACTOR, SchemaDefinitionCassandra.REPLICATION_FACTOR_VALUE);
////	    
////	    SchemaStatement keyspaceStmnt = SchemaBuilder.createKeyspace("porta_db")
////	            .with().replication(map);  
////	    
////		SchemaStatement udtFeatureTypeCreation = SchemaDefinitionCassandra.createFeatureUDT();
////		SchemaStatement udtDimensionTypeCreation = SchemaDefinitionCassandra.createDimensionUDT();
////
////		UserType featureType = db.getCluster().getMetadata()
////	    	     .getKeyspace(ManageDBCassandra.KEYSPACE_NAME)
////	    	     .getUserType(SchemaDefinitionCassandra.TABLE_FEATURE);
////		
////		UserType dimensionType = db.getCluster().getMetadata()
////	    	     .getKeyspace(ManageDBCassandra.KEYSPACE_NAME)
////	    	     .getUserType(SchemaDefinitionCassandra.TABLE_DIMENSIONS);
////		
////		SchemaStatement tableMeasureCreation = SchemaDefinitionCassandra.createMeasureTable(featureType, dimensionType);
////		
////		System.out.println(keyspaceStmnt.toString());
////		System.out.println(udtFeatureTypeCreation.toString());
////		System.out.println(udtDimensionTypeCreation.toString());
////		System.out.println(tableMeasureCreation.toString());
//		
////		db.createPreparedStatementInsertion(); 
////		db.getPartitions();
//	    //prova inserimento measure
////	    insertMeasuresProva(db, session);
//	    
//		//query measure cassandra
////		LocalDateTime datetime = LocalDateTime.of(2015, 11, 2, 0, 0);
////		Collection<Measure> tempMeasures = db.readMeasures(-1, "-1", datetime, "hours", 48);
//
//		//disconnessione dal db
//	    disconnectDB();
//	}
//	
//	private void connectDB(boolean keyspaceExists)
//	{
//		if(keyspaceExists)
//		{
//			this.db = new ManageDBCassandra("127.0.0.1", "porta_db");
//		}
//		else
//		{
//			this.db = new ManageDBCassandra("127.0.0.1");
//		}
//		this.session = this.db.openConnection();
////		ResultSet rs = this.session.execute("select release_version from system.local");
////	    Row row = rs.one();
////	    System.out.println(row.getString("release_version"));
//	}
//	
//	private void disconnectDB()
//	{
//		this.session.close();
//		this.db.closeConnection();
//	}
//	
//	private void createKeyspace()
//	{
//		connectDB(false);
//		db.schemaCreation();
//		disconnectDB();
//	}
//	
//	private void insertMeasuresProva(ManageDBCassandra db, Session session)
//	{
//		String insertStmnt = "INSERT INTO " + SchemaDefinitionCassandra.TABLE_MEASURE
//				+ " ( " 
//				+ SchemaDefinitionCassandra.COL_MACCHINA + ", "
//				+ SchemaDefinitionCassandra.COL_COMPONENTE + ", "
//				+ SchemaDefinitionCassandra.COL_GIORNO + ", "
//				+ SchemaDefinitionCassandra.COL_TIMESTAMP + ", "
//				+ SchemaDefinitionCassandra.COL_UUID + ", "
//				+ SchemaDefinitionCassandra.COL_DIMENSIONI + ", "
//				+ SchemaDefinitionCassandra.COL_FEATURE + ", "
//				+ SchemaDefinitionCassandra.COL_STATUS
//				+ " ) "
//				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
//	    
//	    MeasureCassandra measureCassandra = new MeasureCassandra(db.getCluster(), session, session.prepare(insertStmnt));
//	    
//	    
//	    //Istanza Measure di test per l'inserimento corretto
//	    Measure measure = new Measure();
//	    
//	    //creazione delle dimension della measure
//	    Collection<DimensionInstance> dims = new ArrayList<DimensionInstance>();
//	    
//	    DimensionInstance macchina = new DimensionInstance();
//	    Dimension dim1 = new Dimension(3);
//	    macchina.setDimension(dim1);
//	    macchina.setName("Macchina");
//	    macchina.setInstanceId("12345");
//	    dims.add(macchina);
//	    
//	    DimensionInstance componente = new DimensionInstance();
//	    Dimension dim2 = new Dimension(4);
//	    componente.setDimension(dim2);
//	    componente.setName("Componente");
//	    componente.setInstanceId("componente_prova");
//	    dims.add(componente);
//	    
//	    DimensionInstance partProgram = new DimensionInstance();
//	    Dimension dim3 = new Dimension(5);
//	    partProgram.setDimension(dim3);
//	    partProgram.setName("partProgram");
//	    partProgram.setInstanceId("partProgram_prova");
//	    dims.add(partProgram);
//	    
//	    DimensionInstance tool = new DimensionInstance();
//	    Dimension dim4 = new Dimension(6);
//	    tool.setDimension(dim4);
//	    tool.setName("tool");
//	    tool.setInstanceId("tool_prova");
//	    dims.add(tool);
//	    
//	    DimensionInstance mode = new DimensionInstance();
//	    Dimension dim5 = new Dimension(7);
//	    mode.setDimension(dim5);
//	    mode.setName("mode");
//	    mode.setInstanceId("mode_prova");
//	    dims.add(mode);
//	    
//	    //creazione delle feature della measure
//	    Collection<FeatureData> features = new ArrayList<FeatureData>();
//	    
//	    //FEATURE 1
//    	FeatureData featureData1 = new FeatureData();
//	    Feature feature1 = new Feature();
//	    feature1.setId(1);
//	    feature1.setName("feature1");
//	    featureData1.setFeature(feature1);
//	    featureData1.setValue(1);
//	    
//	    //creazione dei context status della relativa feature
//	    Collection<ContextStatus> contextsStatus = new ArrayList<ContextStatus>();
//	    Context context1feature1 = new Context();
//	    context1feature1.setId(1);
//	    ContextStatus contextStatus1feature1 = new ContextStatus(context1feature1, "status 1");
//	    contextsStatus.add(contextStatus1feature1);
//
//		Context context2feature1 = new Context();
//		context2feature1.setId(2);
//		ContextStatus contextStatus2feature1 = new ContextStatus(context2feature1, "status 2");
//		contextsStatus.add(contextStatus2feature1);
//	    
//	    featureData1.setContextsStatus(contextsStatus);
//	    featureData1.setStatus("status_1");
//		    
//		features.add(featureData1);
//	    
//	    //FEATURE 2
//	    {
//	    	FeatureData featureData2 = new FeatureData();
//		    Feature feature2 = new Feature();
//		    feature2.setId(2);
//		    feature2.setName("feature2");
//		    featureData2.setFeature(feature2);
//		    featureData2.setValue(2);
//		    
//		    //creazione dei context status della relativa feature
//		    Collection<ContextStatus> contextsStatus2 = new ArrayList<ContextStatus>();
//		    Context context1feature2 = new Context();
//		    context1feature2.setId(1);
//		    ContextStatus contextStatus1feature2 = new ContextStatus(context1feature2, "status 1");
//		    contextsStatus2.add(contextStatus1feature2);
//		    
//		    Context context2feature2 = new Context();
//		    context2feature2.setId(2);
//		    ContextStatus contextStatus2feature2 = new ContextStatus(context2feature2, "status 2");
//		    contextsStatus2.add(contextStatus2feature2);
//		    
//		    featureData2.setContextsStatus(contextsStatus2);
//		    featureData2.setStatus("status_2");
//		    
//		    features.add(featureData2);
//	    }
//	    
//	    //inserimento dei valori nella measure
//	    measure.setTimestamp(Calendar.getInstance().getTime());
//	    measure.setDimensionInstances(dims);
//	    measure.setFeaturesData(features);
//	    measure.setStatus("status_prova");
//	    BoundStatement stmnt = measureCassandra.createInsertQuery(measure);
//	    session.execute(stmnt);
//	}
//}
