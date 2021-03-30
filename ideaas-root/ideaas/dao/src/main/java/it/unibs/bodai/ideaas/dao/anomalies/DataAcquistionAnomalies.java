package it.unibs.bodai.ideaas.dao.anomalies;

public class DataAcquistionAnomalies {
	
	private String date;
	private double anomalyPerc;
	
	public DataAcquistionAnomalies(String date, double anomalyPerc) {
		super();
		this.date = date;
		this.anomalyPerc = anomalyPerc;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @return the anomalyPerc
	 */
	public double getAnomalyPerc() {
		return anomalyPerc;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @param anomalyPerc the anomalyPerc to set
	 */
	public void setAnomalyPerc(double anomalyPerc) {
		this.anomalyPerc = anomalyPerc;
	}
	
	
}
