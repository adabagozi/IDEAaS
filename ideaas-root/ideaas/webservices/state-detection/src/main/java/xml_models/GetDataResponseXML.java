package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="GetDataResponse")
public class GetDataResponseXML {
	
	private MS monitoredSystem;
	private DataSetListXML dataSetList;
	
	public GetDataResponseXML() {}
	
	public GetDataResponseXML(MS monitoresdSystem, DataSetListXML dataSetList) {
		this.setMonitoredSystem(monitoresdSystem);
		this.setDataSetList(dataSetList);
	}
	
	public GetDataResponseXML(MS monitoresdSystem) {
		this.setMonitoredSystem(monitoredSystem);
	}
	
	@XmlElement(name="MonitoredSystem")
	public MS getMonitoredSystem() {
		return monitoredSystem;
	}

	public void setMonitoredSystem(MS monitoredSystem) {
		this.monitoredSystem = monitoredSystem;
	}

	@XmlElement(name="DataSetList")
	public DataSetListXML getDataSetList() {
		return dataSetList;
	}

	public void setDataSetList(DataSetListXML dataSetList) {
		this.dataSetList = dataSetList;
	}
	
}