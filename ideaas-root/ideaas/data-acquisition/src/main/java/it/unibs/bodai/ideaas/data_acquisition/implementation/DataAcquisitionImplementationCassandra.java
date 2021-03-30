package it.unibs.bodai.ideaas.data_acquisition.implementation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.Dimension;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.Measure;
import it.unibs.bodai.ideaas.dao.model.ParameterInstance;
import it.unibs.bodai.ideaas.dao.timing.TimingDataAcquisition;
import it.unibs.bodai.ideaas.data_acquisition.IDataAcquisition;
import utils.DateUtils;

/**
 * @author Ada Bagozi
 *
 */
public class DataAcquisitionImplementationCassandra implements IDataAcquisition {
	private IDAO dao;
	private IDAO daoCassandra;
	
	private DateUtils dateUtils = new DateUtils();
	private static final String DATE_FORMAT_BIN = "M/d/yyyy h:m:s a";
	
	//TODO spostare connessione database in manageDB
//	private ManageDBCassandra dbCassandra = new ManageDBCassandra("127.0.0.1", ManageDBCassandra.KEYSPACE_NAME, true);

	/**
	 * @param iDAO
	 */
	protected DataAcquisitionImplementationCassandra(IDAO iDAO, IDAO iDAOCassandra) {
		this.dao = iDAO;
		this.daoCassandra = iDAOCassandra;
	}

	public DataAcquisitionImplementationCassandra() throws IOException {
		// this.dao = iDAO;
		this(DAOFactory.getDAOFactory().getDAO(), DAOFactory.getDAOFactory().getDAOCassandra());
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

		// TODO Save Time requested
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
		features = this.dao.readFeatures();
		this.readFilesFromPath(directoryPath);
		
		//Creating shared object
		//	     BlockingQueue<DataAcquisitionPair> sharedQueue = new LinkedBlockingQueue<DataAcquisitionPair>();
		//	 
		//	     //Creating Producer and Consumer Thread
		//	     Thread prodThread = new Thread(new DataAcquisitionProducer(sharedQueue, directoryPath));
		//	     Thread consThread = new Thread(new DataAcquisitionConsumer(sharedQueue));
		//
		//	     //Starting producer and Consumer thread
		//	     prodThread.start();
		//	     consThread.start();


	}

	public void readFilesFromPath(String directoryPath) {

		File folder = new File(directoryPath);
		for(File file: folder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(this.extentionFile)) {
				System.out.println("\n\n" + file.getName());
				//read file into stream, try-with-resources
				try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
					//TODO tolto il parallel perche crea problemi con insert in mongo DB (stream.parallel().forEach...)
					stream.forEach(this::binRowToMeasures);//Stream in parallelo. 
				} catch (IOException e) {
					e.printStackTrace();
				}
				

				this.checkAndInsert(true,file.getName());

			} else if (file.isDirectory()) {
				System.out.println("\n*********************************************************************** " + file.getName() + " ***********************************************************************");
				if(file.getName().toLowerCase().contains(this.machineFolderName.toLowerCase())) {
					this.machine = file.getName().toLowerCase().replace(this.machineFolderName.toLowerCase(), "");
				}
				this.readFilesFromPath(directoryPath + "/" + file.getName());
			} else {
//				System.out.println("Ne file .bin ne directory: " + file.getName());
			}
		}
	}

	/****************************************************************************************************************************************/
	Collection<Feature> features = new ArrayList<>();
	//TODO sarebbe meglio avere questi parametri in un file di configurazione ovviamente
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
	//Domanda per Max su HashMap

	//TODO trasformare la generazione delle istanze leggendo e scrivendo nel database mysql; Leggere le posizioni degli elementi dal database
	public void binRowToMeasures(String row){
		String collectionName = "not setted";
		try {	
		//	Collection<ParameterInstance> parameterInstances = new ArrayList<>();
		
			//	TODO: creata in questo scope causa il problema di funzionamento
		//	Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
			DimensionInstance partProgram = new DimensionInstance();
			String[] rowElements = row.split(componentSplit); //abbiamo 4 gurppi; primo timestamp e part program, gli altri tre sono i dati dei singoli mandrini
			Instant instantDate = null;

			if(rowElements != null && rowElements.length>0) {
				int i=0;
				for (String rowElement: rowElements) {
					if(rowElement != "") {
						switch (i) {
						case 0: //reading timestamp and part program
							String[] timestamp_partProgram = rowElement.split(separator);
							
							if (!timestamp_partProgram[timestampPosition].isEmpty()) {
								instantDate = dateUtils.getTimestamp(timestamp_partProgram[timestampPosition], DATE_FORMAT_BIN);
							}
							try {
								if (!timestamp_partProgram[partProgramPosition].isEmpty()) {
									partProgram = new DimensionInstance(timestamp_partProgram[partProgramPosition], "Part Program: "+timestamp_partProgram[partProgramPosition], true, null, new Dimension(5, "Part Program"));
								}
							} catch (Exception e) {
								partProgram = new DimensionInstance("Not found", "", true, null, new Dimension(5, "Part Program"));
							}
							break;

						default://reading components parameters
							Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
							//TODO inserire in dimensions indicazione della posizione
							DimensionInstance machineDimInstance = this.dao.readDimensionInstance("Macchina", machine);
							
//							System.out.println("Macchina: " + machineDimInstance.toJSON());
//									new DimensionInstance(machine, "Macchina: "+machine, true, null,  new Dimension(3, "Macchina"));
							dimensionInstances.clear();
							dimensionInstances.add(machineDimInstance); // 3 e l'id nel database della dimensione macchina 
							dimensionInstances.add(partProgram);
							
							Measure measure = new Measure();
							measure.setTimestamp(Date.from(instantDate));

							String[] compStrings = rowElement.split(separator);
							if (!compStrings[modePosition].isEmpty()) {
								dimensionInstances.add(new DimensionInstance(compStrings[modePosition], "Mode: "+compStrings[modePosition], true, null, new Dimension(7, "Mode")));
							}
							if (!compStrings[toolPosition].isEmpty()) {
								dimensionInstances.add(new DimensionInstance(compStrings[toolPosition], "Tool: "+compStrings[toolPosition], true, null, new Dimension(6, "Tools")));
							}
							if (!compStrings[compPosition].isEmpty()) {
								dimensionInstances.add( this.dao.readDimensionInstance("Componente", componentSplit+compStrings[compPosition]));
							}
							measure.setDimensionInstances(dimensionInstances);

							for (Feature f : features) {
								if (compStrings[f.getPositionInInputFile()] != null && compStrings[f.getPositionInInputFile()] != "") {
									FeatureData featureData = new FeatureData(f, Double.parseDouble(compStrings[f.getPositionInInputFile()]));
									measure.addFeatureDataHashMap(featureData);
								}
							}
							if(instantDate != null) {
								collectionName = this.getCollectionName(instantDate);
								List<Measure> measureObjectList = new ArrayList<>(); 
								if(this.measureObjectsToInsert.containsKey(collectionName) && this.measureObjectsToInsert.get(collectionName)!=null)
								{
									measureObjectList = this.measureObjectsToInsert.get(collectionName);
								}
								numMeasures += 1;
								measureObjectList.add(measure); 
								measureObjectsToInsert.put(collectionName, measureObjectList); 
							}
							break;
						}
					}
					i += 1;
				}
				this.checkAndInsert(false, collectionName);
			}
			
			
		}catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void checkAndInsert(boolean insertNoCheck, String collectionName) {
		//TODO spostare connessione cassandra all'interno del dao
		if (numMaxMeasures < numMeasures || (numMeasures>0 && insertNoCheck)) {
			this.transformationTime = System.currentTimeMillis() - this.transformationTime;
			long startTime = System.currentTimeMillis();
			boolean inserted = this.dao.insertMeasuresList(measureObjectsToInsert);
	        if (!inserted) { 
	        		System.out.println("Errore inserimento insieme di file");
	        	}else {
	        		System.out.print("=");
	        }
	        this.dao.insertDataAcquisitionTiming(new TimingDataAcquisition(numMeasures, transformationTime, System.currentTimeMillis() - startTime, collectionName + " MongoDB", 0));
	       
	        startTime = System.currentTimeMillis();
			
	        this.daoCassandra.insertMeasuresList(measureObjectsToInsert);
	        this.dao.insertDataAcquisitionTiming(new TimingDataAcquisition(numMeasures, transformationTime, System.currentTimeMillis() - startTime, collectionName + " Cassandra", 0));
		       
	        this.measureObjectsToInsert.clear(); //Per Cassandra
	        transformationTime = System.currentTimeMillis();
	        numMeasures = 0;
		} 
	}

	/** 
	 * Metodo per ottenere il nome della collection dall'istante di tempo i
	 * @param i istante di tempo 
	 * @return una stringa che rappresenta il nome della collection formata da "anno_mese", ad esempio "2016_01"
	 */
	public String getCollectionName(Instant i){
		LocalDateTime ldt = LocalDateTime.ofInstant(i, ZoneId.of("Z"));
		String s = DateTimeFormatter.ofPattern("yyyy_MM").format(ldt);
		return s;
	}

}