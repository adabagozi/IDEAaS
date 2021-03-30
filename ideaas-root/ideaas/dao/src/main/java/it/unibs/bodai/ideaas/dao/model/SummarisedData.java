package it.unibs.bodai.ideaas.dao.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import it.unibs.bodai.ideaas.custom_libraries.MyClustreamKernel;
import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.utils.BestMetch;
import utils.StaticVariables;


/**
 * @author Ada Bagozi
 *
 */
/**
 * @author ada
 *
 */
/**
 * @author ada
 *
 */
public class SummarisedData {
	private static final Logger LOG = Logger.getLogger(SummarisedData.class.getName());

	public static final String KEY_ID = "_id";
	public static final String KEY_SUM_DATA_STABLE_ID = "stable_summarised_data_id";
	public static final String KEY_SUM_DATA_PREVIOUS_ID = "prev_summarised_data_id";
	public static final String KEY_TIMESTAMP_INITIAL = "start_timestamp";
	public static final String KEY_TIMESTAMP_FINAL = "end_timestamp";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_STATUS = "state";
	public static final String KEY_DIMENSIONS = "dimensions";
	public static final String KEY_FEATURE_SPACE = "feature_space";
	public static final String KEY_SYNTHESIS = "syntheses";


	public static final String KEY_IS_STABLE = "is_stable";

	//TODO: togliere finiti i test
	public static final String KEY_TOTAL_SYNTHESIS = "total_synthesis";
	public static final String KEY_DISCARTED_SYNTHESIS = "synthesis_discarted";
	public static final String KEY_OLD_SYNTHESIS = "old_synthesis";
	public static final String KEY_INTERVAL_TYPE = "interval_type";
	public static final String KEY_INTERVAL_VALUE = "interval_value";
	public static final String KEY_EXPERIMENT_NUMBER = "experiment_number";
	public static final String KEY_EXPERIMENT_DESCRIPTION = "experiment_description";
	public static final String KEY_INPUT_DATA = "input_data";

	public static final String KEY_DIST_CENTROIDS_PREV = "distance_centroids_prev";
	public static final String KEY_DIST_RADIUS_PREV = "distance_radius_prev";
	public static final String KEY_DIST_DENSITY_PREV = "distance_density_prev";
	public static final String KEY_DIST_RADIUS_VAR_PREV = "distance_radius_variation_prev";
	public static final String KEY_DIST_DENSITY_VAR_PREV = "distance_density_variation_prev";
	
	public static final String KEY_DIST_CENTROIDS_STAB = "distance_centroids_stab";
	public static final String KEY_DIST_RADIUS_STAB = "distance_radius_stab";
	public static final String KEY_DIST_DENSITY_STAB = "distance_density_stab";
	public static final String KEY_DIST_RADIUS_VAR_STAB = "distance_radius_variation_stab";
	public static final String KEY_DIST_DENSITY_VAR_STAB = "distance_density_variation_stab";
	
	public static final String KEY_TOTAL_RECORDS = "records_total";
	public static final String KEY_NEW_RECORDS = "records_new";
	



	private String id;
	private String status = "ok";
	private long timestampValue = -1;
	private Date timestamp;
	private Date timestampInitial;
	private Date timestampFinal;
	private boolean isStable;
	
	public String id_stable = "";
	public String id_prev = "";
	// TODO: vedere come gestire la questione del summarised data on-demand
	private boolean isSetted;

	private SummarisedData stableSummarisedData;
	private SummarisedData previousSummarisedData;

	private Collection<DimensionInstance> dimensionInstances;
	private Context context;

	//TODO: sara da togliere finiti i test (o forse no)
	//	private Collection<Synthesi> synthesis = new ArrayList<>(); 

	private HashMap<Integer, Synthesi> synthesis = new HashMap<>(); 
	private ArrayList<Synthesi> synthList = new ArrayList<>();
	/**
	 * Attualmente non stiamo lavorando sulla struttura cluster sintesi, ma solo sulle sintesi, 
	 * lasciamo clusters perchè ci sta lavorando Naim
	 * Parlare con Naim e chiedere a Bianchini se va bene togliere questa struttura e mantenere solo le sintesi.
	 * 
	 */
	private Collection<Cluster> clusters = new ArrayList<>();

	private FeatureSpace featureSpace;

	//TODO: mettere in modo universale dal file di configurazione
	private String collectionName = "data_summarisation";

	private int recordNumberTotal = 0;
	private int recordNumberNew = 0;

	//TODO: vedere come gestire questi parametri alla fine dei test
	private int oldSynthesis = 0;
	private int totalSynthesisGenerated = 0;
	private int synthesisDiscarted = 0;
	private int intervalValue;
	private String intervalType;
	private int experimentNumber = 0;
	private String experiment_description;
	
	private BestMetch bestMetchStab = new BestMetch();
	private BestMetch bestMetchPrev = new BestMetch();


	/**
	 * 
	 */
	private Collection<MultidimensionalRecord> inputData = new ArrayList<>();

	/**
	 * Sistemare le sintesi facendone un estensione di kernels
	 */
	private MyClustreamKernel[] kernels = new MyClustreamKernel[0]; //TODO Valutare se mantenere i Myclustreamkernel o sintesi


	/**
	 * @param id
	 * @param timestamp
	 * @param timestampInitial
	 * @param timestampFinal
	 * @param stableSummarisedData
	 * @param previousSummarisedData
	 * @param dimensions
	 * @param parameters
	 * @param synthesis
	 * @param clusters
	 */
	public SummarisedData(String id, Date timestamp, Date timestampInitial, Date timestampFinal,
			SummarisedData stableSummarisedData, SummarisedData previousSummarisedData,
			Collection<DimensionInstance> dimensionInstances, Context context, HashMap<Integer, Synthesi> synthesis,
			Collection<Cluster> clusters, FeatureSpace featureSpace) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.timestampInitial = timestampInitial;
		this.timestampFinal = timestampFinal;
		this.stableSummarisedData = stableSummarisedData;
		this.previousSummarisedData = previousSummarisedData;
		this.dimensionInstances = dimensionInstances;
		this.context = context;
		this.synthesis = synthesis;
		this.clusters = clusters;
		this.isSetted = true;
		if(this.stableSummarisedData == null){
			this.isStable = true;
		}else {
			this.isStable = false;
		}
		this.featureSpace = featureSpace;
	}

	/**
	 * @param id
	 * @param timestamp
	 * @param timestampInitial
	 * @param timestampFinal
	 * @param stableSummarisedData
	 * @param previousSummarisedData
	 * @param dimensions
	 * @param parameters
	 * @param synthesis
	 * @param clusters
	 */
	public SummarisedData(String id, Date timestamp, Date timestampInitial, Date timestampFinal,
			SummarisedData stableSummarisedData, SummarisedData previousSummarisedData,
			Collection<DimensionInstance> dimensionInstances, Context context, FeatureSpace featureSpace) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.timestampInitial = timestampInitial;
		this.timestampFinal = timestampFinal;
		this.stableSummarisedData = stableSummarisedData;
		this.previousSummarisedData = previousSummarisedData;
		this.dimensionInstances = dimensionInstances;
		this.context = context;
		this.isSetted = true;
		if(this.stableSummarisedData == null){
			this.isStable = true;
		}else {
			this.isStable = false;
		}
		this.featureSpace = featureSpace;
	}

	/**
	 * 
	 */
	public SummarisedData() {
		super();
		this.isSetted = false;
		this.synthesis = new HashMap<Integer, Synthesi>();
		this.clusters = new ArrayList<>();

	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}


	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	/**
	 * @return the timestampInitial
	 */
	public Date getTimestampInitial() {
		return timestampInitial;
	}


	/**
	 * @param timestampInitial the timestampInitial to set
	 */
	public void setTimestampInitial(Date timestampInitial) {
		this.timestampInitial = timestampInitial;
	}


	/**
	 * @return the timestampFinal
	 */
	public Date getTimestampFinal() {
		return timestampFinal;
	}


	/**
	 * @param timestampFinal the timestampFinal to set
	 */
	public void setTimestampFinal(Date timestampFinal) {
		this.timestampFinal = timestampFinal;
	}


	/**
	 * @return the isStable
	 */
	public boolean isStable() {
		return isStable;
	}


	/**
	 * @param isStable the isStable to set
	 */
	public void setStable(boolean isStable) {
		this.isStable = isStable;
	}


	/**
	 * @return the collectionName
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * @param collectionName the collectionName to set
	 */
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	/**
	 * @return the stableSummarisedData
	 * @throws IOException 
	 */
	public SummarisedData getStableSummarisedData() throws IOException {
		if(!this.stableSummarisedData.isSetted && !this.id_stable.equalsIgnoreCase("")){
			Optional<SummarisedData> sumData = DAOFactory.getDAOFactory().getDAO().readSummarisedDataById(this.collectionName, this.id_stable);
			if(sumData.isPresent()){
				this.setStableSummarisedData(sumData.get());
			}else{
				throw new IOException("Summarized data with selected id dosn't exist");
			}
		}
		return this.stableSummarisedData;
	}


	/**
	 * @return the stableSummarisedData
	 * @throws IOException 
	 */
	public SummarisedData getStableSummarisedDataNoCheckSetted(){
		//		if(!this.getStableSummarisedData().isSetted){
		//			Optional<SummarisedData> sumData = DAOFactory.getDAOFactory().getDAO().readSummarisedDataById("data_summarization", this.getStableSummarisedData().getId());
		//			if(sumData.isPresent()){
		//					this.setStableSummarisedData(sumData.get());
		//			}else{
		//				throw new IOException("Summarized data with selected id dosn't exist");
		//			}
		//		}
		return this.stableSummarisedData;
	}

	/**
	 * @param stableSummarisedData the stableSummarisedData to set
	 */
	public void setStableSummarisedData(SummarisedData stableSummarisedData) {

		this.stableSummarisedData = stableSummarisedData;
	}


	/**
	 * @return the previousSummarisedData
	 * @throws IOException 
	 */
	public SummarisedData getPreviousSummarisedData() throws IOException {
		if(!this.getPreviousSummarisedData().isSetted && !this.id_prev.equalsIgnoreCase("")){
			Optional<SummarisedData> sumData = DAOFactory.getDAOFactory().getDAO().readSummarisedDataById(this.getCollectionName(), this.id_prev);
			if(sumData.isPresent()){
				this.setPreviousSummarisedData(sumData.get());
			}else{
				throw new IOException("Summarized data with selected id dosn't exist");
			}
		}
		return this.previousSummarisedData;
	}

	public SummarisedData getPreviousSummarisedDataNoCheckSetted() {

		return this.previousSummarisedData;
	}
	/**
	 * @param previousSummarisedData the previousSummarisedData to set
	 */
	public void setPreviousSummarisedData(SummarisedData previousSummarisedData) {
		this.previousSummarisedData = previousSummarisedData;
	}


	/**
	 * @return the dimensions
	 */
	public Collection<DimensionInstance> getDimensionInstances() {
		if(this.dimensionInstances == null) {
			this.dimensionInstances = new ArrayList<>();
		}
		return dimensionInstances;
	}


	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensionIstances(Collection<DimensionInstance> dimensionInstances) {
		this.dimensionInstances = dimensionInstances;
	}


	/**
	 * @param dimensionInstance
	 */
	public void addDimensionInstance(DimensionInstance dimensionInstance) {
		if(this.dimensionInstances == null) {
			this.dimensionInstances = new ArrayList<>();
		}
		this.dimensionInstances.add(dimensionInstance);
	}


	/**
	 * @return the parameters
	 */
	public Context getContext() {
		if(this.context == null) {
			this.context = new Context();
		}
		return context;
	}



	/**
	 * @param parameters the parameters to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the synthesis
	 */
	public HashMap<Integer, Synthesi> getSynthesis() {
		return synthesis;
	}

	public ArrayList<Synthesi> getSynth(){
		return this.synthList;
	}
	

	/**
	 * @param synthesis the synthesis to set
	 */
	public void setSynthesis(HashMap<Integer, Synthesi> synthesis) {
		this.synthesis = synthesis;
	}


	/**
	 * @param synthesis the synthesis to set
	 */
	public void addSynthesi(Synthesi synthesis) {
		this.synthList.add(synthesis);
		if(this.synthesis==null){
			this.synthesis = new HashMap<>();
		}
		this.synthesis.put(synthesis.getId(), synthesis);
	}


	/**
	 * @return the inputData
	 */
	public Collection<MultidimensionalRecord> getInputData() {
		if(this.inputData == null) {
			this.inputData = new ArrayList<>();
		}
		return inputData;
	}

	/**
	 * @param inputData the inputData to set
	 */
	public void setInputData(Collection<MultidimensionalRecord> inputData) {
		this.inputData = inputData;
	}




	/**
	 * @return the kernels
	 */
	public MyClustreamKernel[] getKernels() {
		return kernels;
	}

	/**
	 * @param kernels the kernels to set
	 */
	public void setKernels(MyClustreamKernel[] kernels) {
		this.kernels = new MyClustreamKernel[kernels.length];
		this.kernels = kernels;
	}

	/**
	 * @return the clusters
	 */
	public Collection<Cluster> getClusters() {
		return clusters;
	}


	/**
	 * @return the clusters
	 */
	public Optional<Cluster> getClusterWithID(int id) {
		for (Cluster cluster : this.clusters) {
			if(cluster.getId() == id){
				return Optional.of(cluster);
			}
		}
		return Optional.empty();
	}


	/**
	 * @param clusters the clusters to set
	 */
	public void setClusters(Collection<Cluster> clusters) {
		this.clusters = clusters;
	}

	/**
	 * @param synthesis the synthesis to set
	 */
	public void addCluster(Cluster cluster) {
		if(this.clusters==null){
			this.clusters = new ArrayList<>();
		}
		this.clusters.add(cluster);
	}


	/**
	 * @return the featureSpace
	 */
	public FeatureSpace getFeatureSpace() {
		return featureSpace;
	}

	/**
	 * @param featureSpace the featureSpace to set
	 */
	public void setFeatureSpace(FeatureSpace featureSpace) {
		this.featureSpace = featureSpace;
	}


	/**
	 * @return the numberOfNewData
	 */
	public int getNumberOfNewData() {
		return recordNumberNew;
	}

	/**
	 * @param numberOfNewData the numberOfNewData to set
	 */
	public void setNumberOfNewData(int numberOfNewData) {
		this.recordNumberNew = numberOfNewData;
	}

	public int numberOfClustersDifference(SummarisedData other){
		if(this.isStable){
			return 0;
		}
		return Math.abs(this.getClusters().size() - other.getClusters().size());
	}

	public int numberOfSynthesisDifference(SummarisedData other){
		if(this.isStable){
			return 0;
		}
		return Math.abs(this.getClusters().size() - other.getSynthesis().size());
	}


	/**
	 * @return the oldSynthesis
	 */
	public int getOldSynthesis() {
		return oldSynthesis;
	}

	/**
	 * @param oldSynthesis the oldSynthesis to set
	 */
	public void setOldSynthesis(int oldSynthesis) {
		this.oldSynthesis = oldSynthesis;
	}

	/**
	 * @return the totalSynthesisGenerated
	 */
	public int getTotalSynthesisGenerated() {
		return totalSynthesisGenerated;
	}

	/**
	 * @param totalSynthesisGenerated the totalSynthesisGenerated to set
	 */
	public void setTotalSynthesisGenerated(int totalSynthesisGenerated) {
		this.totalSynthesisGenerated = totalSynthesisGenerated;
	}

	/**
	 * @return the sunthesisDiscarted
	 */
	public int getSynthesisDiscarted() {
		return synthesisDiscarted;
	}

	/**
	 * @param sunthesisDiscarted the sunthesisDiscarted to set
	 */
	public void setSynthesisDiscarted(int sunthesisDiscarted) {
		this.synthesisDiscarted = sunthesisDiscarted;
	}


	/**
	 * @return the intervalValue
	 */
	public int getIntervalValue() {
		return intervalValue;
	}

	/**
	 * @param intervalValue the intervalValue to set
	 */
	public void setIntervalValue(int intervalValue) {
		this.intervalValue = intervalValue;
	}

	/**
	 * @return the intervalType
	 */
	public String getIntervalType() {
		return intervalType;
	}

	/**
	 * @param intervalType the intervalType to set
	 */
	public void setIntervalType(String intervalType) {
		this.intervalType = intervalType;
	}



	/**
	 * @return the numberOfData
	 */
	public int getNumberOfData() {
		return recordNumberTotal;
	}

	/**
	 * @param numberOfData the numberOfData to set
	 */
	public void setNumberOfData(int numberOfData) {
		this.recordNumberTotal = numberOfData;
	}

	/**
	 * @return the experimentNumber
	 */
	public int getExperimentNumber() {
		return experimentNumber;
	}

	/**
	 * @param experimentNumber the experimentNumber to set
	 */
	public void setExperimentNumber(int experimentNumber) {
		this.experimentNumber = experimentNumber;
	}

	public void setBestMetchStab(BestMetch stabBM) {
		this.bestMetchStab = stabBM;
	}
	
	public void setBestMetchPrev(BestMetch prevBM) {
		this.bestMetchPrev = prevBM;
	}
	//	/**
	//	 * Metodo per il calcolo della distanza tra due set di cluster
	//	 * @param other Summarised data to compare
	//	 * @return
	//	 */
	//	public double clustersBestMetch(SummarisedData other){
	//		double totClusters =this.getClusters().size() + other.getClusters().size();
	//
	//		double distanceC1C2 = 0; 
	//		for(Cluster cluster: this.getClusters()){
	//			double min = Double.POSITIVE_INFINITY;
	//			for (Cluster clusterOther: other.getClusters()){
	//				double d0 = cluster.getDistance(clusterOther);
	//				min = d0 < min ? d0 : min; 
	//			}
	//
	//			distanceC1C2 += min;
	//		}
	//		double distanceC2C1 = 0; 
	//		for(Cluster clusterOther: other.getClusters()){
	//			double min = Double.POSITIVE_INFINITY;
	//			for (Cluster cluster: this.getClusters()){
	//				double d0 = clusterOther.getDistance(cluster);
	//				min = d0 < min ? d0 : min; 
	//			}
	//			distanceC2C1 += min;
	//		}
	//		double distanceClusters = distanceC1C2 + distanceC2C1;
	//		double clustersBestMetch = 0;
	//		if(distanceClusters > 0 && totClusters>0){
	//			clustersBestMetch = distanceClusters/totClusters;
	//		}
	//		return clustersBestMetch;
	//	}
	//
		
		/**
		 * Metodo per il calcolo della distanza tra due set di sintesi valutando i loro centroidi
		 * @param other Summarised data to compare
		 * @return
		 */
		public BestMetch synthesesBestMetch(SummarisedData other){
			if (other == null){
				return new BestMetch();
			}
			double totSynthesis = this.getSynthesis().size() + other.getSynthesis().size();
	
			double distanceS1S2 = 0;
			double distanceRS1S2 = 0;
			double distancedS1S2 = 0;			
			double distanceRS1S2Variation = 0;
			double distancedS1S2Variation = 0;
			
			Synthesi synthesisS1 = null;
			Synthesi synthesisS2 = null;
			
			for(Entry<Integer, Synthesi> synthesis : this.getSynthesis().entrySet()) {
				double min = Double.POSITIVE_INFINITY;

				synthesisS1 = synthesis.getValue();
				for(Entry<Integer, Synthesi> synthesisOther : other.getSynthesis().entrySet()) {
					double d0 = synthesis.getValue().getDistance(synthesisOther.getValue());
					if(d0 < min) {
						min = d0; 
						synthesisS2 =  synthesisOther.getValue();
					}
				}
	
				distanceS1S2 += min;
				distanceRS1S2 += Math.abs(synthesisS1.getRadius()-synthesisS2.getRadius());
				distancedS1S2 += Math.abs(synthesisS1.getDensity()-synthesisS2.getDensity());
				distanceRS1S2Variation += synthesisS1.getRadius()-synthesisS2.getRadius();
				distancedS1S2Variation += synthesisS1.getDensity()-synthesisS2.getDensity();
			}
			double distanceS2S1 = 0; 
			double distanceRS2S1 = 0;
			double distancedS2S1 = 0;
			double distanceRS2S1Variation = 0;
			double distancedS2S1Variation = 0;
	
			for(Entry<Integer, Synthesi> synthesisOther : other.getSynthesis().entrySet()) {
				synthesisS2 = synthesisOther.getValue();
				double min = Double.POSITIVE_INFINITY;
				for(Entry<Integer, Synthesi> synthesis : this.getSynthesis().entrySet()) {
					
					double d0 = synthesisOther.getValue().getDistance(synthesis.getValue());
					
					if(d0 < min) {
						min = d0;
						synthesisS1 =  synthesis.getValue();
					}
				}
				distanceS2S1 += min;
				distanceRS2S1 += Math.abs(synthesisS1.getRadius()-synthesisS2.getRadius());
				distancedS2S1 += Math.abs(synthesisS1.getDensity()-synthesisS2.getDensity());
				distanceRS2S1Variation += synthesisS1.getRadius()-synthesisS2.getRadius();
				distancedS2S1Variation += synthesisS1.getDensity()-synthesisS2.getDensity();
			}
			double synthesesBestMetchCentroids = 0;
			double synthesesBestMetchRadius = 0;
			double synthesesBestMetchDensity = 0;
			double synthesesBestMetchRadiusVariation = 0;
			double synthesesBestMetchDensityVariation = 0;
			
			if(totSynthesis>0){
				synthesesBestMetchCentroids = (distanceS1S2 + distanceS2S1)/totSynthesis;
				synthesesBestMetchRadius = (distanceRS1S2 + distanceRS2S1)/totSynthesis;
				synthesesBestMetchDensity = (distancedS1S2 + distancedS2S1)/totSynthesis;
				synthesesBestMetchRadiusVariation = (distanceRS1S2Variation + distanceRS2S1Variation)/totSynthesis;
				synthesesBestMetchDensityVariation = (distancedS1S2Variation + distancedS2S1Variation)/totSynthesis;
			}
			
			
			return new BestMetch(synthesesBestMetchCentroids, synthesesBestMetchRadius, synthesesBestMetchDensity, synthesesBestMetchRadiusVariation, synthesesBestMetchDensityVariation);
		}
		
	
	/**
	 * @return the timestampValue
	 */
	public long getTimestampValue() {
		return timestampValue;
	}

	/**
	 * @param timestampValue the timestampValue to set
	 */
	public void setTimestampValue(long timestampValue) {
		this.timestampValue = timestampValue;
	}


	public String getExperimentDescription() {
		return experiment_description;
	}

	public void setExperimentDescription(String experiment_description) {
		this.experiment_description = experiment_description;
	}

	/**
	 * Metodo per trasformare la prima parte di data summarisation in json
	 * @return JSONObject first part of data summarisation json
	 */
	public JSONObject toJSONCommon() {

		JSONObject jsonSummarizedData = new JSONObject();
		JSONArray jsonDimensionInstances = new JSONArray();
		JSONArray jsonSyntheses = new JSONArray();
		//		
		
		// check stato delle sintesi
		for(Entry<Integer, Synthesi> synthesi : this.getSynthesis().entrySet()) {
			ArrayList<Synthesi> synthesesStable = new ArrayList<Synthesi>();
			ArrayList<Synthesi> synthesesPrev = new ArrayList<Synthesi>();
			for(Integer idSynthesisHistory : synthesi.getValue().parentHistoryIdList) {
				if(this.getPreviousSummarisedDataNoCheckSetted() != null) {
					Synthesi s = this.getPreviousSummarisedDataNoCheckSetted().getSynthesis().get(idSynthesisHistory);
					if ( s != null) {
						synthesesPrev.add(s);
					} 
				}
				if(this.getStableSummarisedDataNoCheckSetted() != null) {
					Synthesi s = this.getStableSummarisedDataNoCheckSetted().getSynthesis().get(idSynthesisHistory);
					if ( s != null) {
						synthesesStable.add(s);
					} 
				}
			}
			synthesi.getValue().checkSythesisState(synthesesStable, synthesesPrev);

			jsonSyntheses.put(synthesi.getValue().toJSON());
		}
		jsonSummarizedData.put(KEY_SYNTHESIS, jsonSyntheses);

		if(StaticVariables.SAVE_DATA_IN_SYNTHESES) {
			JSONArray jsonInputData = new JSONArray();
			for (MultidimensionalRecord md: this.getInputData()) {
				jsonInputData.put(md.toJSON());
			}
			jsonSummarizedData.put(KEY_INPUT_DATA, jsonInputData);
		}
		//		
		for (DimensionInstance dimensionInstance : this.getDimensionInstances()) {
			jsonDimensionInstances.put(dimensionInstance.toJSON());
		}
		jsonSummarizedData.put(KEY_DIMENSIONS, jsonDimensionInstances);

		jsonSummarizedData.put(KEY_FEATURE_SPACE, this.getFeatureSpace().toJSON());



		return jsonSummarizedData;
	}



	/**
	 * Metodo per la trasformazione di un oggetto Measure in un elemento di tipo @Document
	 * @return document documento generato dalla misura
	 */
	public Document toDocument(){
		Document doc = Document.parse( this.toJSONCommon().toString() );
		doc.append(KEY_STATUS, this.getStatus());
		doc.append(KEY_TIMESTAMP, this.getTimestamp());	
		doc.append(KEY_TIMESTAMP_INITIAL, this.getTimestampInitial());	
		doc.append(KEY_TIMESTAMP_FINAL, this.getTimestampFinal());	
		doc.append(KEY_IS_STABLE,  String.format("%s",this.isStable));

//		doc.append(KEY_TOTAL_SYNTHESIS,  String.format("%s", ""+this.getTotalSynthesisGenerated()));
//		doc.append(KEY_DISCARTED_SYNTHESIS,  String.format("%s", ""+this.getSynthesisDiscarted()));
//		doc.append(KEY_OLD_SYNTHESIS, String.format("%s", ""+this.getOldSynthesis()));
		
		doc.append(KEY_INTERVAL_TYPE, String.format("%s", ""+this.getIntervalType()));
		doc.append(KEY_INTERVAL_VALUE, String.format("%s", ""+this.getIntervalValue()));

		doc.append(KEY_EXPERIMENT_NUMBER, String.format("%s", ""+this.getExperimentNumber()));
		doc.append(KEY_EXPERIMENT_DESCRIPTION, String.format("%s", ""+this.getExperimentDescription()));
		doc.append(KEY_SUM_DATA_STABLE_ID, this.stableSummarisedData != null ? String.format("%s",this.stableSummarisedData.getId()) : "");	
		doc.append(KEY_SUM_DATA_PREVIOUS_ID,  this.previousSummarisedData != null ? String.format("%s", this.previousSummarisedData.getId()) : "");	

		doc.append(KEY_NEW_RECORDS, String.format("%s", this.getNumberOfNewData()));
		doc.append(KEY_TOTAL_RECORDS, String.format("%s", this.getNumberOfData()));




		// Distanza da stabile (bestMatchFromStable)
		doc.append(KEY_DIST_CENTROIDS_STAB,  String.format("%s",this.bestMetchStab.getDiffCentroids()));
		doc.append(KEY_DIST_RADIUS_STAB,  String.format("%s",this.bestMetchStab.getDiffRadius()));

		doc.append(KEY_DIST_CENTROIDS_PREV,  String.format("%s",this.bestMetchPrev.getDiffCentroids()));
		doc.append(KEY_DIST_RADIUS_PREV,  String.format("%s",this.bestMetchPrev.getDiffRadius()));

/*		if(this.stableSummarisedData != null) {
			this.bestMetchStab = this.synthesesBestMetch(this.stableSummarisedData);

		}
		if(this.previousSummarisedData != null) {
			this.bestMetchPrev = this.synthesesBestMetch(this.previousSummarisedData);
			doc.append(KEY_DIST_DENSITY_PREV,  String.format("%s",this.bestMetchPrev.getDiffDensity()));
			doc.append(KEY_DIST_RADIUS_VAR_PREV,  String.format("%s",this.bestMetchPrev.getDiffRadiusVariation()));
			doc.append(KEY_DIST_DENSITY_VAR_PREV,  String.format("%s",this.bestMetchPrev.getDiffDensityVariation()));
		}
		*/
		return doc;
	}

	/**
	 * Metodo per la trasformazione di un oggetto Measure in un elemento di tipo @Document
	 * @return document documento generato dalla misura
	 */
	public JSONObject toJSON(){
		JSONObject jsonSummarizedData = toJSONCommon();
		jsonSummarizedData.put(KEY_STATUS, this.getStatus());
		jsonSummarizedData.put(KEY_TIMESTAMP, this.getTimestamp().toString());	
		jsonSummarizedData.put(KEY_TIMESTAMP_INITIAL, this.getTimestampInitial().toString());	
		jsonSummarizedData.put(KEY_TIMESTAMP_FINAL, this.getTimestampFinal().toString());	
		jsonSummarizedData.put(KEY_IS_STABLE,  String.format("%s",this.isStable));

//		jsonSummarizedData.put(KEY_TOTAL_SYNTHESIS,  String.format("%s", ""+this.getTotalSynthesisGenerated()));
//		jsonSummarizedData.put(KEY_DISCARTED_SYNTHESIS,  String.format("%s", ""+this.getSynthesisDiscarted()));
//		jsonSummarizedData.put(KEY_OLD_SYNTHESIS, String.format("%s", ""+this.getOldSynthesis()));

		jsonSummarizedData.put(KEY_NEW_RECORDS, String.format("%s", this.getNumberOfNewData()));
		jsonSummarizedData.put(KEY_TOTAL_RECORDS, String.format("%s", this.getNumberOfData()));
		
		jsonSummarizedData.put(KEY_INTERVAL_TYPE, String.format("%s", ""+this.getIntervalType()));
		jsonSummarizedData.put(KEY_INTERVAL_VALUE, String.format("%s", ""+this.getIntervalValue()));
		jsonSummarizedData.put(KEY_EXPERIMENT_NUMBER, String.format("%s", ""+this.getExperimentNumber()));
		jsonSummarizedData.put(KEY_EXPERIMENT_DESCRIPTION, String.format("%s", ""+this.getExperimentDescription()));

		jsonSummarizedData.put(KEY_SUM_DATA_STABLE_ID, this.stableSummarisedData != null ? String.format("%s",this.stableSummarisedData.getId()) : "");	
		jsonSummarizedData.put(KEY_SUM_DATA_PREVIOUS_ID,  this.previousSummarisedData != null ? String.format("%s", this.previousSummarisedData.getId()) : "");
		
	//	this.bestMetchStab = this.synthesesBestMetch(this.stableSummarisedData);

		jsonSummarizedData.put(KEY_DIST_CENTROIDS_STAB,  String.format("%s",this.bestMetchStab.getDiffCentroids()));
		jsonSummarizedData.put(KEY_DIST_RADIUS_STAB,  String.format("%s",this.bestMetchStab.getDiffRadius()));
		jsonSummarizedData.put(KEY_DIST_DENSITY_STAB,  String.format("%s",this.bestMetchStab.getDiffDensity()));
		
	//	this.bestMetchPrev = this.synthesesBestMetch(this.previousSummarisedData);
		jsonSummarizedData.put(KEY_DIST_CENTROIDS_PREV,  String.format("%s",this.bestMetchPrev.getDiffCentroids()));
		jsonSummarizedData.put(KEY_DIST_RADIUS_PREV,  String.format("%s",this.bestMetchPrev.getDiffRadius()));
		jsonSummarizedData.put(KEY_DIST_DENSITY_PREV,  String.format("%s",this.bestMetchPrev.getDiffDensity()));
		return jsonSummarizedData;
	}
	
	
	public void setFromDocument(Document doc) throws NumberFormatException{
		this.setId(doc.get(KEY_ID).toString());
		this.setTimestamp((Date) doc.get(KEY_TIMESTAMP));
		this.setTimestampInitial((Date) doc.get(KEY_TIMESTAMP_INITIAL));
		this.setTimestampFinal((Date) doc.get(KEY_TIMESTAMP_FINAL));
		this.setStable(doc.get(KEY_IS_STABLE).toString().equalsIgnoreCase("true") ? true:false);
//		this.setTotalSynthesisGenerated(Integer.parseInt(doc.get(KEY_TOTAL_SYNTHESIS).toString()));
//		this.setSynthesisDiscarted(Integer.parseInt(doc.get(KEY_DISCARTED_SYNTHESIS).toString()));
//		this.setOldSynthesis(Integer.parseInt(doc.get(KEY_OLD_SYNTHESIS).toString()));


		this.setExperimentNumber(Integer.parseInt(doc.get(KEY_EXPERIMENT_NUMBER).toString()));
		this.setIntervalType(doc.get(KEY_INTERVAL_TYPE).toString());
		this.setIntervalValue(Integer.parseInt(doc.get(KEY_INTERVAL_VALUE).toString()));

		//TODO capire se è corretto
		this.setFeatureSpace(new FeatureSpace((Document) doc.get(KEY_FEATURE_SPACE)));


		this.setDimensionInstancesFromDocument((Collection<Document>) doc.get(KEY_DIMENSIONS));
		this.setSynthesisFromDocument((Collection<Document>) doc.get(KEY_SYNTHESIS));

		if(doc.get(KEY_SUM_DATA_STABLE_ID) != null){
			this.setStableSummarisedData(new SummarisedData());
			this.stableSummarisedData.setId(doc.get(KEY_SUM_DATA_STABLE_ID).toString());
			System.out.println(doc.get(KEY_SUM_DATA_STABLE_ID).toString());
			this.id_stable = doc.get(KEY_SUM_DATA_STABLE_ID).toString();
		}
		
		
		if(doc.get(KEY_SUM_DATA_PREVIOUS_ID) != null){
			this.setPreviousSummarisedData(new SummarisedData());
			this.previousSummarisedData.setId(doc.get(KEY_SUM_DATA_PREVIOUS_ID).toString());
//			System.out.println(this.previousSummarisedData.getId());

			this.id_prev = doc.get(KEY_SUM_DATA_PREVIOUS_ID).toString();
		}
	}

	/**
	 * @param dimensionInstancesDocument
	 */
	private void setDimensionInstancesFromDocument(Collection<Document> dimensionInstancesDocument){
		for (Document dimensionInstanceDocument: dimensionInstancesDocument) {
			this.addDimensionInstance(new DimensionInstance(dimensionInstanceDocument));
		}
	}

	/**
	 * @param synthesisDocument
	 */
	private void setSynthesisFromDocument(Collection<Document> synthesisDocument){
		for (Document synthesiDocument : synthesisDocument) {
			this.addSynthesi(new Synthesi(synthesiDocument));
			//System.out.println("adding");
		}
//		System.out.println("size: "+this.synthesis.entrySet().size());
	}


	/**
	 * @param clustersDocument
	 */
	private void setClustersFromDocument(Collection<Document> clustersDocument){
		for (Document clusterDocument : clustersDocument) {
			this.addCluster(new Cluster(clusterDocument));
		}
	}

	//
	//	/**
	//	 * Metodo per trasformare la prima parte di data summarisation in json
	//	 * @return JSONObject first part of data summarisation json
	//	 */
	//	public JSONObject toJSONCommon() {
	//		JSONObject jsonSummarizedData = new JSONObject();
	//		JSONArray jsonDimensionInstances = new JSONArray();
	//		JSONArray jsonClusters = new JSONArray();
	//		JSONArray jsonSynthesis = new JSONArray();
	//		JSONArray jsonInputData = new JSONArray();
	//		
	//		for (Cluster cluster : this.getClusters()) {
	//			jsonClusters.put(cluster.toJSON());
	//		}
	//		jsonSummarizedData.put(KEY_CLUSTERS, jsonClusters);
	//
	//		//TODO: da togliere finiti i test sul clustering
	//		for (Synthesi synthesi : this.getSynthesis()) {
	//			jsonSynthesis.put(synthesi.toJSON());
	//		}
	//		jsonSummarizedData.put(KEY_SYNTHESIS, jsonSynthesis);
	//		
	////		System.out.println("Num Input Data: "+this.getInputData().size());
	//		for (MultidimensionalRecord md: this.getInputData()) {
	//			jsonInputData.put(md.toJSON());
	//		}
	//		jsonSummarizedData.put(KEY_INPUT_DATA, jsonInputData);
	//		
	//		jsonSummarizedData.put(KEY_CONTEXTS, this.getContext().toJSON());
	//
	//		for (DimensionInstance dimensionInstance : this.getDimensionInstances()) {
	//			jsonDimensionInstances.put(dimensionInstance.toJSON());
	//		}
	//
	//		jsonSummarizedData.put(KEY_DIMENSIONS, jsonDimensionInstances);
	//		
	//		return jsonSummarizedData;
	//	}
	//	
	//
	//
	//	/**
	//	 * Metodo per la trasformazione di un oggetto Measure in un elemento di tipo @Document
	//	 * @return document documento generato dalla misura
	//	 */
	//	public Document toDocument(){
	//		Document doc = Document.parse( this.toJSONCommon().toString() );
	//		doc.append(KEY_STATUS, this.getStatus());
	//		doc.append(KEY_TIMESTAMP, this.getTimestamp());	
	//		doc.append(KEY_TIMESTAMP_INITIAL, this.getTimestampInitial());	
	//		doc.append(KEY_IS_STABLE,  String.format("%s",this.isStable));
	//		doc.append(KEY_FEATURE_SPACE_ID, String.format("%s", ""+this.getFeatureSpace().getId()));
	//		doc.append(KEY_RECORD_NUMBER_NEW,  String.format("%s", ""+this.getNumberOfNewData()));
	//		doc.append(KEY_RECORD_NUMBER_TOTAL,  String.format("%s", ""+this.getNumberOfData()));
	//		doc.append(KEY_TOTAL_SYNTHESIS,  String.format("%s", ""+this.getTotalSynthesisGenerated()));
	//		doc.append(KEY_DISCARTED_SYNTHESIS,  String.format("%s", ""+this.getSynthesisDiscarted()));
	//		doc.append(KEY_OLD_SYNTHESIS, String.format("%s", ""+this.getOldSynthesis()));
	//		doc.append(KEY_INTERVAL_TYPE, String.format("%s", ""+this.getIntervalType()));
	//		doc.append(KEY_INTERVAL_VALUE, String.format("%s", ""+this.getIntervalValue()));
	//
	//		doc.append(KEY_CLUSTERING_TIME, String.format("%s", ""+this.getTimeClustering()));
	//		doc.append(KEY_EXTRACTION_TIME, String.format("%s", ""+this.getTimeExtraction()));
	//		doc.append(KEY_TOTAL_TIME, String.format("%s", ""+this.getTimeTotal()));
	//		
	//		doc.append(KEY_EXPERIMENT_NUMBER, String.format("%s", ""+this.getExperimentNumber()));
	//
	//		doc.append(KEY_SUM_DATA_STABLE_ID, this.stableSummarisedData != null ? String.format("%s",this.stableSummarisedData.getId()) : "");	
	//		doc.append(KEY_SUM_DATA_PREVIOUS_ID,  this.previousSummarisedData != null ? String.format("%s", this.previousSummarisedData.getId()) : "");	
	//		
	//		return doc;
	//	}
	//	
	//	/**
	//	 * Metodo per la trasformazione di un oggetto Measure in un elemento di tipo @Document
	//	 * @return document documento generato dalla misura
	//	 */
	//	public JSONObject toJSON(){
	//		JSONObject jsonSummarizedData = toJSONCommon();
	//		jsonSummarizedData.put(KEY_STATUS, this.getStatus());
	//		jsonSummarizedData.put(KEY_TIMESTAMP, this.getTimestamp().toString());	
	//		jsonSummarizedData.put(KEY_TIMESTAMP_INITIAL, this.getTimestampInitial().toString());	
	//		jsonSummarizedData.put(KEY_TIMESTAMP_FINAL, this.getTimestampFinal().toString());	
	//		jsonSummarizedData.put(KEY_IS_STABLE,  String.format("%s",this.isStable));
	//		jsonSummarizedData.put(KEY_FEATURE_SPACE_ID, String.format("%s", ""+this.getFeatureSpace().getId()));
	//		jsonSummarizedData.put(KEY_RECORD_NUMBER_NEW,  String.format("%s", ""+this.getNumberOfNewData()));
	//		jsonSummarizedData.put(KEY_RECORD_NUMBER_TOTAL,  String.format("%s", ""+this.getNumberOfData()));
	//		jsonSummarizedData.put(KEY_TOTAL_SYNTHESIS,  String.format("%s", ""+this.getTotalSynthesisGenerated()));
	//		jsonSummarizedData.put(KEY_DISCARTED_SYNTHESIS,  String.format("%s", ""+this.getSynthesisDiscarted()));
	//		jsonSummarizedData.put(KEY_OLD_SYNTHESIS, String.format("%s", ""+this.getOldSynthesis()));
	//		jsonSummarizedData.put(KEY_INTERVAL_TYPE, String.format("%s", ""+this.getIntervalType()));
	//		jsonSummarizedData.put(KEY_INTERVAL_VALUE, String.format("%s", ""+this.getIntervalValue()));
	//
	//		jsonSummarizedData.put(KEY_CLUSTERING_TIME, String.format("%s", ""+this.getTimeClustering()));
	//		jsonSummarizedData.put(KEY_EXTRACTION_TIME, String.format("%s", ""+this.getTimeExtraction()));
	//		jsonSummarizedData.put(KEY_TOTAL_TIME, String.format("%s", ""+this.getTimeTotal()));
	//
	//		jsonSummarizedData.put(KEY_EXPERIMENT_NUMBER, String.format("%s", ""+this.getExperimentNumber()));
	//
	//		jsonSummarizedData.put(KEY_SUM_DATA_STABLE_ID, this.stableSummarisedData != null ? String.format("%s",this.stableSummarisedData.getId()) : "");	
	//		jsonSummarizedData.put(KEY_SUM_DATA_PREVIOUS_ID,  this.previousSummarisedData != null ? String.format("%s", this.previousSummarisedData.getId()) : "");
	//		
	//		return jsonSummarizedData;
	//	}
	//
	//	public void setFromDocument(Document doc) throws NumberFormatException{
	//		this.setId(doc.get(KEY_ID).toString());
	//		this.setTimestamp((Date) doc.get(KEY_TIMESTAMP));
	//		this.setTimestampInitial((Date) doc.get(KEY_TIMESTAMP_INITIAL));
	//		this.setTimestampFinal((Date) doc.get(KEY_TIMESTAMP_FINAL));
	//		this.setFeatureSpace(new FeatureSpace(Integer.parseInt(doc.get(KEY_FEATURE_SPACE_ID).toString())));
	//		this.setStable(doc.get(KEY_IS_STABLE).toString().equalsIgnoreCase("true") ? true:false);
	//		this.setTotalSynthesisGenerated(Integer.parseInt(doc.get(KEY_TOTAL_SYNTHESIS).toString()));
	//		this.setSynthesisDiscarted(Integer.parseInt(doc.get(KEY_DISCARTED_SYNTHESIS).toString()));
	//		this.setOldSynthesis(Integer.parseInt(doc.get(KEY_OLD_SYNTHESIS).toString()));
	//
	//		this.setNumberOfData(Integer.parseInt(doc.get(KEY_RECORD_NUMBER_TOTAL).toString()));
	//		this.setNumberOfNewData(Integer.parseInt(doc.get(KEY_RECORD_NUMBER_NEW).toString()));
	//		
	//		this.setTimeClustering(Double.parseDouble(doc.get(KEY_CLUSTERING_TIME).toString()));
	//		this.setTimeExtraction(Double.parseDouble(doc.get(KEY_EXTRACTION_TIME).toString()));
	//		this.setTimeTotal(Double.parseDouble(doc.get(KEY_TOTAL_TIME).toString()));
	//
	//		this.setExperimentNumber(Integer.parseInt(doc.get(KEY_EXPERIMENT_NUMBER).toString()));
	//		this.setIntervalType(doc.get(KEY_INTERVAL_TYPE).toString());
	//		this.setIntervalValue(Integer.parseInt(doc.get(KEY_INTERVAL_VALUE).toString()));
	//
	//		this.setDimensionInstancesFromDocument((Collection<Document>) doc.get(KEY_DIMENSIONS));
	//		this.setContext(new Context((Document) doc.get(KEY_CONTEXTS)));
	//		this.setClustersFromDocument((Collection<Document>) doc.get(KEY_CLUSTERS));
	//		this.setSynthesisFromDocument((Collection<Document>) doc.get(KEY_SYNTHESIS));
	//		
	//		if(doc.get(KEY_SUM_DATA_STABLE_ID) != null){
	//			this.setStableSummarisedData(new SummarisedData());
	//			this.stableSummarisedData.setId(doc.get(KEY_SUM_DATA_STABLE_ID).toString());
	//		}
	//		if(doc.get(KEY_SUM_DATA_PREVIOUS_ID) != null){
	//			this.setPreviousSummarisedData(new SummarisedData());
	//			this.previousSummarisedData.setId(doc.get(KEY_SUM_DATA_PREVIOUS_ID).toString());
	//		}
	//	}
	//
	//	/**
	//	 * @param dimensionInstancesDocument
	//	 */
	//	private void setDimensionInstancesFromDocument(Collection<Document> dimensionInstancesDocument){
	//		for (Document dimensionInstanceDocument: dimensionInstancesDocument) {
	//			this.addDimensionInstance(new DimensionInstance(dimensionInstanceDocument));
	//		}
	//	}
	//
	//	/**
	//	 * @param synthesisDocument
	//	 */
	//	private void setSynthesisFromDocument(Collection<Document> synthesisDocument){
	//		for (Document synthesiDocument : synthesisDocument) {
	//			this.addSynthesi(new Synthesi(synthesiDocument));
	//		}
	//	}
	//
	//	/**
	//	 * @param clustersDocument
	//	 */
	//	private void setClustersFromDocument(Collection<Document> clustersDocument){
	//		for (Document clusterDocument : clustersDocument) {
	//			this.addCluster(new Cluster(clusterDocument));
	//		}
	//	}
	//	

	/**
	 * 
	 */
	public void printSynthesis(){
		//		System.out.println("Numero Dati: "+ this.getNumberOfData());
		//		
		//		System.out.println("Num Sintesi: "+this.getSynthesis().size());
		//		System.out.println("Sintesi Scartate: "+ this.getSynthesisDiscarted());
		//		for(Entry<Integer, Synthesi> synthesi : this.getSynthesis().entrySet()) {
		//				System.out.println("----> Num Dati: " + synthesi.getValue().getNumberData());
		//			System.out.println("      -> SS: " + String.format("%.2f", s.getSS()));
		//			System.out.println("      -> raggio: " + String.format("%.2f", s.getRadius()));
		//			for(SynthesiElement se : synthesi.getValue().getSynthesiElements()){
		//				System.out.println("      -> " + se.getFeature().getName() + ": " + String.format("%.2f", se.getCenterValue()) + " LS: " + String.format("%.2f", se.getLs()) + " SS: " + String.format("%.2f", se.getSs()));
		//			}
		//		}
		//		System.out.println("numSynthesis: "+synthesis.size());

		for(Entry<Integer, Synthesi> entry : this.getSynthesis().entrySet()) {
			//		for (Synthesi s: synthesis) {
			String multidimRec = "";
			boolean first = true;
			multidimRec += "ID: "+entry.getValue().getId() + "   R: " + String.format("%.2f", entry.getValue().getRadius()) + "    N: " + entry.getValue().getNumberData() + "   parentID: " + entry.getValue().getParentIdList().toString() + "   parentHistoryID: " + entry.getValue().getParentHistoryId().toString() + "        center: ";


			for(Entry<Integer, SynthesiElement> synthesiElement : entry.getValue().getSynthesiElements().entrySet()) {
				if (first) {
					multidimRec += String.format("(%.2f", synthesiElement.getValue().getCenterValue());
					first = false;
				}else {
					multidimRec += "; "+String.format("%.2f", synthesiElement.getValue().getCenterValue());
				}
			}

			multidimRec += ")"; 
			System.out.println(multidimRec);
		}
	}

	public void printClusters(){
		System.out.println("Num Clusters: "+this.getClusters().size());
		for(Cluster c: this.getClusters()){
			System.out.println("----> Cluster ID: "+ c.getId());
			for(ClusterElement ce : c.getClusterElements()){
				System.out.println("      -> " + ce.getFeature().getName() + ": " + String.format("%.2f", ce.getCenterValue()));
			}
		}
	}


	public SummarisedData generateCopy() {
		SummarisedData sumData = new SummarisedData();
		Document doc = this.toDocument();
		doc.append(KEY_ID, this.getId());
		
		sumData.setFromDocument(doc);
		return sumData;
	}
}
