package com.educatoon.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("123456");
        System.out.println(hashed);
	}

}
