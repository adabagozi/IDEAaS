package xml_models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="Enterprise")
public class Azienda {
	
	private String azienda_instance;
	private String state;
	
	public Azienda() {}
	
	public Azienda(String azienda_instance) {
		this.setAzienda_instance(azienda_instance);
	}
	
	public Azienda(String azienda_instance, String state) {
		this.setAzienda_instance(azienda_instance);
		this.setState(state);
	}

	public String getAzienda_instance() {
		return azienda_instance;
	}

	@XmlValue
	public void setAzienda_instance(String azienda_instance) {
		this.azienda_instance = azienda_instance;
	}
	
	@XmlAttribute(name="state")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}

