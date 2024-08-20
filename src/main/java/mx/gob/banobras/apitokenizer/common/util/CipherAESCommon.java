package mx.gob.banobras.apitokenizer.common.util;
/**
 * CipherAESCommon.java:
 * 
 * Clase para encriptar y descriptar cadenas alfanumericas, usanod el cifrado AES y 
 * el modo AES/CBC/PKCS5PADDING
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import lombok.Data;
import mx.gob.banobras.apitokenizer.application.port.out.ICipherClient;
import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.CipherResponseDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.DataDTO;

@Data
@Component
public class CipherAESCommon {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(CipherAESCommon.class);


	/** Constante de la llave secreta */
	/** private static final String CIPHER_KEY = System.getenv().get("CIPHER_KEY"); */
	/** Constante del vector de cifrado */
	/** private static final String CIPHER_IV = System.getenv().get("CIPHER_IV"); */
	/** Constante de la tipo de cifrado */
	/** private static final String CIPHER_AES = System.getenv().get("CIPHER_AES"); */
	/** Constante del modo de cifrado */
	/** private static final String CIPHER_MODE = System.getenv().get("CIPHER_MODE"); */
	
	/** Constante de la llave secreta */
	private static final String METHOD_AUTH = System.getenv().get("METHOD_AUTH"); 

	
	/** Variable para inejctar la clase ILdapApiRestClient, para conexión a LDAP */
	private final ICipherClient iCipeherClient;
	
	
	public CipherAESCommon(ICipherClient iCipeherClient) {
		this.iCipeherClient = iCipeherClient;
	}

	/**
	 * Metodo para descriptar las credenciales del usuario, el primer dato
	 * corresponde a userName, el segundo a el password.
	 * 
	 * @param TokenizerDTO objeto que contien los datos de las credenciales.
	 * @return tokenizerDTO.
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws KeyManagementException 
	 * 
	 * @throws IllegalArgumentException Exception en caso no realizar el
	 *                                  descriptado.
	 */
	public TokenizerDTO getDataCredentials(TokenizerDTO tokenizerDTO)
			throws IllegalArgumentException {
		String[] valDecrypt;
		CipherResponseDTO cipherResponseDTO = new CipherResponseDTO();
		try {
			log.info("Incia obtener datos de credentials" + "****** " + METHOD_AUTH);
			
			DataDTO dataDTO = new DataDTO();
			dataDTO.setData(tokenizerDTO.getCredentials());
			
			cipherResponseDTO = iCipeherClient.decode(dataDTO);
			
			valDecrypt = cipherResponseDTO.getDataDTO().getData().split(" ");
			/** Si los parametros en credentials son igual a mas de 2 */
			if (valDecrypt.length >= 2) {
				tokenizerDTO.setUserName(valDecrypt[0]);
				tokenizerDTO.setPassword(valDecrypt[1]);
			} else if (valDecrypt.length == 1) {
				tokenizerDTO.setUserName(valDecrypt[0]);
			} else {
				throw new IllegalArgumentException(ConstantsToken.MSG_CREDENTIALS_INVALID.getName());
			}

		} catch ( NoSuchAlgorithmException | KeyManagementException| IOException | InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			throw new IllegalArgumentException(ConstantsToken.MSG_CREDENTIALS_INVALID.getName());
		}
		log.info("Termina obtener datos de credentials.");
		return tokenizerDTO;

	}

}
