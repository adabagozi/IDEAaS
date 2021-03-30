/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibs.bodai.ideaas.dao.utils;

/**
 *
 * @author Ada
 */
public class Configuration {
 
    private String serverHostnameMetamodelDB;
    private int serverPortMetamodelDB;
    private String mysqlMetamodelDBName;
    private String mysqlMetamodelUsername;
    private String mysqlMetamodelPassword;
    
	private String serverHostnameModelDB;
    private int serverPortModelDB;
    private String mysqlModelDBName;
    private String mysqlModelUsername;
    private String mysqlModelPassword;
    
    private String serverHostnameMongoDB;
    private int serverPortMongoDB;
    private String mongoDBName;
    private String mongoUsername;
    private String mongoPassword;

    
    public Configuration(String serverHostnameMetamodelDB, int serverPortMetamodelDB, String mysqlMetamodelDBName, String mysqlMetamodelUsername, String mysqlMetamodelPassword, String serverHostnameModelDB, int serverPortModelDB, String mysqlModelDBName, String mysqlModelUsername, String mysqlModelPassword, String serverHostnameMongoDB, int serverPortMongoDB, String mongoDBName, String mongoUsername, String mongoPassword) {
        this.serverHostnameMetamodelDB = serverHostnameMetamodelDB;
        this.serverPortMetamodelDB = serverPortMetamodelDB;
        this.mysqlMetamodelDBName = mysqlMetamodelDBName;
        this.mysqlMetamodelUsername = mysqlMetamodelUsername;
        this.mysqlMetamodelPassword = mysqlMetamodelPassword;
        this.serverHostnameModelDB = serverHostnameModelDB;
        this.serverPortModelDB = serverPortModelDB;
        this.mysqlModelDBName = mysqlModelDBName;
        this.mysqlModelUsername = mysqlModelUsername;
        this.mysqlModelPassword = mysqlModelPassword;
        this.serverHostnameMongoDB = serverHostnameMongoDB;
        this.serverPortMongoDB = serverPortMongoDB;
        this.mongoDBName = mongoDBName;
        this.mongoUsername = mongoUsername;
        this.mongoPassword = mongoPassword;
    }

    public Configuration() {
   
    }

	/**
	 * @return the serverHostnameMetamodelDB
	 */
	public String getServerHostnameMetamodelDB() {
		return serverHostnameMetamodelDB;
	}

	/**
	 * @param serverHostnameMetamodelDB the serverHostnameMetamodelDB to set
	 */
	public void setServerHostnameMetamodelDB(String serverHostnameMetamodelDB) {
		this.serverHostnameMetamodelDB = serverHostnameMetamodelDB;
	}

	/**
	 * @return the serverPortMetamodelDB
	 */
	public int getServerPortMetamodelDB() {
		return serverPortMetamodelDB;
	}

	/**
	 * @param serverPortMetamodelDB the serverPortMetamodelDB to set
	 */
	public void setServerPortMetamodelDB(int serverPortMetamodelDB) {
		this.serverPortMetamodelDB = serverPortMetamodelDB;
	}

	/**
	 * @return the mysqlMetamodelDBName
	 */
	public String getMysqlMetamodelDBName() {
		return mysqlMetamodelDBName;
	}

	/**
	 * @param mysqlMetamodelDBName the mysqlMetamodelDBName to set
	 */
	public void setMysqlMetamodelDBName(String mysqlMetamodelDBName) {
		this.mysqlMetamodelDBName = mysqlMetamodelDBName;
	}

	/**
	 * @return the mysqlMetamodelUsername
	 */
	public String getMysqlMetamodelUsername() {
		return mysqlMetamodelUsername;
	}

	/**
	 * @param mysqlMetamodelUsername the mysqlMetamodelUsername to set
	 */
	public void setMysqlMetamodelUsername(String mysqlMetamodelUsername) {
		this.mysqlMetamodelUsername = mysqlMetamodelUsername;
	}

	/**
	 * @return the mysqlMetamodelPassword
	 */
	public String getMysqlMetamodelPassword() {
		return mysqlMetamodelPassword;
	}

	/**
	 * @param mysqlMetamodelPassword the mysqlMetamodelPassword to set
	 */
	public void setMysqlMetamodelPassword(String mysqlMetamodelPassword) {
		this.mysqlMetamodelPassword = mysqlMetamodelPassword;
	}

	/**
	 * @return the serverHostnameModelDB
	 */
	public String getServerHostnameModelDB() {
		return serverHostnameModelDB;
	}

	/**
	 * @param serverHostnameModelDB the serverHostnameModelDB to set
	 */
	public void setServerHostnameModelDB(String serverHostnameModelDB) {
		this.serverHostnameModelDB = serverHostnameModelDB;
	}

	/**
	 * @return the serverPortModelDB
	 */
	public int getServerPortModelDB() {
		return serverPortModelDB;
	}

	/**
	 * @param serverPortModelDB the serverPortModelDB to set
	 */
	public void setServerPortModelDB(int serverPortModelDB) {
		this.serverPortModelDB = serverPortModelDB;
	}

	/**
	 * @return the mysqlModelDBName
	 */
	public String getMysqlModelDBName() {
		return mysqlModelDBName;
	}

	/**
	 * @param mysqlModelDBName the mysqlModelDBName to set
	 */
	public void setMysqlModelDBName(String mysqlModelDBName) {
		this.mysqlModelDBName = mysqlModelDBName;
	}

	/**
	 * @return the mysqlModelUsername
	 */
	public String getMysqlModelUsername() {
		return mysqlModelUsername;
	}

	/**
	 * @param mysqlModelUsername the mysqlModelUsername to set
	 */
	public void setMysqlModelUsername(String mysqlModelUsername) {
		this.mysqlModelUsername = mysqlModelUsername;
	}

	/**
	 * @return the mysqlModelPassword
	 */
	public String getMysqlModelPassword() {
		return mysqlModelPassword;
	}

	/**
	 * @param mysqlModelPassword the mysqlModelPassword to set
	 */
	public void setMysqlModelPassword(String mysqlModelPassword) {
		this.mysqlModelPassword = mysqlModelPassword;
	}

	/**
	 * @return the serverHostnameMongoDB
	 */
	public String getServerHostnameMongoDB() {
		return serverHostnameMongoDB;
	}

	/**
	 * @param serverHostnameMongoDB the serverHostnameMongoDB to set
	 */
	public void setServerHostnameMongoDB(String serverHostnameMongoDB) {
		this.serverHostnameMongoDB = serverHostnameMongoDB;
	}

	/**
	 * @return the serverPortMongoDB
	 */
	public int getServerPortMongoDB() {
		return serverPortMongoDB;
	}

	/**
	 * @param serverPortMongoDB the serverPortMongoDB to set
	 */
	public void setServerPortMongoDB(int serverPortMongoDB) {
		this.serverPortMongoDB = serverPortMongoDB;
	}

	/**
	 * @return the mongoDBName
	 */
	public String getMongoDBName() {
		return mongoDBName;
	}

	/**
	 * @param mongoDBName the mongoDBName to set
	 */
	public void setMongoDBName(String mongoDBName) {
		this.mongoDBName = mongoDBName;
	}

	/**
	 * @return the mongoUsername
	 */
	public String getMongoUsername() {
		return mongoUsername;
	}

	/**
	 * @param mongoUsername the mongoUsername to set
	 */
	public void setMongoUsername(String mongoUsername) {
		this.mongoUsername = mongoUsername;
	}

	/**
	 * @return the mongoPassword
	 */
	public String getMongoPassword() {
		return mongoPassword;
	}

	/**
	 * @param mongoPassword the mongoPassword to set
	 */
	public void setMongoPassword(String mongoPassword) {
		this.mongoPassword = mongoPassword;
	}

    
}