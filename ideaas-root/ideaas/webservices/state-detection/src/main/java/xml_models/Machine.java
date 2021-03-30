package xml_models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="Machine")
public class Machine {
	
	private String machine_instance_id;
	private String state;
	
	public Machine() {};
	
	public Machine(String machine_instance_id) {
		this.setMachine_instance_id(machine_instance_id);
	}
	
	public Machine(String machine_instance_id, String state) {
		this.setMachine_instance_id(machine_instance_id);
		this.setState(state);
	}

	public String getMachine_instance_id() {
		return machine_instance_id;
	}

	@XmlValue
	public void setMachine_instance_id(String machine_instance_id) {
		this.machine_instance_id = machine_instance_id;
	}

	public String getState() {
		return state;
	}

	@XmlAttribute(name="state")
	public void setState(String state) {
		this.state = state;
	}
	

}
