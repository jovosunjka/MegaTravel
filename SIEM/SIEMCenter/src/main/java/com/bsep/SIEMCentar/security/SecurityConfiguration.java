package com.bsep.SIEMCentar.security;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import .annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bsep.SIEMCenter.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // ovo smo ubacili da bismo u controllerima mogli 
													//koristiti anotaciju @PreAuthorize
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
		AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
		authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationTokenFilter;
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.csrf().disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.authorizeRequests()
				//.antMatchers("/userAdmin/**")
				//	.hasAuthority("USER_ADMINISTRATOR")
				.antMatchers("/users/login", "/passengers/registrate")
					.permitAll();
				//.antMatchers(HttpMethod.POST, "/api/**")
				//	.hasAuthority("ROLE_ADMIN") //only administrator can add and edit data
				/*.anyRequest().authenticated();*/
				//if we use AngularJS on client side
				//.and().csrf().csrfTokenRepository(csrfTokenRepository());
		
		// Custom JWT based authentication
		httpSecurity.addFilterBefore(authenticationTokenFilterBean(),
				UsernamePasswordAuthenticationFilter.class);
	} 
	
	/**
	 * If we use AngularJS as a client application, it will send CSRF token using 
	 * the name X-XSRF token. We have to tell Spring to expect this name instead of 
	 * X-CSRF-TOKEN (which is default one)
	 * @return
	 */
//	private CsrfTokenRepository csrfTokenRepository() {
//		  HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//		  repository.setHeaderName("X-XSRF-TOKEN");
//		  return repository;
//	}

}
