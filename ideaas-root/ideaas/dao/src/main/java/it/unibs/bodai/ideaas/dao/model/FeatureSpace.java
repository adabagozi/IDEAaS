package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Ada Bagozi
 *
 */
public class FeatureSpace {
	private static final Logger LOG = Logger.getLogger(FeatureSpace.class.getName());
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";	
	public static final String KEY_FEATURES = "features";
	
	private int id;
	private String name;
	private boolean isActive;
	private String state = "undefined";
	private Collection<Feature> features;
	private HashMap<Integer, Feature> features_list = new HashMap<>(); //TODO togliere feature e mantenere solo la versione con hashmap
	/**
	 * @param id
	 * @param name
	 * @param isActive
	 * @param features
	 */
	public FeatureSpace(int id, String name, boolean isActive, Collection<Feature> features) {
		super();
		this.id = id;
		this.name = name;
		this.isActive = isActive;
		this.features = features;
	}
	
	public FeatureSpace(int id, String name, Collection<Feature> features) {
		this.id = id;
		this.name = name;
		this.features = features;
	}
	
	public FeatureSpace(int id) {
		this(id, "", null);
	}

	public FeatureSpace() {
		this(0, "", null);
	}
	
	public FeatureSpace(int id, String name, String state, boolean isActive) {
		this.id = id;
		this.name = name;
		this.state = state;
		this.isActive = isActive;
	}

    public FeatureSpace(Document elementDocument) {
		super();
		this.setFromDocument(elementDocument);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}



	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}



	/**
	 * @return the features
	 */
	public Collection<Feature> getFeatures() {
		return this.features;
	}
	

	
	public boolean existFeatureWhithID(int id){
		for (Feature feature : this.features) {
			if(feature.getId() == id){
				return true;
			}
		}
		return false;
	}
		
	public Optional<Feature> getFeatureWhithID(int id){
		for (Feature feature : this.features) {
			if(feature.getId() == id){
				return Optional.of(feature);
			}
		}
		return Optional.empty();
	}
	
	public Optional<Feature> getFeatureWhithName(String featureName){
		for (Feature feature : this.features) {
			if(feature.getName().equalsIgnoreCase(featureName)){
				return Optional.of(feature);
			}
		}
		return Optional.empty();
	}
	
	
	/**
	 * @param features the features to set
	 */
	public void setFeatures(Collection<Feature> features) {
		this.features = features;
	}

	public void addFeature(Feature feature){
		if(this.getFeatures() == null){ 
			this.features = new ArrayList<>();
		}
		this.features.add(feature);
	}



	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
	    hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
	    hash = 53 * hash + this.id;
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		FeatureSpace fsp = (FeatureSpace) obj;
		if(!(this.name.equals(fsp.name) && this.id == fsp.id)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @author Naim
	 */
	
	public FeatureSpace(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the features_list
	 */
	public HashMap<Integer, Feature> getFeatures_list() {
		return features_list;
	}

	/**
	 * @param features_list the features_list to set
	 */
	public void setFeatures_list(HashMap<Integer, Feature> features_list) {
		this.features_list = features_list;
	}
	
	public void setFeatures_list(Collection<Feature> features) {
		this.features = features;
		
		for(Feature feature: features) {
			this.features_list.put(feature.getId(), feature);
		}
	}
	
	
	
	public JSONObject toJSON() {
		JSONObject jsonFeatureSpace = new JSONObject();
		JSONArray jsonFeatures = new JSONArray();
		
		jsonFeatureSpace.put(KEY_ID, String.format("%s", this.getId()));
		jsonFeatureSpace.put(KEY_NAME, String.format("%s", this.getName()));
	
		for(Feature feature: this.getFeatures()) {
			jsonFeatures.put(feature.toJSON());
		}
		jsonFeatureSpace.put(KEY_FEATURES, jsonFeatures);
		return jsonFeatureSpace;
	}
	
	public void setFromDocument(Document doc) throws NumberFormatException{
		this.setId(Integer.parseInt(doc.get(KEY_ID).toString()));
		this.setName(doc.get(KEY_NAME).toString());

		Collection<Document> elementsDocument = (Collection<Document>) doc.get(KEY_FEATURES);	
		for(Document elementDocument: elementsDocument){
			this.addFeature(new Feature(elementDocument));
		}
	}
	
}
