package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AlertStatus")
public class AlertStatusXML {
	
	private FeatureSpaceListXML featureSpaceListXML;
	private MS ms;
	
	public AlertStatusXML() {}
	
	public AlertStatusXML(MS monitoredSystem, FeatureSpaceListXML featureSpaceList) {
		this.ms = monitoredSystem;
		this.featureSpaceListXML = featureSpaceList;
	}
	
	public AlertStatusXML(MS monitoredSystem) {
		this.ms = monitoredSystem;
	}

	
	@XmlElement(name = "FeatureSpaceList")
	public FeatureSpaceListXML getFeatureSpaceListXML() {
		return featureSpaceListXML;
	}

	public void setFeatureSpaceListXML(FeatureSpaceListXML featureSpaceListXML) {
		this.featureSpaceListXML = featureSpaceListXML;
	}
	
	@XmlElement(name="MonitoredSystem")
	public MS getMs() {
		return ms;
	}

	public void setMs(MS ms) {
		this.ms = ms;
	}
}
