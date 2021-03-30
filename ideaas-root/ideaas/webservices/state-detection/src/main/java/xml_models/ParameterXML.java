package xml_models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import it.unibs.bodai.ideaas.dao.model.Feature;

@XmlRootElement(name="Parameter")
public class ParameterXML {

	private String parameter_name;
	private String parameter_value;
	
	public ParameterXML() {}
	
	public ParameterXML(String parameter_name, String parameter_value) {
		this.setParameter_name(parameter_name);
		this.setParameter_value(parameter_value);
	}

	@XmlAttribute(name="name")
	public String getParameter_name() {
		return parameter_name;
	}
	
	public void setParameter_name(String parameter_name) {
		this.parameter_name = parameter_name;
	}
	
	public String getParameter_value() {
		return parameter_value;
	}
	
	@XmlValue
	public void setParameter_value(String parameter_value) {
		this.parameter_value = parameter_value;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
	    hash = (int) (53 * hash + (this.parameter_name != null ? (Math.random() * Math.PI) : 0));
	    hash = (int) (53 * hash + Math.random() * Math.PI);
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		ParameterXML pxml = (ParameterXML) obj;
		if(!(this.parameter_name.equals(pxml.parameter_name) && this.parameter_value.equals(pxml.parameter_value))) {
			return false;
		}
		return true;
	}
	
}
