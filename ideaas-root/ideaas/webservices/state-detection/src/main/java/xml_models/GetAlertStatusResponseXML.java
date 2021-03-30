package xml_models;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
//<?xml version="1.0" encoding="UTF-8"?>
//<GetAlertStatusResponse>
//  <Message>Stato del sistema 2016-08-01 05:09:38.012</Message>
//  <Timestamp>2016-08-01T05:09:38.012Z</Timestamp> 
//  <AlertStatusList>
//	  <AlertStatus>
//		<MonitoredSystem>
//		  <Enterprise state="error_1">Azienda1</Enterprise>
//		  <Plant state ="error_1">Stabilimento1</Plant>
//		  <Machine state ="error_1">101143</Machine>
//		  <Component state ="error_1">Unit1.0</Component>
//		</MonitoredSystem>
//		<FeatureSpaceList>
//			<FeatureSpace name="Indurimento asse" state="error_1">
//			  <Feature name="Carico mandrino" unit="amp" state="error">
//			  	<Value timestamp="2016-08-01T09:35Z" state="error">20,456</Value>
//			  </Feature>
//			  <Feature name="Velocita mandrino" unit="rpm" state="ok">
//			  	<Value timestamp="2016-08-01T09:37Z" state="ok">245</Value>
//			  </Feature>
//			</FeatureSpace>
//		</FeatureSpaceList>
//	  </AlertStatus>
//  </AlertStatusList>
//</GetAlertStatusResponse>
@XmlRootElement(name="GetAlertStatusResponse")
public class GetAlertStatusResponseXML {

	private String message;
	private String timestamp;
	private ArrayList<AlertStatusXML> alertStatusList;
	
	private AlertStatusListXML alertStatusListXML;

	public GetAlertStatusResponseXML() {};
	
	public GetAlertStatusResponseXML(String timestamp, String message, ArrayList<AlertStatusXML> alertStatusList) {
		this.timestamp = timestamp;
		this.message = message;
		this.alertStatusList = alertStatusList;
	}
	
	public GetAlertStatusResponseXML(String timestamp, String message, AlertStatusListXML alertStatusListXML) {
		this.timestamp = timestamp;
		this.message = message;
		this.alertStatusListXML = alertStatusListXML;
	}
	
	public GetAlertStatusResponseXML(String timestamp, String message) {
		this.timestamp = timestamp;
		this.message = message;
	}
	
	/**
	 * @return the message
	 */
	@XmlElement(name="Message")
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the timestamp
	 */
	@XmlElement(name="Timestamp")
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	@XmlElement(name="AlertStatusList")
	public List<AlertStatusXML> getAlertStatusList() {
		return alertStatusList;
	}

	public void setAlertStatusList(ArrayList<AlertStatusXML> alertStatusList) {
		this.alertStatusList = alertStatusList;
	}
	
	@XmlElement(name="AlertStatusList")
	public AlertStatusListXML getAlertStatusListXML() {
		return alertStatusListXML;
	}

	public void setAlertStatusListXML(AlertStatusListXML alertStatusListXML) {
		this.alertStatusListXML = alertStatusListXML;
	}
	
	
}
