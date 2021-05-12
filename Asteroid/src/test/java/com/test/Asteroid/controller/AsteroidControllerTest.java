package com.test.Asteroid.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class AsteroidControllerTest {

	@Test
	public void  testGetAsteroid() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        
        final String baseUrl = "http://localhost:8080/asteroid/?planet=Earth";
        URI uri = new URI(baseUrl);
     
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
         
        Assert.assertEquals(200, result.getStatusCode().value());
        
	}

}
