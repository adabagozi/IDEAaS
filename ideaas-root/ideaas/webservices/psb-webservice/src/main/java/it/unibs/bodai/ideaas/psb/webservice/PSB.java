package it.unibs.bodai.ideaas.psb.webservice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.message.internal.MediaTypes;
import org.json.JSONArray;
import org.json.JSONObject;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.psb.IPSBBusinessLogic;
import it.unibs.bodai.ideaas.psb.PSBFactory;
import utils.DateUtils;

/**
 * @author Ada Bagozi
 *
 */
@Path("service")
public class PSB {
	private static final Logger LOG = Logger.getLogger(PSB.class.getName());


	private IPSBBusinessLogic iPSBBusinessLogic;
	private DateUtils dateUtils;
	private IDAO dao;




	public PSB() throws IOException {
		this.iPSBBusinessLogic = PSBFactory.getPSBFactory().getPSBBusinessLogic();
		this.dateUtils = new DateUtils();
		this.dao = DAOFactory.getDAOFactory().getDAO();
	}


	@GET
	@Path("/{saluto}")
	public String testGet(@PathParam("saluto") String saluto) {
		String messageHTML = saluto + " mondo.";
		return messageHTML;
	}

	@GET
	@Path("/getSummarisedData")
	public String getSummarisedData() {
		System.out.println("******************************************************* getSummarisedData *******************************************");
//		String start_datetime = "";
//		String end_date = "";
		String start_datetime = "01/01/2016 00:00:00";
		String end_datetime = "31/01/2016 23:59:59";
		int experiment_number = 2;
		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(1);
		if (fs.isPresent()) {
			//		int expNumber = Integer.parseInt(experiment_number);
			JSONObject json = new JSONObject();//this.iPSBBusinessLogic.getSumData(fs.get(), start_datetime, end_datetime, experiment_number);
			return json.toString();
		}else {
			System.out.println("======> getSummarisedData: feature space no present");
			JSONObject json = new JSONObject();
			json.put("state", "error");
			json.put("message", "Feature Space non trovata");
			return json.toString();
		}
	}
	
	
	@POST
	@Path("/getSummarisedDataPOST")
	public String getSummarisedData(@FormParam("feature_space_id") int feature_space_id, @FormParam("start_datetime") String start_datetime, @FormParam("end_datetime") String end_datetime, @FormParam("experiment_number") int experiment_number) {
		LOG.severe("START: "+start_datetime);
		LOG.severe("END: "+end_datetime);
		LOG.severe("FEATURE_SPACE_ID: "+feature_space_id);
		LOG.severe("EXPERIMENT_NUMBER: "+experiment_number);
		
		Optional<FeatureSpace> fs = this.dao.readFeatureSpace(feature_space_id);
		if (fs.isPresent()) {
			JSONObject json = new JSONObject();
			//		int expNumber = Integer.parseInt(experiment_number);
//			json = this.iPSBBusinessLogic.getSumData(fs.get(), start_datetime, end_datetime, experiment_number);
			json.put("state", "success");
			json.put("message", "Dati recuperati per il test: "+ experiment_number+" e la feature space: "+fs.get().getName());
			return json.toString();
		}else {
			System.out.println("======> getSummarisedData: feature space no present");
			JSONObject json = new JSONObject();
			json.put("state", "error");
			json.put("message", "Feature Space non trovata");
			return json.toString();
		}
	}
	
	@POST
	@Path("/readData")
	public String readData(@FormParam("select_features") int select_features,		
			@FormParam("select_utensile") String select_utensile, @FormParam("select_part_program") String select_part_program,
			@FormParam("select_mode") String select_mode, @FormParam("select_macchina") String select_macchina, 
			@FormParam("select_componente") String select_componente,
			@FormParam("datetime") String datetime, @FormParam("intervalType") String intervalType,
			@FormParam("intervalValue") int intervalValue) {
		LocalDateTime dateTime = this.dateUtils.getLocalDateTime(datetime);

		JSONObject json = new JSONObject();
//		json.put("fs", select_features);
		try {
			json.put("status", "success");
			json.put("data", this.iPSBBusinessLogic.getMeasures(dateTime, select_features, select_macchina, select_componente, select_utensile, select_mode, select_part_program, intervalType, intervalValue));
		} catch (Exception e) {
			json.put("status", "error");
			json.put("data", e.getMessage());
			e.printStackTrace();
		}
		return json.toString();
	}

}
