package it.unibs.bodai.ideaas.dao.timing;

public class TimingDataReading {
	
	private int id;
	private int numFiles;
	private long readingTime;
	private String description;
	private String query;
	
	
	
	
	/**
	 * 
	 */
	public TimingDataReading() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * @param numFiles
	 * @param transformTime
	 * @param insertTime
	 */
	public TimingDataReading(int numFiles, long readingTime, String description, String query) {
		super();
		this.numFiles = numFiles;
		this.readingTime = readingTime;
		this.description = description;
		this.query = query;
	}



	public int getId() {
		return id;
	}



	public int getNumFiles() {
		return numFiles;
	}



	public long getReadingTime() {
		return readingTime;
	}



	public String getDescription() {
		return description;
	}



	public String getQuery() {
		return query;
	}



	public void setId(int id) {
		this.id = id;
	}



	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}



	public void setReadingTime(long readingTime) {
		this.readingTime = readingTime;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public void setQuery(String query) {
		this.query = query;
	}
}
