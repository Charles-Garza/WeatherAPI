package application.controller;

import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WeatherController implements EventHandler
{
	@FXML private Label Weather, prompt, output, summary;
	@FXML private Button btn;
	@FXML private ImageView img;
	@FXML private TextField cityLocation;
	@FXML private ProgressIndicator progIndicator;
	private String latitude, longitude, isp, 
	//developerAPIKey = "da16336962592e022a90a30895ec83b3"; // Charles's developer key
	temperature, condition, status = "OK", errMsg = "None",
	icon, formattedAddress;
	// If the site returns an error with retrieving information
	//  then update the key to a new key
	private String developerAPIKey = "714eda33e43cde0ca430cedc8fa306a7"; // Ko's developer key
	private String devIPAPIKey = "919422be279ea2188f9a6adb9969869b"; // Charles's IP API key
	private String devGeolocationAPIKey = "lfvvfPHo9lhqAXy3AY3OypEewp32rQBG";
	public void initialize() 
	{
		progIndicator.setOpacity(0);
		// Sets various graphic component variables to information obtained
		btn.setText("Search!");
		output.setWrapText(true);
		
		/* San Antonio's latitude and longitude
		 *	latitude = "29.424349";
		 *	longitude = "-98.491142"; 
		*/
		
    	String latLongs[];
		try 
		{
			latLongs = getIP();
			if(latLongs != null)
			{
				latitude = latLongs[0];
				longitude = latLongs[1];
			}
			weatherDataCapture();
			setLabels();
		} 
		catch (Exception e) 
		{
			
		}
		output.setText(formattedAddress);
	}
	
	public void button(ActionEvent event) throws Exception
	{
		progIndicator.setOpacity(1);
		progIndicator.setProgress(-1);
		
		startThread();
	}
	
	public void startThread()
	{
		SwingWorker sw1 = new SwingWorker() 
        {
 
            @SuppressWarnings("unchecked")
			@Override
            protected String doInBackground() throws Exception 
            {
    			// define what thread will do here
            	String latLongs[] = tryLatLongsUntilSuccess();
        		if(latLongs != null)
        		{
        			latitude = latLongs[0];
        			longitude = latLongs[1];
        		}
        		Thread.sleep(50);
                publish(1);
                String res = "Finished Execution";
                return res;
            }
 
            @Override
            protected void process(List chunks)
            {
                // define what the event dispatch thread 
                // will do with the intermediate results received
                // while the thread is executing
                progIndicator.setOpacity(1);
            }
 
            @Override
            protected void done() 
            {
                // this method is called when the background 
                // thread finishes execution
        		progIndicator.setOpacity(0);
        		Platform.runLater(new Runnable() 
        		{
        	        @Override
        	        public void run() 
        	        {
        	            //javaFX operations should go here
        	        	if(errMsg.equals("none"))
        	        	{
        	        		summary.setText(null);
        	        		output.setText("Location Not Available");
        	        		Weather.setText(null);
        	        		img.setOpacity(0);
        	        	}
        	        	else if(errMsg.equals("ZERO_RESULTS"))
        	        	{
        	        		summary.setText(null);
        	        		output.setText("Location Not Available");
        	        		Weather.setText(null);
        	        		img.setOpacity(0);
        	        	}
        	        	else
        	        	{
        	        		setLabels();
        	        	}
        	        }
        	   });
            }
        };
        // executes the swingworker on worker thread
        sw1.execute(); 
    }
	
	public void setLabels()
	{
		// I changed the progressbar to a progress indicator so it would visually look better
		if(status.equals("OK"))
		{	
			summary.setText(condition);
			output.setText(formattedAddress);
			Weather.setText(temperature);
			img.setOpacity(1);
			currentWeather(icon); 
		}
	}
	
	public void weatherDataCapture()
	{
		// Trys to obtain information from text field
		try
		{	
			// Captures the temperature from the API
			temperature = readJSONObject("currently", "temperature");
			condition = readJSONObject("minutely", "summary");
			/* tokenizes the information in order to get more precise data from API
			JSONArray tokens = new JSONArray(readJSONObject("minutely", "data")); */
			
			// Captures the Icon message from the API
			icon = readJSONObject("currently", "icon");
			
			/* Use for daily forecast
			for(int i = 0; i < tokens.length(); i++) // Can change to 7 to get weekly forecast
			{
				JSONObject jObj = tokens.getJSONObject(i);
				// Used for testing and possibly usable later
				/*String probability = jObj.get("precipProbability").toString();
				String intensity = jObj.get("precipIntensity").toString();
				String time = jObj.get("time").toString();
				System.out.println(condition + "\nProbability: " +probability + "\n" + "Intensity: " + intensity + 
								   "\n" + "Time: "+ time + "\n");
			}*/
		}        
		catch(Exception err) // Throws an error
		{
			errMsg = err + ""; // Stores error in variable
		}
	}

	// Sets the weather image to the corresponding image
	private void currentWeather(String condition)
	{
		if(condition.equals("clear-day"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/ClearDay.png"));
		}
		else if(condition.equals("clear-night"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/ClearNight.png"));
		}
		else if(condition.equals("rain"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Rain.png"));
		}
		else if(condition.equals("snow"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Snow.png"));
		}
		else if(condition.equals("sleet"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Sleet.png"));
		}
		else if(condition.equals("wind"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Wind.png"));
		}
		else if(condition.equals("fog"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Fog.png"));
		}
		else if(condition.equals("cloudy"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Cloudy.png"));
		}
		else if(condition.equals("partly-cloudy-day"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/PartlyCloudyDay.png"));
		}
		else if(condition.equals("partly-cloudy-night"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/PartlyCloudyNight.png"));
		}
		else if(condition.equals("hail"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Hail.png"));
		}
		else if(condition.equals("thunderstorm"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Thunderstorm.png"));
		}
		else if(condition.equals("tornado"))
		{
			img.setImage(new Image("/resources/drawable/Weather_Icons/Tornado.png"));
		}
	}
	
	// Opens the weatherAPI with the provided latitude and longitude of a given city
	private URL openURL(String developerKey, String latitude, String longitude) throws Exception
	{
		URL url = new URL("https://api.darksky.net/forecast/" + developerKey + "/" + latitude + "," + longitude);
		URLConnection request = url.openConnection();
		request.connect();
		return url;
	}
	
	// Creates a JSONObject and returns a String of the location within the JSONObject's
	//  URL String
	private String readJSONObject(String parent, String child) throws Exception
	{
		JSONObject json = new JSONObject(IOUtils.toString(openURL(developerAPIKey, latitude, longitude), Charset.forName("UTF-8")));
		String location = json.getJSONObject(parent).get(child).toString();
		return location;
	}
	
	private String[] getIP(){
		formattedAddress = "";
		try{
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
			                whatismyip.openStream()));
			String ip = in.readLine(); //you get the IP as a String
//			System.out.println(ip);
			String request = "http://api.ipstack.com/" + ip + "?access_key=" + devIPAPIKey;
			URL url = new URL(request);
//			System.out.println(url);
			
			JSONObject json = new JSONObject(IOUtils.toString((url), Charset.forName("UTF-8")));
			latitude = json.get("latitude").toString();
			longitude = json.get("longitude").toString();
			formattedAddress += " " + json.get("city").toString();
			formattedAddress += ", " + json.get("region_code").toString();
			formattedAddress += ", " + json.get("country_code").toString();
		} catch (Exception e){
			System.out.println(e);
		}
//		System.out.println("Formatted address: " + formattedAddress);
		return null;
	}
	
	// Obtains the latitude and longitude of every address in the world, reading the information 
	//   in via an XML file
	private String[] getLatLongPositions()
    {
		int responseCode;
		formattedAddress = "";
		String temp = cityLocation.getText();
		temp = temp.replaceAll("\\s+", "");
		String api = "http://www.mapquestapi.com/geocoding/v1/address?key=" + devGeolocationAPIKey + "&location=" + temp;
		try{
			URL url = new URL(api);
			
			JSONObject json = new JSONObject(IOUtils.toString((url), Charset.forName("UTF-8")));
			
			JSONArray jsonArr = json.getJSONArray("results");
			responseCode = json.getJSONObject("info").getInt("statuscode");
//			System.out.println(responseCode);
			if(responseCode == 0)
			{
				JSONObject jObj = jsonArr.getJSONObject(0);
				JSONArray locations_components_arr = jObj.getJSONArray("locations");
				JSONObject jObj2 = locations_components_arr.getJSONObject(0);
				if(!jObj2.get("adminArea5").toString().equals(""))
					formattedAddress += jObj2.get("adminArea5").toString() + ", ";
				if(!jObj2.get("adminArea3").toString().equals(""))
					formattedAddress += jObj2.get("adminArea3").toString() + ", ";
				if(!jObj2.get("adminArea1").toString().equals(""))
					formattedAddress += jObj2.get("adminArea1").toString();
				
				double longit = (double) jObj2.getJSONObject("displayLatLng").get("lng");
				double latit = (double) jObj2.getJSONObject("displayLatLng").get("lat"); 
				latitude = latit + "";
				longitude = longit + "";
				
//				System.out.println(formattedAddress);
//	  	        System.out.println(jObj + "\n" + jObj2 + "\n" + latit + "," + longit);
	  	        errMsg = "None";
	  	        status = "OK";
	  	        return new String[] {latitude, longitude, formattedAddress};
			}
			else
			{
				status = responseCode + "";
				errMsg = (status);
				System.out.println(errMsg);
			}
		} catch(Exception e){
			System.out.println(e);
		}
		return null;
    }
	
	private String[] tryLatLongsUntilSuccess() throws Exception
	{
		String[] result = getLatLongPositions();
		if(errMsg.equals("ZERO_RESULTS"))
		{
			return null;
		}
		else if(!errMsg.equals("None"))
		{
			while(!errMsg.equals("None"))
			{
				result = getLatLongPositions();
			}
		}
		return result;
	}

	@Override
	public void handle(Event event) {
		// TODO Auto-generated method stub
		
	}
}