package it.unibs.bodai.ideaas.data_acquisition.implementation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import org.bson.Document;

public class DataAcquisitionProducer implements Runnable {
	private final String extentionFile = ".bin";
	private final String separator = ",";
	private final String machineFolderName = "macchina_";
	private final String componentSplit = "2";
	private final int toolPosition = 1;
	private final int compPosition = 2;
	
	private final BlockingQueue<DataAcquisitionPair> sharedQueue;
	private String pathDirectory;


	private String machine;
	List<Document> measureDocumentList = new ArrayList<>();
	HashMap<String, List<Document>> measureDocumentsToInsert = new HashMap<>();
	int numMaxMeasures = 3000;
	
	
 	public DataAcquisitionProducer(BlockingQueue<DataAcquisitionPair> sharedQueue, String pathDirectory) {
		this.sharedQueue = sharedQueue;
		this.pathDirectory = pathDirectory;
	}

	@Override
	public void run() {	
		System.out.println("File: ");
		File f = new File(this.pathDirectory+"/macchina_101143/2015_11/20151102.bin");

		System.out.println(f.getAbsolutePath());
		
		try (Stream<String> stream = Files.lines(Paths.get(f.getAbsolutePath()))) {
			stream.parallel().forEach(System.out::println);
//			stream.parallel().forEach(this::binRowToMeasures);//Stream in parallelo. 

		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		this.readFilesFromPath(this.pathDirectory);
//		for(int i=0; i<10; i++){
//			try {
//				long startTime = System.currentTimeMillis();
//				int numFiles = 0;
//				System.out.println("Produced: " + i);
//				Measure measure = new Measure();
//				measureDocumentsToInsert.put("testACaso", measureDocumentList);
//				sharedQueue.put(new DataAcquisitionPair(measureDocumentsToInsert, System.currentTimeMillis()-startTime, numFiles));
//			} catch (InterruptedException ex) {
//				Logger.getLogger(DataAcquisitionProducer.class.getName()).log(Level.SEVERE, null, ex);
//			}
//		}
	}
	
	
	public void readFilesFromPath(String directoryPath) {
		File folder = new File(directoryPath);
		for(File file: folder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(this.extentionFile)) {
				System.out.println("File Name: " + file.getName());
				//read file into stream, try-with-resources
//				try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
//					stream.parallel().forEach(this::binRowToMeasures);//Stream in parallelo. 
//					
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				//try {
					//FileUtils.readFileToString(file, StandardCharsets.UTF_8);
//					String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
				//} catch (IOException e) {
				//	System.out.println("IOException "+e.getMessage());
				//	e.printStackTrace();
				//}
				/* do somthing with content */
			} else if (file.isDirectory()) {
				System.out.println("\n*********************************************************************** " + file.getName() + " ***********************************************************************");
				if(file.getName().toLowerCase().contains(this.machineFolderName.toLowerCase())) {
					this.machine = file.getName().toLowerCase().replace(this.machineFolderName.toLowerCase(), "");
				}
				System.out.println("\nmachine:  " + this.machine);
				
				this.readFilesFromPath(directoryPath + "/" + file.getName());
			} else {
				System.out.println("Ne file .bin ne directory: " + file.getName());
			}
		}
	}

	
	public void binRowToMeasures(String row){
		
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
