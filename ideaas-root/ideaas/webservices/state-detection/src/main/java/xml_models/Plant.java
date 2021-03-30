package xml_models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="Plant")
public class Plant {
	
	private String plant_instance;
	private String state;
	
	public Plant() {}
	
	public Plant(String plant_instance) {
		this.setPlant_instance(plant_instance);
	}
	
	public Plant(String plant_instance, String state) {
		this.setPlant_instance(plant_instance);
		this.setState(state);
	}

	public String getPlant_instance() {
		return plant_instance;
	}

	@XmlValue
	public void setPlant_instance(String plant_instance) {
		this.plant_instance = plant_instance;
	}
	
	@XmlAttribute(name="state")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
