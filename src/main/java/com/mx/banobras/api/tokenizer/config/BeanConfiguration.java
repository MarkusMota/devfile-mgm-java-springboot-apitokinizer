package com.mx.banobras.api.tokenizer.config;

import org.springframework.context.annotation.Bean;

/**
 * BeanConfiguration.java:
 * 
 * Clase para la configuracion de los servicios 
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */


import org.springframework.context.annotation.Configuration;

import com.mx.banobras.api.tokenizer.application.outputport.ILdapOutPort;
import com.mx.banobras.api.tokenizer.application.service.TokenizerUseCaseService;
import com.mx.banobras.api.tokenizer.common.util.CipherAESCommon;
import com.mx.banobras.api.tokenizer.dominio.model.Tokenizer;


@Configuration
public class BeanConfiguration {

	/**
	 * Metodo para obtener crear la inyección del servicio del Token.
	 * 
	 * @param tokenizer - Objeto Toenizer para crear el token.
	 * @param iLdapOutPort - Interfaz para validar con LDAP el usuario.
	 * 
	 * @throws none.
	 */
	@Bean
	TokenizerUseCaseService tokenizerUseCaseService(
			Tokenizer tokenizer, 
			ILdapOutPort iLdapOutPort,
			CipherAESCommon cipherAESCommon) {
		return new TokenizerUseCaseService(tokenizer, iLdapOutPort, cipherAESCommon);
	}
	

	
	
	
		
}
