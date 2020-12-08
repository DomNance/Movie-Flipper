package com.sacstate.csc131.oscarmovieproject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WebController.class)
class WebControllerTest {
	
	@Autowired MockMvc mvc;
	
	@Test
	void MockMovieByIdEndpoint_MakeCallAndGetRepsonse_MovieReturnedIsMovieExpected() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/movieById/123");
		MvcResult result = mvc.perform(request).andReturn();
		
		String expectedResult = "{\"winner\":\"False\",\"ceremony\":\"4\",\"year_film\":\"1930\",\"name\":\"Richard Day\",\"film\":\"Whoopee!\",\"category\":\"ART DIRECTION\",\"year_ceremony\":\"1931\"}";
		
		
		assertEquals(expectedResult, result.getResponse().getContentAsString());
	}
	
	@Test
	void MockMovieByTitleEndpoint_MakeCallAndGetRepsonse_MovieReturnedIsMovieExpected() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/movieByTitle/Saria");
		MvcResult result = mvc.perform(request).andReturn();
		
		String expectedResult = "[{\"winner\":\"False\",\"ceremony\":\"92\",\"year_film\":\"2019\",\"name\":\"Bryan Buckley and Matt Lefebvre\",\"film\":\"Saria\",\"category\":\"SHORT FILM (Live Action)\",\"year_ceremony\":\"2020\"}]";
		
		
		assertEquals(expectedResult, result.getResponse().getContentAsString());
	}
	
	@Test
	void MockSearchAnyFieldEndpoint_MakeCallAndGetRepsonse_MovieReturnedIsMovieExpected() throws Exception {
		
		LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
		requestParams.add("film", "Saria");
		requestParams.add("year_film", "2019");
		requestParams.add("ceremony", "92");
		requestParams.add("year_ceremony", "2020");
		requestParams.add("name", "Bryan Buckley and Matt Lefebvre");
		requestParams.add("category", "SHORT FILM (Live Action)");
		requestParams.add("winner", "False");
		
		RequestBuilder request = MockMvcRequestBuilders.get("/searchAnyField").params(requestParams);
		MvcResult result = mvc.perform(request).andReturn();
		
		String expectedResult = "[{\"winner\":\"False\",\"ceremony\":\"92\",\"year_film\":\"2019\",\"name\":\"Bryan Buckley and Matt Lefebvre\",\"film\":\"Saria\",\"category\":\"SHORT FILM (Live Action)\",\"year_ceremony\":\"2020\"}]";
		
		
		assertEquals(expectedResult, result.getResponse().getContentAsString());
	}

}
