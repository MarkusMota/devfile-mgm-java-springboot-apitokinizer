package com.mx.banobras.api.tokenizer.application.service;

/**
 * TokenizerUseCaseService.java:
 * 
 * Clase de tipo @Service que contiene las funciones del caso de uso del Api TOkenizer
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mx.banobras.api.tokenizer.application.inputport.ITokenizerInputPort;
import com.mx.banobras.api.tokenizer.application.outputport.ILdapOutPort;
import com.mx.banobras.api.tokenizer.common.util.CipherAESCommon;
import com.mx.banobras.api.tokenizer.common.util.CommonConstant;
import com.mx.banobras.api.tokenizer.dominio.model.Tokenizer;
import com.mx.banobras.api.tokenizer.dominio.model.TokenizerDTO;
import com.mx.banobras.api.tokenizer.infraestructure.adapter.input.dto.ErrorMessageDTO;
import com.mx.banobras.api.tokenizer.infraestructure.adapter.input.dto.TokenizerResponseDTO;
import com.mx.banobras.api.tokenizer.infraestructure.adapter.output.client.LdapVO;

@Service
public class TokenizerUseCaseService implements ITokenizerInputPort {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(TokenizerUseCaseService.class);

	/** Variable para inejctar la clase Tokenizer */
	private final Tokenizer tokenizer;
	/** Variable para inejctar la clase ILdapOutPort, para conexión a LDAP */
	private final ILdapOutPort iLdapOutPort;
	/** Variable para inejctar la clase CipherAESCommon, para desencriptar */
	private final CipherAESCommon cipherAESCommon;
	
	/**
	 * Constructor para inyectar los objetos Tokenizer, ILdapOutPort
	 * 
	 * @param tokenizer    Objeto de dominio el Api Tokenizer
	 * @param iLdapOutPort Interface de puerto de salida para conectarse al LDAP
	 * 
	 */
	public TokenizerUseCaseService(
			Tokenizer tokenizer, 
			ILdapOutPort iLdapOutPort,
			CipherAESCommon cipherAESCommon) {
		this.tokenizer = tokenizer;
		this.iLdapOutPort = iLdapOutPort;
		this.cipherAESCommon = cipherAESCommon;
	}

	/**
	 * Metodo para crear el Token
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para generar el toekn.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del toekn.
	 * 
	 */
	@Override
	public TokenizerResponseDTO createToken(TokenizerDTO tokenizerDTO) {

		/** Variable que contiene el objeto de respuesta del token */
		TokenizerResponseDTO tokenizerResponseDTO = null;

		try {

			
			if(tokenizerDTO.getCredentials().isEmpty()) {
				throw new IllegalArgumentException(CommonConstant.MSG_CREDENTIALS_EMPTY.getName());
			}
			tokenizerDTO = cipherAESCommon.getDataCredentials(tokenizerDTO);
			
			log.info(new StringBuilder().append("Busca el usuairo en LDAP: ").append(tokenizerDTO.getUserName()));
			/** Busca el usuario que exista en LDAP */
			LdapVO ldapVO = iLdapOutPort.findByUsername(tokenizerDTO);
			/** Si encontro el usuario continua para generar el token */
			if (ldapVO != null) {
				log.info("Inicia getToken()");
				tokenizerResponseDTO = tokenizer.getToken(tokenizerDTO);
				
			} else {
				ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), new Date(),
						"Usuario no existe en LDAP");
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
				tokenizerResponseDTO.setErrorMessageDTO(message);
			}

		} catch (javax.naming.CommunicationException ex1) {
			ex1.printStackTrace();
			log.error("No hay conexion a LDAP", ex1);
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), new Date(),
					"Servicio no disponible - LDAP");
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}catch (IllegalArgumentException eil) {
			log.error(eil.getMessage(), eil);
			ErrorMessageDTO errorMessage = new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), eil.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error de Exception", ex);
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), new Date(),
					ex.getCause().getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}
		log.info("Finaliza getToken()");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para validar el Token
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para validar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del tokrn.
	 * 
	 */
	@Override
	public TokenizerResponseDTO validateToken(TokenizerDTO tokenizerDTO) {
		TokenizerResponseDTO tokenizerResponseDTO = null;
		log.info("Inica validarToken");
		try {

			if(tokenizerDTO.getCredentials().isEmpty()) {
				throw new IllegalArgumentException(CommonConstant.MSG_CREDENTIALS_EMPTY.getName());
			}
			tokenizerDTO = cipherAESCommon.getDataCredentials(tokenizerDTO);
			
			log.info("Busca el usuairo en LDAP");
			/** Busca el usuario que exista en LDAP */
			LdapVO ldapVO = iLdapOutPort.findByUsername(tokenizerDTO);

			/** Si encontro el usuario continua para generar el token */
			if (ldapVO != null) {
				log.info("Inica validaToken()");
				tokenizerResponseDTO = tokenizer.validaToken(tokenizerDTO);
				log.info("Termina validaToken()");
			} else {
				ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), new Date(),
						"Usuario no existe en LDAP");
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
				tokenizerResponseDTO.setErrorMessageDTO(message);
			}

		} catch (javax.naming.CommunicationException ex1) {
			ex1.printStackTrace();
			log.error("No hay conexion a LDAP", ex1);
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), date,
					"Servicio no disponible - LDAP");
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Se fue por la Exception", ex);
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), date,
					ex.getCause().getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}
		log.info("Termina validarToken");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para crear el Token
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para generar el toekn.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del toekn.
	 * 
	 */
	@Override
	public TokenizerResponseDTO createTokenPublic(TokenizerDTO tokenizerDTO) {

		/** Variable que contiene el objeto de respuesta del token */
		TokenizerResponseDTO tokenizerResponseDTO = null;
		try {

			log.info("Inicia getToken()");
			
			tokenizerDTO = cipherAESCommon.getDataCredentials(tokenizerDTO);
			
			tokenizerResponseDTO = tokenizer.getToken(tokenizerDTO);

		}catch (IllegalArgumentException eil) {
			log.error(eil.getMessage(), eil);
			ErrorMessageDTO errorMessage = new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), eil.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error de Exception", ex);
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), new Date(),
					ex.getCause().getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}
		log.info("Finaliza getToken()");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para validar el Token
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para validar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del tokrn.
	 * 
	 */
	@Override
	public TokenizerResponseDTO validateTokenPublic(TokenizerDTO tokenizerDTO) {
		TokenizerResponseDTO tokenizerResponseDTO = null;
		log.info("Inica validateTokenPublic()");
		try {
			tokenizerDTO = cipherAESCommon.getDataCredentials(tokenizerDTO);
			tokenizerResponseDTO = tokenizer.validaToken(tokenizerDTO);

		}catch (IllegalArgumentException eil) {
			log.error(eil.getMessage(), eil);
			ErrorMessageDTO errorMessage = new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), eil.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Exception", ex);
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), date,
					ex.getCause().getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}
		log.info("Termina validateTokenPublic()");
		return tokenizerResponseDTO;
	}

}
