package xml_models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement(name="Component")
public class Componente {
	
	private String instance_id;
	private String state;
	
	public Componente() {}
	
	public Componente(String instance_id) {
		this.setInstance_id(instance_id);
	}
	
	public Componente(String instance_id, String state) {
		this.setInstance_id(instance_id);
		this.setState(state);
		
	}
	
	public String getInstance_id() {
		return instance_id;
	}
	
	@XmlValue
	public void setInstance_id(String instance_id) {
		this.instance_id = instance_id;
	}

	@XmlAttribute(name="state")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	
}
