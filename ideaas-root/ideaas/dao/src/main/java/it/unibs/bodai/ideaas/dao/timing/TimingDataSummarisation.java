package it.unibs.bodai.ideaas.dao.timing;

public class TimingDataSummarisation {
	
	private int id;
	private int numData;
	private int numNewData;
	private int numSyntheses;
	private int numNewSyntheses;
	private long totalTime;
	private long insertTime;
	private long extractTime;
	private long summarisationTime;
	private long relevanceEvaluationTime;
	private String description;
	
	
	/**
	 * 
	 */
	public TimingDataSummarisation() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param numFiles
	 * @param totalTime
	 * @param insertTime
	 */
	public TimingDataSummarisation(int num_data, int num_new_data, int num_syntheses, int num_new_syntheses, long totalTime, long insertTime, long summarisationTime, long extractTime, long relevanceEvaluationTime, String description) {
		super();
		this.numData = num_data;
		this.numNewData = num_new_data;
		this.numSyntheses = num_syntheses;
		this.numNewSyntheses = num_new_syntheses;
		this.totalTime = totalTime;
		this.insertTime = insertTime;
		this.summarisationTime = summarisationTime;
		this.extractTime = extractTime;
		this.relevanceEvaluationTime = relevanceEvaluationTime;
		this.description = description;
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
	 * @return the numData
	 */
	public int getNumData() {
		return numData;
	}


	/**
	 * @return the numNewData
	 */
	public int getNumNewData() {
		return numNewData;
	}


	/**
	 * @return the numSyntheses
	 */
	public int getNumSyntheses() {
		return numSyntheses;
	}


	/**
	 * @return the numNewSyntheses
	 */
	public int getNumNewSyntheses() {
		return numNewSyntheses;
	}


	/**
	 * @param numData the numData to set
	 */
	public void setNumData(int numData) {
		this.numData = numData;
	}


	/**
	 * @param numNewData the numNewData to set
	 */
	public void setNumNewData(int numNewData) {
		this.numNewData = numNewData;
	}


	/**
	 * @param numSyntheses the numSyntheses to set
	 */
	public void setNumSyntheses(int numSyntheses) {
		this.numSyntheses = numSyntheses;
	}


	/**
	 * @param numNewSyntheses the numNewSyntheses to set
	 */
	public void setNumNewSyntheses(int numNewSyntheses) {
		this.numNewSyntheses = numNewSyntheses;
	}


	/**
	 * @return the transformTime
	 */
	public long getTotalTime() {
		return totalTime;
	}


	/**
	 * @param totalTime the transformTime to set
	 */
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
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
	 * @return the summarisationTime
	 */
	public long getSummarisationTime() {
		return summarisationTime;
	}


	/**
	 * @param summarisationTime the summarisationTime to set
	 */
	public void setSummarisationTime(long summarisationTime) {
		this.summarisationTime = summarisationTime;
	}

	

	/**
	 * @return the extractTime
	 */
	public long getExtractTime() {
		return extractTime;
	}


	/**
	 * @param extractTime the extractTime to set
	 */
	public void setExtractTime(long extractTime) {
		this.extractTime = extractTime;
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


	public long getRelevanceEvaluationTime() {
		return relevanceEvaluationTime;
	}


	public void setRelevanceEvaluationTime(long relevanceEvaluationTime) {
		this.relevanceEvaluationTime = relevanceEvaluationTime;
	}

	
    
}
