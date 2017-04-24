package org.ikane.api;

import java.lang.invoke.MethodHandles;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Api {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private OAuth2RestOperations oAuthRestTemplate;

	@GetMapping(path="/api/hello")
	public String sayHello() {
		return "hello";
	}
	
	@GetMapping(path="/api/date")
	public String getDate() {
		return Instant.now().toString();
	}
	
	
	@GetMapping(path="/api/oauth")
	public String getOauthResource() {
		
		LOGGER.info("Making Oauth Api Call");
		
		String result = this.oAuthRestTemplate.getForObject("http://localhost:9999/uaa/coucou", String.class);
		
		LOGGER.info("Call Result: {}", result);
		
		return result;
	}
}
