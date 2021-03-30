package send_alert_objects;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import api_responses.SendAlertResponse;
import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.implementation.storage.SendAlertMySQL;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.Measure;
import utils.Consts;
import utils.RequestHandler;
import utils.StatePropagation;
import xml_models.SendAlertResponseXML;

public class FspDataAndState {
	private IDAO dao;

	public FspDataAndState() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public int featureDataAndFeatureSpace(ArrayList<FeatureSpace> fsps, Collection<Measure> measures_checked, ArrayList<DimensionInstance> systemDimensions) {

		int alert_numbers = 0;

		//TODO aggiornare lo stato delle feature space precedenti.... cambiarlo... E non ricaricare i dati dal database
		ArrayList<FeatureSpace> unique_feature_spaces = this.dao.sendAlertFeatureSpaceState();	

		SendAlertResponseXML fsp_xml_to_send = new SendAlertResponseXML();
		for(Measure m : measures_checked) {			
			//Step 1: for every measure create an object feature space and feature data and store this rows in an arraylist
			ArrayList<FeatureSpaceData> fsp_data = new ArrayList<>();
			for(FeatureSpace fsp : fsps) {

				Collection<FeatureData> fdList = new ArrayList(); //m.getFeaturesData();
				for(FeatureData fd: fdList) {
					for(Feature f : fsp.getFeatures()) {
						if(fd.getFeature().getId() == f.getId()) {
							FeatureSpaceData fdata = new FeatureSpaceData(fsp, fd);
							fsp_data.add(fdata);
						}
					}
				}
			}

			//Step 2: evaluate status for every feature space, based on rows computed above
			ArrayList<FeatureSpace> nfsps = setFeatureSpaceState(fsps, fsp_data);

			//Step 3: compare last status saved from unique_fsp with the new one of the Step 2			
			for(FeatureSpace pfsp : unique_feature_spaces) {
				//System.out.println(pfsp.getName() + " - State : " + pfsp.getState());
				for(FeatureSpace cfsp : nfsps) {
					if(pfsp.getName().equals(cfsp.getName())) {
						//Step 3.1: check if status has been changed
						if(!(pfsp.getState().equals(cfsp.getState()))) {
							pfsp.setState(cfsp.getState());
							//Step 3.2: save the current state changed in mysql db
							SendAlertMySQL sendAlert = new SendAlertMySQL(m.getId(), new Timestamp(System.currentTimeMillis()), pfsp.getName(), pfsp.getState(), pfsp.getId());
							this.dao.saveSendAlertStateMySQL(sendAlert);
							//Step 3.3: call xml builder method and return the relative xml
							fsp_xml_to_send = SendAlertResponse.build_send_alert_xml(pfsp, fsps, m, systemDimensions);
							File xml_file = new File("sendAlert.xml");
							JAXBContext context;
							try {
								context = JAXBContext.newInstance(SendAlertResponseXML.class);
								Marshaller marshaller = context.createMarshaller();
								marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
								alert_numbers++;
								//								marshaller.marshal(fsp_xml_to_send, System.out);
								//								
								StringWriter stringWriter = new StringWriter();
								marshaller.marshal(fsp_xml_to_send, stringWriter);
								String xml=stringWriter.toString();

								//								long start_send_xml = System.currentTimeMillis();
								//								RequestHandler.sendRequestHTTPClient(Consts.HYLASOFT_API, xml);
								//								submit_xml = System.currentTimeMillis() - start_send_xml;
								//								
								//								RequestHandler.sendRequest(Consts.HYLASOFT_API, xml_file); //Hylasoft API:Consts.HYLASOFT_API  ========= Local API: Consts.LOCAL_API
							} catch (JAXBException e) {
								e.printStackTrace();
							} 
							//							catch (MalformedURLException e) {
							//								e.printStackTrace();
							//							} catch (IOException e) {
							//								e.printStackTrace();
							//							}
						}
					}
				}
			}	
		}
		return alert_numbers;
	}




	public int checkDataAndSendAlert(ArrayList<FeatureSpace> feature_spaces, Collection<Measure> measures_checked, ArrayList<DimensionInstance> systemDimensions) {
		int alert_numbers = 0;
		//		System.out.println("feature_spaces :"+feature_spaces.size());

		//TODO introdurre le hashmap per abattere i tempi di for.
		for(Measure m : measures_checked) {	
			ArrayList<FeatureSpaceData> feature_space_data_list = new ArrayList<>();
			for(FeatureSpace feature_space : feature_spaces) {

				FeatureSpaceData feature_space_data = new FeatureSpaceData(feature_space);

				Collection<FeatureData> fdList = new ArrayList(); //m.getFeaturesData();
				for(FeatureData feature_data: fdList) {
					if (feature_space.getFeatures_list().containsKey(feature_data.getFeature().getId())) {
						feature_space_data.addFeatureData(feature_data);
					}
				}
				if(!feature_space_data.getFeatureDataList().isEmpty()) {
					String featureState = this.checkFeatureSpaceStatus(feature_space_data);
					//Step 3.1: check if status has been changed
					if(!(feature_space_data.getFeatureSpace().getState().equals(featureState))) {
						feature_space_data.getFeatureSpace().setState(featureState);
						feature_space_data_list.add(feature_space_data);

						alert_numbers = alert_numbers + this.sendAlertFeatureSpace(m, feature_spaces, feature_space_data, systemDimensions);
					}
				}
			}

		}
		return alert_numbers;
	}

	public int sendAlertFeatureSpace(Measure m, ArrayList<FeatureSpace> feature_spaces, FeatureSpaceData feature_space_data, ArrayList<DimensionInstance> systemDimensions) {
		int alert_numbers = 0;
		SendAlertResponseXML fsp_xml_to_send = new SendAlertResponseXML();
		//Step 3.2: save the current state changed in mysql db
		SendAlertMySQL sendAlert = new SendAlertMySQL(m.getId(), new Timestamp(System.currentTimeMillis()), feature_space_data.getFeatureSpace().getName(), feature_space_data.getFeatureSpace().getState(), feature_space_data.getFeatureSpace().getId());
		this.dao.saveSendAlertStateMySQL(sendAlert);

		//Step 3.3: call xml builder method and return the relative xml
		fsp_xml_to_send = SendAlertResponse.build_send_alert_xml(feature_space_data.getFeatureSpace(), feature_spaces, m, systemDimensions);
		File xml_file = new File("sendAlert.xml");
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(SendAlertResponseXML.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			alert_numbers++;
//			marshaller.marshal(fsp_xml_to_send, System.out);
			//			
			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(fsp_xml_to_send, stringWriter);
			String xml=stringWriter.toString();

			//			long start_send_xml = System.currentTimeMillis();
			//			RequestHandler.sendRequestHTTPClient(Consts.HYLASOFT_API, xml);
			//			submit_xml = System.currentTimeMillis() - start_send_xml;
			//			
			//			RequestHandler.sendRequest(Consts.HYLASOFT_API, xml_file); //Hylasoft API:Consts.HYLASOFT_API  ========= Local API: Consts.LOCAL_API
		} catch (JAXBException e) {
			e.printStackTrace();
		} 
		//		catch (MalformedURLException e) {
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}

		return alert_numbers;
	}


	/**
	 * Evaluate state for every feature space monitored (from custom object featurespacedata)  
	 * @param feature_spaces
	 * @param fsp_data
	 * @return
	 */
	private String checkFeatureSpaceStatus(FeatureSpaceData feature_space_data) {
		ArrayList<String> fsp_statuses = new ArrayList<>();
		//TODO potrebbe decisamente essere meglio
		for (FeatureData feature_data: feature_space_data.getFeatureDataList()) {
			fsp_statuses.add(feature_data.getStatus());
		}
		int ok_status = Collections.frequency(fsp_statuses, Consts.STATE_OK);
		int warning_status = Collections.frequency(fsp_statuses, Consts.STATE_WARNING);
		int error_status = Collections.frequency(fsp_statuses, Consts.STATE_ERROR);

		return StatePropagation.setChangeState(ok_status, warning_status, error_status, fsp_statuses);	
	}

	/**
	 * Evaluate state for every feature space monitored (from custom object featurespacedata)  
	 * @param feature_spaces
	 * @param fsp_data
	 * @return
	 */
	public static ArrayList<FeatureSpace> setFeatureSpaceState(ArrayList<FeatureSpace> feature_spaces, ArrayList<FeatureSpaceData> fsp_data) {
		ArrayList<FeatureSpace> unique_feature_spaces = new ArrayList<>();
		//PErchè?
		for(FeatureSpace fsp : feature_spaces) {
			if(!unique_feature_spaces.contains(fsp)) {
				unique_feature_spaces.add(fsp);
			}
		}

		for(FeatureSpace fsp : unique_feature_spaces) {
			ArrayList<FeatureSpaceData> rows_for_single_fsp = new ArrayList<>();
			for(FeatureSpaceData fspd : fsp_data) {
				if(fsp.getId() == fspd.getFeatureSpace().getId()) {
					rows_for_single_fsp.add(fspd);
				}
			}

			ArrayList<String> fsp_statuses = new ArrayList<>();
			for(FeatureSpaceData row : rows_for_single_fsp) {
				fsp_statuses.add(row.getF_data().getStatus());
			}

			int ok_status = Collections.frequency(fsp_statuses, Consts.STATE_OK);
			int warning_status = Collections.frequency(fsp_statuses, Consts.STATE_WARNING);
			int error_status = Collections.frequency(fsp_statuses, Consts.STATE_ERROR);

			String fsp_state = StatePropagation.setChangeState(ok_status, warning_status, error_status, fsp_statuses);	
			fsp.setState(fsp_state);

		}
		return unique_feature_spaces;
	}


}
