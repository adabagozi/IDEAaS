package it.unibs.bodai.ideaas.dao.timing;

public class DataAcquistionAnomalies {

    private String date;
    private double anomalyPerc;
    private String featureId;

    public DataAcquistionAnomalies(String date, double anomalyPerc, String featureId) {
        super();
        this.date = date;
        this.anomalyPerc = anomalyPerc;
        this.featureId = featureId;
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

    public String getFeatureId() {
        return this.featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }


}
