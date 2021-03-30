package it.unibs.bodai.ideaas.data_summarisation;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.data_summarisation.implementation.DataSummarisationIDEAaSImplementation;
/**
 * 
 * @author Ada Bagozi
 *
 */
public class DataSummarisationFactory {

	private static final Logger LOG = Logger.getLogger(DataSummarisationFactory.class.getName());
	
	
	private static final DataSummarisationFactory factory = new DataSummarisationFactory();
	
	private static Optional<IDataSummarisation> dataSummarizationInstance = Optional.empty(); 


	private DataSummarisationFactory() {
		// TODO Auto-generated constructor stub
	}
	public static final DataSummarisationFactory getDataSummarisationFactory(){
		return factory;
	}

	public IDataSummarisation getDataSummarisation() throws IOException {
//		if (!dataSummarizationInstance.isPresent()) {
//			dataSummarizationInstance = Optional.of(new DataSummarisationIDEAaSImplementation());
//		}
		return new DataSummarisationIDEAaSImplementation();
	}
	
//	public IDataSummarisation getDataSummarisationCluStream() throws IOException {
//		if (!dataSummarizationInstance.isPresent()) {
//			dataSummarizationInstance = Optional.of(new DataSummarisationCluStreamImplementation());
//		}
//		return dataSummarizationInstance.get();
//	}
}
