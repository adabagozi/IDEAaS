package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MongoUtils {

	//used in send alert for getting its measures in mongodb ==> see StateDetection in webservice
	public static void getCollectionName(String date_from_frontend) {
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy_MM");
		String collection_date_regex = "(\\d{4}_\\d{2})";
		ArrayList<Date> collections_date = new ArrayList<Date>();
		Pattern m = Pattern.compile(collection_date_regex);
		
		System.out.println(m);
		
//		Date collection_date = date_format.
//		collections_date.add(collection_date);

	}
	
	public static void main(String[] args) {
		MongoUtils m = new MongoUtils();
		m.getCollectionName("");
	}


}
