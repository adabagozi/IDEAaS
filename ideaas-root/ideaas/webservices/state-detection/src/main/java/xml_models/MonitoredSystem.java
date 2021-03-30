package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;

@XmlRootElement(name="MonitoredSystem")
public class MonitoredSystem {
	private String enterprise;
	private String plant;
	private String machine;
	private String component;
	
	public MonitoredSystem() {}
	
	public MonitoredSystem(String enterprise, String plant, String machine, String component) {
		this.setEnterprise(enterprise);
		this.setPlant(plant);
		this.setMachine(machine);
		this.setMandrino(component);
	}
	/**
	 * @return the enterprise
	 */
	@XmlElement(name="Enterprise")
	public String getEnterprise() {
		return enterprise;
	}
	/**
	 * @param enterprise the enterprise to set
	 */
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	/**
	 * @return the plant
	 */
	@XmlElement(name="Plant")
	public String getPlant() {
		return plant;
	}
	/**
	 * @param plant the plant to set
	 */
//	@XmlElement(name="Plant")
	public void setPlant(String plant) {
		this.plant = plant;
	}
	/**
	 * @return the machine
	 */
	@XmlElement(name="Machine")
	public String getMachine() {
		return machine;
	}
	/**
	 * @param machine the machine to set
	 */
	public void setMachine(String machine) {
		this.machine = machine;
	}
	
	/**
	 * @return the component
	 */
	@XmlElement(name="Component")
	public String getMandrino() {
		return component;
	}
	
	/**
	 * @param component the component to set
	 */
	public void setMandrino(String component) {
		this.component = component;
	}
	
	
	
	public ArrayList<DimensionInstance> getDimensionInstances(){
		ArrayList<DimensionInstance> dimInstances = new ArrayList<>();
		
		//TODO leggere dal db i dati, perciò creare un DimensionInstanceMySQL (è anche ora direi)
		
		Dimension dimEnterprise = new Dimension(1, "Azienda");
		Dimension dimPlant = new Dimension(2, "Impianto");
		Dimension dimMachine = new Dimension(3, "Macchina");
		Dimension dimComponent = new Dimension(4, "Componente");
		
		DimensionInstance dimInstEnterprise = new DimensionInstance(this.getEnterprise(), dimEnterprise);
		DimensionInstance dimInstPlant = new DimensionInstance(this.getPlant(), dimPlant);
		DimensionInstance dimInstMachine = new DimensionInstance(this.getMachine(), dimMachine);
		DimensionInstance dimInstComp = new DimensionInstance(this.getMandrino(), dimComponent);

		dimInstances.add(dimInstEnterprise);
		dimInstances.add(dimInstPlant);
		dimInstances.add(dimInstMachine);
		dimInstances.add(dimInstComp);
		
		return dimInstances;
	}
	
	
//	<Enterprise>Azienda1</Enterprise>
//    <Plant>Stabilimento1</Plant>
//    <Machine>101143</Machine>
//    <Component>Unit1.0</Machine>

}
