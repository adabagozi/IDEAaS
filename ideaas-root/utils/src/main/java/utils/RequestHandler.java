package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class RequestHandler {

	public static void sendRequest(String api_url, File xml) throws MalformedURLException, IOException {
		//System.out.println(IOUtils.toString(xmlInputFile.toURI().toURL()));
		//	String url = "http://sibylsystem.microsi.it:8080/clustering/webresources/clustering/";
		String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		String retVal = "";
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.
		HttpURLConnection connection = (HttpURLConnection) new URL(api_url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setRequestProperty("Accept-Charset", charset);
		connection.setRequestProperty("Content-Type", "text/xml; boundary=" + boundary);
		DataOutputStream request = new DataOutputStream(
				connection.getOutputStream());
		request.writeBytes(twoHyphens + boundary + crlf);
		request.writeBytes("Content-Disposition: form-data; name=\"file" + "\";filename=\"" + xml.getName() + "\"" + crlf);
		request.writeBytes(crlf);
		request.write(IOUtils.toByteArray(new FileInputStream(xml)));
		request.writeBytes(crlf);
		request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
		request.flush();
		request.close();

		InputStream responseStream = new 
				BufferedInputStream(connection.getInputStream());
		BufferedReader responseStreamReader = 
				new BufferedReader(new InputStreamReader(responseStream));
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();

		while ((line = responseStreamReader.readLine()) != null) {
			stringBuilder.append(line).append("\n");
		}

		responseStreamReader.close();
		String response = stringBuilder.toString();
		System.out.println(response);

		if (connection.getResponseCode() != 200) {
			throw new IOException(String.format("Clustering reply was not a 200! Response code: %d", connection.getResponseCode()));
		}else {
			retVal = "Richiesta inviata con successo";

			//System.out.println("IL CLUSTERING HA RISPOSTO");
			//		retVal = new File(String.format("output_%s", xml.getName()));
			//		FileUtils.writeStringToFile(retVal, response, charset);
		}
		//	responseStream.close();
		connection.disconnect();
	}

	public static void sendRequestHTTPClient(String api_url, String xml) throws MalformedURLException, IOException {
		//		String url = "https://selfsolve.apple.com/wcResults.do";

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost postMethod = new HttpPost(api_url);

		// add header
		postMethod.setHeader("Accept", "application/soap+xml,multipart/related,text/*");        
		postMethod.setHeader("Cache-Control", "no-cache");
		postMethod.setHeader("Pragma", "no-cache");
		postMethod.setHeader("Content-Type", "text/xml; charset=utf-8");
		StringEntity xmlEntity = new StringEntity(xml);
		postMethod.setEntity(xmlEntity );


		HttpResponse response = client.execute(postMethod);
		System.out.println("Response Code : "
				+ response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}  
	}
	
	public static void callRemoteURL(String api_url) throws MalformedURLException, IOException {
		//System.out.println(IOUtils.toString(xmlInputFile.toURI().toURL()));
		//	String url = "http://sibylsystem.microsi.it:8080/clustering/webresources/clustering/";
		String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		String retVal = "";
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.
		HttpURLConnection connection = (HttpURLConnection) new URL(api_url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setRequestProperty("Accept-Charset", charset);
		connection.setRequestProperty("Content-Type", "text/xml; boundary=" + boundary);
	
		if (connection.getResponseCode() != 200) {
			throw new IOException(String.format("Clustering reply was not a 200! Response code: %d", connection.getResponseCode()));
		}else {
			retVal = "Richiesta inviata con successo";

			//System.out.println("IL CLUSTERING HA RISPOSTO");
			//		retVal = new File(String.format("output_%s", xml.getName()));
			//		FileUtils.writeStringToFile(retVal, response, charset);
		}
		//	responseStream.close();
		connection.disconnect();
	}


}
