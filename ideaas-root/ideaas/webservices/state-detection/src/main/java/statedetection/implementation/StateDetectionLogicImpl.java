package statedetection.implementation;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;

import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.timing.StateDetectionTiming;
import statedetection.IStateDetectionBusinessLogic;
import storage.MeasuresMongoDB;
import utils.DateUtils;

public class StateDetectionLogicImpl implements IStateDetectionBusinessLogic {

	private MeasuresMongoDB measuresMongoDB;
	private IDAO dao;
	private DateUtils dateUtils;

	public StateDetectionLogicImpl() throws IOException {
		this.measuresMongoDB = new MeasuresMongoDB();
		this.dao = DAOFactory.getDAOFactory().getDAO();
		this.dateUtils = new DateUtils();
	}

	public ArrayList<Measure> getAlertStatusMeasures(ArrayList<DimensionInstance> activeComponents) throws IOException {
		ArrayList<org.bson.Document> currentComponentsDocuments = measuresMongoDB.currentSystemMeasures(activeComponents);
		ArrayList<Measure> currentSystemMeasures = new ArrayList<>();
		for(org.bson.Document doc : currentComponentsDocuments) {
			Measure m = new Measure();
			m.setFromDocument(doc);
			currentSystemMeasures.add(m);
		}
		return currentSystemMeasures;
	}

	@Override
	public Collection<Measure> updatedSendAlertQuery(String collection_name, Date a, Date t, ArrayList<DimensionInstance> dimension_instances) {
		Collection<Measure> dimensions_measures_minute = measuresMongoDB.updatedSendAlertMeasures(collection_name, a, t, dimension_instances);
		return dimensions_measures_minute;
	}

	/***
	 * Tests
	 *
	 */
	@Override
	public ArrayList<BasicDBObject> testTimingGetAlertStatus(ArrayList<DimensionInstance> activeComponents) throws IOException {
		return measuresMongoDB.testTimingGetAlertStatus(activeComponents);
	}

	@Override
	public StateDetectionTiming testGetDataTiming(String start_date, String end_date, ArrayList<DimensionInstance> dimensionInstances) {
		return measuresMongoDB.testGetDataTiming(start_date, end_date, dimensionInstances);
	}


}
