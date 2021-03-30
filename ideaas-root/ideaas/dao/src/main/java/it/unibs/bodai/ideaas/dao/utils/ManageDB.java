/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibs.bodai.ideaas.dao.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

import org.apache.commons.dbcp2.BasicDataSource;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Classe per la gestione delle connessioni ai database.
 *
 * <br><br>Nel progetto BODAI sono presenti <strong>3</strong> database:
 * <ul>
 * <li><strong>mongoDB</strong> - Database destinato al salvataggio dei dati considerati big data, si tratta di un database noSql.</li>
 * <li><strong>bodai_metamodel</strong> - Database destinato al salvataggio e manipolazione del metamodello dei dati. Attraverso il db bodai_metamodel verrà generato il db bodai_model</li>
 * <li><strong>bodai_model</strong> - Database Database destinato al salvataggio e alla manipolazione del modello dei dati.</li>
 * </ul>
 * @author Ada
 */

public class ManageDB {
	//TODO separare anche il magage db a seconda dell'implementazione
	/** Variabile per l'impostazione dei dati per la connessione al database bodai_model */
	private BasicDataSource dataSourceModelDB;
	/** Variabile per l'impostazione dei dati per la connessione al database bodai_metamodel */
	private BasicDataSource dataSourceMetamodelDB;

	private String bigDataServer;
	private int bigDataPort;
	private String bigDataDBName;
	private String bigDataUser;
	private String bigDataPassword;

	private String configFileName;
    private Configuration configuration;
    private String error;

    /**
     * Costrutture della classe
     * @param configFileName nome del file xml nel quale sono stati salvati i dati per le connessioni
     * @throws IOException
     */
    public ManageDB(String configFileName) throws IOException{
        this.configFileName = configFileName;
        this.error      = "";


        IConfigProvider cp = new XMLCongifProvider(this.configFileName);
        this.configuration = cp.fetchConfig();

        this.dataSourceModelDB = this.getDataSource(
				this.configuration.getServerHostnameModelDB(),
                this.configuration.getServerPortModelDB(),
                this.configuration.getMysqlModelDBName(),
                this.configuration.getMysqlModelUsername(),
                this.configuration.getMysqlModelPassword());


        System.out.println(this.configuration.getMysqlModelDBName());
        System.out.println(this.configuration.getMysqlModelUsername());
        System.out.println(this.configuration.getMysqlModelPassword());
        
        this.dataSourceMetamodelDB = this.getDataSource(
                this.configuration.getServerHostnameMetamodelDB(),
                this.configuration.getServerPortMetamodelDB(),
                this.configuration.getMysqlMetamodelDBName(),
                this.configuration.getMysqlMetamodelUsername(),
                this.configuration.getMysqlMetamodelPassword());

        this.bigDataServer = this.configuration.getServerHostnameMongoDB();
        this.bigDataPort = this.configuration.getServerPortMongoDB();
        this.bigDataDBName = this.configuration.getMongoDBName();
        this.bigDataUser = this.configuration.getMongoUsername();
        this.bigDataPassword = this.configuration.getMongoPassword();
    }


    /**
     * @param host nome dell'host sul quale risiedono il database (solitamente localhost, nulla vieta che sia inserito un host remoto)
     * @param port porta dell'host attraverso la quale sarà possibile effettuare la connessione (solitamente si tratta della 22)
     * @param dbName nome del database per l'apertura della connessione
     * @param username nome utente autorizzato alla connessione
     * @param password password di autenticazione dell'utente
     * @return la variabile datasource inizializzata con i diversi parametri di input
     */
    private BasicDataSource getDataSource(String host, int port, String dbName, String username, String password) {
        BasicDataSource ds = new BasicDataSource();

        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
        // String url = String.format("jdbc:mysql://%s/%s", host, dbName);
        // System.out.println(url);


        String url = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, dbName);
        System.out.println(url); System.out.println(dbName);
        ds.setUrl(url);

        ds.setUsername(username);
        ds.setPassword(password);

        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);

        return ds;
    }

    /**
     * Metodo per l'appertura della connessione al database bodai_model
     * @return La connessione al database bodai_model aperta
     * @throws SQLException
     */
    public Connection connectMySqlModelDB() throws SQLException {
        Connection connection = null;
        try {
            // get JDBC driver for connection with the db
            connection = dataSourceModelDB.getConnection();
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
        }
        return connection;
    }



    /**
     * Metodo per l'appertura della connessione al database bodai_metamodel
     * @return La connessione al database bodai_metamodel aperta
     * @throws SQLException
     */
    public Connection connectMySqlMetamodelDB() throws SQLException {
        Connection connection = null;
        try {
            // get JDBC driver for connection with the db
            connection = dataSourceMetamodelDB.getConnection();
        }catch (Exception e){
            connection.rollback();
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Metodo per chiudere la connessione aperta
     * @param connection connessione al database da chiudere
     */
    public void disconectMySQL(Connection connection){
        try {
            connection.close();
        } catch (Exception e) {
            error =  String.format("Impossible to disconnect: %s", e);
            System.out.println(error);
        }
    }


    /**
     * TODO vedere se possibile toglierlo
     * Metodo della connessione al mongoDB
     * TODO aggiungere throw exception e rendere questa connessione simile a quelle precedenti.
     * @param DBName nome del database a cui connettersi
     * @return
     */
	public MongoDatabase connectToMongo() {

    		MongoClient client = new MongoClient(this.bigDataServer, this.bigDataPort);
	    	MongoDatabase database = client.getDatabase(bigDataDBName);
	        return database;
    }



    /**
	 * @return the bigDataDBName
	 */
	public String getBigDataDBName() {
		return bigDataDBName;
	}



	/**
	 * @return the bigDataServer
	 */
	public String getBigDataServer() {
		return bigDataServer;
	}





	/**
	 * @return the bgDataPort
	 */
	public int getBigDataPort() {
		return bigDataPort;
	}



	/**
     * Metodo che ritorna gli errori avvenuti
     * @return error errori avvenuti nella classe
     */
    @SuppressWarnings("unused")
	private String getError() {
        return error;
    }



    /**
     * Metodo per settare gli errori
     * @param error errore da settare
     */
    @SuppressWarnings("unused")
	private void setError(String error) {
        this.error = error;
    }
}
