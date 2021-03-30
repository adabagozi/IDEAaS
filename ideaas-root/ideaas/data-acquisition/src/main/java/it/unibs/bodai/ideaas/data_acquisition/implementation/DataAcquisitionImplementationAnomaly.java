package it.unibs.bodai.ideaas.data_acquisition.implementation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
query: db.getCollection('2016_11').ensureIndex({"dimensions.id":1})
query: db.getCollection('2016_11').ensureIndex({"dimensions.instance_id":1})
query: db.getCollection('2016_11').ensureIndex({"feature.id":1})
query: db.getCollection('2016_11').ensureIndex({"timestamp":1})
 */
public class DataAcquisitionImplementationAnomaly implements IDataAcquisition {
	private IDAO dao;

	private static final Logger LOG = Logger.getLogger(DAOImpl.class.getName());
	private DateUtils dateUtils = new DateUtils();
	private static final String DATE_FORMAT_BIN = "M/d/yyyy h:m:s a";
	Logger mongoLogger = Logger.getLogger("org.mongodb.driver");

	// TODO spostare connessione database in manageDB
	// private ManageDBCassandra dbCassandra = new ManageDBCassandra("127.0.0.1",
	// ManageDBCassandra.KEYSPACE_NAME, true);

	/**
	 * @param iDAO
	 */
	protected DataAcquisitionImplementationAnomaly(IDAO iDAO) {
		this.dao = iDAO;
		mongoLogger.setLevel(Level.SEVERE);
	}

	public DataAcquisitionImplementationAnomaly() throws IOException {
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
			File csvFile = new File("ideaas/data-acquisition/src/main/resources/feature_values.csv");
			
			String[] header = new String[9];
			// {"", "num_data", "num_syntheses", "num_features", "num_iterations", "mean_time (ms)", "mean_single_point_time (ms)", "datetime"};
//			header[0] = "timestamp";
//			int i = 1;
//			for (Feature f : features) {
//				header[i] = f.getName();
//				i++;
//			}
			try ( CSVHandler csvHandler = new CSVHandler(csvFile.getAbsolutePath(), header)) {

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
	private ArrayList<Double> featureAmplificationFactor = new ArrayList<>();
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
					try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {
						// TODO tolto il parallel perche crea problemi con insert in mongo DB
						// (stream.parallel().forEach...)
						stream
							.forEach(s -> this.binRowToMeasures(s,  csvHandler)); // Stream in parallelo.
						
						//stream.forEachOrdered(action);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
	
				    this.checkAndInsert(true, file.getName());
	
				} else if (file.isDirectory()) {
					System.out.println("\n*********************************************************************** "
							+ file.getName() + " ***********************************************************************");
					if (file.getName().toLowerCase().contains(this.machineFolderName.toLowerCase())) {
						this.machine = file.getName().toLowerCase().replace(this.machineFolderName.toLowerCase(), "");
					}
					this.readFilesFromPath(directoryPath + "/" + file.getName(), csvHandler);
				} else {
					// System.out.println("Ne file .bin ne directory: " + file.getName());
				}
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
		
	}

	/****************************************************************************************************************************************/
	Collection<Feature> features = new ArrayList<>();
	// TODO sarebbe meglio avere questi parametri in un file di configurazione
	// ovviamente
	private final String extentionFile = ".bin";
	private final String separator = ",";
	private final String machineFolderName = "macchina_";
	private final String componentSplit = "Unit ";
	private final int compPosition = 0;
	private final int toolPosition = 1;
	private final int modePosition = 2;
	private final int timestampPosition = 0;
	private final int partProgramPosition = 1;
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
		try {
			Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
			DimensionInstance partProgram = new DimensionInstance();
			DimensionInstance tool = new DimensionInstance();
			String[] rowElements = row.split(componentSplit); // abbiamo 4 gurppi; primo timestamp e part program, gli
																// altri tre sono i dati dei singoli mandrini
			Instant instantDate = null;

			if (rowElements != null && rowElements.length > 0) {
				int i = 0;

				for (String rowElement : rowElements) {
					if (rowElement != "") {

						switch (i) {
						case 0: // reading timestamp and part program
							String[] timestamp_partProgram = rowElement.split(separator);

							if (!timestamp_partProgram[timestampPosition].isEmpty()) {

								instantDate = dateUtils.getTimestamp(timestamp_partProgram[timestampPosition],
										DATE_FORMAT_BIN);

							}
							try {
								if (!timestamp_partProgram[partProgramPosition].isEmpty()) {
									partProgram = this.dao.readDimensionInstance("Part Program",
											timestamp_partProgram[partProgramPosition]);
									if (partProgram.getInstanceId() == null) {
										partProgram = this.dao.insertDimensionInstance(
												new DimensionInstance(timestamp_partProgram[partProgramPosition],
														"Part Program: " + timestamp_partProgram[partProgramPosition],
														true, null, partProgram.getDimension()));
									}
								}
							} catch (Exception e) {
								partProgram = new DimensionInstance("Not found", "", true, null,
										new Dimension(5, "Part Program"));
								System.out.println(e.getMessage());
							}
							break;

						default:// reading components parameters
							dimensionInstances = new ArrayList<>();
							// TODO inserire in dimensions indicazione della posizione
							DimensionInstance machineDimInstance = this.dao.readDimensionInstance("Macchina", machine);
							dimensionInstances.add(machineDimInstance); // 3 e l'id nel database della dimensione
																		// macchina
							dimensionInstances.add(partProgram);

							Measure measure = new Measure();
							measure.setTimestamp(Date.from(instantDate));

							String[] compStrings = rowElement.split(separator);
							if (!compStrings[modePosition].isEmpty()) {
								dimensionInstances.add(new DimensionInstance(compStrings[modePosition],
										"Mode: " + compStrings[modePosition], true, null, new Dimension(7, "Mode")));
							}
//							if (!compStrings[toolPosition].isEmpty()) {
//								dimensionInstances.add(new DimensionInstance(compStrings[toolPosition],
//										"Tool: " + compStrings[toolPosition], true, null,
//										new Dimension(6, "Utensili")));
//							}
							try {
								if (!compStrings[toolPosition].isEmpty()) {
									tool = this.dao.readDimensionInstance("Utensili", compStrings[toolPosition]);
									if (tool.getInstanceId() == null) {
										tool = this.dao.insertDimensionInstance(
												new DimensionInstance(compStrings[toolPosition],
														"Utensili: " + compStrings[toolPosition],
														true, null, tool.getDimension()));
									}
								}
							} catch (Exception e) {
								tool = new DimensionInstance("Not found", "", true, null,
										new Dimension(6, "Utensili"));
								System.out.println(e.getMessage());
							}
							if (!compStrings[compPosition].isEmpty()) {
								dimensionInstances.add(this.dao.readDimensionInstanceWithParentId("Componente", componentSplit + compStrings[compPosition], machineDimInstance.getId()));
							}
							measure.setDimensionInstances(dimensionInstances);

							if (first >= 2) {
								if (last30Min == null) {
									last30Min = LocalDateTime.ofInstant(instantDate, ZoneId.of("Z"));
								}
								LocalDateTime current = LocalDateTime.ofInstant(instantDate, ZoneId.of("Z"));
								LocalDateTime last30Min_2 = last30Min.plusMinutes(30);
								if (last30Min_2.isBefore(current)) {
//									if (first30Min > 2 && first30Min < 15) {
//										amplificationFactor = amplificationFactor + 0.1;
//									} else if (first30Min > 15 && first30Min < 30) {
//										amplificationFactor = amplificationFactor - 0.1;
//									} else if (first30Min > 40 && first30Min < 70) {
//										amplificationFactor = amplificationFactor + 0.1;
//									} else if (first30Min > 60) {
//										amplificationFactor = amplificationFactor - 0.1;
//									}
//									if (amplificationFactor < 0) {
//										amplificationFactor = 0;
//									}

                                    boolean alreadyIncremented = false;
									LocalDateTime d1 = null;
									LocalDateTime d2 = null;
									for (Feature f : features) {


										 if(first30Min > 6) { //&& first30Min < 75 ) {
											 Random rand = new Random();

											 // Introduzione anomalia

											 amplificationFactor = rand.nextInt(50) + 0;
										 }else {
											 amplificationFactor = 0;
										 }
										 featureAmplificationFactor.add(amplificationFactor);
										 if (!alreadyIncremented) {
											 first30Min++;
											 last30Min = last30Min_2;
											 d1 = current.plusMinutes(10);
											 d2 = current.plusMinutes(20);
											 System.out.println("****************");
											 alreadyIncremented = true;
										 }
										this.dao.insertDataAcquisitionAnomalies(new DataAcquistionAnomalies(
												this.dateUtils.getStringFromDateTime(last30Min), amplificationFactor, f.getId()+""));
										this.dao.insertDataAcquisitionAnomalies(new DataAcquistionAnomalies(
												this.dateUtils.getStringFromDateTime(d1), amplificationFactor, f.getId()+""));
										this.dao.insertDataAcquisitionAnomalies(new DataAcquistionAnomalies(
												this.dateUtils.getStringFromDateTime(d2), amplificationFactor, f.getId()+""));
									 }
								}
							}
						//	String[] ftoPrint = new String[8];
							int fIndex = 0;
							for (Feature f : features) {
								if (compStrings[f.getPositionInInputFile()] != null
										&& compStrings[f.getPositionInInputFile()] != "") {
									double value = Double.parseDouble(compStrings[f.getPositionInInputFile()]);
					//				ftoPrint[fTo]= value+"";
									if (StaticVariables.introduce_anomalies) {
										value = value + value * featureAmplificationFactor.get(fIndex);
									}
									FeatureData featureData = new FeatureData(f, value);
									measure.addFeatureDataHashMap(featureData);
								}else {
				//					ftoPrint[fTo]= "";
//									
								}
							fIndex++;
								
							}
				//			csv.printRow(measure.getTimestamp().toString(), ftoPrint[0], ftoPrint[1],ftoPrint[2], ftoPrint[3], ftoPrint[4], ftoPrint[5], ftoPrint[6], ftoPrint[7]);

							if (instantDate != null) {
								collectionName = this.getCollectionName(instantDate);
								List<Measure> measureObjectList = new ArrayList<>();
								if (this.measureObjectsToInsert.containsKey(collectionName)
										&& this.measureObjectsToInsert.get(collectionName) != null) {
									measureObjectList = this.measureObjectsToInsert.get(collectionName);
								}
								numMeasures += 1;
								measureObjectList.add(measure);
								//this.saveInCSV("features_value", measure);
							   measureObjectsToInsert.put(collectionName, measureObjectList);
							}
							break;
						}
					}
					i += 1;
				}
				this.checkAndInsert(false, collectionName);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
//			this.dao.insertDataAcquisitionTiming(
//					new TimingDataAcquisition(numMeasures, transformationTime, System.currentTimeMillis() - startTime,
//							collectionName + " - " + dateFileName, amplificationFactor));

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
		s = s+"_anomaly"; //+ StaticVariables.collectionNameExt;
		return s;
	}

}