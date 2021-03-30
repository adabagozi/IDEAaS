package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MonitoredSystem")
public class MonitoredSystemXML {
	private String enterprise;
	private String plant;
	private String machine;
	private String component;
	private String state_enterprise;
	private String state_plant;
	private String state_machine;
	private String state_component;
	
	
	public MonitoredSystemXML() {};
	
	public MonitoredSystemXML(String enterprise, String plant, String machine, String component) {
		this.enterprise = enterprise;
		this.plant = plant;
		this.machine = machine;
		this.component = component;
	}
	
	/**
	 * @return the enterprise
	 */
//	@XmlElement(name="Enterprise/@state_enterprise")
	@XmlElement(name="Enterprise")
	public String getEnterprise() {
		return enterprise;
	}
	/**
	 * @param enterprise the enterprise to set
	 */
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	/**
	 * @return the plant
	 */
//	@XmlElement(name="Plant/@state_plant")
	@XmlElement(name="Plant")
	public String getPlant() {
		return plant;
	}
	/**
	 * @param plant the plant to set
	 */
//	@XmlElement(name="Plant")
	public void setPlant(String plant) {
		this.plant = plant;
	}
	/**
	 * @return the machine
	 */
//	@XmlElement(name="Machine/@state_machine")
	@XmlElement(name="Machine")
	public String getMachine() {
		return machine;
	}
	/**
	 * @param machine the machine to set
	 */
	public void setMachine(String machine) {
		this.machine = machine;
	}
	/**
	 * @return the component
	 */
//	@XmlElement(name="Component/@state_component")
	public String getMandrino() {
		return component;
	}
	/**
	 * @param component the component to set
	 */
	public void setMandrino(String component) {
		this.component = component;
	}
	
	@XmlElement(name="Component")
	public String getComponent() {
		return component;
	}
	
	public void setComponent(String component) {
		this.component = component;
	}
	
	public String getState_enterprise() {
		return state_enterprise;
	}
	
	public void setState_enterprise(String state_enterprise) {
		this.state_enterprise = state_enterprise;
	}
	
	public String getState_plant() {
		return state_plant;
	}
	
	public void setState_plant(String state_plant) {
		this.state_plant = state_plant;
	}
	
	public String getState_machine() {
		return state_machine;
	}
	
	public void setState_machine(String state_machine) {
		this.state_machine = state_machine;
	}
	
	public String getState_component() {
		return state_component;
	}
	
	public void setState_component(String state_component) {
		this.state_component = state_component;
	}
	
	
	
}
