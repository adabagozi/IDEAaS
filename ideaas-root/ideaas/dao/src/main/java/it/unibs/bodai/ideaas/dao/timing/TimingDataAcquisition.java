package it.unibs.bodai.ideaas.dao.timing;

public class TimingDataAcquisition {
	
	private int id;
	private int numFiles;
	private long transformTime;
	private long insertTime;
	private String description;
	private double anomalyPerc;
	
	
	
	
	/**
	 * 
	 */
	public TimingDataAcquisition() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param id
	 * @param numFiles
	 * @param transformTime
	 * @param insertTime
	 */
	public TimingDataAcquisition(int id, int numFiles, long transformTime, long insertTime) {
		super();
		this.id = id;
		this.numFiles = numFiles;
		this.transformTime = transformTime;
		this.insertTime = insertTime;
	}
	
	
	/**
	 * @param numFiles
	 * @param transformTime
	 * @param insertTime
	 */
	public TimingDataAcquisition(int numFiles, long transformTime, long insertTime, String description, double anomalyPerc) {
		super();
		this.numFiles = numFiles;
		this.transformTime = transformTime;
		this.insertTime = insertTime;
		this.description = description;
		this.anomalyPerc = anomalyPerc;
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
	 * @return the numFiles
	 */
	public int getNumFiles() {
		return numFiles;
	}


	/**
	 * @param numFiles the numFiles to set
	 */
	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}


	/**
	 * @return the transformTime
	 */
	public long getTransformTime() {
		return transformTime;
	}


	/**
	 * @param transformTime the transformTime to set
	 */
	public void setTransformTime(long transformTime) {
		this.transformTime = transformTime;
	}


	/**
	 * @return the insertTime
	 */
	public long getInsertTime() {
		return insertTime;
	}


	/**
	 * @param insertTime the insertTime to set
	 */
	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	public double getAnomalyPerc() {
		return anomalyPerc;
	}


	public void setAnomalyPerc(double anomalyPerc) {
		this.anomalyPerc = anomalyPerc;
	}

	
    
}
