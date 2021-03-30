package api_responses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.ParameterInstance;
import utils.Consts;
import utils.ManageArrayList;
import xml_models.Azienda;
import xml_models.Componente;
import xml_models.DataSetListXML;
import xml_models.DataSetXML;
import xml_models.FeatureSpaceListXML;
import xml_models.FeatureSpaceXML;
import xml_models.FeatureXML_Response;
import xml_models.GetDataRespXML;
import xml_models.MS;
import xml_models.Machine;
import xml_models.ParameterListXML;
import xml_models.ParameterXML;
import xml_models.Plant;
import xml_models.ValueXML;

public class GetDataResponse {

	public static GetDataRespXML get_xml(ArrayList<DimensionInstance> dimension_instances,
			ArrayList<FeatureSpace> feature_spaces_mysql, ArrayList<Feature> features_mysql,
			Collection<Measure> measures_checked) {

		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);

		Azienda enterprise = new Azienda();
		Plant plant = new Plant();
		Machine machine = new Machine();
		Componente component = new Componente();

		for (DimensionInstance dim : dimension_instances) {
			if (dim.getDimension().getId() == Consts.ENTERPRISE_DIMENSION_ID) {
				enterprise = new Azienda(dim.getInstanceId());
			} else if (dim.getDimension().getId() == Consts.PLANT_DIMENSION_ID) {
				plant = new Plant(dim.getInstanceId());
			} else if (dim.getDimension().getId() == Consts.MACHINE_DIMENSION_ID) {
				machine = new Machine(dim.getInstanceId());
			} else if (dim.getDimension().getId() == Consts.COMPONENT_DIMENSION_ID) {
				component = new Componente(dim.getInstanceId());
			}
		}

		MS monitored_system = new MS(enterprise, plant, machine, component);

		ArrayList<ParameterListXML> parameterListXMLs = new ArrayList<ParameterListXML>();
		ArrayList<DataSetXML> datasets = new ArrayList<>();
		for (Measure m : measures_checked) {
			Collection<ParameterInstance> parameterInstances = m.getParameterInstances();
			ArrayList<ParameterXML> parameterXMLs = new ArrayList<>();
			for (ParameterInstance p : parameterInstances) {
				String parameter_instance = p.getInstanceId();
				String parameter_name = p.getParameter().getName();
				ParameterXML parameter = new ParameterXML(parameter_name, parameter_instance);
				parameterXMLs.add(parameter);
			}

			ParameterListXML parameter_xml_object = new ParameterListXML(parameterXMLs);

			Collection<FeatureData> feature_data = new ArrayList(); //m.getFeaturesData();

			ArrayList<FeatureSpace> unique_feature_spaces = new ArrayList<>();
			for (FeatureSpace fsp : feature_spaces_mysql) {
				if (!unique_feature_spaces.contains(fsp)) {
					unique_feature_spaces.add(fsp);
				}

				for (FeatureData fdata : feature_data) {
					for (Feature f : fsp.getFeatures()) {
						if (fdata.getFeature().getId() == f.getId()) {
							Collection<Feature> features_in_featurespace = new ArrayList<>();
							features_in_featurespace.add(f);
							fsp.setFeatures(features_in_featurespace);
						}
					}
				}
			}

			if (!parameterListXMLs.contains(parameter_xml_object)) {
				parameterListXMLs.add(parameter_xml_object);

				ArrayList<FeatureSpaceXML> featureSpaceXMLs = new ArrayList<>();
				for (FeatureSpace fsp : unique_feature_spaces) {
					ArrayList<FeatureXML_Response> features_xml = new ArrayList<>();
					for (FeatureSpace feature_space : feature_spaces_mysql) {
						if (fsp.getId() == feature_space.getId()) {
							for (Feature f : feature_space.getFeatures()) {
								for (FeatureData fd : feature_data) {
									if (f.getId() == fd.getFeature().getId()) {
										ArrayList<ValueXML> values_xml = new ArrayList<>();
										ValueXML value = new ValueXML(String.valueOf(df.format(m.getTimestamp())),
												fd.getStatus(), String.valueOf(fd.getValue()));
										if (!values_xml.contains(value)) {
											values_xml.add(value);
										}
										FeatureXML_Response feature_xml = new FeatureXML_Response(
												fd.getFeature().getName(), fd.getFeature().getMeasureUnit(), null,
												values_xml);
										features_xml.add(feature_xml);
									}
								}
							}

						}
					}
					FeatureSpaceXML fsp_xml = new FeatureSpaceXML(fsp.getName(), null, features_xml);
					featureSpaceXMLs.add(fsp_xml);

				}

				FeatureSpaceListXML featureSpaceListXML = new FeatureSpaceListXML(featureSpaceXMLs);
				DataSetXML dataSetXML = new DataSetXML(parameter_xml_object, featureSpaceListXML);
				datasets.add(dataSetXML);
			}

			else if (parameterListXMLs.contains(parameter_xml_object)) {
				int index = ManageArrayList.getIndexOf(datasets, parameter_xml_object);
				if (index >= 0) {
					DataSetXML dataSetXML = datasets.get(index);
					FeatureSpaceListXML featureSpaceList = dataSetXML.getFeatureSpaceList();
					List<FeatureSpaceXML> featureSpaces_xml = featureSpaceList.getFeatureSpace();
					for (FeatureSpaceXML fspxml : featureSpaces_xml) {
						ArrayList<FeatureXML_Response> features_xml = fspxml.getFeatures();
						for (FeatureXML_Response f : features_xml) {
							for (FeatureData fd : feature_data) {
								if (f.getName().equals(fd.getFeature().getName())) {
									double value = fd.getValue();
									ValueXML valueXML = new ValueXML(String.valueOf(df.format(m.getTimestamp())),
											fd.getStatus(), String.valueOf(value));
									List<ValueXML> values = f.getValue();
									if (!values.contains(valueXML)) {
										values.add(valueXML);
									}
								}
							}
						}

					}
				} else {
					System.out.println("Parameter list not found");
				}
			}

		}

		DataSetListXML dataSetListXML = new DataSetListXML(datasets);
		GetDataRespXML getDataRespXML = new GetDataRespXML(monitored_system, dataSetListXML);

		return getDataRespXML;
	}

}
