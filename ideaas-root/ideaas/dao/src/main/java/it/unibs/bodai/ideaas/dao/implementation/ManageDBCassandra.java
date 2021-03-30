package it.unibs.bodai.ideaas.dao.implementation;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Classe per la gestione della connessione e comunicazione con il DB Cassandra
 * @author Gianmaria Micheli
 *
 */
public class ManageDBCassandra {	
	private String ip;
	private String keyspace = null;
	private Cluster cluster;
	private Session session;
	
	/**
	 * per quando si deve ancora creare il keyspace
	 * @param ip
	 */
	public ManageDBCassandra(String ip)
	{
		this.ip = ip;
	}
	
	/**
	 * per quando il keyspace è stato già creato
	 * @param ip
	 * @param keyspace
	 */
	public ManageDBCassandra(String ip, String keyspace)
	{
		this.ip = ip;
		this.keyspace = keyspace;
	}
	
	/**
	 * per connettere quando il keyspace è stato creato
	 * @param ip
	 * @param keyspace
	 * @param open se aprire o no la connessione
	 */
	public ManageDBCassandra(String ip, String keyspace, boolean open)
	{
		this.ip = ip;
		this.keyspace = keyspace;
		if(open)
		{
			this.cluster = Cluster.builder()                                                    
		            .addContactPoint(this.ip)
		            .build();
		}
		this.session = this.cluster.connect(this.keyspace);
	}
	
	/* GESTIONE DELLA CONNESSIONE */
	
	/**
	 * apre la connessione con cassandra
	 * @return la sessione di connessione
	 */
	public Session openConnection()
	{
		this.cluster = Cluster.builder()                                                    
	            .addContactPoint(this.ip)
	            .build();
		
		if(keyspace != null)
			this.session = this.cluster.connect(this.keyspace);
		else
			this.session = this.cluster.connect();
		
//		this.measureCassandra = new MeasureCassandra(this.cluster, this.session, this.insertStatement);
//		this.measureCassandra.getPartitions();
		return session;
	}
	
	/**
	 * apre una sessione di connessione con cassandra, se non presente
	 */
	public void openSession()
	{
		if(this.session != null)
			this.session = this.cluster.connect(this.keyspace);
	}
	
	/**
	 * chiude la sessione di connessione con cassandra
	 */
	public void closeSession()
	{
		this.session.close();
	}
	
	/**
	 * chiude l'intera connessione, sia la sessione che la connessione generica con il cluster
	 */
	public void closeConnection()
	{
		this.session.close();
		this.cluster.close();
	}
	
	public Cluster getCluster()
	{
		return this.cluster;
	}

	public Session getSession() {
		return session;
	}
	
	
}
