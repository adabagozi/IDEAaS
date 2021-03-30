package it.unibs.bodai.ideaas.dao.anomalies;

import it.unibs.bodai.ideaas.dao.utils.BestMetch;

public class DataSummarisationAnomalies {

	private String stableId;
	private String prevId;

	public String getStableId() {
		return stableId;
	}

	public void setStableId(String stableId) {
		this.stableId = stableId;
	}

	public String getPrevId() {
		return prevId;
	}

	public void setPrevId(String prevId) {
		this.prevId = prevId;
	}

	public String getSnapshotId() {
		return snapshotId;
	}

	public void setSnapshotId(String snapshotId) {
		this.snapshotId = snapshotId;
	}

	private String snapshotId;
	private String startDate;
	private String endDate;
	private int algorithm;
	private double dist_centroids_stab = 0;
	private double dist_radius_stab = 0;
	private int dist_radius_var_stab = 0;
	private double dist_centroids_prev = 0;
	private double dist_radius_prev = 0;
	private int dist_radius_var_prev = 0;
	private String description;
	
	public DataSummarisationAnomalies(String stableId, String prevId, String snapshotId, String startDate, String endDate, int algorithm, double dist_centroids_stab,
			double dist_radius_stab, int dist_radius_var_stab, double dist_centroids_prev, double dist_radius_prev,
			int dist_radius_var_prev, String description) {
		super();
		this.stableId = stableId;
		this.prevId = prevId;
		this.snapshotId = snapshotId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.algorithm = algorithm;
		this.dist_centroids_stab = dist_centroids_stab;
		this.dist_radius_stab = dist_radius_stab;
		this.dist_radius_var_stab = dist_radius_var_stab;
		this.dist_centroids_prev = dist_centroids_prev;
		this.dist_radius_prev = dist_radius_prev;
		this.dist_radius_var_prev = dist_radius_var_prev;
		this.description = description;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @return the algorithm
	 */
	public int getAlgorithm() {
		return algorithm;
	}

	/**
	 * @return the dist_centroids_stab
	 */
	public double getDist_centroids_stab() {
		return dist_centroids_stab;
	}

	/**
	 * @return the dist_radius_stab
	 */
	public double getDist_radius_stab() {
		return dist_radius_stab;
	}

	/**
	 * @return the dist_radius_var_stab
	 */
	public int getDist_radius_var_stab() {
		return dist_radius_var_stab;
	}

	/**
	 * @return the dist_centroids_prev
	 */
	public double getDist_centroids_prev() {
		return dist_centroids_prev;
	}

	/**
	 * @return the dist_radius_prev
	 */
	public double getDist_radius_prev() {
		return dist_radius_prev;
	}

	/**
	 * @return the dist_radius_var_prev
	 */
	public int getDist_radius_var_prev() {
		return dist_radius_var_prev;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(int algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @param dist_centroids_stab the dist_centroids_stab to set
	 */
	public void setDist_centroids_stab(double dist_centroids_stab) {
		this.dist_centroids_stab = dist_centroids_stab;
	}

	/**
	 * @param dist_radius_stab the dist_radius_stab to set
	 */
	public void setDist_radius_stab(double dist_radius_stab) {
		this.dist_radius_stab = dist_radius_stab;
	}

	/**
	 * @param dist_radius_var_stab the dist_radius_var_stab to set
	 */
	public void setDist_radius_var_stab(int dist_radius_var_stab) {
		this.dist_radius_var_stab = dist_radius_var_stab;
	}

	/**
	 * @param dist_centroids_prev the dist_centroids_prev to set
	 */
	public void setDist_centroids_prev(double dist_centroids_prev) {
		this.dist_centroids_prev = dist_centroids_prev;
	}

	/**
	 * @param dist_radius_prev the dist_radius_prev to set
	 */
	public void setDist_radius_prev(double dist_radius_prev) {
		this.dist_radius_prev = dist_radius_prev;
	}

	/**
	 * @param dist_radius_var_prev the dist_radius_var_prev to set
	 */
	public void setDist_radius_var_prev(int dist_radius_var_prev) {
		this.dist_radius_var_prev = dist_radius_var_prev;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
    
}
