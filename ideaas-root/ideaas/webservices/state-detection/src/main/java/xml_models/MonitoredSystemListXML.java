package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MonitoredSystemList")
public class MonitoredSystemListXML {

	private ArrayList<MonitoredSystemXML> monitoredSystem;
	
	public MonitoredSystemListXML() {}
	
	public MonitoredSystemListXML(ArrayList<MonitoredSystemXML> monitoredSystem) {
		this.monitoredSystem = monitoredSystem;
	}
	
	@XmlElement(name="MonitoredSystem")
	public ArrayList<MonitoredSystemXML> getMonitoredSystem() {
		return monitoredSystem;
	}

	public void setMonitoredSystem(ArrayList<MonitoredSystemXML> monitoredSystem) {
		this.monitoredSystem = monitoredSystem;
	}
	
	
}
