package statedetection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;

import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.timing.StateDetectionTiming;
import xml_models.SendAlertResponse;

public interface IStateDetectionBusinessLogic {

	/**
	 * API - Measures
	 */
	public ArrayList<Measure> getAlertStatusMeasures(ArrayList<DimensionInstance> activeComponents) throws IOException;
	
	public Collection<Measure> updatedSendAlertQuery(String collection_name, Date a, Date t, ArrayList<DimensionInstance> dimension_instances);
	
	/**
	 * Tests
	 */
	public ArrayList<BasicDBObject> testTimingGetAlertStatus(ArrayList<DimensionInstance> activeComponents) throws IOException;

	public StateDetectionTiming testGetDataTiming(String start_date, String end_date, ArrayList<DimensionInstance> dimensionInstances);


}
