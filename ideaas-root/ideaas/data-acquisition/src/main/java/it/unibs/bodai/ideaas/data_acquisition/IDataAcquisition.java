package it.unibs.bodai.ideaas.data_acquisition;

import java.io.InputStream;
import java.util.Collection;

import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.ParameterInstance;

/**
 * 
 * @author Ada Bagozi
 *
 */
public interface IDataAcquisition {
	
	/**
	 * @param inputFile
	 * @param macchina
	 * @throws Exception
	 */
	public void saveMeasures(InputStream inputFile, String macchina) throws Exception;

	/**
	 * 
	 * Metodo per la generazione di misure in modo artificiale
	 * 
	 */
	public void generateMeasures(double mean, double std, int numMeasures, Collection<ParameterInstance> parameterInstances, Collection<DimensionInstance> dimensionInstances);

	
	/**
	 * Metodo che legge i file di input presenti in una cartella, genera le rispettive misure e le salva mongoDB
	 * @param folderPath
	 * @throws Exception
	 */
	public void generateMeasuresFromFilesInFolder(String directoryPath) throws Exception;


}
