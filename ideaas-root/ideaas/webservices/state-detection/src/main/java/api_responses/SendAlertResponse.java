package api_responses;

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
import it.unibs.bodai.ideaas.dao.model.ParameterInstance;
import utils.Consts;
import utils.FeatureSpacesUtils;
import xml_models.Azienda;
import xml_models.Componente;
import xml_models.FeatureSpaceXML;
import xml_models.FeatureXML_Response;
import xml_models.MS;
import xml_models.Machine;
import xml_models.ParameterListXML;
import xml_models.ParameterXML;
import xml_models.Plant;
import xml_models.SendAlertResponseXML;
import xml_models.ValueXML;

public class SendAlertResponse {

	public static SendAlertResponseXML build_send_alert_xml(FeatureSpace fsp, ArrayList<FeatureSpace> fsps, Measure m,
			ArrayList<DimensionInstance> systemDimensions) {

		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);

		FeatureSpacesUtils.setFeaturesToFeatureSpaces(fsp, fsps);

		ArrayList<FeatureXML_Response> feautures_for_fsp = new ArrayList<>();
		for (Feature f : fsp.getFeatures()) {
			Collection<FeatureData> fdList = new ArrayList(); //m.getFeaturesData();
			for (FeatureData fd : fdList) {
				if (fd.getFeature().getId() == f.getId()) {
					FeatureXML_Response featureXML = new FeatureXML_Response(fd.getFeature().getName(),
							fd.getFeature().getMeasureUnit(), fd.getStatus(),
							new ValueXML(String.valueOf(df.format(m.getTimestamp())), fd.getStatus(),
									String.valueOf(fd.getValue())));
					feautures_for_fsp.add(featureXML);
				}
			}
		}

		Azienda enterprise = new Azienda();
		Plant plant = new Plant();
		Machine machine = new Machine();
		Componente component = new Componente();

		String machine_measure_instance = "";
		String component_measure_instance = "";
		for (DimensionInstance dim : m.getDimensionInstances()) {
			if (dim.getDimension().getId() == Consts.MACHINE_DIMENSION_ID) {
				machine = new Machine(dim.getInstanceId(), fsp.getState());
				machine_measure_instance = dim.getInstanceId();
			} else if (dim.getDimension().getId() == Consts.COMPONENT_DIMENSION_ID) {
				component = new Componente(dim.getInstanceId(), fsp.getState());
				component_measure_instance = dim.getInstanceId();
			}
		}

		for (DimensionInstance d : systemDimensions) {
			if (d.getInstanceId().equals(component_measure_instance)
					&& d.getParentInstance().getInstanceId().equals(machine_measure_instance)) {
				enterprise = new Azienda(d.getParentInstance().getParentInstance().getParentInstance().getInstanceId(),
						fsp.getState());
				plant = new Plant(d.getParentInstance().getParentInstance().getInstanceId(), fsp.getState());
			}
		}
		MS monitored_system = new MS(enterprise, plant, machine, component);

		ArrayList<ParameterXML> parameters_xml = new ArrayList<>();
		for (ParameterInstance p : m.getParameterInstances()) {
			String parameter_instance = p.getInstanceId();
			String parameter_name = p.getParameter().getName();
			ParameterXML parameter = new ParameterXML(parameter_name, parameter_instance);
			parameters_xml.add(parameter);
		}
		ParameterListXML parameter_list_xml = new ParameterListXML(parameters_xml);

		FeatureSpaceXML featureSpaceXML = new FeatureSpaceXML(fsp.getName(), fsp.getState(), feautures_for_fsp);

		SendAlertResponseXML sendAlertResponseXML = new SendAlertResponseXML(fsp.getName() + " " + fsp.getState(),
				String.valueOf(df.format(new Date(System.currentTimeMillis()))), parameter_list_xml, monitored_system,
				featureSpaceXML);
		return sendAlertResponseXML;
	}

}
