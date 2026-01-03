package com.myshop.myshopbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class })
public class MyshopbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyshopbackendApplication.class, args);
	}

}
