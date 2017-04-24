package org.ikane.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Client
public class Oauth2ClientConfig {

	@Value("${oauth.resource:http://localhost:8080}")
	private String baseUrl;

	@Value("${oauth.authorize:http://localhost:9999/uaa/oauth/authorize}")
	private String authorizeUrl;

	@Value("${oauth.token:http://localhost:9999/uaa/oauth/token}")
	private String tokenUrl;
	
	@Bean
	public OAuth2RestOperations restTemplate(OAuth2ClientContext oauth2ClientContext) {
		return new OAuth2RestTemplate(remote(), oauth2ClientContext);
	}

	@Bean
	public OAuth2ProtectedResourceDetails remote() {
		ClientCredentialsResourceDetails clientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
		clientCredentialsResourceDetails.setAccessTokenUri(tokenUrl);
		clientCredentialsResourceDetails.setClientId("acme");
		clientCredentialsResourceDetails.setClientSecret("acmesecret");
		clientCredentialsResourceDetails.setScope(Arrays.asList("openid"));
		return clientCredentialsResourceDetails;
	}
}
