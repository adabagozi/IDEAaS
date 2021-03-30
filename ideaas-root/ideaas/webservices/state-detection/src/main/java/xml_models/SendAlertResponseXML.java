package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SendAlertResponse")
public class SendAlertResponseXML {
	private String message;
	private String timestamp;
	private MS monitored_system;
	private ParameterListXML parameterListXML;
	private FeatureSpaceXML featureSpaceXML;
	
	public SendAlertResponseXML() {}
	
	public SendAlertResponseXML(String message, String timestamp, ParameterListXML parameterListXML, MS monitored_system, FeatureSpaceXML featureSpaceXML) {
		this.setMessage(message);
		this.setTimestamp(timestamp);
		this.setMonitored_system(monitored_system);
		this.setParameterListXML(parameterListXML);
		this.setFeatureSpaceXML(featureSpaceXML);
	}
	
	@XmlElement(name="Message")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@XmlElement(name="Timestamp")
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	@XmlElement(name="MonitoredSystem")
	public MS getMonitored_system() {
		return monitored_system;
	}

	public void setMonitored_system(MS monitored_system) {
		this.monitored_system = monitored_system;
	}
	
	@XmlElement(name="ParameterList")
	public ParameterListXML getParameterListXML() {
		return parameterListXML;
	}

	public void setParameterListXML(ParameterListXML parameterListXML) {
		this.parameterListXML = parameterListXML;
	}
	
	@XmlElement(name="FeatureSpace")
	public FeatureSpaceXML getFeatureSpaceXML() {
		return featureSpaceXML;
	}

	public void setFeatureSpaceXML(FeatureSpaceXML featureSpaceXML) {
		this.featureSpaceXML = featureSpaceXML;
	}
	
}
