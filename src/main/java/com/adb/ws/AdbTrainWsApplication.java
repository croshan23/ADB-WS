package com.adb.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AdbTrainWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdbTrainWsApplication.class, args);
	}
	
	/*
	 * Making beans available in SpringContext. These classes are not itself beans.
	 * So you need to make it if you want it to behave as beans.
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
	
/*	 if you want to have custome bean name use @Bean(name="yourName")
	 however just className still works
	@Bean(name="myOwnName") just to make your own named bean
	public AppProperties appProperties() {
		return new AppProperties();
	}*/
	
	
}
