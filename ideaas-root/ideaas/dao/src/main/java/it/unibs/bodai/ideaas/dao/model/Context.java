package it.unibs.bodai.ideaas.dao.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Ada Bagozi
 *
 */
public class Context {
	private static final Logger LOG = Logger.getLogger(Context.class.getName());
	
	public static final String KEY_CONTEXT_ID = "id";
	public static final String KEY_CONTEXT_PARAMS = "context_parameters";
	
	private int id;
	private String name;
	private boolean active;
//	private ContextType contextType;
	private Collection<Parameter> parameters = new ArrayList<>();
	private Collection<ParameterInstance> parameterInstances = new ArrayList<ParameterInstance>();
	
	/**
	 * @param id
	 * @param name
	 * @param active
	 * @param parameters
	 */
	public Context(int id, String name, boolean active, Collection<Parameter> parameters) {
		super();
		this.id = id;
		this.name = name;
		this.active = active;
		this.parameters = parameters;
	}
	
	/**
	 * @param id
	 */
	public Context(int id) {
		super();
		this.id = id;
	}
	
	public Context() {
		super();
	}
	
	/**
	 * @param contextDocument document from with is setted the context
	 */
	public Context(Document contextDocument) {
		super();
		this.setFromDocument(contextDocument);
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
	 * @return the features
	 */
	public Collection<Parameter> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Collection<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void addParameter(Parameter parameter) {
		this.parameters.add(parameter);
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
	 * @param parameterInstances the parameterInstances to set
	 */
	public void addParameterInstance(ParameterInstance parameterInstance) {
		this.parameterInstances.add(parameterInstance);
	}
	
	public JSONObject toJSON(){
		JSONObject jsonContext = new JSONObject();
		jsonContext.put(KEY_CONTEXT_ID, String.format("%s", this.getId()));

		JSONArray jsonInstances = new JSONArray();
		for (ParameterInstance instance : this.getParameterInstances()) {
			jsonInstances.put(instance.toJSON());
		}
		jsonContext.put(KEY_CONTEXT_PARAMS, jsonInstances);
		return jsonContext;
	}

	public void setFromDocument(Document doc) throws NumberFormatException {
		this.setId(Integer.parseInt(doc.get(KEY_CONTEXT_ID).toString()));
		Collection<Document> instancesDocument = (Collection<Document>) doc.get(KEY_CONTEXT_PARAMS);	
		for(Document instanceDocument: instancesDocument){
			this.addParameterInstance(new ParameterInstance(instanceDocument));
		}
	}
	
}
