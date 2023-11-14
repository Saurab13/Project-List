package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {
	@Bean
	UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(getUserDetailsService());
		provider.setPasswordEncoder(passwordEncoder());

		return provider;

	}

	@Bean
	AuthenticationManager authenticationManagerbean(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();

	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests((authorize) -> authorize.requestMatchers("/user/**").hasRole("USER")
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/**").permitAll().anyRequest()
						.authenticated())

				.formLogin(login -> login.loginPage("/signin").defaultSuccessUrl("/user/index"));

		http.authenticationProvider(authenticationProvider());
		DefaultSecurityFilterChain build = http.build();
		return build;

	}

}
