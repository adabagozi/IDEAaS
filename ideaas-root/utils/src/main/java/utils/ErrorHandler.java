package utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Massimo Bono
 */
public class ErrorHandler implements org.xml.sax.ErrorHandler {
    
	/**
	 * A set of all the warning detected during the XML analysis.
	 * This value is always not NULL.
	 */
	private List<SAXParseException> warning_errors;
	/**
	 * A set of all the errors detected during the Xml analysis.
	 * This value is always not NULL. 
	 */
	private List<SAXParseException> error_errors;
	/**
	 * A set of fatal errors detected dunring the XML analysis.
	 * Usually there is up to 1 fatal errors in the XMl validation.
	 * This value is always not NULL.
	 */
	private List<SAXParseException> fatal_errors;	
	
    public ErrorHandler() {
		super();
		this.warning_errors=new ArrayList<>();
		this.error_errors=new ArrayList<>();
		this.fatal_errors=new ArrayList<>();
	}

    public void warning(SAXParseException e) throws SAXException {
		this.warning_errors.add(e);
        System.out.println(e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
    	this.error_errors.add(e);
        System.out.println(e.getMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
    	assert this.fatal_errors.size()<1 : this.fatal_errors;
    	this.fatal_errors.add(e);
        System.out.println(e.getMessage());
    }
}
