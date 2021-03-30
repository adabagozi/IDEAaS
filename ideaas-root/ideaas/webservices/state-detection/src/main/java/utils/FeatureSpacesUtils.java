package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;

public class FeatureSpacesUtils {

	public static void countFspState(FeatureSpace fsp, ArrayList<String> statuses) {
		ArrayList<String> fsp_statuses = new ArrayList<>();
		
		for(String s : statuses) {
			if(s.contains(fsp.getName())) {
//				System.out.println(s);
				String[] fsp_row = s.split(" ");
				fsp_statuses.add(fsp_row[0]);
			}
		}
		
		int ok_status = Collections.frequency(fsp_statuses, Consts.STATE_OK);
		int warning_status = Collections.frequency(fsp_statuses, Consts.STATE_WARNING);
		int error_status = Collections.frequency(fsp_statuses, Consts.STATE_ERROR);
//		System.out.println("Feature Space - " + fsp.getName() + " Ok Status: " + ok_status + " Warning status: " + warning_status + " Error status: " + error_status);
		
		String current_state = StatePropagation.setChangeState(ok_status, warning_status, error_status, fsp_statuses);
		fsp.setState(current_state);
//		System.out.println("FeatureSpace now state: " + fsp.getState() + " FeeatureSpace: " + fsp.getName());
		
	}
	
	public static ArrayList<FeatureSpace> setFeatureToFeatureSpaces(ArrayList<FeatureSpace> featureSpaces) {
		ArrayList<FeatureSpace> unique_feature_spaces = new ArrayList<>();
		for(FeatureSpace fsp_a : featureSpaces) {
			for(FeatureSpace fsp_b : featureSpaces) {
				if(fsp_a.getId() == fsp_b.getId()) {
					fsp_a.setFeatures(fsp_b.getFeatures());
				}
			}
			if(!unique_feature_spaces.contains(fsp_a)) {
				unique_feature_spaces.add(fsp_a);
			}
		}
		return unique_feature_spaces;
	}
	
	public static FeatureSpace setFeaturesToFeatureSpaces(FeatureSpace fsp, ArrayList<FeatureSpace> feature_spaces) {
		Collection<Feature> features = new ArrayList<>();
		for(FeatureSpace fspace : feature_spaces) {
			if(fspace.getId() == fsp.getId()) {
//				System.out.println("Fsp: " + fsp.getName() + " Fsp with feature: " + fspace.getName());
				for(Feature f : fspace.getFeatures()) {
//					System.out.println("Fsp feature: "  + fsp.getName() + " Feature id: " + f.getId());
					features.add(f);
				}
			}
		}
		fsp.setFeatures(features);
		return fsp;
	}

}
