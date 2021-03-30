package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.StaticVariables;

/**
 * Class that reppresent a multidimensional record
 * @author Ada Bagozi
 *
 */
public class MultidimensionalRecord {
	private static final Logger LOG = Logger.getLogger(MultidimensionalRecord.class.getName());

	private Collection<FeatureData> dataRecords;

	/**
	 * @param feature
	 */
	public MultidimensionalRecord(Collection<FeatureData> featureData) {
		super();
		this.dataRecords = featureData;
	}

	/**
	 *
	 */
	public MultidimensionalRecord() {
		super();
		this.dataRecords = new ArrayList<>();
	}


	/**
	 * @return the featureData
	 */
	public Collection<FeatureData> getDataRecords() {
		return dataRecords;
	}

	/**
	 * @param featureData the featureData to set
	 */
	public void setDataRecords(Collection<FeatureData> featureData) {
		this.dataRecords = featureData;
	}

	public void addFeatureData(FeatureData featureData){
		if (this.getDataRecords() == null){
			this.dataRecords = new ArrayList<>();
		}
		this.dataRecords.add(featureData);
	}


	public void setFeaturesDataFromDocument(Document doc, FeatureSpace fs) throws NumberFormatException{
		Collection<Document> featuresDocument = (Collection<Document>) doc.get("features");	
		for(Document featureDataDocument: featuresDocument){
			int id = Integer.parseInt(featureDataDocument.get("id").toString());
			Optional<Feature> feature = fs.getFeatureWhithID(id);
			
			if (feature.isPresent()){
				double value = Double.parseDouble(featureDataDocument.get("value").toString());
				if (StaticVariables.normalize) {
					value = feature.get().normalizeData(value);
				}
				this.addFeatureData(new FeatureData(feature.get(), value));
			}
		}
	}
	
	public void setFromDocument(Document doc) throws NumberFormatException{
		Collection<Document> elementsDocument = (Collection<Document>) doc.get("multi_dim_record");	
//		System.out.println("elementsDoc: " + elementsDocument.toString());
		for(Document featureDataDoc: elementsDocument){
			FeatureData featureDataElement = new FeatureData();
			featureDataElement.setFromDocument(featureDataDoc);

			this.addFeatureData(featureDataElement);
		}
	}
	
	public JSONObject toJSON(){
		JSONObject jsonMultiRecord = new JSONObject();
		JSONArray jsonRecords = new JSONArray();
//		System.out.println("DataRecords Size" + this.getDataRecords().size());
		for (FeatureData dataRecords : this.getDataRecords()) {
			jsonRecords.put(dataRecords.toJSON());
		}
		jsonMultiRecord.put("multi_dim_record", jsonRecords);

		return jsonMultiRecord;
	}

	public double[] dataRecordsToDoubleArray(){
		double[] arrayElement = new double[this.getDataRecords().size()];
		int j = 0;

		for (FeatureData featureData : this.getDataRecords()) {
			arrayElement[j] = featureData.getValue();
			j++;
		}
		
		return arrayElement;
	}
}
