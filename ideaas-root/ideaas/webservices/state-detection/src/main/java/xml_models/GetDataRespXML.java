package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="GetDataResponse")
public class GetDataRespXML {
	
	private MS monitored_system;
	private DataSetListXML dataSetListXML;
	
	public GetDataRespXML() {}
	
	public GetDataRespXML(MS monitored_system) {
		this.setMonitored_system(monitored_system);
	}
	
	public GetDataRespXML(DataSetListXML dataSetListXML) {
		this.setDataSetListXML(dataSetListXML);
	}
	
	public GetDataRespXML(MS monitored_system, DataSetListXML dataSetListXML) {
		this.setMonitored_system(monitored_system);
		this.setDataSetListXML(dataSetListXML);
	}

	@XmlElement(name="MonitoredSystem")
	public MS getMonitored_system() {
		return monitored_system;
	}

	public void setMonitored_system(MS monitored_system) {
		this.monitored_system = monitored_system;
	}

	@XmlElement(name="DataSetList")
	public DataSetListXML getDataSetListXML() {
		return dataSetListXML;
	}

	public void setDataSetListXML(DataSetListXML dataSetListXML) {
		this.dataSetListXML = dataSetListXML;
	}
	
}
