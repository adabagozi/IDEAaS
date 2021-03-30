package it.unibs.bodai.ideaas.dao;

import java.io.IOException;
import java.util.Optional;

import it.unibs.bodai.ideaas.dao.implementation.DAOImpl;
import it.unibs.bodai.ideaas.dao.implementation.DAOImplCassandra;

public class DAOFactory {
	private static final String CONFIG_FILENAME = "serverConfiguration.xml";
	private static final String CONFIG_FILENAME_SMART4CPPS = "serverConfigurationSmart4CPPS.xml";
	private static final String CONFIG_FILENAME_COVID = "serverConfigurationCovid19.xml";
    
    private static final DAOFactory factory = new DAOFactory();
    //TODO vengono generate due istanze diverse di Dao al fine di poter a confronto le implementazioni
    //TODO pensare ad un modo migliore per farlo
    private static IDAO daoInstance; 
    private static IDAO daoInstanceCassandra; 
    
	private DAOFactory() {
    }
    
    public static final DAOFactory getDAOFactory(){
    		return factory;
    }
    
    public IDAO getDAO() throws IOException {
//        if (daoInstance != null) {
    	//daoInstance = new DAOImpl(CONFIG_FILENAME);
    	daoInstance = new DAOImpl(CONFIG_FILENAME_SMART4CPPS);
    	//daoInstance = new DAOImpl(CONFIG_FILENAME_COVID);
        
//        }else {
//        	}

        return daoInstance;
    }
    
    public IDAO getDAOCassandra() throws IOException {
        if (daoInstanceCassandra != null) {
        		daoInstanceCassandra = new DAOImplCassandra(CONFIG_FILENAME);
        }

        return daoInstanceCassandra;
    }
}
