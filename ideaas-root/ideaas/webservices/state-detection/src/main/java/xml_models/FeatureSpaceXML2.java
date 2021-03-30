package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;

@XmlRootElement(name="FeatureSpace")
public class FeatureSpaceXML2 extends FeatureSpace{
	private String state = "";
	
	private ArrayList<FeatureXML> features = new ArrayList<>();

	@XmlAttribute(name="name")
	public String getGetName() {
		return this.getName();
	}
	@XmlAttribute(name="state")
	public String getState() {
		return this.state;
	}
	/**
	 * @return the features
	 */
	public ArrayList<FeatureXML> getFeaturesXml() {
		return features;
	}
	/**
	 * @param features the features to set
	 */
	public void setFeaturesXml(ArrayList<FeatureXML> features) {
		this.features = features;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	
}
