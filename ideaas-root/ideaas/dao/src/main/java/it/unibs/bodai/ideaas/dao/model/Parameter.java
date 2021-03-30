package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;




/**
 * Classe che rappresenta un parametro
 * @author Ada Bagozi
 *
 */
public class Parameter {
	private static final Logger LOG = Logger.getLogger(Parameter.class.getName());
	public final String KEY_DIMENSION_ID = "id";
	public final String KEY_DIMENTION_NAME = "name";
	public final String KEY_INSTANCE_ID = "instance_id";
	
	private int id;
	private String name;
	private boolean active;
	
	private Collection<ParameterInstance> parameterInstances;
	
    
    
    /**
	 * @param id
	 * @param name
	 * @param value
	 * @param measureUnit
	 * @param contextes
	 */
	public Parameter(int id, String name, String measureUnit, Collection<ParameterInstance> parameterInstances) {
		super();
		this.id = id;
		this.name = name;
		this.parameterInstances = parameterInstances;
	}
	
	public Parameter(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Parameter() {
		super();
			// TODO Auto-generated constructor stub
	}



	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the parameterInstances
	 */
	public Collection<ParameterInstance> getParameterInstances() {
		return parameterInstances;
	}

	/**
	 * @param parameterInstances the parameterInstances to set
	 */
	public void setParameterInstances(Collection<ParameterInstance> parameterInstances) {
		this.parameterInstances = parameterInstances;
	}

	/**
	 * @return the contextes
	 */
	public Collection<Context> getContextes() {
		//TODO: get contexes from db
		return new ArrayList<Context>();
	}

}
