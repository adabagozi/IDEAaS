package it.unibs.bodai.ideaas.data_summarisation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.FeatureData;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecord;
import it.unibs.bodai.ideaas.dao.model.MultidimensionalRecordMap;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;

/**
 * 
 * @author Ada Bagozi
 *
 */
public interface IDataSummarisation {
	public SummarisedData runClustering(SummarisedData previousSummarisedData, Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end) throws Exception;
	
	public SummarisedData runClusteringExcludeZeros(boolean excludeZeros, SummarisedData previousSummarisedData, Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end) throws Exception;
	
	public SummarisedData runClusteringTests(SummarisedData previousSummarisedData, Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end, String cluStreamIdentificator, String distanceIdentificator) throws Exception;
	
	/**
	 * TODO trasformare in implementazione
	 * @param previousSummarisedData
	 * @param inputData
	 * @param featureSpace
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public SummarisedData runClusteringMOAImpl(SummarisedData previousSummarisedData, Collection<MultidimensionalRecord> inputData, FeatureSpace featureSpace, Date start, Date end) throws Exception;
	
//	/**
//	 * @author Daniele Comincini
//	 *
//	 */
//	
//	/**
//	 * Metodo che effettua una serie di test creando dei punti multidimensionali con le caratteristiche 
//	 * specificate e calcola quale di questi sia il più vicono ad un altro punto multidimensionale creato
//	 * parallelizzando sulle sintesi con Spark
//	 * @param dao
//	 * @param idTest
//	 * @param numTests
//	 * @param syntheses
//	 * @param newPointFeatures
//	 */
//	
//	public void runSparkParallelSynthFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures);
//	/**
//	 * Metodo che effettua una serie di test creando dei punti multidimensionali con le caratteristiche 
//	 * specificate e calcola quale di questi sia il più vicono ad un altro punto multidimensionale creato
//	 * parallelizzando sulle dimensioni con Spark
//	 * @param dao
//	 * @param idTest
//	 * @param numTests
//	 * @param syntheses
//	 * @param newPointFeatures
//	 */
//	
//	public void runSparkParallelDimFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures);
//	
//	/**
//	 * Metodo che effettua una serie di test creando dei punti multidimensionali con le caratteristiche 
//	 * specificate e calcola quale di questi sia il più vicono ad un altro punto multidimensionale creato
//	 * parallelizzando sulle sintesi 
//	 * @param dao
//	 * @param idTest
//	 * @param numTests
//	 * @param syntheses
//	 * @param newPointFeatures
//	 */
//	
//	public void runParallelSynthFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures);
//	
//	/**
//	 * Metodo che effettua una serie di test creando dei punti multidimensionali con le caratteristiche 
//	 * specificate e calcola quale di questi sia il più vicono ad un altro punto multidimensionale creato
//	 * parallelizzando sulle dimensioni
//	 * @param dao
//	 * @param idTest
//	 * @param numTests
//	 * @param syntheses
//	 * @param newPointFeatures
//	 */
//	
//	public void runParallelDimFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures);
//	
//	/**
//	 * Metodo che effettua una serie di test creando dei punti multidimensionali con le caratteristiche 
//	 * specificate e calcola quale di questi sia il più vicono ad un altro punto multidimensionale creato
//	 * parallelizzando sulle dimensioni e sulle sintesi
//	 * @param dao
//	 * @param idTest
//	 * @param numTests
//	 * @param syntheses
//	 * @param newPointFeatures
//	 */
//	
//	public void runParallelSynthDimFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures);
//	
//	/**
//	 * Metodo che effettua una serie di test creando dei punti multidimensionali con le caratteristiche 
//	 * specificate e calcola quale di questi sia il più vicono ad un altro punto multidimensionale creato
//	 * in modo seriale
//	 * @param dao
//	 * @param idTest
//	 * @param numTests
//	 * @param syntheses
//	 * @param newPointFeatures
//	 */
//	
//	public void runSerialFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecord> syntheses, Collection<FeatureData> newPointFeatures);
//	
//	/**
//	 * Metodo che effettua una serie di test creando dei punti multidimensionali con le caratteristiche 
//	 * specificate e calcola quale di questi sia il più vicono ad un altro punto multidimensionale creato
//	 * in modo seriale usando hashmap
//	 * @param dao
//	 * @param idTest
//	 * @param numTests
//	 * @param syntheses
//	 * @param newPointFeatures
//	 */
//
//	public void runSerialHashMapFindNearestSyntheses(IDAO dao, int idTest, int numTests, Collection<MultidimensionalRecordMap> syntheses, HashMap<Integer,FeatureData> newPointFeatures);
}
