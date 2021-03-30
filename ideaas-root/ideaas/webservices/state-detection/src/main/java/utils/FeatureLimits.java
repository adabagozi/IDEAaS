package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;

import it.unibs.bodai.ideaas.dao.DAOFactory;
import it.unibs.bodai.ideaas.dao.IDAO;

public class FeatureLimits {

	private IDAO dao;
	private DateUtils dateUtils;

	//@Before
	public void setup() {
		try {
			this.dao = DAOFactory.getDAOFactory().getDAO();
			this.dateUtils = new DateUtils();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getCollections(String[] collections, String[] machines_instances, MongoDatabase db) {
		ArrayList<String> string_values = new ArrayList<>();
		for(String c : collections) {
			for(String m : machines_instances) {
				//new Document("$sort" , new Document("features.value" , -1)),
					AggregateIterable<Document> aggregate = db.getCollection(c)
							.aggregate(
									Arrays.asList(
											new Document("$unwind" , "$features"),
											new Document("$match", new Document("dimensions.instance_id", m).append("dimensions.id", "3")),
											new Document("$group", new Document("_id", "$features.name").append("max", new Document("$max", "$features.value")).append("min", new Document("$min", "$features.value")))
											)
									);
					for(Document d : aggregate) {
						String features_string = d.get("_id").toString();
						String str_features_values = d.get("max").toString();
						String str_features_values_min = d.get("min").toString();
						String return_value = c + " " + m + " " + features_string + " " + str_features_values + " " + str_features_values_min;
						string_values.add(return_value);
					}
				}
			}
			return string_values;
		}

}
