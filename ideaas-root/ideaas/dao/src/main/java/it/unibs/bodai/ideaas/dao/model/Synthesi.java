package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yahoo.labs.samoa.instances.Instance;

import utils.StaticVariables;
import utils.UniqueList;


/**
 * @author Ada Bagozi
 *
 */
public class Synthesi {
	private static final Logger LOG = Logger.getLogger(Synthesi.class.getName());
	private static final int dataPercentage = 1;
	public static final String KEY_ID = "id";
	public static final String KEY_PARENT_IDs = "previous_syntheses";
	public static final String KEY_PARENT_HISTORY_IDs = "history_previous_syntheses";
	public static final String KEY_SS = "SS";
	public static final String KEY_STATUS = "state";
	public static final String KEY_RADIUS = "radius";
	public static final String KEY_RECORD_NUMBER = "number_of_records";
	public static final String KEY_RECORD_NUMBER_TOTAL = "number_of_records_total";
	public static final String KEY_CENTROID = "centroid";
	public static final String KEY_CENTROID_DIST_PREV = "centroid_dist_from_prev";
	public static final String KEY_CENTROID_DIST_STAB = "centroid_dist_from_stab";
	public static final String KEY_RADIUS_DIST_PREV = "radius_dist_from_prev";
	public static final String KEY_RADIUS_DIST_STAB = "radius_dist_from_stab";
	
	
	//TODO da togliere in produzione;
	public static final String KEY_ERROR = "error";
	public static final String KEY_OK = "ok";
	public static final String KEY_UNCHANGED = "unchanged";
	public static final String KEY_WARNING = "warning";
	
	private int id;
	private HashMap<Integer, SynthesiElement> synthesiElements;
	private long numberData;
	List<Integer> parentIdList = new UniqueList<>();
	List<Integer> parentHistoryIdList = new UniqueList<>();
	
	private double centroidDistPrev = -1;
	private double centroidDistStab = -1;
	private double radiusDistPrev = -1;
	private double radiusDistStab = -1;
	
	private double radius = 0;
	private int totalData = 0;

	private Cluster cluster;

	private String status = KEY_UNCHANGED;
	
	//TODO da togliere in produzione;
	/**
	 * Stato della sintesi: stato: 0 - sintesi supera controlli, 1 - sintesi in warning, 2 - sintesi non significativa 
	 */
	private int state;
	
	public ArrayList<Instance> instances = new ArrayList<>(); 
	
	/**
	 * @param synthesiElements
	 * @param numberData
	 * @param cluster
	 */
	public Synthesi(HashMap<Integer, SynthesiElement> synthesiElements, long numberData, int state, Cluster cluster) {
		super();
		this.synthesiElements = synthesiElements;
		this.numberData = numberData;
		this.state = state;
		this.cluster = cluster;
	}
	
	/**
	 * @param synthesiElements
	 * @param numberData
	 * @param cluster
	 */
	public Synthesi(int id, HashMap<Integer, SynthesiElement> synthesiElements, long numberData, int state, Cluster cluster, List<Integer> parentIdList, List<Integer> parentHistoryIdList) {
		super();
		this.id = id;
		this.synthesiElements = synthesiElements;
		this.numberData = numberData;
		this.state = state;
		this.cluster = cluster;
		this.parentIdList = parentIdList;
		this.parentHistoryIdList = parentHistoryIdList;
	}
	
	public Synthesi() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Synthesi(int id, List<Integer> parentIdList, List<Integer> parentHistoryIdList) {
		super();
		this.id = id;
		this.parentIdList = parentIdList;
		this.parentHistoryIdList = parentHistoryIdList;
		// TODO Auto-generated constructor stub
	}
	
	public Synthesi(Document synthesiDocument) {
		super();
		this.setFromDocument(synthesiDocument);
	}
	/**
	 * @return the synthesiElements
	 */
	public HashMap<Integer, SynthesiElement> getSynthesiElements() {
		return synthesiElements;
	}
	/**
	 * @param synthesiElements the synthesiElements to set
	 */
	public void setSynthesiElements(HashMap<Integer, SynthesiElement> synthesiElements) {
		this.synthesiElements = synthesiElements;
	}

	/**
	 * @param synthesiElement add synthesi element
	 */
	public void addSynthesiElements(SynthesiElement synthesiElement) {
		if(this.synthesiElements == null){
			this.synthesiElements = new HashMap<>();
		}
		this.synthesiElements.put(synthesiElement.getFeature().getId(), synthesiElement);
	}
	/**
	 * @return the elementNumber
	 */
	public long getNumberData() {
		return numberData;
	}
	/**
	 * @param numberData the elementNumber to set
	 */
	public void setNumberData(long numberData) {
		this.numberData = numberData;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the cluster
	 */
	public Cluster getCluster() {
		return cluster;
	}
	/**
	 * @param cluster the cluster to set
	 */
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
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
	 * @return the parentIdArrayList
	 */
	public List<Integer> getParentIdList() {
		if (parentIdList == null) {
			parentIdList = new UniqueList<>();
		}
		return parentIdList;
	}
	
	public String getParentIdListAsString() {
		String listString = "";
		for( Integer i: this.parentIdList) {
			if(listString.equalsIgnoreCase("")) {
				listString = listString + i; 
			}else {

				listString = listString + ", " + i;
			}
		}
		return listString;
	}
	
//	/**
//	 * @return the parentIdArrayList
//	 */
//	public String parentIdToString() {
//		String listIds = "";
//		if (this.getParentIdArrayList().isEmpty()) {
//			listIds = "null";
//		}else {
//			for (int i = 0; i < this.getParentIdArrayList().size(); i++) {
//				int id = this.getParentIdArrayList().get(i);
//				if (i==0) {
//					listIds +=  id;
//				}else {
//					listIds +=   ", " + id;
//				}
//			}
//		}
//		
//		return listIds;
//	}
	/**
	 * @param parentIdList the parentIdArrayList to set
	 */
	public void setParentIdList(List<Integer> parentIdList) {
		this.parentIdList = parentIdList;
	}

	
	public List<Integer> getParentHistoryId() {
		if (parentHistoryIdList == null) {
			parentHistoryIdList = new UniqueList<>();
		}
		return parentHistoryIdList;
	}

	public void setParentHistoryIdList(List<Integer> parentHistoryIdList) {
		this.parentHistoryIdList = parentHistoryIdList;
	}

	/**
	 * @param totalDataNumber numero totale di elementi coinvolti
	 * @return codice associato allo stato: 0 - sintesi supera controlli, 1 - sintesi in warning, 2 - sintesi non significativa
	 */
	public int checkSynthesi(int totalDataNumber){
		if(totalDataNumber>0){
			int state = 0;
			double percentageLimit = 0.1;

			double percentageLimitWarning = 10;
			double percentValues = this.getNumberData()*100/totalDataNumber;
			if(percentValues <= percentageLimit){
				state = 2;
			}
			if(percentValues > percentageLimit && percentValues<=percentageLimitWarning){
				state = 1;
			}
			this.setState(state);
			return state;
		}
		return 2;
	}

	/**
	 * TODO -> magari potrebbe essere interessante o meglio metterlo come 
	 * Se l'oggetto in cui sto facendo lo stream è grande posso usare parallelStram.
	 * @return
	 */
	public double calculateRadius() {
		double ls2N = this.synthesiElements.entrySet().stream()
				.mapToDouble(s -> s.getValue().getLs())
				.map(d -> d*d)
				.sum();
		ls2N /= Math.pow(this.getNumberData(), 2);
		double ssN = this.getSS() / this.getNumberData();
		return Math.sqrt(ssN - ls2N);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getDensity() {
		if (this.getRadius()>0) {
			return this.getNumberData()/Math.pow(this.getRadius(), this.synthesiElements.size());
		}
		return StaticVariables.EPSILON;
	}
	/**
	 * TODO -> magari potrebbe essere interessante o meglio metterlo come 
	 * @return
	 */
	/**
	 * @return
	 */
	public double getSS() {
		return this.synthesiElements.entrySet().stream().mapToDouble(s -> s.getValue().getSs()).sum();
		/*double sS = 0;
		for (SynthesiElement s : this.synthesiElements) {
			sS = sS+s.getSs();
		}
		return sS;*/
	}


	/**
	 * Metodo per la restituzione del numero di elementi coinvolti (feature coinvolte)
	 * @return size of synthesiElements
	 */
	public int size() {
		return this.synthesiElements.size();
	}

	public double getDistance(Synthesi other) {
		double retVal = 0;

		List<Double> lsn1 = new ArrayList<>();
		List<Double> lsn2 = new ArrayList<>();

		if (this.size() != other.size()) {
			throw new RuntimeException();
		}

		for(Entry<Integer, SynthesiElement> entry : this.getSynthesiElements().entrySet()) {
			lsn1.add(entry.getValue().getCenterValue());
			lsn2.add(other.getSynthesiElements().get(entry.getKey()).getCenterValue());
		}
		for (int i=0; i<lsn1.size(); i++) {
			retVal += Math.pow(lsn1.get(i) - lsn2.get(i), 2);
		}
		return Math.sqrt(retVal);	
	}

	
	/**
	 * @return the centroidDistPrev
	 */
	public double getCentroidDistPrev() {
		return centroidDistPrev;
	}

	/**
	 * @return the centroidDistStab
	 */
	public double getCentroidDistStab() {
		return centroidDistStab;
	}

	/**
	 * @return the radiusDistPrev
	 */
	public double getRadiusDistPrev() {
		return radiusDistPrev;
	}

	/**
	 * @return the radiusDistStab
	 */
	public double getRadiusDistStab() {
		return radiusDistStab;
	}

	/**
	 * @param centroidDistPrev the centroidDistPrev to set
	 */
	public void setCentroidDistPrev(double centroidDistPrev) {
		this.centroidDistPrev = centroidDistPrev;
	}

	/**
	 * @param centroidDistStab the centroidDistStab to set
	 */
	public void setCentroidDistStab(double centroidDistStab) {
		this.centroidDistStab = centroidDistStab;
	}

	/**
	 * @param radiusDistPrev the radiusDistPrev to set
	 */
	public void setRadiusDistPrev(double radiusDistPrev) {
		this.radiusDistPrev = radiusDistPrev;
	}

	/**
	 * @param radiusDistStab the radiusDistStab to set
	 */
	public void setRadiusDistStab(double radiusDistStab) {
		this.radiusDistStab = radiusDistStab;
	}
	

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	
	public int getTotalData() {
		return totalData;
	}

	public void setTotalData(int totalData) {
		this.totalData = totalData;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		if(this.status.equalsIgnoreCase(KEY_UNCHANGED)) {
			this.status = status;
		}else if (this.status.equalsIgnoreCase(KEY_OK) && (status.equalsIgnoreCase(KEY_WARNING) || status.equalsIgnoreCase(KEY_ERROR))) {
			this.status = status;
		}else if (this.status.equalsIgnoreCase(KEY_WARNING) && status.equalsIgnoreCase(KEY_ERROR)) {
			this.status = status;
		}
	}

	public void checkSythesisState(ArrayList<Synthesi> otherSynthesesStab, ArrayList<Synthesi> otherSynthesesPrev) {
		if (this.getNumberData() > (this.dataPercentage*this.getTotalData()/100)) {
			for(Synthesi s: otherSynthesesPrev) {
				this.setCentroidDistPrev(this.getCentroidDistPrev() + this.getDistance(s));
				this.setRadiusDistPrev(this.getRadiusDistPrev() + Math.abs(this.getRadius() - s.getRadius()));
			}
			
			for(Synthesi s: otherSynthesesStab) {
				this.setCentroidDistStab(this.getCentroidDistStab() + this.getDistance(s));
				this.setRadiusDistStab(this.getRadiusDistStab() + Math.abs(this.getRadius() - s.getRadius()));
			}
			
			//TODO magari la variazione la valutiamo dopo una certa epsilon
			if (this.getCentroidDistPrev() > 0 
					|| this.getRadiusDistPrev() > 0 
					|| otherSynthesesPrev.isEmpty()) {
				this.setStatus(KEY_OK);
			}
			this.checkSythesisElementsStatus(otherSynthesesPrev);
		}else {
			this.setStatus(KEY_UNCHANGED);
		}
	}
	private boolean checkRadius = false;
	private void checkSythesisElementsStatus(ArrayList<Synthesi> otherSynthesesPrev) {
		for(Entry<Integer, SynthesiElement> synthesiElementEntrySet : this.getSynthesiElements().entrySet()) {
			String valueToCheck = synthesiElementEntrySet.getValue().getStatusCenter();
			if (this.checkRadius) {
				valueToCheck = synthesiElementEntrySet.getValue().getStatusRadius();
			}
			if(valueToCheck.equalsIgnoreCase(KEY_UNCHANGED)) {
				for(Synthesi s: otherSynthesesPrev) {
					SynthesiElement sOther = s.getSynthesiElements().get(synthesiElementEntrySet.getKey());
					double distCenter = Math.abs(sOther.getCenterValue() - synthesiElementEntrySet.getValue().getCenterValue());
					double distRadius = Math.abs(sOther.getDeviation() - synthesiElementEntrySet.getValue().getDeviation());
					if (distCenter != 0 || distRadius != 0) {
						this.setStatus(KEY_OK);
						synthesiElementEntrySet.getValue().setStatus(KEY_OK);
					}
				}
			}else {
				this.setStatus(valueToCheck);
			}
		}
	}
	
	
	public JSONObject toJSON() {
		JSONObject jsonSynthesi = new JSONObject();
		JSONArray jsonSynthesiElement = new JSONArray();
		JSONArray jsonParentIDs = new JSONArray();
		JSONArray jsonHistoryParentIDs = new JSONArray();
		JSONArray dataPoints = new JSONArray();
		
		jsonSynthesi.put(KEY_RECORD_NUMBER, String.format("%s", this.getNumberData()));
//		jsonSynthesi.put(KEY_RECORD_NUMBER_TOTAL, String.format("%s", this.getTotalData()));
		jsonSynthesi.put(KEY_SS, String.format("%s", this.getSS()));
		jsonSynthesi.put(KEY_RADIUS, String.format("%s", this.getRadius()));
		jsonSynthesi.put(KEY_ID, String.format("%s", this.getId()));

		jsonSynthesi.put(KEY_CENTROID_DIST_PREV, String.format("%s", this.getCentroidDistPrev()));
		jsonSynthesi.put(KEY_CENTROID_DIST_STAB, String.format("%s", this.getCentroidDistStab()));
		jsonSynthesi.put(KEY_RADIUS_DIST_PREV, String.format("%s", this.getRadiusDistPrev()));
		jsonSynthesi.put(KEY_RADIUS_DIST_STAB, String.format("%s", this.getRadiusDistStab()));
		
		for(Integer parentID: this.getParentIdList()) {
			JSONObject jsonParentID = new JSONObject();
			jsonParentID.put(KEY_ID, parentID);
			jsonParentIDs.put(jsonParentID);
		}
		jsonSynthesi.put(KEY_PARENT_IDs, jsonParentIDs);
		
		for(Integer historyParentID: this.getParentHistoryId()) {
			JSONObject jsonParentID = new JSONObject();
			jsonParentID.put(KEY_ID, historyParentID);
			jsonHistoryParentIDs.put(jsonParentID);
		}
		jsonSynthesi.put(KEY_PARENT_HISTORY_IDs, jsonHistoryParentIDs);
		
		
		jsonSynthesi.put(KEY_STATUS, String.format("%s", this.getStatus()));

		for(Entry<Integer, SynthesiElement> synthesiElement : this.getSynthesiElements().entrySet()) {
			
			jsonSynthesiElement.put(synthesiElement.getValue().toJSON());
		}
		jsonSynthesi.put(KEY_CENTROID, jsonSynthesiElement);
		
		for(Instance i: this.instances) {
			JSONArray instJSON = new JSONArray();
			for(Double d: i.toDoubleArray()) {
				instJSON.put(d);
			}
			dataPoints.put(instJSON);
		}
		
		jsonSynthesi.put("dataPoints", dataPoints);
		
		
		return jsonSynthesi;
	}


	public void setFromDocument(Document doc) throws NumberFormatException{
		this.setNumberData(Long.parseLong(doc.get(KEY_RECORD_NUMBER).toString()));
		if(doc.containsKey(KEY_WARNING)) {
			this.setState(Integer.parseInt(doc.get(KEY_WARNING).toString()));
		}
		Collection<Document> elementsDocument = (Collection<Document>) doc.get(KEY_CENTROID);	
		for(Document elementDocument: elementsDocument){
			this.addSynthesiElements(new SynthesiElement(elementDocument));
		}
	}
}
