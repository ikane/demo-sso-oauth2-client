package org.ikane.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * The Class OAuthConfiguration that sets up the OAuth2 single sign on
 * configuration and the web security associated with it.
 */
@Component
@Controller
//@EnableOAuth2Sso
public class OAuthClientConfiguration extends WebSecurityConfigurerAdapter {
	
	/*
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
	        .formLogin()
	            .loginPage("/login")
	            .defaultSuccessUrl("/index")

	        .and()
	            .authorizeRequests().antMatchers("/login").permitAll()
	                                .antMatchers("/**").authenticated();
		
		http.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}
	*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.formLogin().and()
			.httpBasic();
	}
}
