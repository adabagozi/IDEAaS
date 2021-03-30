package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class Cluster {
	private static final Logger LOG = Logger.getLogger(Cluster.class.getName());

	public static final String KEY_CENTROID = "centroid";
	public static final String KEY_RADIUS = "radius";
	public static final String KEY_RECORD_NUMBER = "record_number";

	public static final String KEY_SYNTHESIS = "synthesis";


	private int id;
	private double radius;
	private boolean found;
	private Collection<ClusterElement> clusterElements = new ArrayList<ClusterElement>();
	private Collection<Synthesi> synthesisInCluster = new ArrayList<Synthesi>();

	/**
	 * @param id
	 * @param clusterElements
	 */
	public Cluster(int id, Collection<ClusterElement> clusterElements) {
		super();
		this.id = id;
		this.radius = 0;
		this.found = false;
		this.clusterElements = clusterElements;
	}

	public Cluster(int id, Collection<ClusterElement> clusterElements, Collection<Synthesi> synthesisInCluster) {
		super();
		this.id = id;
		this.radius = 0;
		this.found = false;
		this.clusterElements = clusterElements;
		this.synthesisInCluster = synthesisInCluster;
	}


	public Cluster() {
		super();

		this.clusterElements = new ArrayList<>();
		// TODO Auto-generated constructor stub
	}


	public Cluster(int id) {
		super();
		this.id = id;
		this.radius = 0;
		this.found = false;
		this.clusterElements = new ArrayList<>();
	}


	public Cluster(Document clusterDocument) {
		super();
		this.setFromDocument(clusterDocument);
		// TODO Auto-generated constructor stub
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
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}


	/**
	 * @param radius the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}


	/**
	 * @return the found
	 */
	public boolean isFound() {
		return found;
	}


	/**
	 * @param found the found to set
	 */
	public void setFound(boolean found) {
		this.found = found;
	}


	/**
	 * @return the clusterElements
	 */
	public Collection<ClusterElement> getClusterElements() {
		return clusterElements;
	}


	/**
	 * @param clusterElements the clusterElements to set
	 */
	public void setClusterElements(Collection<ClusterElement> clusterElements) {
		this.clusterElements = clusterElements;
	}


	/**
	 * @param clusterElement the clusterElement to add to cluter elements collection
	 */
	public void addClusterElements(ClusterElement clusterElement) {

		this.clusterElements.add(clusterElement);
	}

	public double calculateRadius() {
		double r = 0;
		for (Synthesi s: this.getSynthesisInCluster()) {
			double radiusTemp = this.getDistanceToSynthesi(s) + s.getRadius();
			if (r < radiusTemp) {
				r = radiusTemp;
			}
		}
		this.setRadius(r);
		return r;
	}

	
	public double getDistanceToSynthesi(Synthesi synthesi) {
		if (this.clusterElements.size() != synthesi.getSynthesiElements().size()) {
			throw new RuntimeException();
		}
		double retVal = 0;
//		Iterator<ClusterElement> itThis = this.clusterElements.iterator();
//		Iterator<SynthesiElement> itOther = synthesi.getSynthesiElements().iterator();
//		ClusterElement ccThis;
//		SynthesiElement ccOther;
//
//		while (itThis.hasNext()) {
//			ccThis = itThis.next();
//			ccOther = itOther.next();
//			retVal += Math.pow(ccThis.getCenterValue() - ccOther.getCenterValue(),2);
//		}
		return Math.sqrt(retVal);
	}


	
	public double getDistance(Cluster other) {
		if (this.clusterElements.size() != other.clusterElements.size()) {
			throw new RuntimeException();
		}
		double retVal = 0;
		Iterator<ClusterElement> itThis = this.clusterElements.iterator();
		Iterator<ClusterElement> itOther = other.clusterElements.iterator();
		ClusterElement ccThis, ccOther;

		while (itThis.hasNext()) {
			ccThis = itThis.next();
			ccOther = itOther.next();
			retVal += Math.pow(ccThis.getCenterValue() - ccOther.getCenterValue(),2);
		}
		return Math.sqrt(retVal);
	}





	public double[] clusterToDoubleArray(){
		double[] arrayElement = new double[this.getClusterElements().size()];
		int j = 0;
		for (ClusterElement clusterElement : this.getClusterElements()) {
			arrayElement[j] = clusterElement.getCenterValue();
			j++;
		}
		return arrayElement;
	}


	/**
	 * @return the synthesisInCluster
	 */
	public Collection<Synthesi> getSynthesisInCluster() {
		return synthesisInCluster;
	}


	/**
	 * @param synthesisInCluster the synthesisInCluster to set
	 */
	public void setSynthesisInCluster(Collection<Synthesi> synthesisInCluster) {
		this.synthesisInCluster = synthesisInCluster;
	}


	/**
	 * @param synthesisInCluster the synthesisInCluster to set
	 */
	public void addSynthesis(Synthesi synthesi) {
		if (this.synthesisInCluster == null) {
			this.synthesisInCluster = new ArrayList<>();
		}
		this.synthesisInCluster.add(synthesi);
	}


	public long getRecordNumber() {
		long number = 0;
		for(Synthesi s : this.getSynthesisInCluster()) {
			number = number + s.getNumberData();
		}
		return number;
	}


	public JSONObject toJSON(){
		JSONObject jsonCluster = new JSONObject();
		JSONArray jsonCentroidValues = new JSONArray();
		JSONArray jsonSynthesis = new JSONArray();

		for (ClusterElement clusterElement : this.getClusterElements()) {
			jsonCentroidValues.put(clusterElement.toJSON());
		}

		jsonCluster.put(KEY_CENTROID, jsonCentroidValues);

		jsonCluster.put(KEY_RECORD_NUMBER, String.format("%s", this.getRecordNumber()));
		jsonCluster.put(KEY_RADIUS, String.format("%s", this.calculateRadius()));


		for (Synthesi synthesi : this.getSynthesisInCluster()) {
			jsonSynthesis.put(synthesi.toJSON());
		}
		jsonCluster.put(KEY_SYNTHESIS, jsonCentroidValues);

		return jsonCluster;
	}


	public void setFromDocument(Document doc) {

		this.setRadius(Integer.parseInt(doc.get(KEY_RADIUS).toString()));
		Collection<Document> clusterElementsDocument = (Collection<Document>) doc.get(KEY_CENTROID);	
		for(Document clusterElementDocument: clusterElementsDocument){
			this.addClusterElements(new ClusterElement(clusterElementDocument));
		}

		Collection<Document> synthesisDocument = (Collection<Document>) doc.get(KEY_SYNTHESIS);	
		for(Document synthesiDocument: synthesisDocument){
			this.addSynthesis(new Synthesi(synthesiDocument));
		}
	}

}
