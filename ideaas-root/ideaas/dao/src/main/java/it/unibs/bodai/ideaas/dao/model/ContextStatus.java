package it.unibs.bodai.ideaas.dao.model;

import org.bson.Document;
import org.json.JSONObject;

public class ContextStatus{

	public static final String KEY_CONTEXT_ID = "id";
	public static final String KEY_CONTEXT_STATUS = "status";
	
	private Context context;
	private String status = "ok";
	
	public ContextStatus(Context context, String status) {
		this.context = context;
		this.status = status;
	}
	
	
	/**
	 * @param contextDocument document from with is setted the context
	 */
	public ContextStatus(Document contextStatusDocument) {
		super();
		this.setFromDocument(contextStatusDocument);
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}


	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}


	public JSONObject toJSON(){
		JSONObject jsonContextStatus = new JSONObject();
		jsonContextStatus.put(KEY_CONTEXT_ID, String.format("%s", this.getContext().getId()));
		jsonContextStatus.put(KEY_CONTEXT_STATUS, this.getStatus());
		return jsonContextStatus;
	}

	public void setFromDocument(Document doc) throws NumberFormatException {
		this.setContext(new Context(Integer.parseInt(doc.get(KEY_CONTEXT_ID).toString())));
		this.setStatus(doc.get(KEY_CONTEXT_STATUS).toString());
	}
	
}
