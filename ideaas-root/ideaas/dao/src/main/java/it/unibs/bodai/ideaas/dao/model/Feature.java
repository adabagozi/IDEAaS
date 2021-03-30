package it.unibs.bodai.ideaas.dao.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;
import org.netlib.util.booleanW;

import it.unibs.bodai.ideaas.dao.DAOFactory;

/**
 * Classe che rappresenta una feature
 * @author Ada Bagozi
 *
 */
public class Feature implements Serializable{
	/**
	 * 
	 */
	private static final boolean normalize = true;
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(Feature.class.getName());
	
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_UNIT = "measure_unit";
	public static final String KEY_UP_WARNING = "warning_upper_bound";
	public static final String KEY_LOW_WARNING = "warning_lower_bound";
	public static final String KEY_UP_ERROR = "error_upper_bound";
	public static final String KEY_LOW_ERROR = "error_lower_bound";
	public static final String KEY_MIN = "min";
	public static final String KEY_MAX = "max";
	
	public static final String KEY_ERROR = "error";
	public static final String KEY_OK = "ok";
	public static final String KEY_UNCHANGED = "unchanged";
	public static final String KEY_WARNING = "warning";

	private int id;
	private String name;    
	private int positionInInputFile;
	private double lowerBoundWarning;
    private double lowerBoundError;
    private double upperBoundWarning;
    private double upperBoundError;
    private double value;
    private String measureUnit;
    private String state;
    private double min;
    private double max;
    private Measure measure; //TODO se l'ho messa io, perchè l'ho fatto?
   
    private Collection<FeatureSpace> featureSpaces;
    
    //TODO vedere se si può togliere
    private FeatureSpace featureSpace;
    
	

	/**
	 * @param id
	 * @param name
	 * @param lowerBoundWarning
	 * @param lowerBoundError
	 * @param upperBoundWarning
	 * @param upperBoundError
	 * @param measureUnit
	 * @param featureSpaces
	 */
	public Feature(int id, String name, double lowerBoundWarning, double lowerBoundError, double upperBoundWarning,
			double upperBoundError, String measureUnit, Collection<FeatureSpace> featureSpaces) {
		super();
		this.id = id;
		this.name = name;
		this.lowerBoundWarning = lowerBoundWarning;
		this.lowerBoundError = lowerBoundError;
		this.upperBoundWarning = upperBoundWarning;
		this.upperBoundError = upperBoundError;
		this.measureUnit = measureUnit;
		this.featureSpaces = featureSpaces;
	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param lowerBoundWarning
	 * @param lowerBoundError
	 * @param upperBoundWarning
	 * @param upperBoundError
	 * @param measureUnit
	 * @author Naim
	 */
	public Feature(int id, String name, double lowerBoundWarning, double lowerBoundError, double upperBoundWarning,
			double upperBoundError, String measureUnit) {
		super();
		this.id = id;
		this.name = name;
		this.lowerBoundWarning = lowerBoundWarning;
		this.lowerBoundError = lowerBoundError;
		this.upperBoundWarning = upperBoundWarning;
		this.upperBoundError = upperBoundError;
		this.measureUnit = measureUnit;
	}
	
	public Feature(int id, String name, double lowerBoundWarning, double lowerBoundError, double upperBoundWarning,
			double upperBoundError, String measureUnit, FeatureSpace featureSpace) {
		super();
		this.id = id;
		this.name = name;
		this.lowerBoundWarning = lowerBoundWarning;
		this.lowerBoundError = lowerBoundError;
		this.upperBoundWarning = upperBoundWarning;
		this.upperBoundError = upperBoundError;
		this.measureUnit = measureUnit;
		this.featureSpace = featureSpace;
	}

    /**
     * Costruttore della classe Feature con unico parametro impostato: l'id della feature
     * @param id identificatore della feature impostato
     */
    public Feature(int id) {
        this(id, "", 0, 0, 0, 0, "", new ArrayList<>());
    }
    
    public Feature(String name, String measureUnit, String state) {
    		this.name = name;
    		this.measureUnit = measureUnit;
    		this.state = state;
    }
    
    

    /**
     * Costruttore della classe Feature con unico parametro impostato: l'id della feature
     * @param id identificatore della feature impostato
     */
    public Feature(int id, String name) {
        this(id, name, 0, 0, 0, 0, "", new ArrayList<>());
    }

	/**
     * Costruttore della classe Feature senza alcun parametro impostato
     */
    public Feature() {
        this(0,"", 0, 0, 0, 0, "", new ArrayList<>());
    }
    
    public Feature(Document elementDocument) {
		super();
		this.setFromDocument(elementDocument);
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



	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public void setMax(double max) {
		this.max = max;
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
	 * @return the measureUnit
	 */
	public String getMeasureUnit() {
		return measureUnit;
	}



	/**
	 * @param measureUnit the measureUnit to set
	 */
	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}



	


	/**
	 * @return the featureSpaces
	 */
	public Collection<FeatureSpace> getFeatureSpaces() {
//		System.out.println("ID_FEATURE: "+this.getId()); 
		if(this.featureSpaces.isEmpty()) {
			try {
				this.setFeatureSpaces(DAOFactory.getDAOFactory().getDAO().readFeatureSpacesByFeatureId(this.getId()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return featureSpaces;
	}

	/**
	 * @param featureSpaces the featureSpaces to set
	 */
	public void setFeatureSpaces(Collection<FeatureSpace> featureSpaces) {
		this.featureSpaces = featureSpaces;
	}
	
	/**
	 * @param featureSpaces the featureSpaces to set
	 */
	public void addFeatureSpace(FeatureSpace featureSpace) {
		this.featureSpaces.add(featureSpace);
	}

	/**
	 * @return the lowerBoundWarning
	 */
	public double getLowerBoundWarning() {
		return lowerBoundWarning;
	}


	/**
	 * @param lowerBoundWarning the lowerBoundWarning to set
	 */
	public void setLowerBoundWarning(double lowerBoundWarning) {
		this.lowerBoundWarning = lowerBoundWarning;
	}


	/**
	 * @return the lowerBoundError
	 */
	public double getLowerBoundError() {
		return lowerBoundError;
	}


	/**
	 * @param lowerBoundError the lowerBoundError to set
	 */
	public void setLowerBoundError(double lowerBoundError) {
		this.lowerBoundError = lowerBoundError;
	}


	/**
	 * @return the upperBoundWarning
	 */
	public double getUpperBoundWarning() {
		return upperBoundWarning;
	}
	
	public String checkBoundary(double valueToCheck) {
		if(this.upperBoundError <= valueToCheck || this.lowerBoundError >= valueToCheck) {
			 return KEY_ERROR;
		}
		if (this.upperBoundWarning <= valueToCheck || this.lowerBoundWarning >= valueToCheck) {
			 return KEY_WARNING;
		}
		return KEY_UNCHANGED;
	}


	/**
	 * @param upperBoundWarning the upperBoundWarning to set
	 */
	public void setUpperBoundWarning(double upperBoundWarning) {
		this.upperBoundWarning = upperBoundWarning;
	}


	/**
	 * @return the upperBoundError
	 */
	public double getUpperBoundError() {
		return upperBoundError;
	}


	/**
	 * @param upperBoundError the upperBoundError to set
	 */
	public void setUpperBoundError(double upperBoundError) {
		this.upperBoundError = upperBoundError;
	}
	

	/**
	 * Metodo per la visualizzazione in console della feature
	 */
	public void stampaFeature(){
        System.out.println("it.unibs.bodai.ideaas.dao.model -> Feature.stampaFeature()");
        System.out.println("ID: "+this.id+"    nome: "+this.name);
    }

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	/**
	 * @return the positionInInputFile
	 */
	public int getPositionInInputFile() {
		return positionInInputFile;
	}

	/**
	 * @param positionInInputFile the positionInInputFile to set
	 */
	public void setPositionInInputFile(int positionInInputFile) {
		this.positionInInputFile = positionInInputFile;
	}
	
	/**
	 * @author Naim
	 */
	public Feature(int id, String name, String state) {
		this.id = id;
		this.name = name;
		this.state = state;
	}
	
	public Feature(int id, FeatureSpace featureSpace) {
		this.id = id;
		this.featureSpace = featureSpace;
	}

	public FeatureSpace getFeatureSpace() {
		return featureSpace;
	}

	public void setFeatureSpace(FeatureSpace featureSpace) {
		this.featureSpace = featureSpace;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
	    hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
	    hash = 53 * hash + this.id;
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		Feature f = (Feature) obj;
		if(!(this.name.equals(f.name) && this.id == f.id  && f.getFeatureSpace().getId() == this.featureSpace.getId())) {
			return false;
		}
		return true;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	
	public double normalizeData(double value) {
		if (!normalize) {
			return value;
		}
		double divisor = this.getMax() - this.getMin();
		if (divisor == 0) {
			throw new IllegalArgumentException("Argument 'divisor' is 0");
		}
		return (value - this.getMin())/divisor;
	}
	
	public double valueFromNormalizedData(double value) {
		if (!normalize) {
			return value;
		}
		double diff = this.getMax() - this.getMin();
		return (value*diff)+this.getMin();
	}
	
	public JSONObject toJSON() {
		JSONObject jsonFeature = new JSONObject();
		jsonFeature.put(KEY_ID, String.format("%s",  this.getId()));
		jsonFeature.put(KEY_NAME, String.format("%s",  this.getName()));
		jsonFeature.put(KEY_UNIT, String.format("%s",  this.getMeasureUnit()));
		jsonFeature.put(KEY_UP_WARNING, String.format("%s",  this.getUpperBoundWarning()));
		jsonFeature.put(KEY_LOW_WARNING, String.format("%s",  this.getLowerBoundWarning()));
		jsonFeature.put(KEY_UP_ERROR, String.format("%s",  this.getUpperBoundError()));
		jsonFeature.put(KEY_LOW_ERROR, String.format("%s",  this.getLowerBoundError()));
		jsonFeature.put(KEY_MIN, String.format("%s",  this.getMin()));
		jsonFeature.put(KEY_MAX, String.format("%s",  this.getMax()));
	
		return jsonFeature;
	}
	
	public void setFromDocument(Document doc) {
		this.setId(Integer.parseInt(doc.get(KEY_ID).toString()));
		this.setName(doc.get(KEY_NAME).toString());
		this.setMeasureUnit(doc.get(KEY_UNIT).toString());
		this.setUpperBoundWarning(Double.parseDouble(doc.get(KEY_UP_WARNING).toString()));
		this.setLowerBoundWarning(Double.parseDouble(doc.get(KEY_LOW_WARNING).toString()));
		this.setUpperBoundError(Double.parseDouble(doc.get(KEY_UP_ERROR).toString()));
		this.setLowerBoundError(Double.parseDouble(doc.get(KEY_LOW_ERROR).toString()));
	}
}
