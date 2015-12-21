package org.ikane;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@Controller
public class DemoSsoOauth2ClientApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(DemoSsoOauth2ClientApplication.class);
	
	@Override
	public void run(String... arg0) throws Exception {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		try {
			Authentication authentication = securityContext.getAuthentication();
			logger.info(authentication.getDetails().toString());
			
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}
	
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DemoSsoOauth2ClientApplication.class, args);
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        logger.info("\n\thttp://localhost:{}{}\n\tProfiles:{}\n", 
				StringUtils.defaultIfEmpty(env.getProperty("server.port"), "8080"), 
				StringUtils.defaultIfEmpty(env.getProperty("server.contextPath"), "/"),
				Arrays.toString(env.getActiveProfiles()));
    }
    
    @RequestMapping(value="/")
    public String home() {
    	return "index";
    }
    
    @RequestMapping(value="/user")
    @ResponseBody
	public Principal user(Principal user) {
		return user;
	}
    
    /**
     * The Class OAuthConfiguration that sets up the OAuth2 single sign on
     * configuration and the web security associated with it.
     */
    @Component
    @Controller
    @EnableOAuth2Sso
    protected static class OAuthClientConfiguration extends WebSecurityConfigurerAdapter {
    	
    	private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    	private static final String CSRF_ANGULAR_HEADER_NAME = "X-XSRF-TOKEN";
    	
    	@Override
    	public void configure(HttpSecurity http) throws Exception {
    		http.antMatcher("/**").authorizeRequests()
    				.antMatchers("/index.html", "/").permitAll().anyRequest()
    				.authenticated().and().csrf().csrfTokenRepository(csrfTokenRepository())
    				.and().addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
    	}
    	
    	private Filter csrfHeaderFilter() {
    		return new OncePerRequestFilter() {
    			
    			@Override
    			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    					throws ServletException, IOException {
    				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    				if (csrf != null) {
    					Cookie cookie = WebUtils.getCookie(request, CSRF_COOKIE_NAME);
    					String token = csrf.getToken();
    					if (cookie == null || token != null
    							&& !token.equals(cookie.getValue())) {
    						cookie = new Cookie(CSRF_COOKIE_NAME, token);
    						cookie.setPath("/");
    						response.addCookie(cookie);
    					}
    				}
    				filterChain.doFilter(request, response);
    			}
    		};
    	}
    	
    	/**
    	 * Angular sends the CSRF token in a custom header named "X-XSRF-TOKEN"
    	 * rather than the default "X-CSRF-TOKEN" that Spring security expects.
    	 * Hence we are now telling Spring security to expect the token in the
    	 * "X-XSRF-TOKEN" header.<br><br>
    	 * 
    	 * This customization is added to the <code>csrf()</code> filter.
    	 * 
    	 * @return
    	 */
    	private CsrfTokenRepository csrfTokenRepository() {
    		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    		repository.setHeaderName(CSRF_ANGULAR_HEADER_NAME);
    		return repository;
    	}
    	
    }
    
}
