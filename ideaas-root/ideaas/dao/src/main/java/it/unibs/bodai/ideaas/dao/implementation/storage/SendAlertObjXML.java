package it.unibs.bodai.ideaas.dao.implementation.storage;

import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;

public class SendAlertObjXML {

	private Measure measure;
	private FeatureSpace fsp_to_xml;
	
	public SendAlertObjXML(Measure m, FeatureSpace fsp_to_xml) {
		this.setMeasure(m);
		this.setFsp_to_xml(fsp_to_xml);
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	public FeatureSpace getFsp_to_xml() {
		return fsp_to_xml;
	}

	public void setFsp_to_xml(FeatureSpace fsp_to_xml) {
		this.fsp_to_xml = fsp_to_xml;
	}
	
}
