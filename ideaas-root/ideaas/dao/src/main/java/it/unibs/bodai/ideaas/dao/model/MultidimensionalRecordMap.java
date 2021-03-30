package it.unibs.bodai.ideaas.dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Daniele Comincini
 *
 */
public class MultidimensionalRecordMap implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(MultidimensionalRecordMap.class.getName());

	private HashMap<Integer,FeatureData> dataRecords;

	/**
	 * @param feature
	 */
	public MultidimensionalRecordMap(HashMap<Integer,FeatureData> dataRecords) {
		super();
		this.dataRecords = dataRecords;
	}

	/**
	 *
	 */
	public MultidimensionalRecordMap() {
		super();
		this.dataRecords = new HashMap<>();
	}


	/**
	 * @return the featureData
	 */
	public HashMap<Integer,FeatureData> getDataRecords() {
		return dataRecords;
	}

	/**
	 * @param featureData the featureData to set
	 */
	public void setDataRecords(HashMap<Integer,FeatureData> dataRecords) {
		this.dataRecords = dataRecords;
	}

	public void addFeatureData(FeatureData featureData){
		if (this.getDataRecords() == null){
			this.dataRecords = new HashMap<>();
		}
		this.dataRecords.put(featureData.getFeature().getId(),featureData);
	}	

}
