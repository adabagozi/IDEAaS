package it.unibs.bodai.ideaas.standalone.data_acquisition_standalone;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.timing.TimingDataReading;
import it.unibs.bodai.ideaas.data_acquisition.DataAcquisitionFactory;
import it.unibs.bodai.ideaas.data_acquisition.IDataAcquisition;
import utils.RequestHandler;
import utils.StaticVariables;

/**
 * @author Ada Bagozi command on server to run data acquisition: java -jar
 *         data-acquisition-standalone.jar -d "input_porta/" command to run in
 *         background data-acquisition: nohup java -jar
 *         data-acquisition-standalone-0.0.1-SNAPSHOT-jar-with-dependencies.jar
 *         -d "input_porta/" & command to see status: tail -f nohup.out command
 *         to see active process: ps -e | grep java command to see stop process:
 *         kill -9 process_id
 */
public class DataAcquisitionStandaloneMain {

	private IDataAcquisition dataAcquisition;
	private IDAO dao;

	private String directory;
	private boolean withAnomaly = false;

	Logger mongoLogger = Logger.getLogger("org.mongodb.driver");

	public static void main(String[] args) {
		DataAcquisitionStandaloneMain m = new DataAcquisitionStandaloneMain();
		
		
	//	m.populateCommandLineOptions(args);
		m.directory = "input_porta/";
		m.withAnomaly = true;
		m.setup(m.withAnomaly);
		System.out.println("Directory: " + m.directory);
		System.out.println("With anomaly: " + m.withAnomaly);
		if (m.directory != null) {
			try {
				m.dataAcquisition.generateMeasuresFromFilesInFolder(m.directory);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void populateCommandLineOptions(String[] args) {
		CommandLineParser parser = new DefaultParser();

		// create CLI options
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addRequiredOption("d", "directory", true,
				"The directory we will analyze recursively in order to acquire data");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("Data Acquisition Standalone software", options);
				System.exit(0);
			}

			this.directory = line.getOptionValue("directory");
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			System.exit(0);
		}
	}

	private void readDataTesting(String collectionName) {

		String[] machines = { "101170" };
		String[] components = { "Unit 1.0", "Unit 2.0", "Unit 3.0" };
		String[] modes = { "0", "1" };
		String[] part_programs = {"0171506406"};
		int[] tools = { 1, 4, 2, 5, 3, 6, 7, 8, 25 };
		String start_date = "01/07/2016 00:00:00";
		String end_date = "07/07/2016 23:59:59";
		utils.DateUtils dateUtils = new utils.DateUtils();

		
		LocalDateTime end_datetime = dateUtils.getDateTimeFromString(end_date);
		LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);
	
		while (start_datetime.isBefore(end_datetime)) {
			LocalDateTime dateTimeTwo = dateUtils.getOtherDateTime(start_datetime, StaticVariables.MINUTES, 1, true);
			for (String machineID : machines) {
				for (String componentID : components) {
					for (String modeID : modes) {
						for (String partProgramID : part_programs) {
							for (int toolID : tools) {
								String query = dateUtils.getStringFromDateTime(start_datetime)+ " - "+ dateUtils.getStringFromDateTime(dateTimeTwo)+" ---  machineID: " + machineID + "    componentID: " + componentID
										+ "    modeID: " + modeID + "    Part Program: " + partProgramID
										+ "    Tool: " + toolID;
								System.out.println(query);
								long startReading = System.currentTimeMillis();
								Collection<Measure> readMeasures = dao.readMeasures(collectionName, ""+toolID, partProgramID, modeID, machineID, componentID, start_datetime, StaticVariables.HOURS, 12);
								if(readMeasures.size()>0) {
									dao.insertDataReadingTime(new TimingDataReading(readMeasures.size(), System.currentTimeMillis()-startReading, collectionName, "6; "+ query));
								}
							}
							String query = dateUtils.getStringFromDateTime(start_datetime)+ " - "+ dateUtils.getStringFromDateTime(dateTimeTwo)+" ---  machineID: " + machineID + "  componentID: " + componentID
									+ "    modeID: " + modeID + "   Part Program: " + partProgramID;
							System.out.println(query);
							long startReading = System.currentTimeMillis();
							Collection<Measure> readMeasures = dao.readMeasures(collectionName, "-1", partProgramID, modeID, machineID, componentID, start_datetime, StaticVariables.HOURS, 12);
							if(readMeasures.size()>0) {
								dao.insertDataReadingTime(new TimingDataReading(readMeasures.size(), System.currentTimeMillis()-startReading, collectionName, "5; "+query));
							}
						}
						String query = dateUtils.getStringFromDateTime(start_datetime)+ " - "+ dateUtils.getStringFromDateTime(dateTimeTwo)+" ---  machineID: " + machineID + "   componentID: " + componentID
								+ "    modeID: " + modeID;
						System.out.println(query);
						long startReading = System.currentTimeMillis();
						Collection<Measure> readMeasures = dao.readMeasures(collectionName, "-1", "-1", modeID, machineID, componentID, start_datetime, StaticVariables.HOURS, 12);
						if(readMeasures.size()>0) {
							dao.insertDataReadingTime(new TimingDataReading(readMeasures.size(), System.currentTimeMillis()-startReading, collectionName, "4; "+query));
							
						}
					}
					String query = dateUtils.getStringFromDateTime(start_datetime)+ " - "+ dateUtils.getStringFromDateTime(dateTimeTwo)+" ---  machineID: " + machineID + "   componentID: " + componentID;
					System.out.println(query);
					long startReading = System.currentTimeMillis();
					Collection<Measure> readMeasures = dao.readMeasures(collectionName, "-1", "-1", "-1", machineID, componentID, start_datetime, StaticVariables.MINUTES, 1);
					if(readMeasures.size()>0) {
						dao.insertDataReadingTime(new TimingDataReading(readMeasures.size(), System.currentTimeMillis()-startReading, collectionName, "3; "+query));
					}
				}
				String query = dateUtils.getStringFromDateTime(start_datetime)+ " - "+ dateUtils.getStringFromDateTime(dateTimeTwo)+" ---  machineID: " + machineID;
				System.out.println(query);
				long startReading = System.currentTimeMillis();
				Collection<Measure> readMeasures = dao.readMeasures(collectionName, "-1", "-1", "-1", machineID, "-1", start_datetime, StaticVariables.MINUTES, 1);
				if(readMeasures.size()>0) {
					dao.insertDataReadingTime(new TimingDataReading(readMeasures.size(), System.currentTimeMillis()-startReading, collectionName, "2; "+query));
				}
			}
			String query = dateUtils.getStringFromDateTime(start_datetime)+ " - "+ dateUtils.getStringFromDateTime(dateTimeTwo)+" --- timestamps";
			long startReading = System.currentTimeMillis();
			Collection<Measure> readMeasures = dao.readMeasures(collectionName, "-1", "-1", "-1", "-1", "-1", start_datetime, StaticVariables.MINUTES, 1);
			if(readMeasures.size()>0) {
				dao.insertDataReadingTime(new TimingDataReading(readMeasures.size(), System.currentTimeMillis()-startReading, collectionName, "1; "+query));
			}
			start_datetime = LocalDateTime.from(dateTimeTwo);
		}
		
		try {
			RequestHandler.callRemoteURL(StaticVariables.URL_MAIL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setup(boolean withAnomaly) {
		try {
			if(withAnomaly) {
				this.dataAcquisition = DataAcquisitionFactory.getDataAcquisitionFactory().getDataAquisitionAnomaly();
			}else {
				this.dataAcquisition = DataAcquisitionFactory.getDataAcquisitionFactory().getDataAquisition();
			}
			this.dao = DAOFactory.getDAOFactory().getDAO();
			mongoLogger.setLevel(Level.SEVERE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
