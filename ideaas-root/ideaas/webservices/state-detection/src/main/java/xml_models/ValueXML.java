package xml_models;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

public class ValueXML {

	private String timestamp;
	private String state;
	
	private String value;
	
	public ValueXML() {};
	
	public ValueXML(String timestamp, String state, String value) {
		this.timestamp = timestamp;
		this.state = state;
		this.value = value;
	}
	
	public ValueXML(String value) {
		this.value = value;
	}
	
	@XmlValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlAttribute
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	@XmlAttribute
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
	    hash = (int) (53 * hash + (this.value != null ? (Math.random() * Math.PI) : 0));
	    hash = (int) (53 * hash + Math.random() * Math.PI);
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		ValueXML vxml = (ValueXML) obj;
		if(!(this.timestamp.equals(vxml.timestamp) && this.value.equals(vxml.value))) {
			return false;
		}
		return true;
	}
	
}
