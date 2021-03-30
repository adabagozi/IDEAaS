package xml_models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import it.unibs.bodai.ideaas.dao.model.ParameterInstance;

@XmlRootElement(name="Parameter")
public class ParamterInstanceXML extends ParameterInstance{
	
	
	@XmlAttribute(name="name")
	public String getGetName() {
		return this.getName();
	}
	
	@XmlValue
	public String getValue() {
		return this.getInstanceId();
	}
}
