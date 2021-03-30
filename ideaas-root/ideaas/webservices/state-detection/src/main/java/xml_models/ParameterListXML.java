package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="ParameterList")
public class ParameterListXML {
	
	private ArrayList<ParameterXML> parameter;

	public ParameterListXML() {}
	
	public ParameterListXML(ArrayList<ParameterXML> parameter) {
		this.setParameter(parameter);
	}
	
	@XmlElement(name="Parameter")
	public ArrayList<ParameterXML> getParameter() {
		return parameter;
	}
	
	public void setParameter(ArrayList<ParameterXML> parameter) {
		this.parameter = parameter;
	}
	@Override
	public int hashCode() {
		int hash = 3;
	    hash = (int) (53 * hash + (this.parameter != null ? (Math.random() * Math.PI) : 0));
	    hash = (int) (53 * hash + Math.random() * Math.PI);
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		ParameterListXML plxml = (ParameterListXML) obj;
		ArrayList<ParameterXML> parametersList1 = this.parameter;
		ArrayList<ParameterXML> parametersList2 = plxml.parameter;
		
		if(!parametersList1.equals(parametersList2)) {
			return false;
		}
		return true;
	}
}
