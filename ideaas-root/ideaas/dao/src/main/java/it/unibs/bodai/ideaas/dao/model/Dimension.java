package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe rappresentante la dimensione.
 * Tutte le dimensioni, ad esclusione di quelle che hanno il parametro eccezione <strong>true</strong>, avranno un'insieme di istanze.
 * @author Ada Bagozi
 */ 

public class Dimension {
	private static final Logger LOG = Logger.getLogger(Dimension.class.getName());

	private int id;
	private String name;
	private String table_name;
	private Dimension parentDimension;
	private String attributes;
	private boolean exception;
	private boolean active;
	private int inputPosition;
	private Collection<DimensionInstance> dimensionInstances;



	/**
	 * @param id Id della dimensione
	 * @param name
	 * @param parentDimension
	 * @param attributes
	 * @param exception
	 * @param active
	 * @param dimensionInstnces
	 */
	public Dimension(int id, String name, Dimension parentDimension, String attributes, boolean exception,
			boolean active, Collection<DimensionInstance> dimensionInstnces) {
		super();
		this.id = id;
		this.name = name;
		this.parentDimension = parentDimension;
		this.attributes = attributes;
		this.exception = exception;
		this.active = active;
		this.dimensionInstances = dimensionInstnces;
	}



	public Dimension() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Dimension(int id, String name) {
		super();
		this.id = id;
		this.name = name;
		// TODO Auto-generated constructor stub
	}

	public Dimension(int id, String name, Dimension parent_dimension) {
		super();
		this.id = id;
		this.name = name;
		this.parentDimension = parent_dimension;
		// TODO Auto-generated constructor stub
	}

	public Dimension(int id) {
		super();
		this.id = id;
		
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
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the table name
	 */
	public String getTableName() {
		return table_name;
	}



	/**
	 * @param table_name the table name to set
	 */
	public void setTableName(String table_name) {
		this.table_name = table_name;
	}
	
	/**
	 * @return the parentDimension
	 */
	public Dimension getParentDimension() {
		return parentDimension;
	}

	


	/**
	 * @param parentDimension the parentDimension to set
	 */
	public void setParentDimension(Dimension parentDimension) {
		this.parentDimension = parentDimension;
	}



	/**
	 * @return the attributes
	 */
	public String getAttributes() {
		return attributes;
	}



	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}



	/**
	 * @return the exception
	 */
	public boolean isException() {
		return exception;
	}



	/**
	 * @param exception the exception to set
	 */
	public void setException(boolean exception) {
		this.exception = exception;
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
	 * @return the inputPosition
	 */
	public int getInputPosition() {
		return inputPosition;
	}

	/**
	 * @param inputPosition the inputPosition to set
	 */
	public void setInputPosition(int inputPosition) {
		this.inputPosition = inputPosition;
	}



	/**
	 * TODO importante impostare la possibilità di richiedere le istanze quando non sono settate.
	 * @return the dimensionInstnces
	 */
	public Collection<DimensionInstance> getDimensionInstances() {
		return dimensionInstances;
	}



	/**
	 * @param dimensionInstnces the dimensionInstnces to set
	 */
	public void setDimensionInstances(Collection<DimensionInstance> dimensionInstnces) {
		this.dimensionInstances = dimensionInstnces;
	}

	public void addDimensionInstances(DimensionInstance dimensionInstnces){
		if(this.dimensionInstances == null){
			this.dimensionInstances = new ArrayList<>();
		}
		this.dimensionInstances.add(dimensionInstnces);
	}
}
