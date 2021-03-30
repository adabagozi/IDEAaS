package it.unibs.bodai.ideaas.dao.model;

import java.util.Random;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

public class ParameterInstance {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DimensionInstance.class.getName());
	
	public final String KEY_PARAMETER_ID = "id";
	public final String KEY_PARAMETER_NAME = "name";
	public final String KEY_INSTANCE_ID = "instance_id";
		
			
	private String instanceId;
	private String name;
	private boolean active;
	private Parameter parameter;
	
	
	
	/**
	 * @param identificator identificatore dell'istanza; Attenzione non si tratta dell'ID che viene generato nel DB
	 * @param name @string nome dell'istanza 
	 * @param active @boolean parametro che stabilisce se l'istanza è da osservare oppure no
	 * @param parameter @Parameter parametro a cui appartiene l'istanza
	 */
	public ParameterInstance(String identificator, String name, boolean active, Parameter parameter) {
		super();
		this.instanceId = identificator;
		this.name = name;
		this.active = active;
		this.parameter = parameter;
	}


	/**
	 * @param instanceId identificatore dell'istanza; Attenzione non si tratta dell'ID che viene generato nel DB
	 * @param parameter @Parameter parametro a cui appartiene l'istanza
	 */
	public ParameterInstance(String instanceId, Parameter parameter) {
		super();
		this.instanceId = instanceId;
		this.name = "";
		this.active = true;
		this.parameter = parameter;
	}
	

	/**
	 * 
	 */
	public ParameterInstance() {
		super();
	}

	public ParameterInstance(Document parameterInstanceDocument) {
		super();
		this.setFromDocument(parameterInstanceDocument);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the instanceId
	 */
	public String getInstanceId() {
		return instanceId;
	}


	/**
	 * @param instanceId the instanceId to set
	 */
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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
	 * @return the parameter
	 */
	public Parameter getParameter() {
		return parameter;
	}


	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}


	public JSONObject toJSON(){
		JSONObject jsonParameterInstance = new JSONObject();
		jsonParameterInstance.put(this.KEY_PARAMETER_ID, String.format("%s", this.getParameter().getId()));
		jsonParameterInstance.put(this.KEY_PARAMETER_NAME, String.format("%s", this.getParameter().getName()));
		jsonParameterInstance.put(this.KEY_INSTANCE_ID, String.format("%s", this.getInstanceId()));
		return jsonParameterInstance;
	}

	
	//TODO: valutare se fare il get da dao
	public void setFromDocument(Document doc) throws NumberFormatException {
		Parameter param = new Parameter(Integer.parseInt(doc.get(this.KEY_PARAMETER_ID).toString()), doc.get(this.KEY_PARAMETER_NAME).toString());
		this.setParameter(param);
		this.setInstanceId(doc.get(this.KEY_INSTANCE_ID).toString());
	}
	
}
