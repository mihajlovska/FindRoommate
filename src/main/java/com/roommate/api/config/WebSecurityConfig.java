package com.roommate.api.config;

import com.roommate.api.model.AppRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/",
				"/error",
				"/favicon.ico",
				"/**/*.png",
				"/**/*.gif",
				"/**/*.svg",
				"/**/*.jpg",
				"/**/*.html",
				"/**/*.css",
				"/**/*.js").permitAll();
		http.authorizeRequests().antMatchers("/", "/signup", "/login", "/logout").permitAll();
		http.authorizeRequests().antMatchers("/userInfo").access("hasRole('" + AppRole.ROLE_USER + "')");
		http.authorizeRequests().antMatchers("/admin").access("hasRole('" + AppRole.ROLE_ADMIN + "')");
		http.authorizeRequests().antMatchers("/welcomeAuthenticated").access("hasRole('" + AppRole.ROLE_USER + "')");
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
		http.authorizeRequests().and().formLogin()
				.loginProcessingUrl("/j_spring_security_check") 
				.loginPage("/login")
				.defaultSuccessUrl("/welcomeAuthenticated")
				.failureUrl("/login?error=true")
				.usernameParameter("username")
				.passwordParameter("password");
		http.authorizeRequests().and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
		http.apply(new SpringSocialConfigurer()).signupUrl("/signup");
	}

    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
