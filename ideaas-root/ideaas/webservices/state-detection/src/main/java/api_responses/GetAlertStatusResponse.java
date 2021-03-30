package api_responses;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;
import utils.Consts;
import utils.StatePropagation;
import xml_models.AlertStatusListXML;
import xml_models.AlertStatusXML;
import xml_models.Azienda;
import xml_models.Componente;
import xml_models.FeatureSpaceListXML;
import xml_models.FeatureSpaceXML;
import xml_models.FeatureXML_Response;
import xml_models.GetAlertStatusResponseXML;
import xml_models.MS;
import xml_models.Machine;
import xml_models.Plant;
import xml_models.ValueXML;

public class GetAlertStatusResponse {
	// build xml

	public static GetAlertStatusResponseXML getXML(ArrayList<DimensionInstance> dimensions_mysql,
			ArrayList<FeatureSpace> featureSpacesMySQL, Collection<Measure> measures) throws IOException {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);

		ArrayList<AlertStatusXML> alertStatusXMLs = new ArrayList<>();
		for (Measure m : measures) {
			// getting data from measure
			Date timestamp = m.getTimestamp();
			String isodate = df.format(timestamp);
			Collection<FeatureData> feature_data_measure_checked = new ArrayList(); //m.getFeatureData();
			Collection<DimensionInstance> dimension_instances = new ArrayList(); //m.getDimensionInstances();

			/**
			 * Monitored system block
			 */
			String machine_measure_instance = "";
			String component_measure_instance = "";
			for (DimensionInstance dim_measure : dimension_instances) {
				if (dim_measure.getDimension().getId() == Consts.COMPONENT_DIMENSION_ID) {
					component_measure_instance = dim_measure.getInstanceId();
				} else if (dim_measure.getDimension().getId() == Consts.MACHINE_DIMENSION_ID) {
					machine_measure_instance = dim_measure.getInstanceId();
				} else {
					System.out.println("Dimension instance not matched");
				}
			}

			String plant_mysql_instance = "";
			String enterprise_mysql_instance = "";
			for (DimensionInstance dim_mysql : dimensions_mysql) {
				String component_mysql_instance = dim_mysql.getInstanceId();
				String machine_mysql_instance = dim_mysql.getParentInstance().getInstanceId();
				if (component_measure_instance.equals(component_mysql_instance)
						&& machine_measure_instance.equals(machine_mysql_instance)) {
					plant_mysql_instance = dim_mysql.getParentInstance().getParentInstance().getInstanceId();
					enterprise_mysql_instance = dim_mysql.getParentInstance().getParentInstance().getParentInstance()
							.getInstanceId();
				}
			}

			Azienda enterprise = new Azienda(enterprise_mysql_instance);
			Plant plant = new Plant(plant_mysql_instance);
			Machine machine = new Machine(machine_measure_instance);
			Componente component = new Componente(component_measure_instance);

			MS monitored_system = new MS(enterprise, plant, machine, component);

			/**
			 * Feature Space Block
			 */
			ArrayList<FeatureSpace> featureSpacesUnique = new ArrayList<>();
			for (FeatureSpace fspace : featureSpacesMySQL) {
				if (!featureSpacesUnique.contains(fspace)) {
					featureSpacesUnique.add(fspace);
				}
			}

			ArrayList<FeatureSpaceXML> featureSpacesXML = new ArrayList<>();
			for (FeatureSpace featureSpace : featureSpacesUnique) {
				ArrayList<FeatureXML_Response> featuresXML = new ArrayList<>();
				for (FeatureSpace feSpace : featureSpacesMySQL) {
					if (featureSpace.getId() == feSpace.getId()) {
						for (FeatureData fd : feature_data_measure_checked) {
							for (Feature f : feSpace.getFeatures()) {
								if (fd.getFeature().getId() == f.getId()) {
									ValueXML valueXML = new ValueXML(String.valueOf(df.format(m.getTimestamp())),
											fd.getStatus(), String.valueOf(fd.getValue()));
									ArrayList<ValueXML> valueXMLs = new ArrayList<>();
									valueXMLs.add(valueXML);
									FeatureXML_Response feature_xml = new FeatureXML_Response(fd.getFeature().getName(),
											fd.getFeature().getMeasureUnit(), fd.getStatus(), valueXMLs);
									featuresXML.add(feature_xml);
								}
							}

						}
					}
				}

				FeatureSpaceXML featureSpaceXML = new FeatureSpaceXML(featureSpace.getName(), "", featuresXML);

				featureSpacesXML.add(featureSpaceXML);
			}

			String dimension_state = StatePropagation.featureSpaceState(featureSpacesXML);
			enterprise.setState(dimension_state);
			plant.setState(dimension_state);
			machine.setState(dimension_state);
			component.setState(dimension_state);
			FeatureSpaceListXML featureSpaceListXML = new FeatureSpaceListXML(featureSpacesXML);
			AlertStatusXML alertStatusXML = new AlertStatusXML(monitored_system, featureSpaceListXML);
			alertStatusXMLs.add(alertStatusXML);

		}

		AlertStatusListXML alertStatusListXML = new AlertStatusListXML(alertStatusXMLs);
		GetAlertStatusResponseXML getData = new GetAlertStatusResponseXML(
				String.valueOf(df.format(new Date(System.currentTimeMillis()))), "GetAlertStatusResponse",
				alertStatusListXML);
		return getData;

	}

}
