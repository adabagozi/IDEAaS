package xml_models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DateSetList")
public class DataSetListXML {
	
	private ArrayList<DataSetXML> dataset;

	public DataSetListXML() {}
	
	public DataSetListXML(ArrayList<DataSetXML> dataset) {
		this.setDataset(dataset);
	}
	
	@XmlElement(name="DataSet")
	public ArrayList<DataSetXML> getDataset() {
		return dataset;
	}

	public void setDataset(ArrayList<DataSetXML> dataset) {
		this.dataset = dataset;
	}
	
}
