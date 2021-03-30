package dao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;
import it.unibs.bodai.ideaas.dao.model.DimensionInstance;
import it.unibs.bodai.ideaas.dao.model.Feature;
import it.unibs.bodai.ideaas.dao.model.FeatureSpace;
import it.unibs.bodai.ideaas.dao.model.SummarisedData;
import utils.RequestHandler;

public class TestDao {
	private IDAO dao;

	@Before
	public void setup() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


//	@Test
	public void readSummarisedData() {

		DimensionInstance machineDimInstance = this.dao.readDimensionInstance("Macchina", "101170");
		System.out.println(machineDimInstance.getId());
		DimensionInstance spindleDimInstance = this.dao.readDimensionInstanceWithParentId("Componente",
				"Unit 1.0", machineDimInstance.getId());
		System.out.println(spindleDimInstance.getId());
		try {
			RequestHandler.callRemoteURL("http://bagozi.it/sendMail.php");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Optional<SummarisedData> summarisedDataById = this.dao.readSummarisedDataById("summariserd_data", "595945f90a9e3ea11ae6b22f");
//		if(summarisedDataById.isPresent()) {
//
//			System.out.println("Expertiment: "+summarisedDataById.get().getExperimentNumber());
//			
//		}else {
//			System.out.println("File non trovato: 595945f90a9e3ea11ae6b22f");
//		}
	}
	
	
	//@Test
	public void readFeatures() {
		Collection<Feature> features = this.dao.readFeatures();
		if(!features.isEmpty()) {
			for(Feature f: features) {
				
				for(FeatureSpace fs: f.getFeatureSpaces()) {
					System.out.println(f.getName() +" ---- "+ fs.getName());
				}
//				f.stampaFeature();
			}
			
		}else {
			System.out.println("Nessuna Feature Presente");
		}
	}
}
