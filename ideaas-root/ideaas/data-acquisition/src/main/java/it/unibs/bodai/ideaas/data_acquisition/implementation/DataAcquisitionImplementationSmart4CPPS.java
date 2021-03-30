package it.unibs.bodai.ideaas.data_acquisition.implementation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.implementation.DAOImpl;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.ParameterInstance;
import it.unibs.bodai.ideaas.dao.timing.DataAcquistionAnomalies;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;
import it.unibs.bodai.ideaas.data_acquisition.IDataAcquisition;
import utils.CSVHandler;
import utils.DateUtils;
import utils.RequestHandler;
import utils.StaticVariables;

/**
 * @author Ada Bagozi
 *
//Index: dimensions.id, dimensions.instance_id, feature.id, timestamp
query: db.getCollection('2016_01').ensureIndex({"dimensions.id":1})
query: db.getCollection('2016_01').ensureIndex({"dimensions.instance_id":1})
query: db.getCollection('2016_01').ensureIndex({"feature.id":1})
query: db.getCollection('2016_01').ensureIndex({"timestamp":1})
 */
public class DataAcquisitionImplementationSmart4CPPS implements IDataAcquisition {
	private IDAO dao;

	private static final Logger LOG = Logger.getLogger(DAOImpl.class.getName());
	private DateUtils dateUtils = new DateUtils();
	private static final String DATE_FORMAT_BIN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	//private static final String DATE_FORMAT_BIN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
//	private static final String DATE_FORMAT_BIN = "M/d/yyyy h:m:s a";
	Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
	Logger mongoLogger2 = Logger.getLogger( "com.mongodb" );
//	Logger mySQLLogger = Logger.getLogger("com.mysql.jdbc.log.StandardLogger");

	// TODO spostare connessione database in manageDB
	// private ManageDBCassandra dbCassandra = new ManageDBCassandra("127.0.0.1",
	// ManageDBCassandra.KEYSPACE_NAME, true);

	/**
	 * @param iDAO
	 */
	protected DataAcquisitionImplementationSmart4CPPS(IDAO iDAO) {
		this.dao = iDAO;
		mongoLogger.setLevel(Level.SEVERE);
		mongoLogger2.setLevel(Level.SEVERE);
	//	mySQLLogger.setLevel(Level.SEVERE);
	}

	public DataAcquisitionImplementationSmart4CPPS() throws IOException {
		// this.dao = iDAO;
		this(DAOFactory.getDAOFactory().getDAO());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibs.bodai.ideaas.data_acquisition.IDataAcquisition#saveMeasures(java.io.
	 * InputStream, java.lang.String)
	 */
	public void saveMeasures(InputStream inputFile, String macchina) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibs.bodai.ideaas.data_acquisition.IDataAcquisition#generateMeasures(
	 * double, double)
	 */
	public void generateMeasures(double mean, double std, int numMeasures,
		Collection<ParameterInstance> parameterInstances, Collection<DimensionInstance> dimensionInstances) {
		List<Measure> measuresList = new ArrayList<>();

		HashMap<String, List<Measure>> measuresToInsert = new HashMap<>();

		for (int i = 0; i < numMeasures; i++) {
			Measure measure = new Measure();
			measure.setTimestamp(new Date());
			measure.setParameterInstances(parameterInstances);
			measure.setDimensionInstances(dimensionInstances);

			Collection<Feature> features = this.dao.readFeatures();
			for (Feature f : features) {
				Random r = new Random();
				double randomValue = mean + std * r.nextGaussian();
				FeatureData featureData = new FeatureData(f, randomValue);
				measure.addFeatureDataHashMap(featureData);
			}
			measuresList.add(measure);
		}
		measuresToInsert.put("generated_measures", measuresList);
		this.dao.insertMeasuresList(measuresToInsert);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibs.bodai.ideaas.data_acquisition.IDataAcquisition#
	 * generateMeasuresFromFilesInFolder(java.lang.String)
	 */
	@Override
	public void generateMeasuresFromFilesInFolder(String directoryPath) throws Exception {
		this.transformationTime = System.currentTimeMillis();
		if (this.dao != null) {
			features = this.dao.readFeatures();
			dimensions = this.dao.readDimensions();
			
			File csvFile = new File("src/main/resources/feature_values.csv");
			
			/*String[] header = new String[9];// {"", "num_data", "num_syntheses", "num_features", "num_iterations", "mean_time (ms)", "mean_single_point_time (ms)", "datetime"};
			header[0] = "timestamp";
			int i = 1;
			for (Feature f : features) {
				header[i] = f.getName();
				i++;
			}*/
			try ( CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath())) {

				this.readFilesFromPath(directoryPath, csvHandler);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			System.out.println("DAO null");
		}
	}

	private double amplificationFactor = 0;
	private String dateFileName = "";
	LocalDateTime last30Min = null;
	int first = 0;
	int first30Min = 1;
//	CSVHandler csvHandler = 
	public void readFilesFromPath(String directoryPath, final CSVHandler csvHandler) {
		  
		
			File folder = new File(directoryPath);
	
			File[] files = folder.listFiles();
			Arrays.sort(files);
	
			for (File file : files) {
	
				if (file.isFile() && file.getName().endsWith(this.extentionFile)) {
					dateFileName = file.getName();
	
					first++;
					System.out.println("\n\n" + file.getName() + "\n\n");
					// read file into stream, try-with-resources
					try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
						// TODO tolto il parallel perche crea problemi con insert in mongo DB
						// (stream.parallel().forEach...)
						stream
							.forEach(s -> this.binRowToMeasures(s,  csvHandler)); // Stream in parallelo.
						
						//stream.forEachOrdered(action);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
	
					// this.checkAndInsert(true, file.getName());
	
				} else if (file.isDirectory()) {
					System.out.println("\n*********************************************************************** "
							+ file.getName() + " ***********************************************************************");
					if (file.getName().toLowerCase().contains(this.machineFolderName.toLowerCase())) {
						this.machine = file.getName().toLowerCase().replace(this.machineFolderName.toLowerCase(), "");
					}
					System.out.println(this.machine);
					this.readFilesFromPath(directoryPath + "/" + file.getName(), csvHandler);
				} else {
					// System.out.println("Ne file .bin ne directory: " + file.getName());
				}
			}
			try {
				RequestHandler.callRemoteURL("http://bagozi.it/sendMail.php");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("-------------------- DONE --------------------");
		
	}

	/****************************************************************************************************************************************/
	Collection<Feature> features = new ArrayList<>();	
	ArrayList<Dimension> dimensions = new ArrayList<>();
	// TODO sarebbe meglio avere questi parametri in un file di configurazione
	// ovviamente
	private final String extentionFile = ".csv";
	private final String separator = ",";
	private final String machineFolderName = "machine_";
	//private final String componentSplit = "Unit ";
	//private final int compPosition = 0;
	//private final int toolPosition = 1;
//	private final int modePosition = 2;
	private final int timestampPosition = 14;
//	private final int partProgramPosition = 1;
	private int numMaxMeasures = 900;

	private String machine;

	private HashMap<String, List<Measure>> measureObjectsToInsert = new HashMap<>();
	private long transformationTime = 0;
	private int numMeasures = 0;

	/****************************************************************************************************************************************/
	// Domanda per Max su HashMap

	// TODO trasformare la generazione delle istanze leggendo e scrivendo nel
	// database mysql; Leggere le posizioni degli elementi dal database
	public void binRowToMeasures(String row, CSVHandler csv) {
		String collectionName = "not setted";
			Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
			//String[] rowElements = row.split(componentSplit); // abbiamo 4 gurppi; primo timestamp e part program, gli
			String[] compStrings = row.split(separator);
											// altri tre sono i dati dei singoli mandrini
			if(compStrings != null && compStrings.length>0) {
				Instant instantDate = null;
				Measure measure = new Measure();
				try {
					String timestamp = compStrings[timestampPosition];
					if (!timestamp.isEmpty() && !timestamp.equalsIgnoreCase("TimeStamp")) {
						instantDate = dateUtils.getTimestamp(timestamp, DATE_FORMAT_BIN);
						measure.setTimestamp(Date.from(instantDate));
						System.out.print("=");
						
						dimensionInstances = new ArrayList<>();
						for(Dimension d: dimensions) {
							String identifier = compStrings[d.getInputPosition()];
							if(d.getInputPosition() == -1) {
								identifier = machine;
							}
							if (!identifier.isEmpty()) {
								DimensionInstance dimInst = this.dao.readDimensionInstance(d, identifier);
								if (dimInst.getInstanceId() == null) {
									//System.out.println(d.getName() + ": "+identifier);
									DimensionInstance dimInstanceToInsert = new DimensionInstance(identifier, d.getName() + " - " + identifier, true, null, d);
									dimInst = this.dao.insertDimensionInstance(dimInstanceToInsert);
								}
								dimensionInstances.add(dimInst);
							}
						}
						measure.setDimensionInstances(dimensionInstances);
						for (Feature f : features) {
							if (compStrings[f.getPositionInInputFile()] != null
									&& compStrings[f.getPositionInInputFile()] != "") {
								double value = Double.parseDouble(compStrings[f.getPositionInInputFile()]);
								FeatureData featureData = new FeatureData(f, value);
								measure.addFeatureDataHashMap(featureData);
							}
							
						}
						if (instantDate != null) {
							collectionName = this.getCollectionName(instantDate);
							List<Measure> measureObjectList = new ArrayList<>();
							if (this.measureObjectsToInsert.containsKey(collectionName)
									&& this.measureObjectsToInsert.get(collectionName) != null) {
								measureObjectList = this.measureObjectsToInsert.get(collectionName);
							}
							numMeasures += 1;
							measureObjectList.add(measure);
						//	this.saveInCSV("features_value", measure);
						   measureObjectsToInsert.put(collectionName, measureObjectList);
						}
					}
					this.checkAndInsert(false, collectionName);
				} catch (DateTimeParseException e) {
					System.out.println("could not parse Date");
					e.printStackTrace();
				}
				catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
	}

	public void saveInCSV(String name, Measure measure) {

	}

	public void checkAndInsert(boolean insertNoCheck, String collectionName) {
		if (numMaxMeasures < numMeasures || (numMeasures > 0 && insertNoCheck)) {
			this.transformationTime = System.currentTimeMillis() - this.transformationTime;
			long startTime = System.currentTimeMillis();

			boolean inserted = this.dao.insertMeasuresList(measureObjectsToInsert);

			if (!inserted) {
				System.out.println("Errore inserimento insieme di file");
			} else {
				System.out.print("=");
			}
//			this.dao.insertDataAcquisitionTiming(new TimingDataAcquisition(numMeasures, transformationTime, System.currentTimeMillis() - startTime, collectionName + " - " + dateFileName, amplificationFactor));

			this.measureObjectsToInsert.clear();
			transformationTime = System.currentTimeMillis();
			numMeasures = 0;
		}
	}

	/**
	 * Metodo per ottenere il nome della collection dall'istante di tempo i
	 * 
	 * @param i
	 *            istante di tempo
	 * @return una stringa che rappresenta il nome della collection formata da
	 *         "anno_mese", ad esempio "2016_01"
	 */
	public String getCollectionName(Instant i) {
		LocalDateTime ldt = LocalDateTime.ofInstant(i, ZoneId.of("Z"));
		String s = DateTimeFormatter.ofPattern("yyyy_MM").format(ldt);
		s = s; // + StaticVariables.collectionNameExt;
		return s;
	}

}