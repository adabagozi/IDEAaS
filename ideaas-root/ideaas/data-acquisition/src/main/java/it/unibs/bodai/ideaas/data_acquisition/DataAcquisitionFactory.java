package it.unibs.bodai.ideaas.data_acquisition;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import it.unibs.bodai.ideaas.data_acquisition.implementation.DataAcquisitionImplementation;
import it.unibs.bodai.ideaas.data_acquisition.implementation.DataAcquisitionImplementationAnomaly;
import it.unibs.bodai.ideaas.data_acquisition.implementation.DataAcquisitionImplementationSmart4CPPS;
/**
 * 
 * @author Ada Bagozi
 *
 */
public class DataAcquisitionFactory {

	private static final Logger LOG = Logger.getLogger(DataAcquisitionFactory.class.getName());

	private static final DataAcquisitionFactory factory = new DataAcquisitionFactory();

	private static Optional<IDataAcquisition> dataAcquisitionInstance = Optional.empty(); 
	private static Optional<IDataAcquisition> dataAcquisitionAnomalyInstance = Optional.empty(); 


	private DataAcquisitionFactory() {
		// TODO Auto-generated constructor stub
	}
	public static final DataAcquisitionFactory getDataAcquisitionFactory(){
		return factory;
	}

	public IDataAcquisition getDataAquisition() throws IOException {
		if (!dataAcquisitionInstance.isPresent()) {
			dataAcquisitionInstance = Optional.of(new DataAcquisitionImplementation());
		}
		return dataAcquisitionInstance.get();
	}
	
	public IDataAcquisition getDataAquisitionAnomaly() throws IOException {
		if (!dataAcquisitionAnomalyInstance.isPresent()) {
			dataAcquisitionAnomalyInstance = Optional.of(new DataAcquisitionImplementationAnomaly());
		}
		return dataAcquisitionAnomalyInstance.get();
	}
	
	public IDataAcquisition getDataAquisitionSmart4CPPS() throws IOException {
		if (!dataAcquisitionAnomalyInstance.isPresent()) {
			dataAcquisitionAnomalyInstance = Optional.of(new DataAcquisitionImplementationSmart4CPPS() );
		}
		return dataAcquisitionAnomalyInstance.get();
	}
	
	//TODO
	public IDataAcquisition getDataAquisitionCovid19() throws IOException {
		if (!dataAcquisitionAnomalyInstance.isPresent()) {
			dataAcquisitionAnomalyInstance = Optional.of(new DataAcquisitionImplementationSmart4CPPS() );
		}
		return dataAcquisitionAnomalyInstance.get();
	}
}
