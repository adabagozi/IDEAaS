package psb;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.psb.IPSBBusinessLogic;
import it.unibs.bodai.ideaas.psb.PSBFactory;
import utils.DateUtils;
import utils.StaticVariables;

public class TestPSB {
	private IDAO dao;
	private IPSBBusinessLogic iPSBBusinessLogic;
	private DateUtils dateUtils = new DateUtils();

	@Before
	public void setup() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();
			this.iPSBBusinessLogic = PSBFactory.getPSBFactory().getPSBBusinessLogic();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




//	@Test
	public void getSummarisedData() {
		int[] experiment_numbers = {1};
		String interval_type = "days";
		int[] interval_values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

		String[] machines = {"101170"};
		String[] components = {"Unit 1.0", "Unit 2.0", "Unit 3.0"};
		String[] modes = {"0","1"};
//		String[] part_programs = {"0171507161", "0171507268 GR6136", "0171506822 GR6136", "0171506825 GR6136", "0171506808"};
		String[] part_programs = {"0171507160"};
		for (int interval_value: interval_values) {
			for(int experiment_number: experiment_numbers){

				int feature_space_id = 1;
				String start_datetime = "06/01/2016 00:00:00";
				String end_datetime = "31/01/2016 23:59:59";
				LocalDateTime datetime = this.dateUtils.getDateTimeFromString(start_datetime);
				LocalDateTime endDateTime = this.dateUtils.getDateTimeFromString(end_datetime);

				Optional<FeatureSpace> fs = this.dao.readFeatureSpace(feature_space_id);
				if (fs.isPresent()) {
					while (datetime.isBefore(endDateTime)) {
						LocalDateTime start = LocalDateTime.from(datetime);
						datetime = datetime.plusDays(interval_value);
						
						for(String machineID: machines) {
							DimensionInstance machineInstance = dao.readDimensionInstance("Macchina", machineID);
							for(String componentID: components) {
								//TODO bisognerebbe dare la possibilità di leggere la dimension in base al parent (non solo qui, ma anche in acquisitione  altrove)
								DimensionInstance componentInstance = dao.readDimensionInstance("Componente", componentID);
								for(String modeID: modes) {
									DimensionInstance modeInstance = dao.readDimensionInstance("Mode", modeID);
//											new DimensionInstance(modeID,"Mode: " + modeID, true, null, new Dimension(7, "Mode"));
									for(String partProgramID: part_programs) {
										DimensionInstance partProgramInstance = dao.readDimensionInstance("Part Program", partProgramID);
//												new DimensionInstance(partProgramID,
//												"Part Program: " + partProgramID, true, null, new Dimension(5, "Part Program"));
										
										JSONObject json = new JSONObject();
										json = this.iPSBBusinessLogic.getSumData(fs.get(), dateUtils.getStringFromDateTime(start), dateUtils.getStringFromDateTime(datetime), experiment_number, machineID, componentID, modeID, partProgramID);

										if(!json.toString().equalsIgnoreCase("")) {
											json.put("state", "success");
											json.put("message", "Dati recuperati per il test: "+ experiment_number+" e la feature space: "+fs.get().getName());
											String intType = interval_type.equalsIgnoreCase("days") ? "D" : "H";
											String fileName = this.buildFileName(start, experiment_number, feature_space_id, intType, interval_value, machineInstance.getId(), componentInstance.getId(), modeInstance.getId(), partProgramInstance.getId());
											this.saveFile(json.toString(), fileName);
										}

									}	
								}
							}
						}
					}
				}else {
					System.out.println("======> getSummarisedData: feature space no present");
				}
				System.out.println("\n------------------------------- Fine Dati Test "+experiment_number+" ------------------------------------");
			}
		}
	}

	public String buildFileName(LocalDateTime start, int experiment, int feature_space_id, String intervalType, int intervalValue, int machine, int component, int mode, int partProgram) {
		String fileName = "";
		String day = start.getDayOfMonth() > 9 ? (""+start.getDayOfMonth()) : ("0"+start.getDayOfMonth());
		String month = start.getMonthValue() > 9 ? (""+start.getMonthValue()) : ("0"+start.getMonthValue());
		fileName = "ymd"+start.getYear()+month+day+"_hms"+start.getHour()+start.getMinute()+start.getSecond()+"_it"+intervalType+"_iv"+intervalValue+"_mcn"+machine+"_spn"+component+"_mod"+mode+"_pp"+partProgram+"_fs"+feature_space_id+"_n"+experiment;//++"_fs"+feature_space_id+"_"+start.getYear()+"_"+start.getMonth()+"_"+day+".json";

		return fileName;
	}

	public void saveFile(String json, String fileName) {
		Writer output = null;
		try {

			File file = new File("src/main/resources/"+fileName);
			output = new BufferedWriter(new FileWriter(file));
			output.write(json);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	

//	@Test
	public void readData() {
		int featureSpaceID = 4;
		String machine = "101170";
		String unit = "Unit 1.0";
		LocalDateTime dateTime = this.dateUtils.getLocalDateTime("2016-07-01T00:00:00.000Z");
		try {
			System.out.println(this.iPSBBusinessLogic.getMeasures(dateTime, featureSpaceID, machine, unit, "-1", "-1", "-1", StaticVariables.DAYS, 1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
