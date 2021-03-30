package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//<?xml version="1.0" encoding="UTF-8"?>
//<GetData>
//  <StartDate>2016-08-01T10:10:00Z</StartDate>
//  <EndDate>2016-08-01T11:20:00Z</EndDate>
//  <MonitoredSystem>
//    <Enterprise>Azienda1</Enterprise>
//    <Plant>Stabilimento1</Plant>
//    <Machine>101143</Machine>
//    <Component>Unit1.0</Machine>
//  </MonitoredSystem>
//</GetData>


@XmlRootElement(name="GetData")
public class GetDataInputFilter {
	
	
	private String startDate;
	
	private String endDate;

	private MonitoredSystem monitoredSystem;
	
	public GetDataInputFilter() {}
	
	public GetDataInputFilter(String startDate, String endDate, MonitoredSystem monitoredSystem) {
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setMonitoredSystem(monitoredSystem);
	}

	/**
	 * @return the startDate
	 */
	@XmlElement(name="StartDate")
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	@XmlElement(name="EndDate")
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the monitoredSystem
	 */
	@XmlElement(name="MonitoredSystem")
	public MonitoredSystem getMonitoredSystem() {
		return monitoredSystem;
	}

	/**
	 * @param monitoredSystem the monitoredSystem to set
	 */
	public void setMonitoredSystem(MonitoredSystem monitoredSystem) {
		this.monitoredSystem = monitoredSystem;
	}
   
	
}

