package utils;

import java.util.ArrayList;
import java.util.Collections;

import xml_models.FeatureSpaceXML;
import xml_models.FeatureXML_Response;

public class StatePropagation {
	
	public static String featureSpaceState(ArrayList<FeatureSpaceXML> featureSpacesXML) {
		String dimension_state = "";
		for(FeatureSpaceXML fsp_xml : featureSpacesXML) {
			ArrayList<String> features_state = new ArrayList<>();
			for(FeatureXML_Response f_xml : fsp_xml.getFeatures()) {
				String state = f_xml.getState();
				features_state.add(state);
			}
			
			int ok_occurences = Collections.frequency(features_state, Consts.STATE_OK);
			int warning_occurrences = Collections.frequency(features_state, Consts.STATE_WARNING);
			int error_occurrences = Collections.frequency(features_state, Consts.STATE_ERROR);
			
			if(ok_occurences == features_state.size()) {
				fsp_xml.setState(Consts.STATE_OK);
				dimension_state = Consts.STATE_OK;
			}
			
			if(warning_occurrences != 0 && error_occurrences == 0) {
				fsp_xml.setState(Consts.STATE_WARNING_1);
				dimension_state = Consts.STATE_WARNING_1;
			}
			
			if(warning_occurrences != 0 && warning_occurrences == features_state.size() && error_occurrences == 0) {
				fsp_xml.setState(Consts.STATE_WARNING_2);
				dimension_state = Consts.STATE_WARNING_2;
			}
			
			if(error_occurrences != 0 && error_occurrences != features_state.size()) {
				fsp_xml.setState(Consts.STATE_ERROR_1);
				dimension_state = Consts.STATE_ERROR_1;
			}
			
			if(error_occurrences == features_state.size()) {
				fsp_xml.setState(Consts.STATE_ERROR_2);
				dimension_state = Consts.STATE_ERROR_2;
			}
			
		}
		return dimension_state;
	}
	
	public static String setChangeState(int oks, int warnings, int errors, ArrayList<String> features_state) {
		String dimension_state = "";

			int ok_occurences = Collections.frequency(features_state, Consts.STATE_OK);
			int warning_occurrences = Collections.frequency(features_state, Consts.STATE_WARNING);
			int error_occurrences = Collections.frequency(features_state, Consts.STATE_ERROR);
			
			if(ok_occurences == features_state.size()) {
				dimension_state = Consts.STATE_OK;
			}
			
			if(warning_occurrences != 0 && error_occurrences == 0) {
				dimension_state = Consts.STATE_WARNING_1;
			}
			
			if(warning_occurrences != 0 && warning_occurrences == features_state.size() && error_occurrences == 0) {
				dimension_state = Consts.STATE_WARNING_2;
			}
			
			if(error_occurrences != 0 && error_occurrences != features_state.size()) {
				dimension_state = Consts.STATE_ERROR_1;
			}
			
			if(error_occurrences == features_state.size()) {
				dimension_state = Consts.STATE_ERROR_2;
			}
			
		return dimension_state;
	}
	
	
}