package it.unibs.bodai.ideaas.standalone.data_summarisation_standalone;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.anomalies.DataSummarisationAnomalies;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import it.unibs.bodai.ideaas.dao.utils.BestMetch;
import it.unibs.bodai.ideaas.data_summarisation.DataSummarisationFactory;
import it.unibs.bodai.ideaas.data_summarisation.IDataSummarisation;
import utils.DateUtils;
import utils.RequestHandler;
import utils.StaticVariables;

public class DataSummarisationStandaloneMain {
    private static String summarisedDataCollectionName = "summarised_data"+StaticVariables.collectionNameExt;
    private final static Logger mongoLogger = Logger.getLogger("org.mongodb.driver");


    /**
     * RUN: nohup java -jar data-summarisation-standalone-0.0.1-SNAPSHOT-jar-with-dependencies.jar &
     * see status: tail -f nohup.out
     * @param args
     */
    public static void main(String[] args) {
        try {
            DataSummarisationStandaloneMain.mongoLogger.setLevel(Level.SEVERE);

            IDAO dao = DAOFactory.getDAOFactory().getDAO();
            IDataSummarisation dataSummarisation = DataSummarisationFactory.getDataSummarisationFactory().getDataSummarisation();
            DateUtils dateUtils = new DateUtils();
            //DataSummarisationStandaloneMain.generetaCSVSummarisedData(dao, dateUtils);
            DataSummarisationStandaloneMain.generateSummarisedData(dao, dataSummarisation, dateUtils);
         //     DataSummarisationStandaloneMain.generateSummarisedDataByStableId(dao, dataSummarisation, dateUtils);
                //DataSummarisationStandaloneMain.generateSummarisedDataStable(dao, dataSummarisation, dateUtils);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //		DataSummarisationStandaloneMain.testComincini();
    }
    private static void generateSummarisedDataByStableId(IDAO dao, IDataSummarisation dataSummarisation, DateUtils dateUtils) {
        String[] machines = {"SnapuAuto"};
        String[] components = {"-1"};
        int[] featureSpaces = {2, 3};
        //int featureSpaceID = 1; //corrente e velocità asse Z
        //String[] components = {"Unit 3.0"};
        int experimentNumber = 1;
        String experimentDescription = "";


        String intervalType = StaticVariables.MINUTES;


        long extraction_time = 0;
        long clustering_time = 0;
        long inserting_time = 0;
        long relevance_evaluation_time = 0;
        long total_time = 0;

        //String toolID = "-1";
        //String partProgramID = "-1";
        String modeID = "-1";


        String[] tools = {"-1"};
         String[] partPrograms = {"0171507851", "0171507182"}; //dal 19/04 e dal 29/04 al 30/04: particolare attenzione al 29/04, tra primo e secondo turno sono segnati 135 minuti di guasto/manutenzione
        //String[] partPrograms = {"0171507817"}; //dal 24/05 alle 14:00 al 27/05: particolare attenzione al 24/05 secondo turno segnati 60 minuti di guasto
       //String[] partPrograms = {"0171507182"};//dal 27/05 al 04/06: particolare attenzione al 27/05 secondo turno segnati 85 minuti di guasto, 28/05 primo turno segnati 90 minuti di manutenzione, 03/06 tra primo e secondo turno segnati minuti di guasto (questo ordine in realtà dal punto di vista economico non individua una grande variabilità ma è dovuto al fatto che è un ordine molto grande in termini di pezzi realizzati)

        //String[] partPrograms = {"0171507268"};//0171507268 in data 20/05 secondo turno non ci sono stati fermi dichiarati)
        String [] stableIds= {"601cd4e9cea48565072b514b", "601cd61340474967bae3f43d", "601cd42862ae65653572b844"}; //0171507851 Data 19/04, esclusi 0, 1 GG //NR dati ~32000
       // String [] stableIds= {"601cb6d693465802120ed9be", ""};  //0171507817 Data 24/05, esclusi 0, 2 H //NR dati ~36000
       // String [] stableIds= {"601cba1672a5ee3cac7cf7fb", ""}; //0171507182 Data 27/05, esclusi 0, 1 GG //NR dati ~85000
       // String stableId = ""; //0171507268 Data 120/05, esclusi 0, 1 GG //NR dati ~276249
        for (int featureSpaceID : featureSpaces) {
            Optional<FeatureSpace> fs = dao.readFeatureSpace(featureSpaceID);

		if (fs.isPresent()) {
			//
			//String [] anomalyTypes = {"", "_anomaly"};
			String [] anomalyTypes = {""};

			StaticVariables.collectionNameExt = "";
			for (int h=0 ;h<stableIds.length; h++) {
				String stableId = stableIds[h];
			//				System.out.println("Anomaly: "+StaticVariables.collectionNameExt);
				DataSummarisationStandaloneMain.summarisedDataCollectionName = "summarised_data"+StaticVariables.collectionNameExt;
				int [] time_windows = {500};
				for (int k=0 ;k<time_windows.length; k++) {
	
					StaticVariables.TIME_WINDOW = time_windows[k];
					System.out.println("TimeWindows: "+StaticVariables.TIME_WINDOW);
	
					int [] algorithms = {4};
					for (int j =0 ;j<algorithms.length; j++) {
						if(k>0 && algorithms[j]==3) {
							break;
						}
						StaticVariables.SUM_DATA_VERSION = algorithms[j];
						System.out.println("Algorithm: "+StaticVariables.SUM_DATA_VERSION);
						int [] intervals = {5}; //La durata in tempo
						for(int i = 0; i< intervals.length; i++) {
							int intervalValue = intervals[i];
							experimentNumber = i+1;
							String expNumberInterval = "Esperimento: "+experimentNumber+"; Interval: "+intervalValue+" "+StaticVariables.MINUTES+"; ";
							for(String machineID: machines) {
								DimensionInstance machineInstance = dao.readDimensionInstance("Macchina", machineID);
								for(String componentID: components) {

                                        DimensionInstance componentInstance = dao.readDimensionInstance("Componente", componentID);
                                        for (String partProgramID : partPrograms) {
                                            if((partProgramID.equalsIgnoreCase("0171507851") && !stableId.equalsIgnoreCase("601cd61340474967bae3f43d"))
                                            	||(partProgramID.equalsIgnoreCase("0171507182") && !stableId.equalsIgnoreCase("601cd4e9cea48565072b514b")))
                                            	{
	                                        	DimensionInstance partProgramInstance = dao.readDimensionInstance("Part Program", partProgramID);
	                                            for (String toolID : tools) {
	                                                DimensionInstance toolInstance = dao.readDimensionInstance("Utensili", toolID);
	
	                                                String dataset = "machineID: " + machineID + "; componentID: " + componentID + "; toolID: " + toolID + "; partProgramID: " + partProgramID;
	                                              
	                                                System.out.println("\n" + dataset + "\n");
	
	                                                total_time = System.currentTimeMillis();
	
	                                                Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
	                                                dimensionInstances.add(machineInstance);
	                                                dimensionInstances.add(componentInstance);
	                                                dimensionInstances.add(toolInstance);
	                                                dimensionInstances.add(partProgramInstance);
	                                                //dimensionInstances.add(toolInstance);
	
	
	                                                String start_date = "29/04/2019 00:00:00"; 
	                                                String end_date = "30/04/2019 00:00:00";
	                                                if(partProgramID.equalsIgnoreCase("0171507182")) {
	                                                	start_date = "28/05/2019 00:00:00"; 
	                                                	end_date = "04/06/2019 00:00:00";
	                                                }
	                                                //						experimentDescription = experimentDescription + " \n Per dati che vanno dal: "+start_date +" al: " +end_date;
	
	                                                LocalDateTime end_datetime = dateUtils.getDateTimeFromString(end_date);
	                                                LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);
	
	
	                                                LocalDateTime min_datetime = dateUtils.getDateTimeFromString("10/04/2019 00:00:00");
	                                                LocalDateTime max_datetime = dateUtils.getDateTimeFromString("29/04/2019 00:00:00");

	                                                SummarisedData sumDataStable = null;
	                                                SummarisedData sumData = null;
	                                                Optional<SummarisedData> stableFromDoc = dao.readSummarisedDataById(DataSummarisationStandaloneMain.summarisedDataCollectionName, stableId);
                                                    if(stableFromDoc.isPresent()) {
                                                    	sumDataStable = stableFromDoc.get();
                                                    	sumData=sumDataStable;
                                                        sumData.setStableSummarisedData(sumDataStable);
                                                    }
	                                                int totalData = 0;
	                                                while (start_datetime.isBefore(end_datetime)) {
	                                                    String experimentsDesc = "";
	                                                    Instant instant = start_datetime.atZone(ZoneId.of("Z")).toInstant();
	                                                    Date date = Date.from(instant);
	
	                                                    LocalDateTime dateTimeTwo = dateUtils.getOtherDateTime(start_datetime, intervalType, intervalValue, true);
	                                                    Instant instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
	                                                    Date dateTwo = Date.from(instantTwo);
	
	                                                    try {
	                                                    //	if(start_datetime.isBefore(min_datetime) || start_datetime.isAfter(min_datetime)){
		                                                        total_time = System.currentTimeMillis();
		                                                        extraction_time = System.currentTimeMillis();
		                                                        Collection<MultidimensionalRecord> newData = new ArrayList<>();
		                                                        
		                                                        if (sumDataStable == null) {
		                                                            dateTimeTwo = dateUtils.getOtherDateTime(start_datetime, StaticVariables.DAYS, 1, true);
		                                                            instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
		                                                            dateTwo = Date.from(instantTwo);
		                                                            newData = DataSummarisationStandaloneMain.getNewData(dao, dateUtils, fs.get(), machineID, componentID, toolID, modeID, partProgramID, start_datetime, dateTimeTwo, true);
	
	                                                                dateTimeTwo = dateUtils.getDateTimeFromString(start_date);
	                                                                instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
		                                                            
		                                                        } else {
		                                                            newData = DataSummarisationStandaloneMain.getNewData(dao, dateUtils, fs.get(), machineID, componentID, toolID, modeID, partProgramID, start_datetime, dateTimeTwo, true);
		                                                        }
		                                                        extraction_time = System.currentTimeMillis() - extraction_time;
		                                                        //																System.out.println("NumData: "+newData.size());
		                                                        int threshold = 0;
		                                                        System.out.println("Extracted Data: " + newData.size() + "    - date: " + start_date + "    - end_date: " + dateTimeTwo);
		                                                        if (newData.size() > threshold) {
		                                                            totalData += newData.size();
		                                                            if (sumDataStable == null) {
		                                                                clustering_time = System.currentTimeMillis();
		
		                                                                sumDataStable = dataSummarisation.runClusteringExcludeZeros(true, sumData, newData, fs.get(), date, dateTwo);
		                                                                sumDataStable.setStable(true);
		                                                                sumDataStable.setIntervalType(StaticVariables.DAYS);
		                                                                sumDataStable.setIntervalValue(1);
		                                                                sumDataStable.setDimensionIstances(dimensionInstances);
		                                                                sumDataStable.setCollectionName(DataSummarisationStandaloneMain.summarisedDataCollectionName);
		
		                                                                sumDataStable.setExperimentNumber(experimentNumber);
		                                                                experimentsDesc = dateUtils.getStringFromDateTime(start_datetime) + " - " + dateUtils.getStringFromDateTime(dateTimeTwo) + "; " + experimentDescription;
		                                                                sumDataStable.setExperimentDescription(experimentsDesc);
		                                                                sumDataStable.setNumberOfNewData(newData.size());
		                                                                sumDataStable.setNumberOfData(totalData);
		
		                                                                clustering_time = System.currentTimeMillis() - clustering_time;
		
		                                                                inserting_time = System.currentTimeMillis();
		                                                                Document docSummarisedDataResult = sumDataStable.toDocument();
		                                                                ObjectId objectID = dao.insertDocumentAndGetID(docSummarisedDataResult, DataSummarisationStandaloneMain.summarisedDataCollectionName);
		                                                                sumDataStable.setId(objectID.toString());
		                                                                inserting_time = System.currentTimeMillis() - inserting_time;
		                                                                sumData = sumDataStable;
		                                                                sumData.setStableSummarisedData(sumDataStable);
		                                                                experimentsDesc = "distPrev: 0; " + experimentsDesc;
		                                                                experimentsDesc = "distStab: 0; " + experimentsDesc;
		                                                                
		                                                            } else {
		                                                                clustering_time = System.currentTimeMillis();
		
		                                                                sumData = dataSummarisation.runClustering(sumData, newData, fs.get(), date, dateTwo);
		                                                                sumData.setStable(false);
		                                                                //										sumData.setStableSummarisedData(sumDataStable);
		                                                                sumData.setIntervalType(StaticVariables.MINUTES);
		                                                                sumData.setIntervalValue(intervalValue);
		                                                                sumData.setDimensionIstances(dimensionInstances);
		                                                                sumData.setCollectionName(DataSummarisationStandaloneMain.summarisedDataCollectionName);
		
		                                                                sumData.setExperimentNumber(experimentNumber);
		                                                                experimentsDesc = dateUtils.getStringFromDateTime(start_datetime) + " - " + dateUtils.getStringFromDateTime(dateTimeTwo) + "; " + experimentDescription;
		
		                                                                sumData.setExperimentDescription(experimentsDesc);
		                                                                sumData.setNumberOfNewData(newData.size());
		                                                                sumData.setNumberOfData(totalData);
		
		                                                                sumData.setStableSummarisedData(sumDataStable);
		                                                                clustering_time = System.currentTimeMillis() - clustering_time;
		                                                                inserting_time = System.currentTimeMillis();
		
		                                                                inserting_time = System.currentTimeMillis() - inserting_time;
		
		                                                                relevance_evaluation_time = System.currentTimeMillis();
		
		                                                                BestMetch bmStab = sumData.synthesesBestMetch(sumDataStable);
		                                                                BestMetch bmPrev = sumData.synthesesBestMetch(sumData.getPreviousSummarisedDataNoCheckSetted());
		                                                                sumData.setBestMetchStab(bmStab);
		                                                                sumData.setBestMetchPrev(bmPrev);
		
		                                                                Document docSummarisedDataResult = sumData.toDocument();
		                                                                ObjectId objectID = dao.insertDocumentAndGetID(docSummarisedDataResult, DataSummarisationStandaloneMain.summarisedDataCollectionName);
		                                                                String prevId = sumData.getId();
		                                                                sumData.setId(objectID.toString());
		
		                                                                if (sumData != null && newData.size() > threshold) {
		                                                                    dao.insertDataSummarisationAnomalies(new DataSummarisationAnomalies(
		                                                                            sumDataStable.getId(),
		                                                                            prevId,
		                                                                            sumData.getId(),
		                                                                            dateUtils.getStringFromDateTime(start_datetime),
		                                                                            dateUtils.getStringFromDateTime(dateTimeTwo),
		                                                                            StaticVariables.SUM_DATA_VERSION,
		                                                                            bmStab.getDiffCentroids(),
		                                                                            bmStab.getDiffRadius(),
		                                                                            (int) bmStab.getDiffRadiusVariation(),
		                                                                            bmPrev.getDiffCentroids(),
		                                                                            bmPrev.getDiffRadius(),
		                                                                            (int) bmPrev.getDiffRadiusVariation(),
		                                                                            intervalValue + " " + intervalType + "; unit: " + componentID + "; timeWindow: " + StaticVariables.TIME_WINDOW + "; " + StaticVariables.collectionNameExt + ";"));
		                                                                }
		
		                                                                experimentsDesc = "dist_center_prev: " + bmPrev.getDiffCentroids() + "; dist_density_prev: " + bmPrev.getDiffDensity() + "; dist_radius_prev: " + bmPrev.getDiffRadius() + "; dist_density_var_prev: " + bmPrev.getDiffDensityVariation() + "; dist_radius_var_prev: " + bmPrev.getDiffRadiusVariation() + "; " + experimentsDesc;
		                                                                experimentsDesc = "dist_center_stab: " + bmStab.getDiffCentroids() + "; dist_density_stab: " + bmStab.getDiffDensity() + "; dist_radius_stab: " + bmStab.getDiffRadius() + "; " + "; dist_density_var_stab: " + bmStab.getDiffDensityVariation() + "; dist_radius_var_stab: " + bmStab.getDiffRadiusVariation() + "; " + experimentsDesc;
		                                                                relevance_evaluation_time = System.currentTimeMillis() - relevance_evaluation_time;
		
		                                                            }
		                                                            //									transform_time = (System.currentTimeMillis()-transform_time) - extraction_time - clustering_time - inserting_time;
		                                                            					//				dao.insertDataSummarisationTiming(new TimingDataSummarisation(sumData.getNumberOfData(), sumData.getNumberOfData(), prevSynthesisNumber, sumData.getSynthesis().size(), 0, inserting_time, clustering_time, extraction_time, "Experiment n.: "+experimentNumber));
		                                                        } else {
		                                                            //											System.out.println("******* Nessun Dato Trovato");
		
		                                                        }
		
		                                                        start_datetime = LocalDateTime.from(dateTimeTwo);
	                                                    /*	}else {
	                                                    		LocalDateTime dateTimeTemp= dateUtils.getOtherDateTime(start_datetime, StaticVariables.DAYS, 1, true);
	
	                                                            start_datetime = LocalDateTime.from(dateTimeTemp);
	                                                    	}*/
	                                                    	
	                                                        //								total_time = System.currentTimeMillis() - total_time;
	                                                        //								if(sumData!=null && newData.size()>threshold) {
	                                                        //									dao.insertDataSummarisationTiming(new TimingDataSummarisation(totalData, newData.size(), sumData.getSynthesis().size(), 0, total_time, inserting_time, clustering_time, extraction_time, relevance_evaluation_time, experimentsDesc));
	                                                        //								}
	                                                        System.out.print("=");
	                                                    } catch (Exception e) {
	                                                        // TODO Auto-generated catch block
	                                                        System.out.println(e.getMessage());
	                                                        e.printStackTrace();
	                                                        //									}
	                                                        //								}
	                                                        //							}
	                                                    }
	                                                }
	                                            }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    RequestHandler.callRemoteURL(StaticVariables.URL_MAIL);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.println("\n-------------------------------------------------------------- Finito --------------------------------------------------------------");
    }



    private static void generateSummarisedData(IDAO dao, IDataSummarisation dataSummarisation, DateUtils dateUtils) {
        String[] machines = {"SnapuAuto"};
     //   String[] components = {"-1"};
        int[] featureSpaces = {3, 2};
        //int featureSpaceID = 1; //corrente e velocità asse Z
        String[] components = {"U1.0", "U2.0", "U3.0"};
        int experimentNumber = 1;
        String experimentDescription = "";


        String intervalType = StaticVariables.MINUTES;


        long extraction_time = 0;
        long clustering_time = 0;
        long inserting_time = 0;
        long relevance_evaluation_time = 0;
        long total_time = 0;

        //String toolID = "-1";
        //String partProgramID = "-1";
        String modeID = "-1";


        String[] tools = {"-1"};
        String[] partPrograms = {"0171507851"}; //TODO: Replace with DB values
        //    String[] partPrograms = {"0171507182"}; 
//        String[] partPrograms = {"0171507182", "0171507159", "0171506655"}; //TODO: Replace with DB values

        for (int featureSpaceID : featureSpaces) {
            Optional<FeatureSpace> fs = dao.readFeatureSpace(featureSpaceID);

		if (fs.isPresent()) {
			//
			//String [] anomalyTypes = {"", "_anomaly"};
			String [] anomalyTypes = {""};
			for (int h=0 ;h<anomalyTypes.length; h++) {
				StaticVariables.collectionNameExt = anomalyTypes[h];
			//				System.out.println("Anomaly: "+StaticVariables.collectionNameExt);
				DataSummarisationStandaloneMain.summarisedDataCollectionName = "summarised_data"+StaticVariables.collectionNameExt;
				int [] time_windows = {500};
				for (int k=0 ;k<time_windows.length; k++) {
	
					StaticVariables.TIME_WINDOW = time_windows[k];
					System.out.println("TimeWindows: "+StaticVariables.TIME_WINDOW);
	
					int [] algorithms = {4};
					for (int j =0 ;j<algorithms.length; j++) {
						if(k>0 && algorithms[j]==3) {
							break;
						}
						StaticVariables.SUM_DATA_VERSION = algorithms[j];
						System.out.println("Algorithm: "+StaticVariables.SUM_DATA_VERSION);
						int [] intervals = {1}; //La durata in tempo
						for(int i = 0; i< intervals.length; i++) {
							int intervalValue = intervals[i];
							experimentNumber = i+1;
							String expNumberInterval = "Esperimento: "+experimentNumber+"; Interval: "+intervalValue+" "+StaticVariables.MINUTES+"; ";
							for(String machineID: machines) {
								DimensionInstance machineInstance = dao.readDimensionInstance("Macchina", machineID);
								for(String componentID: components) {
//									experimentDescription = "";
//									experimentDescription += "Normalizzati: "+StaticVariables.normalize+"; ";
//									if(anomalyTypes[h].equalsIgnoreCase("")) {
//										experimentDescription += "Anomalie: false; ";
//									}else {
//										experimentDescription += "Anomalie: true; ";
//									}
									
//									experimentDescription += "timewindow: "+StaticVariables.TIME_WINDOW+"; ";
//									experimentDescription +="Feature Space: "+fs.get().getName()+"; ";
//									experimentDescription += expNumberInterval;

                                        DimensionInstance componentInstance = dao.readDimensionInstance("Componente", componentID);
                                        for (String partProgramID : partPrograms) {
                                            DimensionInstance partProgramInstance = dao.readDimensionInstance("Part Program", partProgramID);
                                            for (String toolID : tools) {
                                                DimensionInstance toolInstance = dao.readDimensionInstance("Utensili", toolID);

                                                String dataset = "machineID: " + machineID + "; componentID: " + componentID + "; toolID: " + toolID + "; partProgramID: " + partProgramID;
                                              
                                                System.out.println("\n" + dataset + "\n");

                                                total_time = System.currentTimeMillis();

                                                Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
                                                dimensionInstances.add(machineInstance);
                                                dimensionInstances.add(componentInstance);
                                                dimensionInstances.add(toolInstance);
                                                dimensionInstances.add(partProgramInstance);
                                                //dimensionInstances.add(toolInstance);


                                                String start_date = "19/04/2019 00:00:00"; 
                                                String end_date = "30/05/2019 00:00:00";
//                                                if(partProgramID.equalsIgnoreCase("0171507182")) {
//                                                	start_date = "27/05/2019 00:00:00"; 
//                                                	end_date = "04/06/2019 00:00:00";
//                                                }
                                                //						experimentDescription = experimentDescription + " \n Per dati che vanno dal: "+start_date +" al: " +end_date;
                                             //   String start_date_stable = "20/05/2019 12:30:00";
                                                LocalDateTime end_datetime = dateUtils.getDateTimeFromString(end_date);
                                                LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);


                                                LocalDateTime min_datetime = dateUtils.getDateTimeFromString("29/05/2019 00:00:00");
                                                LocalDateTime max_datetime = dateUtils.getDateTimeFromString("03/06/2019 00:00:00");

                                                SummarisedData sumDataStable = null;
                                                SummarisedData sumData = null;

                                                int totalData = 0;
                                                while (start_datetime.isBefore(end_datetime)) {
                                                    String experimentsDesc = "";
                                                    Instant instant = start_datetime.atZone(ZoneId.of("Z")).toInstant();
                                                    Date date = Date.from(instant);

                                                    LocalDateTime dateTimeTwo = dateUtils.getOtherDateTime(start_datetime, intervalType, intervalValue, true);
                                                    Instant instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
                                                    Date dateTwo = Date.from(instantTwo);

                                                    try {
                                                    //	if(start_datetime.isBefore(min_datetime) || start_datetime.isAfter(max_datetime)){
	                                                        total_time = System.currentTimeMillis();
	                                                        extraction_time = System.currentTimeMillis();
	                                                        Collection<MultidimensionalRecord> newData = new ArrayList<>();
	                                                        if (sumDataStable == null) {
	                                                            dateTimeTwo = dateUtils.getOtherDateTime(start_datetime, StaticVariables.DAYS, 1, true);
	                                                            instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
	                                                            dateTwo = Date.from(instantTwo);
	                                                            newData = DataSummarisationStandaloneMain.getNewData(dao, dateUtils, fs.get(), machineID, componentID, toolID, modeID, partProgramID, start_datetime, dateTimeTwo, true);

                                                               // dateTimeTwo = dateUtils.getDateTimeFromString(start_date);
                                                               // instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
	                                                            
	                                                        } else {
	                                                            newData = DataSummarisationStandaloneMain.getNewData(dao, dateUtils, fs.get(), machineID, componentID, toolID, modeID, partProgramID, start_datetime, dateTimeTwo, true);
	                                                        }
	                                                        extraction_time = System.currentTimeMillis() - extraction_time;
	                                                        //																System.out.println("NumData: "+newData.size());
	                                                        int threshold = 0;
	                                                        System.out.println("Extracted Data: " + newData.size() + "    - date: " + start_datetime + "    - end_date: " + dateTimeTwo);
	                                                        if (newData.size() > threshold) {
	                                                            totalData += newData.size();
	                                                            if (sumDataStable == null) {
	                                                                clustering_time = System.currentTimeMillis();
	
	                                                                sumDataStable = dataSummarisation.runClusteringExcludeZeros(true, sumData, newData, fs.get(), date, dateTwo);
	                                                                sumDataStable.setStable(true);
	                                                                sumDataStable.setIntervalType(StaticVariables.DAYS);
	                                                                sumDataStable.setIntervalValue(1);
	                                                                sumDataStable.setDimensionIstances(dimensionInstances);
	                                                                sumDataStable.setCollectionName(DataSummarisationStandaloneMain.summarisedDataCollectionName);
	
	                                                                sumDataStable.setExperimentNumber(experimentNumber);
	                                                                experimentsDesc = dateUtils.getStringFromDateTime(start_datetime) + " - " + dateUtils.getStringFromDateTime(dateTimeTwo) + "; " + experimentDescription;
	                                                                sumDataStable.setExperimentDescription(experimentsDesc);
	                                                                sumDataStable.setNumberOfNewData(newData.size());
	                                                                sumDataStable.setNumberOfData(totalData);
	
	                                                                clustering_time = System.currentTimeMillis() - clustering_time;
	
	                                                                inserting_time = System.currentTimeMillis();
	                                                                Document docSummarisedDataResult = sumDataStable.toDocument();
	                                                                ObjectId objectID = dao.insertDocumentAndGetID(docSummarisedDataResult, DataSummarisationStandaloneMain.summarisedDataCollectionName);
	                                                                sumDataStable.setId(objectID.toString());
	                                                                inserting_time = System.currentTimeMillis() - inserting_time;
	                                                                sumData = sumDataStable;
	                                                                sumData.setStableSummarisedData(sumDataStable);
	                                                                experimentsDesc = "distPrev: 0; " + experimentsDesc;
	                                                                experimentsDesc = "distStab: 0; " + experimentsDesc;
	                                                                
	                                                            } else {
	                                                                clustering_time = System.currentTimeMillis();
	                                                                String prevId = sumData.getId();
	                                                                sumData = dataSummarisation.runClustering(sumData, newData, fs.get(), date, dateTwo);
	                                                                sumData.setStable(false);
	                                                                //										sumData.setStableSummarisedData(sumDataStable);
	                                                                sumData.setIntervalType(StaticVariables.MINUTES);
	                                                                sumData.setIntervalValue(intervalValue);
	                                                                sumData.setDimensionIstances(dimensionInstances);
	                                                                sumData.setCollectionName(DataSummarisationStandaloneMain.summarisedDataCollectionName);
	
	                                                                sumData.setExperimentNumber(experimentNumber);
	                                                                experimentsDesc = dateUtils.getStringFromDateTime(start_datetime) + " - " + dateUtils.getStringFromDateTime(dateTimeTwo) + "; " + experimentDescription;
	
	                                                                sumData.setExperimentDescription(experimentsDesc);
	                                                                sumData.setNumberOfNewData(newData.size());
	                                                                sumData.setNumberOfData(totalData);
	
	                                                                sumData.setStableSummarisedData(sumDataStable);
	                                                                clustering_time = System.currentTimeMillis() - clustering_time;
	                                                                inserting_time = System.currentTimeMillis();
	
	                                                                inserting_time = System.currentTimeMillis() - inserting_time;
	
	                                                                relevance_evaluation_time = System.currentTimeMillis();
	
	                                                                BestMetch bmStab = sumData.synthesesBestMetch(sumDataStable);
	                                                                BestMetch bmPrev = sumData.synthesesBestMetch(sumData.getPreviousSummarisedDataNoCheckSetted());
	                                                                sumData.setBestMetchStab(bmStab);
	                                                                sumData.setBestMetchPrev(bmPrev);
	
	                                                                Document docSummarisedDataResult = sumData.toDocument();
	                                                                ObjectId objectID = dao.insertDocumentAndGetID(docSummarisedDataResult, DataSummarisationStandaloneMain.summarisedDataCollectionName);
	                                                           
	                                                                sumData.setId(objectID.toString());
	
	                                                                if (sumData != null && newData.size() > threshold) {
	                                                                    dao.insertDataSummarisationAnomalies(new DataSummarisationAnomalies(
	                                                                            sumDataStable.getId(),
	                                                                            prevId,
	                                                                            sumData.getId(),
	                                                                            dateUtils.getStringFromDateTime(start_datetime),
	                                                                            dateUtils.getStringFromDateTime(dateTimeTwo),
	                                                                            StaticVariables.SUM_DATA_VERSION,
	                                                                            bmStab.getDiffCentroids(),
	                                                                            bmStab.getDiffRadius(),
	                                                                            (int) bmStab.getDiffRadiusVariation(),
	                                                                            bmPrev.getDiffCentroids(),
	                                                                            bmPrev.getDiffRadius(),
	                                                                            (int) bmPrev.getDiffRadiusVariation(),
	                                                                           partProgramID + ";" + componentID + ";" + intervalValue+ " min;" + fs.get().getName()));
                                                                  //      intervalValue + " " + intervalType + "; unit: " + componentID + "; timeWindow: " + StaticVariables.TIME_WINDOW + "; partProgram: " + partProgramID + ";"+ StaticVariables.collectionNameExt + ";"));
	                                                                }
	
	                                                                experimentsDesc = "dist_center_prev: " + bmPrev.getDiffCentroids() + "; dist_density_prev: " + bmPrev.getDiffDensity() + "; dist_radius_prev: " + bmPrev.getDiffRadius() + "; dist_density_var_prev: " + bmPrev.getDiffDensityVariation() + "; dist_radius_var_prev: " + bmPrev.getDiffRadiusVariation() + "; " + experimentsDesc;
	                                                                experimentsDesc = "dist_center_stab: " + bmStab.getDiffCentroids() + "; dist_density_stab: " + bmStab.getDiffDensity() + "; dist_radius_stab: " + bmStab.getDiffRadius() + "; " + "; dist_density_var_stab: " + bmStab.getDiffDensityVariation() + "; dist_radius_var_stab: " + bmStab.getDiffRadiusVariation() + "; " + experimentsDesc;
	                                                                relevance_evaluation_time = System.currentTimeMillis() - relevance_evaluation_time;
	
	                                                            }
	                                                            //									transform_time = (System.currentTimeMillis()-transform_time) - extraction_time - clustering_time - inserting_time;
	                                                            					//				dao.insertDataSummarisationTiming(new TimingDataSummarisation(sumData.getNumberOfData(), sumData.getNumberOfData(), prevSynthesisNumber, sumData.getSynthesis().size(), 0, inserting_time, clustering_time, extraction_time, "Experiment n.: "+experimentNumber));
	                                                        } else {
	                                                            //											System.out.println("******* Nessun Dato Trovato");
	
	                                                        }
	
	                                                        start_datetime = LocalDateTime.from(dateTimeTwo);
                                                    	/*}else {
                                                    		LocalDateTime dateTimeTemp= dateUtils.getOtherDateTime(start_datetime, StaticVariables.DAYS, 1, true);

                                                            start_datetime = LocalDateTime.from(dateTimeTemp);
                                                    	}*/
                                                    	
                                                        //								total_time = System.currentTimeMillis() - total_time;
                                                        //								if(sumData!=null && newData.size()>threshold) {
                                                        //									dao.insertDataSummarisationTiming(new TimingDataSummarisation(totalData, newData.size(), sumData.getSynthesis().size(), 0, total_time, inserting_time, clustering_time, extraction_time, relevance_evaluation_time, experimentsDesc));
                                                        //								}
                                                        System.out.print("=");
                                                    } catch (Exception e) {
                                                        // TODO Auto-generated catch block
                                                        System.out.println(e.getMessage());
                                                        e.printStackTrace();
                                                        //									}
                                                        //								}
                                                        //							}
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    RequestHandler.callRemoteURL(StaticVariables.URL_MAIL);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.println("\n-------------------------------------------------------------- Finito --------------------------------------------------------------");
    }



    private static void generateSummarisedDataStable(IDAO dao, IDataSummarisation dataSummarisation, DateUtils dateUtils) {
        String[] machines = {"SnapuAuto"};
        String[] components = {"-1"};
        int[] featureSpaces = {2};
        //int featureSpaceID = 1; //corrente e velocità asse Z
        //String[] components = {"Unit 3.0"};
        int experimentNumber = 1;
        String experimentDescription = "";


        String intervalType = StaticVariables.MINUTES;


        long extraction_time = 0;
        long clustering_time = 0;
        long inserting_time = 0;
        long relevance_evaluation_time = 0;
        long total_time = 0;

        //String toolID = "-1";
        //String partProgramID = "-1";
        String modeID = "-1";


        String[] tools = {"-1"};
      //  String[] partPrograms = {"0171507851"}; //dal 19/04 al 30/04: particolare attenzione al 29/04, tra primo e secondo turno sono segnati 135 minuti di guasto/manutenzione
        //String[] partPrograms = {"0171507817"}; //dal 24/05 alle 14:00 al 27/05: particolare attenzione al 24/05 secondo turno segnati 60 minuti di guasto
       String[] partPrograms = {"0171507182"};//dal 27/05 al 04/06: particolare attenzione al 27/05 secondo turno segnati 85 minuti di guasto, 28/05 primo turno segnati 90 minuti di manutenzione, 03/06 tra primo e secondo turno segnati minuti di guasto (questo ordine in realtà dal punto di vista economico non individua una grande variabilità ma è dovuto al fatto che è un ordine molto grande in termini di pezzi realizzati)
//        String[] partPrograms = {"0171507851", "0171507159", "0171506655"}; //TODO: Replace with DB values

       //String[] partPrograms = {"0171507268"};//0171507268 in data 20/05 secondo turno non ci sono stati fermi dichiarati)
        for (int featureSpaceID : featureSpaces) {
            Optional<FeatureSpace> fs = dao.readFeatureSpace(featureSpaceID);

		if (fs.isPresent()) {
			//
			//String [] anomalyTypes = {"", "_anomaly"};
			String [] anomalyTypes = {""};
			for (int h=0 ;h<anomalyTypes.length; h++) {
				StaticVariables.collectionNameExt = anomalyTypes[h];
			//				System.out.println("Anomaly: "+StaticVariables.collectionNameExt);
				DataSummarisationStandaloneMain.summarisedDataCollectionName = "summarised_data"+StaticVariables.collectionNameExt;
				int [] time_windows = {500};
				for (int k=0 ;k<time_windows.length; k++) {
	
					StaticVariables.TIME_WINDOW = time_windows[k];
					System.out.println("TimeWindows: "+StaticVariables.TIME_WINDOW);
	
					int [] algorithms = {4};
					for (int j =0 ;j<algorithms.length; j++) {
						if(k>0 && algorithms[j]==3) {
							break;
						}
						StaticVariables.SUM_DATA_VERSION = algorithms[j];
						System.out.println("Algorithm: "+StaticVariables.SUM_DATA_VERSION);
						int [] intervals = {5}; //La durata in tempo
						for(int i = 0; i< intervals.length; i++) {
							int intervalValue = intervals[i];
							experimentNumber = i+1;
							String expNumberInterval = "Esperimento: "+experimentNumber+"; Interval: "+intervalValue+" "+StaticVariables.MINUTES+"; ";
							for(String machineID: machines) {
								DimensionInstance machineInstance = dao.readDimensionInstance("Macchina", machineID);
								for(String componentID: components) {
//									experimentDescription = "";
//									experimentDescription += "Normalizzati: "+StaticVariables.normalize+"; ";
//									if(anomalyTypes[h].equalsIgnoreCase("")) {
//										experimentDescription += "Anomalie: false; ";
//									}else {
//										experimentDescription += "Anomalie: true; ";
//									}
									
//									experimentDescription += "timewindow: "+StaticVariables.TIME_WINDOW+"; ";
//									experimentDescription +="Feature Space: "+fs.get().getName()+"; ";
//									experimentDescription += expNumberInterval;

                                        DimensionInstance componentInstance = dao.readDimensionInstance("Componente", componentID);
                                        for (String partProgramID : partPrograms) {
                                            DimensionInstance partProgramInstance = dao.readDimensionInstance("Part Program", partProgramID);
                                            for (String toolID : tools) {
                                                DimensionInstance toolInstance = dao.readDimensionInstance("Utensili", toolID);

                                                String dataset = "machineID: " + machineID + "; componentID: " + componentID + "; toolID: " + toolID + "; partProgramID: " + partProgramID;
                                              
                                                System.out.println("\n" + dataset + "\n");

                                                total_time = System.currentTimeMillis();

                                                Collection<DimensionInstance> dimensionInstances = new ArrayList<>();
                                                dimensionInstances.add(machineInstance);
                                                dimensionInstances.add(componentInstance);
                                                dimensionInstances.add(toolInstance);
                                                dimensionInstances.add(partProgramInstance);
                                                //dimensionInstances.add(toolInstance);


                                                String start_date = "27/05/2019 00:00:00"; 
                                                String end_date = "28/05/2019 00:00:00";

                                                //						experimentDescription = experimentDescription + " \n Per dati che vanno dal: "+start_date +" al: " +end_date;

                                                LocalDateTime end_datetime = dateUtils.getDateTimeFromString(end_date);
                                                LocalDateTime start_datetime = dateUtils.getDateTimeFromString(start_date);


                                                LocalDateTime min_datetime = dateUtils.getDateTimeFromString("10/04/2019 00:00:00");
                                                LocalDateTime max_datetime = dateUtils.getDateTimeFromString("29/04/2019 00:00:00");

                                                SummarisedData sumDataStable = null;
                                                SummarisedData sumData = null;

                                                int totalData = 0;
                                                while (start_datetime.isBefore(end_datetime)) {
                                                    String experimentsDesc = "";
                                                    Instant instant = start_datetime.atZone(ZoneId.of("Z")).toInstant();
                                                    Date date = Date.from(instant);

                                                    LocalDateTime dateTimeTwo = dateUtils.getOtherDateTime(start_datetime, intervalType, intervalValue, true);
                                                    Instant instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
                                                    Date dateTwo = Date.from(instantTwo);

                                                    try {
                                                 //   	if(start_datetime.isBefore(min_datetime) || start_datetime.isAfter(min_datetime)){
	                                                        total_time = System.currentTimeMillis();
	                                                        extraction_time = System.currentTimeMillis();
	                                                        Collection<MultidimensionalRecord> newData = new ArrayList<>();
	                                                        if (sumDataStable == null) {
	                                                            dateTimeTwo = dateUtils.getOtherDateTime(start_datetime, StaticVariables.DAYS, 1, true);
	                                                            instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
	                                                            dateTwo = Date.from(instantTwo);
	                                                            newData = DataSummarisationStandaloneMain.getNewData(dao, dateUtils, fs.get(), machineID, componentID, toolID, modeID, partProgramID, start_datetime, dateTimeTwo, true);

	                                                            
	                                                        } else {
	                                                         //   newData = DataSummarisationStandaloneMain.getNewData(dao, dateUtils, fs.get(), machineID, componentID, toolID, modeID, partProgramID, start_datetime, dateTimeTwo, true);
	                                                        }
	                                                        extraction_time = System.currentTimeMillis() - extraction_time;
	                                                        //																System.out.println("NumData: "+newData.size());
	                                                        int threshold = 0;
	                                                        System.out.println("Extracted Data: " + newData.size() + "    - date: " + start_date + "    - end_date: " + dateTimeTwo);
	                                                        if (newData.size() > threshold) {
	                                                            totalData += newData.size();
	                                                            if (sumDataStable == null) {
	                                                                clustering_time = System.currentTimeMillis();
	
	                                                                sumDataStable = dataSummarisation.runClusteringExcludeZeros(true, sumData, newData, fs.get(), date, dateTwo);
	                                                                sumDataStable.setStable(true);
	                                                                sumDataStable.setIntervalType(StaticVariables.DAYS);
	                                                                sumDataStable.setIntervalValue(1);
	                                                                sumDataStable.setDimensionIstances(dimensionInstances);
	                                                                sumDataStable.setCollectionName(DataSummarisationStandaloneMain.summarisedDataCollectionName);
	
	                                                                sumDataStable.setExperimentNumber(experimentNumber);
	                                                                experimentsDesc = dateUtils.getStringFromDateTime(start_datetime) + " - " + dateUtils.getStringFromDateTime(dateTimeTwo) + "; " + experimentDescription;
	                                                                sumDataStable.setExperimentDescription(experimentsDesc);
	                                                                sumDataStable.setNumberOfNewData(newData.size());
	                                                                sumDataStable.setNumberOfData(totalData);
	
	                                                                clustering_time = System.currentTimeMillis() - clustering_time;
	
	                                                                inserting_time = System.currentTimeMillis();
	                                                                Document docSummarisedDataResult = sumDataStable.toDocument();
	                                                                ObjectId objectID = dao.insertDocumentAndGetID(docSummarisedDataResult, DataSummarisationStandaloneMain.summarisedDataCollectionName);
	                                                                sumDataStable.setId(objectID.toString());
	                                                                inserting_time = System.currentTimeMillis() - inserting_time;
	                                                                sumData = sumDataStable;
	                                                                sumData.setStableSummarisedData(sumDataStable);
	                                                                experimentsDesc = "distPrev: 0; " + experimentsDesc;
	                                                                experimentsDesc = "distStab: 0; " + experimentsDesc;

	                                                                dateTimeTwo = dateUtils.getDateTimeFromString(start_date);
	                                                                instantTwo = dateTimeTwo.atZone(ZoneId.of("Z")).toInstant();
	                                                                
	                                                            } else {
	                                           /*                     clustering_time = System.currentTimeMillis();
	
	                                                                sumData = dataSummarisation.runClustering(sumData, newData, fs.get(), date, dateTwo);
	                                                                sumData.setStable(false);
	                                                                //										sumData.setStableSummarisedData(sumDataStable);
	                                                                sumData.setIntervalType(StaticVariables.MINUTES);
	                                                                sumData.setIntervalValue(intervalValue);
	                                                                sumData.setDimensionIstances(dimensionInstances);
	                                                                sumData.setCollectionName(DataSummarisationStandaloneMain.summarisedDataCollectionName);
	
	                                                                sumData.setExperimentNumber(experimentNumber);
	                                                                experimentsDesc = dateUtils.getStringFromDateTime(start_datetime) + " - " + dateUtils.getStringFromDateTime(dateTimeTwo) + "; " + experimentDescription;
	
	                                                                sumData.setExperimentDescription(experimentsDesc);
	                                                                sumData.setNumberOfNewData(newData.size());
	                                                                sumData.setNumberOfData(totalData);
	
	                                                                sumData.setStableSummarisedData(sumDataStable);
	                                                                clustering_time = System.currentTimeMillis() - clustering_time;
	                                                                inserting_time = System.currentTimeMillis();
	
	                                                                inserting_time = System.currentTimeMillis() - inserting_time;
	
	                                                                relevance_evaluation_time = System.currentTimeMillis();
	
	                                                                BestMetch bmStab = sumData.synthesesBestMetch(sumDataStable);
	                                                                BestMetch bmPrev = sumData.synthesesBestMetch(sumData.getPreviousSummarisedDataNoCheckSetted());
	                                                                sumData.setBestMetchStab(bmStab);
	                                                                sumData.setBestMetchPrev(bmPrev);
	
	                                                                Document docSummarisedDataResult = sumData.toDocument();
	                                                                ObjectId objectID = dao.insertDocumentAndGetID(docSummarisedDataResult, DataSummarisationStandaloneMain.summarisedDataCollectionName);
	                                                                String prevId = sumData.getId();
	                                                                sumData.setId(objectID.toString());
	
	                                                                if (sumData != null && newData.size() > threshold) {
	                                                                    dao.insertDataSummarisationAnomalies(new DataSummarisationAnomalies(
	                                                                            sumDataStable.getId(),
	                                                                            prevId,
	                                                                            sumData.getId(),
	                                                                            dateUtils.getStringFromDateTime(start_datetime),
	                                                                            dateUtils.getStringFromDateTime(dateTimeTwo),
	                                                                            StaticVariables.SUM_DATA_VERSION,
	                                                                            bmStab.getDiffCentroids(),
	                                                                            bmStab.getDiffRadius(),
	                                                                            (int) bmStab.getDiffRadiusVariation(),
	                                                                            bmPrev.getDiffCentroids(),
	                                                                            bmPrev.getDiffRadius(),
	                                                                            (int) bmPrev.getDiffRadiusVariation(),
	                                                                            intervalValue + " " + intervalType + "; unit: " + componentID + "; timeWindow: " + StaticVariables.TIME_WINDOW + "; " + StaticVariables.collectionNameExt + ";"));
	                                                                }
	
	                                                                experimentsDesc = "dist_center_prev: " + bmPrev.getDiffCentroids() + "; dist_density_prev: " + bmPrev.getDiffDensity() + "; dist_radius_prev: " + bmPrev.getDiffRadius() + "; dist_density_var_prev: " + bmPrev.getDiffDensityVariation() + "; dist_radius_var_prev: " + bmPrev.getDiffRadiusVariation() + "; " + experimentsDesc;
	                                                                experimentsDesc = "dist_center_stab: " + bmStab.getDiffCentroids() + "; dist_density_stab: " + bmStab.getDiffDensity() + "; dist_radius_stab: " + bmStab.getDiffRadius() + "; " + "; dist_density_var_stab: " + bmStab.getDiffDensityVariation() + "; dist_radius_var_stab: " + bmStab.getDiffRadiusVariation() + "; " + experimentsDesc;
	                                                                relevance_evaluation_time = System.currentTimeMillis() - relevance_evaluation_time;
	
	                                                            */
	                                                            }
	                                                            //									transform_time = (System.currentTimeMillis()-transform_time) - extraction_time - clustering_time - inserting_time;
	                                                            					//				dao.insertDataSummarisationTiming(new TimingDataSummarisation(sumData.getNumberOfData(), sumData.getNumberOfData(), prevSynthesisNumber, sumData.getSynthesis().size(), 0, inserting_time, clustering_time, extraction_time, "Experiment n.: "+experimentNumber));
	                                                        } else {
	                                                            //											System.out.println("******* Nessun Dato Trovato");
	
	                                                        }
	
	                                                        start_datetime = LocalDateTime.from(dateTimeTwo);
                                                    /*	}else {
                                                    		LocalDateTime dateTimeTemp= dateUtils.getOtherDateTime(start_datetime, StaticVariables.DAYS, 1, true);

                                                            start_datetime = LocalDateTime.from(dateTimeTemp);
                                                    	}*/
                                                    	
                                                        //								total_time = System.currentTimeMillis() - total_time;
                                                        //								if(sumData!=null && newData.size()>threshold) {
                                                        //									dao.insertDataSummarisationTiming(new TimingDataSummarisation(totalData, newData.size(), sumData.getSynthesis().size(), 0, total_time, inserting_time, clustering_time, extraction_time, relevance_evaluation_time, experimentsDesc));
                                                        //								}
                                                        System.out.print("=");
                                                    } catch (Exception e) {
                                                        // TODO Auto-generated catch block
                                                        System.out.println(e.getMessage());
                                                        e.printStackTrace();
                                                        //									}
                                                        //								}
                                                        //							}
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    RequestHandler.callRemoteURL(StaticVariables.URL_MAIL);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.println("\n-------------------------------------------------------------- Finito --------------------------------------------------------------");
    }


    private static Collection<MultidimensionalRecord> getNewData(IDAO dao, DateUtils dateUtils, FeatureSpace featureSpace, String idMachine, String idSpindle,
                                                                 String idTool, String mode, String idPartProgram, LocalDateTime dateTime, LocalDateTime dateTimeTwo, boolean increase) {
        //		System.out.println("Getting new Data from DB");

        Collection<MultidimensionalRecord> newRecords = new ArrayList<>();
        //TODO spostare l'estensione della collection dentro al metodo
        Optional<Iterable<Document>> documents = dao.readMeasures(dateUtils.getCollectionName(dateTime)+StaticVariables.collectionNameExt, idTool, idPartProgram,
                mode, idMachine, idSpindle, dateTime, dateTimeTwo, increase);

        if (documents.isPresent()) {
            for (Document doc : documents.get()) {
                MultidimensionalRecord multiDimRecord = new MultidimensionalRecord();

                multiDimRecord.setFeaturesDataFromDocument(doc, featureSpace);
                newRecords.add(multiDimRecord);
            }
        }
        return newRecords;
    }

}
