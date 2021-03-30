package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DataSet")
public class DataSetXML {
	
	private ParameterListXML parameterList;
	private FeatureSpaceListXML featureSpaceList;
	
	public DataSetXML() {}
	
	public DataSetXML(ParameterListXML parameterList, FeatureSpaceListXML featureSpaceList) {
		this.setParameterList(parameterList);
		this.setFeatureSpaceList(featureSpaceList);
	}
	
	public DataSetXML(ParameterListXML parameterList) {
		this.setParameterList(parameterList);
	}

	@XmlElement(name="ParameterList")
	public ParameterListXML getParameterList() {
		return parameterList;
	}

	public void setParameterList(ParameterListXML parameterList) {
		this.parameterList = parameterList;
	}
	

	@XmlElement(name="FeatureSpaceList")
	public FeatureSpaceListXML getFeatureSpaceList() {
		return featureSpaceList;
	}

	public void setFeatureSpaceList(FeatureSpaceListXML featureSpaceList) {
		this.featureSpaceList = featureSpaceList;
	}
	
}
