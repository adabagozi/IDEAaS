package it.unibs.bodai.ideaas.data_acquisition.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.Parameter;
import it.unibs.bodai.ideaas.dao.model.ParameterInstance;
import it.unibs.bodai.ideaas.data_acquisition.DataAcquisitionFactory;
import it.unibs.bodai.ideaas.data_acquisition.IDataAcquisition;
import utils.RequestHandler;
import utils.StaticVariables;
import utils.UniqueList;

public class TestDataAcquisition {
	private IDataAcquisition dataAcquisition;
	private final static Logger mongoLogger = Logger.getLogger("org.mongodb.driver");

	@Before
	public void setup() {
		try {
			//	this.dataAcquisition = DataAcquisitionFactory.getDataAcquisitionFactory().getDataAquisition();
			//this.dataAcquisition = DataAcquisitionFactory.getDataAcquisitionFactory().getDataAquisitionAnomaly();
			this.dataAcquisition = DataAcquisitionFactory.getDataAcquisitionFactory().getDataAquisitionSmart4CPPS();

			mongoLogger.setLevel(Level.SEVERE);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void generateMeasuresFromFilesInFolder() {
		String directoryPath = "src/main/resources/snapu_auto";
		try {
			this.dataAcquisition.generateMeasuresFromFilesInFolder(directoryPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void generateMeasures() {

		Collection<ParameterInstance> parameterInstances = new ArrayList<>();
		parameterInstances.add(new ParameterInstance("instance1", "Instance 1", true, new Parameter(1, "Parameter 1")));
		parameterInstances.add(new ParameterInstance("instance3", "Instance 3", true, new Parameter(3, "Parameter 3")));
		parameterInstances.add(new ParameterInstance("instance1", "Instance 1", true, new Parameter(2, "Parameter 2")));

		Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
		dimensionInstances.add(new DimensionInstance("101143", new Dimension(1, "Machine 101143", new Dimension(3))));
		dimensionInstances.add(new DimensionInstance("101170", new Dimension(1, "Machine 101170", new Dimension(3))));
		dimensionInstances.add(new DimensionInstance("101185", new Dimension(1, "Machine 101185", new Dimension(3))));

		this.dataAcquisition.generateMeasures(0, 2.5, 3, parameterInstances, dimensionInstances);
	}

//	@Test
	public void readPartProgramsAndTools() {
		String directoryPath = "src/test/resources/input_porta";
		readFilesFromPath(directoryPath);
	}
	
	private void readFilesFromPath(String directoryPath) {
		try {
			File folder = new File(directoryPath);
			int first2 = 0;
			File [] files = folder.listFiles();
			Arrays.sort(files);
			for (File file : files) {
				System.out.println(file.getName());
				first2++;
				if (file.isFile() && file.getName().endsWith(".bin")) {
					
					try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
						stream.forEach(this::binRowToMeasures);// Stream in parallelo.

					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (file.isDirectory()) {
					System.out.println("\n*********************************************************************** "
							+ file.getName() + " ***********************************************************************");
					
					this.readFilesFromPath(directoryPath + "/" + file.getName());
				} else {
					// System.out.println("Ne file .bin ne directory: " + file.getName());
				}
				System.out.println(Arrays.toString(tools.toArray()));
				System.out.println(Arrays.toString(partPrograms.toArray()));
			}
//			try {
//				RequestHandler.callRemoteURL("http://bagozi.it/sendMail.php");
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			System.out.println("-------------------- DONE --------------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	List<String> tools = new UniqueList<>();
	List<String> partPrograms = new UniqueList<>();

	private void binRowToMeasures(String row) {
		String collectionName = "not setted";
		String[] rowElements = row.split("Unit "); // abbiamo 4 gurppi; primo timestamp e part program, gli
		// altri tre sono i dati dei singoli mandrini
		Instant instantDate = null;
		if (rowElements != null && rowElements.length > 0) {
			int i = 0;
			for (String rowElement : rowElements) {
				if (rowElement != "") {
					switch (i) {
					case 0: // reading timestamp and part program
						String[] timestamp_partProgram = rowElement.split(",");
						try {
							if (!timestamp_partProgram[1].isEmpty()) {
								partPrograms.add(timestamp_partProgram[1]);
							}
						} catch (Exception e) {}
						break;

					default:// reading components parameters
						String[] compStrings = rowElement.split(",");
						if (!compStrings[1].isEmpty()) {
							tools.add(compStrings[1]);
						}
						break;
					}
					i += 1;
				}
			}

		}
	}	
}
