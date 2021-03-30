package it.unibs.bodai.ideaas.dao.model;

import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

/**
 * Classe rappresentante le istanze di una dimensione
 * @author Ada Bagozi
 *
 */
public class DimensionInstance {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DimensionInstance.class.getName());
	
	public static final String KEY_DIMENSION_ID = "id";
	public static final String KEY_DIMENTION_NAME = "name";
	public static final String KEY_INSTANCE_ID = "instance_id";
		
	private int id;

	private String instanceId;
	private String name;
	private boolean active;
	private DimensionInstance parentInstance;
	private Dimension dimension;
	
	
	
	/**
	 * @param identificator identificatore dell'istanza; Attenzione non si tratta dell'ID che viene generato nel DB
	 * @param name @string nome dell'istanza 
	 * @param active @boolean paratro che stabilisce se l'istanza è da osservare oppure no
	 * @param parentInstance @DimensionInstance riferimento all istanza padre
	 * @param dimension @Dimension dimensione a cui appartiene l'istanza
	 */
	public DimensionInstance(String identificator, String name, boolean active, DimensionInstance parentInstance,
			Dimension dimension) {
		super();
		this.instanceId = identificator;
		this.name = name;
		this.active = active;
		this.parentInstance = parentInstance;
		this.dimension = dimension;
	}
	
	public DimensionInstance(String identificator, String name, DimensionInstance parentInstance,
			Dimension dimension) {
		super();
		this.instanceId = identificator;
		this.name = name;
		this.parentInstance = parentInstance;
		this.dimension = dimension;
	}

	/**
	 * @param identificator identificatore dell'istanza; Attenzione non si tratta dell'ID che viene generato nel DB
	 * @param name @string nome dell'istanza 
	 * @param active @boolean paratro che stabilisce se l'istanza è da osservare oppure no
	 * @param parentInstance @DimensionInstance riferimento all istanza padre
	 * @param dimension @Dimension dimensione a cui appartiene l'istanza
	 */
	public DimensionInstance(String identificator, Dimension dimension) {
		super();
		this.instanceId = identificator;
		this.name = dimension.getName() + " - "+identificator;
		this.active = true;
		this.parentInstance = null;
		this.dimension = dimension;
	}


	/**
	 * 
	 */
	public DimensionInstance() {
		super();
	}




	/**
	 * @param dimensionInstanceDocument
	 */
	public DimensionInstance(Document dimensionInstanceDocument) {
		super();
		this.setFromDocument(dimensionInstanceDocument);
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
	 * @return the parentInstance
	 */
	public DimensionInstance getParentInstance() {
		//Get parent on demand
		return parentInstance;
	}



	/**
	 * @param parentInstance the parentInstance to set
	 */
	public void setParentInstance(DimensionInstance parentInstance) {
		this.parentInstance = parentInstance;
	}



	/**
	 * @return the dimension
	 */
	public Dimension getDimension() {
		return dimension;
	}



	/**
	 * @param dimension the dimension to set
	 */
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	
	public JSONObject toJSON(){
		JSONObject jsonDimensionInstance = new JSONObject();
		jsonDimensionInstance.put(KEY_DIMENSION_ID, String.format("%s", this.getDimension().getId()));
		jsonDimensionInstance.put(KEY_DIMENTION_NAME, String.format("%s", this.getDimension().getName()));
		jsonDimensionInstance.put(KEY_INSTANCE_ID, String.format("%s", this.getInstanceId()));
		return jsonDimensionInstance;
	}

	public void setFromDocument(Document doc) throws NumberFormatException {
		Dimension dim = new Dimension(Integer.parseInt(doc.get(KEY_DIMENSION_ID).toString()), doc.get(KEY_DIMENTION_NAME).toString());
		this.setDimension(dim);
		this.setInstanceId(doc.get(KEY_INSTANCE_ID).toString());
	}
	
	
	/**
	 * 
	 * @param value
	 * @param parentInstance
	 * @author Naim
	 */
	public DimensionInstance(String value, String name, DimensionInstance parentInstance) {
		super();
		this.instanceId = value;
		this.name = name;
		this.parentInstance = parentInstance;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
	    hash = 53 * hash + (this.getInstanceId() != null ? this.getInstanceId().hashCode() : 0);
	    hash = 53 * hash + Integer.valueOf(this.getInstanceId());
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		DimensionInstance di = (DimensionInstance) obj;
		if(!(this.getInstanceId().equals(di.getInstanceId()) && this.getParentInstance().getInstanceId().equals(di.getParentInstance().getInstanceId()) )) {
			return false;
		}
		return true;
	}
	
	
}
