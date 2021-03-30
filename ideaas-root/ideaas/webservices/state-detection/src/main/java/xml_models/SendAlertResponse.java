package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SendAlertResponse")
public class SendAlertResponse {
	
	
	private String message;
	
	private String timestamp;

	
	
	
	/**
	 * 
	 */
	public SendAlertResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	@XmlElement(name="ParameterList")
	ArrayList<ParamterInstanceXML> parameters = new ArrayList<>();

	@XmlElement(name="MonitoredSystem")
	MonitoredSystemXML monitoredSystem = new MonitoredSystemXML();
	

	/**
	 * @return the parameters
	 */
	public ArrayList<ParamterInstanceXML> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(ArrayList<ParamterInstanceXML> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the monitoredSystem
	 */
	public MonitoredSystemXML getMonitoredSystem() {
		return monitoredSystem;
	}

	/**
	 * @param monitoredSystem the monitoredSystem to set
	 */
	public void setMonitoredSystem(MonitoredSystemXML monitoredSystem) {
		this.monitoredSystem = monitoredSystem;
	}

}
