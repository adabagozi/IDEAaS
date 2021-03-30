package it.unibs.bodai.ideaas.data_acquisition.webservice;

//import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.io.BsonOutput;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;




/**
 * @author Ada Bagozi
 *
 */
@Path("service")
public class DataAcquisition {
	private static final Logger LOG = Logger.getLogger(DataAcquisition.class.getName());

	private IDAO iDao;
	public DataAcquisition() throws IOException {
		iDao = DAOFactory.getDAOFactory().getDAO();//DataAcquisitionFactory.getDataAcquisitionFactory().getDataAquisition();
//		IDAO dao = DAOFactory.getDAOFactory().getDAO();
	}


	@GET
	@Path("/testGet/{saluto}")
	public String testGet(@PathParam("saluto") String saluto) {
		String messageHTML = saluto + " mondo.";
		return messageHTML;
	}
	
	
	@POST
	@Path("/saveMeasures")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String saveMeasures(InputStream inputJsonObj) throws Exception {
		try {
	        // get the metadata for the item as a json stream
//	        InputStream jsonStream = Request.Get(requestUrl).execute().returnContent().asStream();

	        BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputJsonObj));

	        StringBuilder result = new StringBuilder();
	        String line;

	        // read each line of the stream
	        while ((line = streamReader.readLine()) != null) {
	            result.append(line);
	        }
	        streamReader.close();
	        return result.toString();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return "No Parsing";
//		if (inputJsonObj == null)
//			return "Invalid input File";//Response.status(400).entity("Invalid File uploaded").build();

//		StringBuilder crunchifyBuilder = new StringBuilder();
//          try {
//              BufferedReader in = new BufferedReader(new InputStreamReader(inputJsonObj));
//              String line = null;
//              while ((line = in.readLine()) != null) {
//                  crunchifyBuilder.append(line);
//              }
//          } catch (Exception e) {
//              System.out.println("Error Parsing: - ");
//          }
//          return "Data Received: " + crunchifyBuilder.toString();
		
//		StringBuilder textBuilder = new StringBuilder();
//		try (Reader reader = new BufferedReader(new InputStreamReader(inputJsonObj, Charset.forName(StandardCharsets.UTF_8.name())))) {
//		    int c = 0;
//		    while ((c = reader.read()) != -1) {
//		        textBuilder.append((char) c);
//		    }
//		}
		
		//return textBuilder.toString();
//		JSONTokener jsonTokener = new JSONTokener(textBuilder.toString());
//		JSONObject jsonObject = new JSONObject(jsonTokener);
//	    return jsonObject.toString();
     
	}
	
	
	@POST
	@Path("/saveInputData")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public String saveInputData(@FormDataParam("inputJsonObj") FormDataBodyPart jsonFile) throws Exception {
		jsonFile.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        InputStream inputStream = jsonFile.getEntityAs(InputStream.class);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result;
        result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        String toPrint = "";
//        Log.d("JSON Parser", "result: " + result.toString());
        try {
        	JSONArray jsonArray = new JSONArray(result.toString());
        	toPrint = "jsonArray: " + jsonArray.toString();
        }catch (JSONException e) {
        	try {
        		JSONObject jsonObject = new JSONObject(result.toString());

            	toPrint = "jsonObject: "+jsonObject.toString();
                }catch (JSONException ex) {
                	ex.printStackTrace();
    				return e.getMessage();
                }
        }
        toPrint += "<br><br><br> "+" ================================================== "+" <br><br><br>";
        try {
          BsonArray bsonArray = BsonArray.parse(result.toString());
          toPrint += "bsonArray: "+bsonArray.toString();
          List<Document> documents = new ArrayList<>();
          for (BsonValue bsonValue : bsonArray) {
			documents.add(Document.parse(bsonValue.toString()));
          }
          if(documents.size()>0) {
        	  this.iDao.saveDocuments("Smart4CPPS", documents);
        	  //this.iDao.save
        	//  this.iDao.save
          }
        }catch (BSONException e) {
        	try {
//        		BSONObject bsonObject = BSONObject.parse(result.toString()); // new BSONObject(result.toString());
        		Document doc = Document.parse(result.toString());
        		toPrint += "doc: "+doc.toString();
        		this.iDao.insertDocument(doc, "Smart4CPPS");
            }catch (BSONException ex) {
            	ex.printStackTrace();
				return e.getMessage();
            }
        }
        
        return toPrint;
//		try {
//	        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
//			return jsonObject.toString();
//		}catch (JSONException e) {
//			try {
//				JSONArray jsonArray = new JSONArray(new JSONTokener(inputStream));
//				return jsonArray.toString();
//			}catch (JSONException ex) {
//
//				ex.printStackTrace();
//				return ex.getMessage();
//			}
//		} 
	}
	
	// InputStream -> File
    @SuppressWarnings("unused")
	private void copyInputStreamToFile(InputStream inputStream, File file) 
		throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
            	outputStream.write(bytes, 0, read);
            }
        }

    }
	
	@POST
	@Path("/saveMeasuresJSON")
	@Consumes(MediaType.APPLICATION_JSON)
	public String saveMeasuresJSON(JSONObject inputJsonObj) throws Exception {
		
		return inputJsonObj.toString();
	}

//	@POST
//	@Path("/uploadFile")
//	@Consumes({MediaType.MULTIPART_FORM_DATA})
//	public String uploadFile(
//		    @FormDataParam("file") InputStream fileInputStream,
//		    @FormDataParam("file") FormDataContentDisposition fileInputDetails) throws UnknownHostException {
//		 
////		  MongoClient mongoClient = new MongoClient("localhost", 27017);
////		  DB mongoDB = mongoClient.getDatabase("tutorial");
//		    
//		  //Let's store the standard data in regular collection
////		  DBCollection collection = mongoDB.getCollection("filestore");
//		  
//		  // Let's query to ensure ID does not already exist in Mongo
//		  // if it does, we will alert the user 
//		  BasicDBObject query = new BasicDBObject();
////		  query.put("_id", id);
////		  DBCursor cursor = collection.find(query);
//		    
////		  if (!cursor.hasNext()) {
//		    // Build our document and add all the fields
//		    BasicDBObject document = new BasicDBObject();
//		      
//		    //insert the document into the collection 
////		    collection.insert(document);
//		      
//		    // Now let's store the binary file data using filestore GridFS  
////		    GridFS fileStore = new GridFS(mongoDB, "filestore");
//		    GridFSInputFile inputFile = fileStore.createFile(fileInputStream);
//		    inputFile.setId(id);
//		    inputFile.setFilename(fileInputDetails.getFileName());
//		    inputFile.save();
//
////		  }
//		}

}
