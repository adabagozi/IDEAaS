package xml_models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="FeatureSpace")
public class FeatureSpaceXML {
	
	private ArrayList<FeatureXML_Response> features;
	private String name;
	private String state;
	
	public FeatureSpaceXML() {};
	
	public FeatureSpaceXML(String name, String state, ArrayList<FeatureXML_Response> features) {
		this.name = name;
		this.state = state;
		this.features = features;
	}
	
	public FeatureSpaceXML(String name) {
		this.name = name;
	}
	
	@XmlElement(name="Feature")
	public ArrayList<FeatureXML_Response> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<FeatureXML_Response> features) {
		this.features = features;
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}
