package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import it.unibs.bodai.ideaas.dao.model.Feature;

@XmlRootElement(name="Feature")
public class FeatureXML extends Feature{
	private ArrayList<ValueXML> values = new ArrayList<>();
	
	@XmlAttribute(name="name")
	public String getName() {
		return this.getName();
	}
	@XmlAttribute(name="unit")
	public String getUnit() {
		return this.getMeasureUnit();
	}
	
	/**
	 * @return the values
	 */
//	@XmlTransient
	public ArrayList<ValueXML> getValues() {
		return values;
	}
	/**
	 * @param values the values to set
	 */
	public void setValues(ArrayList<ValueXML> values) {
		this.values = values;
	}
	
}
