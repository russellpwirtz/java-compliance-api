package complyadvantage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ComplyAdvantage {

	protected static String apiKey 	= ComplyAdvantage.buildKey(Settings.key);
	
	private HttpURLConnection conn;	
	
	/**
	 * Get search results by string array
	 * 
	 * @return 
	 * @overload 
	 */
	public JSONObject search(String[] names) throws MalformedURLException, IOException{
		JSONObject json = new JSONObject();
		JSONArray s = new JSONArray();
		
		for(String name : names){
			s.add(name);
		}
		
		json.put("names", s);
		return this.search(json.toJSONString());
	}
	
	/**
	 * Get search results by Json Object
	 * @overload 
	 */
	public JSONObject search(JSONObject json) throws MalformedURLException, IOException{
		return this.search(json.toJSONString());
	}
	
	/**
	 * Get names by Json string 
	 * 
	 * @param jsonStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public JSONObject search(String jsonStr) throws MalformedURLException, IOException{
		
		JSONParser parser = new JSONParser(); 
		JSONObject json   = new JSONObject();
		
		try {
			json = (JSONObject) parser.parse(this.doPost(new URL(Settings.searchURL),jsonStr));
		}
		catch (MalformedURLException e) {
			json.put("error", e.getMessage());
		}
		catch (IOException e) {
			json.put("error", e.getMessage());
		}
		catch (ParseException e) {
			json.put("error", e.getMessage());
		}
		
		return json;
	}
	
	/**
	 * Get Entity by id 
	 * 
	 * @param id String (id number of the entity)
	 * @return JSONObject (data about entity)
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public JSONObject getEntity(String id){
		
		JSONParser parser = new JSONParser(); 
		JSONObject json = new JSONObject();
		
		try {
			json = (JSONObject) parser.parse(this.doGet(new URL(Settings.entityURL+id)));
		}
		catch (MalformedURLException e) {
			json.put("error", e.getMessage());
		}
		catch (IOException e) {
			json.put("error", e.getMessage());
		}
		catch (ParseException e) {
			json.put("error", e.getMessage());
		}
		
		return json;

	}
	
	
	/**
	 * Encode string to Base64 format 
	 *  
	 * @param s
	 * @return
	 */
	public String encode(String s){
		String coded = DatatypeConverter.printBase64Binary(s.getBytes());
		return  new String(coded);
	}
	
	/**
	 * Build Authentication key for http requests
	 *  
	 * @param key ()
	 * @return
	 */
	public static String buildKey(String key){
		return key + ":" + "x";
	}

	
	/**
	 * Send get request to ComplyAdvantage Api as json
	 * 
	 * @param url
	 * @throws IOException
	 */
	private String doGet(URL url) throws IOException{
		
		try
		{
			System.setProperty("java.net.preferIPv4Stack" , "true");

			// http request
			this.conn =  (HttpURLConnection) url.openConnection();
			this.conn.setRequestMethod("GET");
			this.conn.setRequestProperty("Authorization", "Basic " + this.encode(apiKey));
			this.conn.setRequestProperty("Accept", "application/json");
			// buffer response
			BufferedReader br = new BufferedReader(new InputStreamReader((this.conn.getInputStream())));
			String response = br.readLine();
			return response;

		}finally{
			this.conn.disconnect();
		}					 
	}
	
	/**
	 * Send post request to ComplyAdvantage Api json
	 * 
	 * @param url
	 * @param params (Data to be posted)
	 * @throws IOException (Response code)
	 */
	private String doPost(URL url, String params) throws IOException{
		
		try
		{
			System.setProperty("java.net.preferIPv4Stack" , "true");

			this.conn = (HttpURLConnection) url.openConnection(); // open connection	
			this.conn.setDoInput(true);
			this.conn.setDoOutput(true); 
			this.conn.setInstanceFollowRedirects(false); 		
			this.conn.setRequestMethod("POST"); 
			this.conn.setRequestProperty("Content-Type", "application/json"); 
			this.conn.setRequestProperty("Authorization", "Basic " + this.encode(apiKey));
			this.conn.setRequestProperty("Accept", "application/json");
			// send request
			OutputStream os = this.conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(params);
			writer.flush();
			writer.close();
			os.close();

			// buffer response
			BufferedReader br = new BufferedReader(new InputStreamReader((this.conn.getInputStream())));
			String response = br.readLine();
								
			return response;

		}finally{
			this.conn.disconnect();
		}			 
	}
}
	
