package it.unibs.bodai.ideaas.dao.timing;

/**
 * @author Daniele Comincini
 *
 */
public class ParallelDataSummarisationTiming {
	
	private int id;
	private int numDimensions;
	private int numSyntheses;
	private long time;
	private String type;
	private double mean;
	private double std;
	private int idTest;
	
	public ParallelDataSummarisationTiming() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param id
	 * @param numDimensions
	 * @param numSyntheses
	 * @param time
	 * @param type
	 * @param mean
	 * @param std
	 * @param idTest
	 */
	public ParallelDataSummarisationTiming(int id, int numDimensions, int numSyntheses, long time, String type, double mean, double std, int idTest) {
			super();
			this.id = id;
			this.numDimensions = numDimensions;
			this.numSyntheses = numSyntheses;
			this.time = time;
			this.type = type;
			this.mean = mean;
			this.std = std;
			this.idTest = idTest;
	}
	
	/**
	 * @param numDimensions
	 * @param numSyntheses
	 * @param time
	 * @param type
	 * @param mean
	 * @param std
	 * @param idTest
	 */
	public ParallelDataSummarisationTiming(int numDimensions, int numSyntheses, long time, String type, double mean, double std, int idTest) {
		super();
		this.numDimensions = numDimensions;
		this.numSyntheses = numSyntheses;
		this.time = time;
		this.type = type;
		this.mean = mean;
		this.std = std;
		this.idTest = idTest;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public int getNumDimensions() {
		return numDimensions;
	}

	/**
	 * @param numDimensions
	 */
	public void setNumDimensions(int numDimensions) {
		this.numDimensions = numDimensions;
	}

	/**
	 * @return
	 */
	public int getNumSyntheses() {
		return numSyntheses;
	}

	/**
	 * @param numSyntheses
	 */
	public void setNumSyntheses(int numSyntheses) {
		this.numSyntheses = numSyntheses;
	}

	/**
	 * @return
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @param mean
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}

	/**
	 * @return
	 */
	public double getStd() {
		return std;
	}

	/**
	 * @param std
	 */
	public void setStd(double std) {
		this.std = std;
	}

	/**
	 * @return
	 */
	public int getIdTest() {
		return idTest;
	}

	/**
	 * @param idTest
	 */
	public void setIdTest(int idTest) {
		this.idTest = idTest;
	}
	
}
