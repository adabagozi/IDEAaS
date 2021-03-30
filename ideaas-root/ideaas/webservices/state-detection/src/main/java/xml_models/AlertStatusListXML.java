package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AlertStatusList")
public class AlertStatusListXML {

	private ArrayList<AlertStatusXML> alertStatusXML;

	public AlertStatusListXML() {}
	
	public AlertStatusListXML(ArrayList<AlertStatusXML> alertStatusXML) {
		this.setAlertStatusXML(alertStatusXML);
	}
	
	@XmlElement(name="AlertStatus")
	public ArrayList<AlertStatusXML> getAlertStatusXML() {
		return alertStatusXML;
	}

	public void setAlertStatusXML(ArrayList<AlertStatusXML> alertStatusXML) {
		this.alertStatusXML = alertStatusXML;
	}

	
}
