package com.mx.banobras.api.tokenizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class BanobrasApiTokenizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BanobrasApiTokenizerApplication.class, args);
	}

}
