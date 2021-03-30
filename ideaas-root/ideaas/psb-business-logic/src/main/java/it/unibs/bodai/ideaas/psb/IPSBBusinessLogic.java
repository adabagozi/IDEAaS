package it.unibs.bodai.ideaas.psb;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;

public interface IPSBBusinessLogic {
	

	public JSONObject getSumData(FeatureSpace feature_space, String start_datetime, String end_datetime, int experimentNumber, String machineID, String componentID, String modeID, String partProgramID);
	/**
	 * @param collectionName
	 * @param dateStart
	 * @return
	 */
	public String readAllDocument(String collectionName, String dateStart);
	
	/**
	 * @param datetime
	 * @param featureSpaceID
	 * @param macchinaID
	 * @param componenteID
	 * @param utensileID
	 * @param mode
	 * @param partProgram
	 * @param intervalType
	 * @param intervalValue
	 * @param newSummarisation
	 * @param isStable
	 * @param experimentNumber
	 * @return
	 * @throws Exception
	 */
	public String stableDataSummarisation(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID, String utensileID, String mode, String partProgram,  String intervalType, int intervalValue, boolean newSummarisation, boolean isStable, int experimentNumber) throws Exception;
	
	
	/**
	 * @param datetime
	 * @param featureSpaceID
	 * @param macchinaID
	 * @param componenteID
	 * @param utensileID
	 * @param mode
	 * @param partProgram
	 * @param intervalType
	 * @param intervalValue
	 * @param newSummarisation
	 * @param isStable
	 * @param experimentNumber
	 * @return
	 * @throws Exception
	 */
	public String stableDataSummarisationTest(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID, String utensileID, String mode, String partProgram,  String intervalType, int intervalValue, boolean newSummarisation, boolean isStable, int experimentNumber, int condition, Collection<MultidimensionalRecord> newRecords) throws Exception;
	
	
	
	/**
	 * @param datetime
	 * @param featureSpaceID
	 * @param macchinaID
	 * @param componenteID
	 * @param utensileID
	 * @param mode
	 * @param partProgram
	 * @param intervalType
	 * @param intervalValue
	 * @param newSummarisation
	 * @param isStable
	 * @param experimentNumber
	 * @return
	 * @throws Exception
	 */
	public String dataSummarisation(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID, String utensileID, String mode, String partProgram,  String intervalType, int intervalValue, boolean newSummarisation, boolean isStable, int experimentNumber) throws Exception;

	/**
	 * @param datetime
	 * @param featureSpaceID
	 * @param macchinaID
	 * @param componenteID
	 * @param utensileID
	 * @param mode
	 * @param partProgram
	 * @param intervalType
	 * @param intervalValue
	 * @param newSummarisation
	 * @param isStable
	 * @param experimentNumber
	 * @return
	 * @throws Exception
	 */
	public String dataSummarisationTest(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID, String utensileID, String mode, String partProgram,  String intervalType, int intervalValue, boolean newSummarisation, boolean isStable, int experimentNumber, int condition, Collection<MultidimensionalRecord> newRecords) throws Exception;

	/**
	 * @param datetime
	 * @param featureSpaceID
	 * @param macchinaID
	 * @param componenteID
	 * @param utensileID
	 * @param mode
	 * @param partProgram
	 * @param intervalType
	 * @param intervalValue
	 * @param newSummarisation
	 * @param experimentNumber
	 * @return
	 */
	public JSONArray getSummarisedData(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID, String utensileID, String mode, String partProgram,  String intervalType, int intervalValue, boolean newSummarisation, int experimentNumber);


	/**
	 * @param featureSpaceID
	 * @param macchinaID
	 * @param componenteID
	 * @param utensileID
	 * @param mode
	 * @param partProgram
	 * @param datetime
	 * @param datetimeTwo
	 * @param increase
	 * @param experimentNumber
	 * @return
	 */
	public JSONObject getSummarisedData(FeatureSpace feature_space, String dataset, String macchinaID, String componenteID, String utensileID, String mode, String partProgram,  LocalDateTime datetime, LocalDateTime datetimeTwo, boolean increase, int experimentNumber);

	
	
	/**
	 * @param datetime
	 * @param featureSpaceID
	 * @param macchinaID
	 * @param componenteID
	 * @param utensileID
	 * @param mode
	 * @param partProgram
	 * @param intervalType
	 * @param intervalValue
	 * @return
	 * @throws Exception
	 */
	public JSONArray getMeasures(LocalDateTime datetime, int featureSpaceID, String macchinaID, String componenteID, String utensileID, String mode, String partProgram,  String intervalType, int intervalValue) throws Exception;
	
	public JSONArray getDataPoints(int experimentNumber) throws Exception;
	
	public Collection<MultidimensionalRecord> generateMultiDimRecord(Feature f1, Feature f2, int condizione, int experiment_number);
	
	public Collection<MultidimensionalRecord> generateMultiDimRecordGaussian(Feature f1, Feature f2, int condizione, int experiment_number);
	
	public JSONArray generateCluStreamSummarisedData();
	
	
	
	public int getNumDocumentsMachineForData(String machineID, LocalDateTime date);
}
