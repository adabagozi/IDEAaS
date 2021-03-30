package xml_models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MonitoredSystem")
public class MS {
	
	private Azienda azienda;
	private Plant plant;
	private Machine machine;
	private Componente component;
	
	public MS() {}
	
	public MS(Azienda enterprise, Plant plant, Machine machine, Componente component) {
		this.setPlant(plant);
		this.setMachine(machine); 
		this.setComponent(component);
		this.setAzienda(enterprise);
		
	}
	
	public MS(Machine machine, Componente component) {
		this.setMachine(machine); 
		this.setComponent(component);
	}
	
	@XmlElement(name="Machine")
	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	@XmlElement(name="Component")
	public Componente getComponent() {
		return component;
	}

	public void setComponent(Componente component) {
		this.component = component;
	}

	
	@XmlElement(name="Plant")		
	public Plant getPlant() {
		return plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}

	@XmlElement(name="Enterprise")
	public Azienda getAzienda() {
		return azienda;
	}

	public void setAzienda(Azienda azienda) {
		this.azienda = azienda;
	}
	

}
