package mx.gob.banobras.apitokenizer.application.service;

/**
 * TokenizerUseCaseService.java:
 * 
 * Clase de tipo @Service que contiene las funciones del caso de uso del Api TOkenizer.
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see Documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import mx.gob.banobras.apitokenizer.application.port.in.ITokenizerCasoUsoService;
import mx.gob.banobras.apitokenizer.application.port.out.ILdapApiRestClient;
import mx.gob.banobras.apitokenizer.common.util.CipherAESCommon;
import mx.gob.banobras.apitokenizer.common.util.ConstantsToken;
import mx.gob.banobras.apitokenizer.dominio.model.Tokenizer;
import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.ErrorMessageDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.LdapResponseDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.TokenizerResponseDTO;


@Service
public class TokenizerCasoUsoServiceImpl implements ITokenizerCasoUsoService {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(TokenizerCasoUsoServiceImpl.class);

	/** Variable para inejctar la clase Tokenizer */
	private final Tokenizer tokenizer;

	/** Variable para inejctar la clase ILdapApiRestClient, para conexión a LDAP */
	private final ILdapApiRestClient iLdapApiRestClient;
	/** Variable para inejctar la clase CipherAESCommon, para desencriptar */
	private final CipherAESCommon cipherAESCommon;


	/**
	 * Constructor para inyectar los objetos Tokenizer, ILdapOutPort,
	 * CipherAESCommon
	 * 
	 * @param Tokenizer    Objeto de dominio el Api Tokenizer.
	 * @param iLdapOutPort Interface de puerto de salida para conectarse al LDAP.
	 * @Param CipherAESCommon Componente para desencriptar datos.
	 */
	public TokenizerCasoUsoServiceImpl(
			Tokenizer tokenizer, 
			ILdapApiRestClient iLdapApiRestClient,
			CipherAESCommon cipherAESCommon) {
		this.tokenizer = tokenizer;
		this.iLdapApiRestClient = iLdapApiRestClient;
		this.cipherAESCommon = cipherAESCommon;
	}

	/**
	 * Metodo para crear el Token
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para generar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del token.
	 * 
	 */
	@Override
	public TokenizerResponseDTO createToken(TokenizerDTO tokenizerDTO) {

		/** Variable que contiene el objeto de respuesta del token */
		TokenizerResponseDTO tokenizerResponseDTO = null;
		LdapResponseDTO ldapResponseDTO = new LdapResponseDTO();

		try {

			if (tokenizerDTO.getCredentials().isEmpty()) {
				throw new IllegalArgumentException(ConstantsToken.MSG_CREDENTIALS_EMPTY.getName());
			}
			tokenizerDTO = cipherAESCommon.getDataCredentials(tokenizerDTO);

			log.info(new StringBuilder().append("Busca el usuairo en LDAP: ").append(tokenizerDTO.getUserName()));

			/** Busca el usuario que exista en LDAP */
			ldapResponseDTO = iLdapApiRestClient.authorizationLDAP(tokenizerDTO);

			/** Si encontro el usuario continua para generar el token */
			if (ldapResponseDTO.getStatusCode() == 200) {
				log.info("Inicia getToken()");
				tokenizerResponseDTO = tokenizer.getToken(tokenizerDTO);

			} else {
				ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(),
						ldapResponseDTO.getErrorMessageDTO().getTimestamp(),
						ldapResponseDTO.getErrorMessageDTO().getMessage());
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
				tokenizerResponseDTO.setErrorMessageDTO(message);
			}

		} catch (IOException | InterruptedException | NoSuchAlgorithmException |

				KeyManagementException ioex) {
			log.error("IOException | InterruptedException");
			log.error(ConstantsToken.COMMUNICATION_EXCEPTION_LDAP.getName(), ioex);
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), new Date(),
					ConstantsToken.COMMUNICATION_EXCEPTION_LDAP.getName() + "-- " + ioex.getMessage() + " -- "
							+ ioex.getMessage() + " -- " + tokenizerDTO.getPassword());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
			Thread.currentThread().interrupt();
		} catch (IllegalArgumentException eil) {
			eil.printStackTrace();
			log.error(ConstantsToken.ILLEGAL_ARG_EXCEPTION.getName(), eil);
			ErrorMessageDTO errorMessage = new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(),
					eil.getMessage() + " -- " + getStack(eil.getStackTrace()));
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), new Date(),
					ex.getCause().getMessage() + " -- " + getStack(ex.getStackTrace()));
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
	 *         contiene los datos del token.
	 * 
	 */
	@Override
	public TokenizerResponseDTO validateToken(TokenizerDTO tokenizerDTO) {
		TokenizerResponseDTO tokenizerResponseDTO = null;
		LdapResponseDTO ldapResponseDTO = new LdapResponseDTO();
		

		log.info("Inica validarToken");
		try {

			if (tokenizerDTO.getCredentials().isEmpty()) {
				throw new IllegalArgumentException(ConstantsToken.MSG_CREDENTIALS_EMPTY.getName());
			}
			tokenizerDTO = cipherAESCommon.getDataCredentials(tokenizerDTO);

			log.info("Busca el usuairo en LDAP");

			/** Busca el usuario que exista en LDAP */
			ldapResponseDTO = iLdapApiRestClient.authorizationLDAP(tokenizerDTO);

			/** Si encontro el usuario continua para generar el token */
			if ((ldapResponseDTO != null && ldapResponseDTO.getStatusCode() == 200)) {
				log.info("Inica validaToken()");
				tokenizerResponseDTO = tokenizer.validaToken(tokenizerDTO);
				log.info("Termina validaToken()");
			} else {
				if (ldapResponseDTO != null) {
					ErrorMessageDTO message = new ErrorMessageDTO(ldapResponseDTO.getStatusCode(),
							ldapResponseDTO.getErrorMessageDTO().getTimestamp(),
							ldapResponseDTO.getErrorMessageDTO().getMessage());
					tokenizerResponseDTO = new TokenizerResponseDTO();
					tokenizerResponseDTO.setStatusCode(ldapResponseDTO.getStatusCode());
					tokenizerResponseDTO.setErrorMessageDTO(message);
				}
			}

		} catch (IOException | InterruptedException | NoSuchAlgorithmException | KeyManagementException ioex) {
			log.error("IOException | InterruptedException");
			log.error(ConstantsToken.COMMUNICATION_EXCEPTION_LDAP.getName(), ioex);
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), new Date(),
					ConstantsToken.COMMUNICATION_EXCEPTION_LDAP.getName() + "-- " + ioex.getMessage() + " -- "
							+ ioex.getMessage() + " -- " + tokenizerDTO.getPassword());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
			Thread.currentThread().interrupt();
		} catch (Exception ex) {
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), date,
					ex.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}
		log.info("Termina validarToken");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para crear el Token para los sistemas publicos.
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para generar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del toekn.
	 * 
	 */
	@Override
	public TokenizerResponseDTO createTokenPublic(TokenizerDTO tokenizerDTO) {

		/** Variable que contiene el objeto de respuesta del token */
		TokenizerResponseDTO tokenizerResponseDTO = null;
		try {

			log.info("Inicia getToken() public");

			tokenizerDTO = cipherAESCommon.getDataCredentials(tokenizerDTO);

			tokenizerResponseDTO = tokenizer.getToken(tokenizerDTO);

		} catch (IllegalArgumentException eil) {
			log.error(ConstantsToken.ILLEGAL_ARG_EXCEPTION.getName(), eil);
			ErrorMessageDTO errorMessage = new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(),
					eil.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);

		} catch (Exception ex) {
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
			ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.FORBIDDEN.value(), new Date(),
					ex.getCause().getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.FORBIDDEN.value());
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}
		log.info("Finaliza getToken() public");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para validar el Token para los sistemas publicos.
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

		} catch (IllegalArgumentException eil) {
			log.error(ConstantsToken.ILLEGAL_ARG_EXCEPTION.getName(), eil);
			ErrorMessageDTO errorMessage = new ErrorMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(),
					eil.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);

		} catch (Exception ex) {
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
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

	private String getStack(StackTraceElement[] sss) {
		StackTraceElement[] stack = sss;
		StringBuilder exception = new StringBuilder();
		for (StackTraceElement s : stack) {
			exception.append(s.toString()).append("&&");
		}
		return exception.toString();

	}

}
