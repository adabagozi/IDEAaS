package xml_models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FeatureSpaceList")
public class FeatureSpaceListXML {

	private ArrayList<FeatureSpaceXML> featureSpace;
	
	public FeatureSpaceListXML() {};

	public FeatureSpaceListXML(ArrayList<FeatureSpaceXML> featureSpace) {
		this.featureSpace = featureSpace;
	}
	
	@XmlElement(name="FeatureSpace")
	public List<FeatureSpaceXML> getFeatureSpace() {
		return featureSpace;
	}

	public void setFeatureSpace(ArrayList<FeatureSpaceXML> featureSpace) {
		this.featureSpace = featureSpace;
	}
	
}
