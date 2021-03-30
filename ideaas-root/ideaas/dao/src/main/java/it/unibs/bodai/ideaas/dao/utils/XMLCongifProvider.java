/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibs.bodai.ideaas.dao.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import utils.XMLHandler;


/**
 *
 * @author Ada Bagozi
 */
public class XMLCongifProvider implements IConfigProvider {
    
    private String xmlFilePath = "";    
    private XMLHandler xmlHandler;

    public XMLCongifProvider(String filePath) throws IOException {
        try {
            this.xmlFilePath = filePath;
            this.xmlHandler = new XMLHandler(this.xmlFilePath, "validate.xsd");
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            throw new IOException(ex);
        }
    }
    
    public Configuration fetchConfig() {
        try {
            String serverURLMongoDB = this.xmlHandler.xpath("/Config/BigDataDB/Server").item(0).getTextContent();
            int serverPortMongoDB = Integer.parseInt(this.xmlHandler.xpath("/Config/BigDataDB/Port").item(0).getTextContent());
            String mongoDBName = this.xmlHandler.xpath("/Config/BigDataDB/DBName").item(0).getTextContent();
            String mongoUsername = this.xmlHandler.xpath("/Config/BigDataDB/DBUser").item(0).getTextContent();
            String mongoPassword = this.xmlHandler.xpath("/Config/BigDataDB/DBPassword").item(0).getTextContent();

            String serverURLModelDB = this.xmlHandler.xpath("/Config/MySQLDB/ModelDB/Server").item(0).getTextContent();
            int serverPortModelDB = Integer.parseInt(this.xmlHandler.xpath("/Config/MySQLDB/ModelDB/Port").item(0).getTextContent());
            String mysqlModelDBName = this.xmlHandler.xpath("/Config/MySQLDB/ModelDB/DBName").item(0).getTextContent();
            String mysqlModelUsername = this.xmlHandler.xpath("/Config/MySQLDB/ModelDB/DBUser").item(0).getTextContent();
            String mysqlModelPassword = this.xmlHandler.xpath("/Config/MySQLDB/ModelDB/DBPassword").item(0).getTextContent();

            String serverURLMetamodelDB = this.xmlHandler.xpath("/Config/MySQLDB/MetamodelDB/Server").item(0).getTextContent();
            int serverPortMetamodelDB = Integer.parseInt(this.xmlHandler.xpath("/Config/MySQLDB/MetamodelDB/Port").item(0).getTextContent());
            String mysqlMetamodelDBName = this.xmlHandler.xpath("/Config/MySQLDB/MetamodelDB/DBName").item(0).getTextContent();
            String mysqlMetamodelUsername = this.xmlHandler.xpath("/Config/MySQLDB/MetamodelDB/DBUser").item(0).getTextContent();
            String mysqlMetamodelPassword = this.xmlHandler.xpath("/Config/MySQLDB/MetamodelDB/DBPassword").item(0).getTextContent();

            Configuration retVal = new Configuration(
                serverURLMetamodelDB, 
                serverPortMetamodelDB, 
                mysqlMetamodelDBName, 
                mysqlMetamodelUsername, 
                mysqlMetamodelPassword, 
                serverURLModelDB, 
                serverPortModelDB, 
                mysqlModelDBName, 
                mysqlModelUsername, 
                mysqlModelPassword, 
                serverURLMongoDB, 
                serverPortMongoDB, 
                mongoDBName, 
                mongoUsername, 
                mongoPassword
            );
            
            return retVal;
        
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XMLCongifProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
