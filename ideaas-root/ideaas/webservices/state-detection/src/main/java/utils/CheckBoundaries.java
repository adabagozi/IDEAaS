package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.Measure;

public class CheckBoundaries {
	
	public static Collection<Measure> featureBoundaries(ArrayList<Feature> features_mysql, Collection<Measure> measures) {
		Collection<Measure> measures_checked = new ArrayList<>();
		for(Measure m : measures) {

			Collection<FeatureData> feature_data = new ArrayList(); //m.getFeaturesData();
			Date timestamp = m.getTimestamp();
			Collection<FeatureData> feature_data_updated = new ArrayList<>();
			for(Feature f : features_mysql) {
				for(FeatureData fd : feature_data) {
//					System.out.println(f.getLowerBoundError() + " " + f.getLowerBoundWarning() + " " + f.getUpperBoundWarning() + " " + f.getUpperBoundError());
					if(f.getId() == fd.getFeature().getId()) {
						fd.setFeature(f);
//						System.out.println(f.getFeatureSpace().getName());
						double feature_value = fd.getValue();
						if(feature_value < f.getLowerBoundError() || feature_value > f.getUpperBoundError()) {
							fd.setStatus(Consts.STATE_ERROR);
//							System.out.println("Feature Value: " + fd.getValue() + " Lower bound error " + f.getLowerBoundError() + " Feature state: " + fd.getStatus());
						} else if (feature_value < f.getLowerBoundWarning() && f.getLowerBoundError() < feature_value || feature_value > f.getUpperBoundWarning() && feature_value < f.getUpperBoundError()) {
							fd.setStatus(Consts.STATE_WARNING);
//							System.out.println("Feature Value: " + fd.getValue() + " Lower bound warning " + f.getLowerBoundWarning() + " Feature state: " + fd.getStatus());
						} else {
							fd.setStatus(Consts.STATE_OK);
//							System.out.println("Feature Value: " + fd.getValue() + " Feature state: " + fd.getStatus());
						}
						FeatureData f_data = new FeatureData(fd.getFeature(), feature_value, timestamp, fd.getStatus());
						feature_data_updated.add(f_data);
					}
				}
			}
		//	m.setFeaturesData(feature_data_updated);
			measures_checked.add(m);
		}
		return measures_checked;
	}

	//TODO 
	public static void contextBoundaries() {
		
	}
}	
