package utils;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import org.w3c.dom.NodeList;

/**
 *
 * @author Ada Bagozi, Massimo Bono 
 */
public class XMLHandler {
    
    private Document xmlTree;

    public XMLHandler(String xml_name, String validate_file_name) throws SAXException, ParserConfigurationException, IOException {
        super();

        this.xmlTree=this.loadXmlFileConfig(xml_name, validate_file_name);
    }
    
    /**
     * like {@link #XMLHandler(String, String)} but with no xml schema
     * @param xmlName
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public XMLHandler(String xmlName) throws SAXException, ParserConfigurationException, IOException {
    	this(xmlName, "");
    }
    
    /**
     * like {@link #XMLHandler(String)} but accepts a file instead of a String
     * @param f
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public XMLHandler(File f) throws SAXException, ParserConfigurationException, IOException {
    	this(f.getAbsolutePath());
    }

    /**
     * loads a xml file from the resource package
     * 
     * @param xml_file the resource to load
     * @param validate_file_name the optional schema file to load to validate the xml itself. empty string if you don't want to validate the xml
     * @return a Document instance represented the document loaded
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    protected Document loadXmlFileConfig(String xml_file, String validate_file_name) throws SAXException, ParserConfigurationException, IOException{
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            schemaFactory=SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = null;
            if(!validate_file_name.equalsIgnoreCase("")){
            	schema=schemaFactory.newSchema(new StreamSource(this.getResource(validate_file_name)));
            }
            DocumentBuilderFactory documentBuilderfactory = DocumentBuilderFactory.newInstance();
            documentBuilderfactory.setIgnoringElementContentWhitespace(true);
            documentBuilderfactory.setNamespaceAware(true);
            if(!validate_file_name.equalsIgnoreCase("")){
            	documentBuilderfactory.setSchema(schema);
            }

            DocumentBuilder builder = documentBuilderfactory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler());


            Document doc=builder.parse(this.getResource(xml_file));
            return doc;
    }
    
    /**
     * Read the node list of an XML File
     * 
     * @param xmlFile the resource to load
     * @param node, the string rappresenting the node to read
     * @return a @string rappresenting the list of elements of the node
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public String readXMLFile(File xmlFile, String node) throws ParserConfigurationException, SAXException, IOException{
		//Get Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		//Build Document
		Document document = builder.parse(xmlFile);

		//Normalize the XML Structure; It's just too important !!
		document.getDocumentElement().normalize();

		//Here comes the root node
		Element root = document.getDocumentElement();
		//System.out.println(root.getNodeName());

		//Get all employees
		NodeList nList = document.getElementsByTagName(node);
		//System.out.println("============================ nList: "+nList.toString());

		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node n = nList.item(temp);
			//System.out.println("");    //Just a separator
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{ 
				//Print each employee's detail
				Element eElement = (Element) n;
				//LOG.info(eElement.toString());    //Just a separator
			}
		}

		return nList.toString();
	}
    

    /**
     * Compute an XPATH expression
     * 
     * @param xpathExpression the expression to compute
     * @return a list of nodes satisfying the XPATH expression
     * @throws XPathExpressionException if the expression given was malformed
     */
    public NodeList xpath(String xpathExpression) throws XPathExpressionException{
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = (XPathExpression) xpath.compile(xpathExpression);
            return (NodeList) expr.evaluate(this.xmlTree, XPathConstants.NODESET);
    }

    
    /**
     * Load a project resource from the "resource package".
     * 
     * @param resource_name the name of the resource to load. Be sure to add an extension to it.
     * @return A stream returning all the data inside the resource fetched
     */
    private InputStream getResource(String resource_name) throws FileNotFoundException {
            InputStream result=this.getClass().getClassLoader().getResourceAsStream("resources/"+resource_name);
            if (result==null) {
                    throw new FileNotFoundException(String.format("resource %s coulldn't be found inside resources package.",resource_name));
            }
            return result;
    }

}
