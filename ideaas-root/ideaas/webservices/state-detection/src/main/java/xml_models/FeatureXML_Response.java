package xml_models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="Feature")
public class FeatureXML_Response {
//<FeatureSpaceList>
//<FeatureSpace name="Indurimento asse" state="error_1">
//<Feature name="Carico mandrino" unit="amp" state="error">
//		<Value timestamp="2016-08-01T09:35Z" state="error">20,456</Value>
//</Feature>
//<Feature name="Velocita mandrino" unit="rpm" state="ok">
//		<Value timestamp="2016-08-01T09:37Z" state="ok">245</Value>
//</Feature>
//</FeatureSpace>
//</FeatureSpaceList>
	
	private String name;
	private String unit;
	private String state;
	private List<ValueXML> values;
	private ValueXML single_value;
	
	public FeatureXML_Response() {};
	
	public FeatureXML_Response(String name, String unit, String state, ArrayList<ValueXML> values) {
		this.name = name;
		this.unit = unit;
		this.state = state;
		this.values = values;
	}
	
	public FeatureXML_Response(String name, String unit, String state, ValueXML single_value) {
		this.name = name;
		this.unit = unit;
		this.state = state;
		this.setSingle_value(single_value);
	}
	
	public FeatureXML_Response(String name, String unit, String state) {
		this.name = name;
		this.unit = unit;
		this.state = state;
	}
	
	public FeatureXML_Response(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	@XmlAttribute
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	@XmlElement
	public List<ValueXML> getValue() {
		return values;
	}
	
	public void setValue(List<ValueXML> value) {
		this.values = value;
	}
	
	@XmlElement(name="value")
	public ValueXML getSingle_value() {
		return single_value;
	}

	public void setSingle_value(ValueXML single_value) {
		this.single_value = single_value;
	}

}
