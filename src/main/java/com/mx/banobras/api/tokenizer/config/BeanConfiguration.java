package com.mx.banobras.api.tokenizer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.mx.banobras.api.tokenizer.application.outputport.IUserRepositoryOutPort;
import com.mx.banobras.api.tokenizer.application.service.TokenizerUseCaseService;
import com.mx.banobras.api.tokenizer.dominio.model.Tokenizer;

@Configuration
public class BeanConfiguration {

	 	
	@Bean
	public TokenizerUseCaseService tokenizerUseCaseService(
			IUserRepositoryOutPort iUserRepositoryOutPort, 
			Tokenizer tokenizer) {
	        return new TokenizerUseCaseService(iUserRepositoryOutPort, tokenizer );
	        
	    }

	
	
}
