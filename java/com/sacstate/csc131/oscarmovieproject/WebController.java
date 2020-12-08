package com.sacstate.csc131.oscarmovieproject;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebController {

	//****************** This endpoint returns a SINGLETON ********************************
	
	//This function receives a GET request with with movieId and returns a SINGLE Oscar-nominated movie.
	//Input: GET request with id.
	//Output: Response to client in the JSON format containing Oscar-nominated movie data.
	//Accepted path variables: movieId
	@GetMapping("/movieById/{id}")
	@ResponseBody
	public String getMovieById(@PathVariable int id) throws JSONException {
		
		//Retrieve the JSON file containing Oscar-nominated movie data from local project
		JSONObject obj = null;
		try (InputStream input = new FileInputStream("oscar_award_data.json")) {
		    try {
				obj = new JSONObject(new JSONTokener(input));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Access the data in the JSON file.
		//Iterate through data and get the movie item by its id.
		JSONArray jsonArr = obj.getJSONArray("results");
		List<JSONObject> itemsToRemove = new ArrayList<JSONObject>();
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject jsonObj = jsonArr.getJSONObject(id);    
	      
		//Sending out the response in the form of String where the string content is in JSON format.
		return jsonObj.toString();
	}

	
	//****************** This endpoint returns a COLLECTION ********************************
	
	//This function receives a GET request with movieTitle and returns a list of Oscar-nominated
	// movies that contains the searched movie title.
	//Input: GET request with movieTitle from client.
	//Output: Response to client in JSON format containing Oscar-nominated movie data.
	//Accepted query parameters: movieTitle
	@GetMapping("/movieByTitle/{title}")
	@ResponseBody
	public String getMovieByTitle(@PathVariable String title) throws JSONException {
		
		//Retrieve the JSON file containing Oscar-nominated movie data from local project
		JSONObject obj = null;
		try (InputStream input = new FileInputStream("oscar_award_data.json")) {
		    try {
				obj = new JSONObject(new JSONTokener(input));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Access the data in the JSON file. Iterate through data and
		//Add to our list of oscar-movies that will be exported later.
		JSONArray jsonArr = obj.getJSONArray("results");
		List<JSONObject> itemsToRemove = new ArrayList<JSONObject>();
		List<JSONObject> list = new ArrayList<JSONObject>();
	    for (int i = 0 ; i < jsonArr.length(); i++) {
	        JSONObject jsonObj = jsonArr.getJSONObject(i);
        	list.add(jsonObj);
	    }
		
	    //iterate through entire list to filter the list against the query parameters provided by client GET request.
	    for (int i = 0 ; i < list.size(); i++) {
	        JSONObject jsonObj = list.get(i);
	        
	        //remove item if it does not contain the provided movieTitle
	        if(!jsonObj.getString("film").contains(title)) {
	        	if(!itemsToRemove.contains(jsonObj)) {
	        		itemsToRemove.add(jsonObj);
	        	}
	        }
	    }
	    
	    //remove unwanted movies
	    list.removeAll(itemsToRemove);
	    itemsToRemove.clear();	    
	      
		//Sending out the response in the form of String where the string content is in JSON format.
		return list.toString();
	}
	
	//************* This endpoint allows search of all fields and returns a COLLECTION *********************

	//This function receives a GET request with query parameters and returns a list of Oscar-nominated
	// movies that is filtered against the provided query parameters.
	//Input: GET request with query parameters from client.
	//Output: Response to client in the JSON format containing Oscar-nominated movie data.
	//Accepted query parameters: film, year_film, year_ceremony, category, name, ceremony, winner
	@RequestMapping("/searchAnyField")
	public @ResponseBody String searchAnyField(@RequestParam("film") String movieName, @RequestParam("year_film") String movieYear,
						@RequestParam("year_ceremony") String ceremonyYear, @RequestParam("category") String awardCategory,
						@RequestParam("name") String winnerName, @RequestParam("ceremony") String ceremonyNumber,
						@RequestParam("winner") String isWinner) throws JSONException {
		

		//Retrieve the JSON file containing Oscar-nominated movie data from local project
		JSONObject obj = null;
		try (InputStream input = new FileInputStream("oscar_award_data.json")) {
		    try {
				obj = new JSONObject(new JSONTokener(input));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Access the data in the JSON file. Iterate through data and
		//Add to our list of oscar-movies that will be exported later.
		JSONArray jsonArr = obj.getJSONArray("results");
		List<JSONObject> itemsToRemove = new ArrayList<JSONObject>();
		List<JSONObject> list = new ArrayList<JSONObject>();
	    for (int i = 0 ; i < jsonArr.length(); i++) {
	        JSONObject jsonObj = jsonArr.getJSONObject(i);
        	list.add(jsonObj);
	    }
		
	    //iterate through entire list to filter the list against the query parameters provided by client GET request.
	    for (int i = 0 ; i < list.size(); i++) {
	        JSONObject jsonObj = list.get(i);
	        
	        //if parameter = "", then user did not provide a filter parameter. 
	        //Otherwise, remove item if it does not match provided parameter.
	        if(movieName != "" && !jsonObj.getString("film").contains(movieName)) {
	        	if(!itemsToRemove.contains(jsonObj)) {
	        		itemsToRemove.add(jsonObj);
	        	}
	        }
	        if(movieYear != "" && !jsonObj.getString("year_film").equals(movieYear)) {
	        	if(!itemsToRemove.contains(jsonObj))
	        		itemsToRemove.add(jsonObj);
	        }
	        if(ceremonyYear != "" && !jsonObj.getString("year_ceremony").equals(ceremonyYear)) {
	        	if(!itemsToRemove.contains(jsonObj))
	        		itemsToRemove.add(jsonObj);	  
	        }
	        if(ceremonyNumber != "" && !jsonObj.getString("ceremony").equals(ceremonyNumber)) {
	        	if(!itemsToRemove.contains(jsonObj))
	        		itemsToRemove.add(jsonObj);
	        }
	        if(winnerName != "" && !jsonObj.getString("name").equals(winnerName)) {
	        	if(!itemsToRemove.contains(jsonObj))
	        		itemsToRemove.add(jsonObj);
	        }
	        if(awardCategory != "" && !jsonObj.getString("category").equals(awardCategory)) {
	        	if(!itemsToRemove.contains(jsonObj))
	        		itemsToRemove.add(jsonObj);
	        }
	        if(isWinner != "" && !jsonObj.getString("winner").equals(isWinner)) {
	        	if(!itemsToRemove.contains(jsonObj))
	        		itemsToRemove.add(jsonObj);	 
	        }	
	        }
	    
	    //Remove the unwanted movie items.
	    list.removeAll(itemsToRemove);
	    itemsToRemove.clear();
	    
	    //Sending out the response in the form of String where the string content is in JSON format.
		return list.toString();
	}
}
