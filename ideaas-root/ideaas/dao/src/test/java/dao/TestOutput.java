package dao;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import com.datastax.driver.core.Session;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.implementation.ManageDBCassandra;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;
import utils.DateUtils;

public class TestOutput {
	private IDAO dao;
	private IDAO daoCassandra;
	private DateUtils dateUtils;
	
	Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
	Logger cassandraLogger = Logger.getLogger("com.datastax.driver");

	@Before
	public void setup() {
		try {
			this.dateUtils = new DateUtils();
			this.dao = DAOFactory.getDAOFactory().getDAO();
			this.daoCassandra = DAOFactory.getDAOFactory().getDAOCassandra();
			
			mongoLogger.setLevel(Level.SEVERE);
			cassandraLogger.setLevel(Level.SEVERE);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

//	@Test
//	public void TestOutputDate() {
//		String start_date = "02/11/2015 12:00:00";
//		String end_date = "03/11/2015 12:00:00";
//		String collection = "2015_11";
//		
//		String machine = "-1";
//		String component ="-1";
//		
//		String interval_type = "hours";
//		
//		boolean isMongo = false;
//		
//		int initial_i = 0;
//		int limit_i = 0;
//		int adding_i = 0;
//
//		switch(interval_type)
//		{
//			case "seconds":
//				initial_i = 60;
//				limit_i = 3600;
//				adding_i = 60;
//				break;
//				
//			case "minutes":
//				initial_i = 5;
//				limit_i = 60;
//				adding_i = 5;
//				break;
//				
//			case "hours":
//				initial_i = 1;
//				limit_i = 24;
//				adding_i = 1;
//				break;
//				
//		}
//		
//		for(int i = initial_i; i<=limit_i; i= i+adding_i)
//		{
//			LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);
//			LocalDateTime end_datetime = dateUtils.getDateTimeFromString(end_date);
//			
//			System.out.print("\n"+interval_type+": " + i + "    ");
//			
//			int interval_value = i;
//
//			while (start_datetime.isBefore(end_datetime)) {
//				System.out.print("=");
//				//TODO SPOSTARE CALCOLO TEMPO LETTURA
//				if(isMongo){
//					long extractionStartTime = System.currentTimeMillis();
//					Collection<Measure> readMeasures = this.dao.readMeasures(collection, "-1", "-1", "-1", machine, component, start_datetime, interval_type, interval_value);
//					this.dao.insertDataAcquisitionTiming(new DataAcquisitionTiming(readMeasures.size(), 
//							0, System.currentTimeMillis() - extractionStartTime,
//							"MongoDB_" + interval_type + "," + interval_value +
//							"," + component.replace(" ",".") + "," + start_datetime));
//				}else{
//					long extractionStartTime = System.currentTimeMillis();
//					
//					Collection<Measure> readMeasures = this.daoCassandra.readMeasures(collection, "-1", "-1", "-1", machine, component, start_datetime, interval_type, interval_value);
//					this.dao.insertDataAcquisitionTiming(new DataAcquisitionTiming(readMeasures.size(), 
//							0, System.currentTimeMillis() - extractionStartTime,
//							"Cassandra_" + interval_type + "," + interval_value +
//							"," + component.replace(" ",".") + "," + start_datetime));
//				}
//
//				switch(interval_type)
//				{
//					case "seconds":
//						start_datetime = start_datetime.plusSeconds(i);
//						//da gestire l'andare oltre endtime, come sotto
//						break;
//					
//					case "minutes":
//						start_datetime = start_datetime.plusMinutes(i);
//						//da gestire l'andare oltre endtime, come sotto
//						break;
//						
//					case "hours":
//						start_datetime = start_datetime.plusHours(i);
//						if(start_datetime.plusHours(i).isAfter(end_datetime))
//						{
//							interval_value = end_datetime.getHour() - start_datetime.getHour();
//							if(interval_value < 0)
//							{
//								interval_value += 24;
//							}
//						}
//						break;
//				}
//			}
//		}
//	}

}
