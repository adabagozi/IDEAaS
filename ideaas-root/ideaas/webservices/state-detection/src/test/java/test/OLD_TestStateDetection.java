	package test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.implementation.storage.MeasureMongoDB;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;

public class OLD_TestStateDetection {
	private IDAO dao;
	
	@Before
	public void setup() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void readSummarisedData() {
		System.out.println("readSummarisedData");
		
		Optional<SummarisedData> summarisedDataById = this.dao.readSummarisedDataById("data_summarisation", "5950c849f4291f22515e79a6");
		
		if(summarisedDataById.isPresent()) {

			System.out.println("Expertiment: "+summarisedDataById.get().getSynthesis());
			
		}else {
			System.out.println("File non trovato: 595945f90a9e3ea11ae6b22f");
		}
	}
	
//	@Test
	public void readFeatureSpace() {
		//System.out.println("readSummarisedData");
		Optional<FeatureSpace> readFeatureSpace = this.dao.readFeatureSpace(2);
		
//		Optional<SummarisedData> summarisedDataById = this.dao.readSummarisedDataById("summariserd_data", "595945f90a9e3ea11ae6b22f");
		if(readFeatureSpace.isPresent()) {

			System.out.println("Expertiment: "+readFeatureSpace.get().getName());
			
		}else {
			System.out.println("File non trovato: 595945f90a9e3ea11ae6b22f");
		}
	}
	
//	@Test 
	public void getMeasure() {
		
		MeasureMongoDB measureMongoDB = new MeasureMongoDB();
		LocalDateTime localDateTime = LocalDateTime.now();
		//BasicDBObject measure = measureMongoDB.buildQueryID("58791504f4291f49578fa6d1");
		BasicDBObject measure = measureMongoDB.buildQuery("", "", "", "", "", "", localDateTime, "", 2000);
		Optional<Document> measure_obj = this.dao.readDoc("summariserd_data", measure);
		System.out.println(measure_obj);
		
		
	}
	
	
	
}
