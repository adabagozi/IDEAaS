package test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Measure;
import statedetection.IStateDetectionBusinessLogic;
import statedetection.StateDetectionFactory;
import storage.MeasuresMongoDB;
import utils.DateUtils;
import weka.classifiers.bayes.net.EditableBayesNet;

public class testBuildQuery {
	
	private IDAO dao;
	private IStateDetectionBusinessLogic stateDetection;
	private DateUtils dateUtils;
	Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
	
	

	@Before
	public void setup() {
		try {
			this.dateUtils = new DateUtils();
			this.dao = DAOFactory.getDAOFactory().getDAO();
			this.stateDetection = StateDetectionFactory.getStateDetectionFactory().getStateDetectionBusinessLogic();
			mongoLogger.setLevel(Level.SEVERE);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testQuery() {
		String start_date = "21/01/2016 05:22:00";
		String interval_type = "hours";
		LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);
		int timeInterval = 1;
		Collection<Measure> measures = this.dao.readMeasuresWithQueryTimeInterval(this.dateUtils.getCollectionName(start_datetime), start_datetime, interval_type, timeInterval);
//		Optional<Iterable<Document>> readMeasuresWithQuery = this.dao.readMeasuresWithQuery(collection, "-1", "-1", "-1", "-1", "-1", "-1", start_datetime, interval_type, timeInterval);
		System.out.println("Collection name: " + this.dateUtils.getCollectionName(start_datetime) + " Start Date: " + start_datetime) ;
		System.out.println("Number of measures found: " + measures.size());
		
		/**
		 * Collection name: 2016_01 Start Date: 2016-01-21T05:22
		 * Number of measures found: 7173
		 */
		
	}
	
	@Test
	public void getData(){
		String timestampStart = "2016-01-21T04:00:00Z"; 
		String timestampEnd = "2016-01-21T04:51:00Z";
		ArrayList<DimensionInstance> dimensionInstances = new ArrayList<>();		
		DimensionInstance machine = new DimensionInstance("101143", new Dimension(3, "Macchina"));
		dimensionInstances.add(machine);
		DimensionInstance component = new DimensionInstance("Unit 3.0", new Dimension(4, "Componente"));
		dimensionInstances.add(component);
		
		String collectionName = this.dateUtils.getCollectionName(dateUtils.getLocalDateTime(timestampEnd));

		LocalDateTime startDateTime = this.dateUtils.getLocalDateTime(timestampStart);
		LocalDateTime endDateTime = this.dateUtils.getLocalDateTime(timestampEnd);
		
//		System.out.println("Collection name: " + collectionName + " Start Date: " + startDateTime + " End Date: " + endDateTime + " Macchina ID: " + machine.getInstanceId() + " Component ID: " + component.getInstanceId()) ;
		
		Collection<Measure> measures = this.dao.readMeasuresWithQueryFromTo(collectionName, "101143", "Unit 3.0", startDateTime, endDateTime);
		System.out.println(measures.size());
		
	}
}
