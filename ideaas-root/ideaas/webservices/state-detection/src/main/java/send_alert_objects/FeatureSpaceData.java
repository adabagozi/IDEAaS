package send_alert_objects;

import java.util.ArrayList;

import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;

public class FeatureSpaceData {

	private FeatureSpace fsp;
	private FeatureData f_data;
	private ArrayList<FeatureData> feature_data_list = new ArrayList<>();
	
	public FeatureSpaceData(FeatureSpace fsp, FeatureData fd) {
		this.setFsp(fsp);
		this.setF_data(fd);
	}
	
	public FeatureSpaceData(FeatureSpace feature_space) {
		this.fsp = feature_space;
		this.feature_data_list = new ArrayList<>();
	}
	public FeatureSpaceData(FeatureSpace fsp, ArrayList<FeatureData> fd) {
		this.setFsp(fsp);
		this.setFd(fd);
	}

	public FeatureSpace getFeatureSpace() {
		return fsp;
	}

	public void setFsp(FeatureSpace fsp) {
		this.fsp = fsp;
	}

	public FeatureData getF_data() {
		return f_data;
	}

	public void setF_data(FeatureData f_data) {
		this.f_data = f_data;
	}

	public ArrayList<FeatureData> getFeatureDataList() {
		return feature_data_list;
	}

	public void setFd(ArrayList<FeatureData> fd) {
		this.feature_data_list = fd;
	}
	
	public void addFeatureData(FeatureData feature_data) {
		this.feature_data_list.add(feature_data);
	}
}
