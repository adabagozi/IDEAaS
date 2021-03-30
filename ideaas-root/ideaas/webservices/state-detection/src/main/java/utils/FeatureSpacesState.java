package utils;

import java.util.ArrayList;
import java.util.Collection;

import it.unibs.bodai.ideaas.dao.implementation.storage.SendAlertObjXML;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;

public class FeatureSpacesState {
	
	public static ArrayList<SendAlertObjXML> setFeatureSpaceState(ArrayList<FeatureSpace> fsps, Collection<Measure> measures) {
		ArrayList<SendAlertObjXML> state_changed_send_alerts = new ArrayList<>();
		
		ArrayList<FeatureSpace> unique_feature_spaces = new ArrayList<>();
		for(FeatureSpace fsp : fsps) {
			if(!unique_feature_spaces.contains(fsp)) {
				unique_feature_spaces.add(fsp);
			}
		}
		
		// ricreati per evitare di avere lo stesso riferimento con le fsp di sopra
		ArrayList<FeatureSpace> fsps_state = new ArrayList<>();
		for(FeatureSpace fsp : unique_feature_spaces) {
			FeatureSpace f = new FeatureSpace(fsp.getId(), fsp.getName());
			f.setState("");
			fsps_state.add(f);
		}
		
		if(!(measures.isEmpty() || measures.size() == 0)) {
			
			for(Measure m : measures) {
				ArrayList<String> fsp_row = new ArrayList<>();

				for(FeatureSpace fsp : fsps) {
//					System.out.println("Obj id: " + m.getId());

					Collection<FeatureData> feature_data = new ArrayList(); //m.getFeaturesData();
					for(FeatureData fd : feature_data) {
						for(Feature f : fsp.getFeatures()) {
							if(f.getId() == fd.getFeature().getId()) {
								fsp_row.add(fd.getStatus() + " " + fd.getFeature().getName() + " " + fsp.getName());
//								System.out.println(fd.getStatus() + " " + fd.getFeature().getName() + " " + fsp.getName());
							}
						}
					}
				}
				
				for(FeatureSpace fspace : unique_feature_spaces) {
					/**
					 * 1. Set Feature Space state in every measure
					 */
					FeatureSpacesUtils.countFspState(fspace, fsp_row);
				}
				
				for(FeatureSpace featsp_st : fsps_state) {
//					System.out.println("Feature with empty state: " + featsp_st.getState());
					for(FeatureSpace fsp : unique_feature_spaces) {
//						System.out.println("Feature space with spaces setted after counts: " + fsp.getState());
						if(fsp.getName().equals(featsp_st.getName())) {
							/**
							 * 2.a Evaluate change state for feature space
							 */
							if(!fsp.getState().equals(featsp_st.getState())) {
//								System.out.println(featsp_st.getName() +  " current state: " + featsp_st.getState() + " To changed in: " + fsp.getState());
								/**
								 * If 2.a true => Change state
								 */
								featsp_st.setState(fsp.getState());
//								System.out.println("Obj id: " + m.getId() + " - Feature space: " + featsp_st.getName() + " - State: " + featsp_st.getState());
								FeatureSpace fsp_to_xml = new FeatureSpace(fsp.getId(), fsp.getName());
								fsp_to_xml.setState(fsp.getState());
								SendAlertObjXML sendAlertMySQL = new SendAlertObjXML(m, fsp_to_xml);
								state_changed_send_alerts.add(sendAlertMySQL);
								
							} else {
								System.out.println("Same state: " + featsp_st.getState() + " " + featsp_st.getName() + " " + fsp.getName() + " "  + fsp.getState());
							}
						}
					}
				}
				
			} 
		} else {
			System.out.println("Measures empty");
		}
		
		return state_changed_send_alerts;
		
	}

	
}


