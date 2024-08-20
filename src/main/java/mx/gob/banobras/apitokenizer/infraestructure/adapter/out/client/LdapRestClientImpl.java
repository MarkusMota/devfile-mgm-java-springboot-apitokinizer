package mx.gob.banobras.apitokenizer.infraestructure.adapter.out.client;

/**
 * LdapRestClient.java:
 * 
 * Clase para conectarse la conexion con el servicio de autenticacion en LDAP. 
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import mx.gob.banobras.apitokenizer.application.port.out.ILdapApiRestClient;
import mx.gob.banobras.apitokenizer.common.util.ConstantsToken;
import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.ErrorMessageDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.LdapResponseDTO;

@Component
public class LdapRestClientImpl implements ILdapApiRestClient {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(LdapRestClientImpl.class);

	/**
	 * Variable que contiene la URL de conexion con el servicio de autenticacion con
	 * ldap
	 */
	@Value("${app.url.ldap.auth}")
	String urlLdapAuth;

	/**
	 * Metodo para validar el usuario que exista en LDAP.
	 * 
	 * @param securityAuthInDTO componente que contiene los datos del token.
	 * 
	 * @return HttpResponse<String> regresa un objeto con los datos del token
	 *         validado
	 * @throws InterruptedException
	 * @throws IOException,         InterruptedException
	 * 
	 */
	@Override
	public LdapResponseDTO authorizationLDAP(TokenizerDTO tokenizerDTO)
			throws IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {

		Gson gson = new Gson();
		HttpClient client = null;
		HttpResponse<String> response = null;
		SSLContext sslContext = null;
		log.info("Inicia rest cliente LDAP");
		log.info(urlLdapAuth);

		if (urlLdapAuth.toUpperCase().contains(ConstantsToken.HTTPS.getName())) {
			log.info("Es por HTTPS");
			sslContext = SSLContext.getInstance(ConstantsToken.SSL.getName());
			sslContext.init(null, new TrustManager[] { MOCK_TRUST_MANAGER }, new SecureRandom());
			client = HttpClient.newBuilder().sslContext(sslContext).build();
		} else {
			client = HttpClient.newBuilder().build();
		}

		HttpRequest request = HttpRequest.newBuilder()
				.setHeader("credentials", tokenizerDTO.getCredentials())
				.setHeader("app-name", tokenizerDTO.getAppName()).setHeader("consumer-id", tokenizerDTO.getConsumerId())
				.setHeader("functional-id", tokenizerDTO.getFunctionalId())
				.setHeader("transaction-id", tokenizerDTO.getTransactionalId())
				.uri(URI.create(urlLdapAuth))
				.POST(HttpRequest.BodyPublishers.noBody()).build();

		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		log.info(new StringBuilder().append("StatusCode: ").append(response.statusCode()).append(response.body()));

		if (response.statusCode() == 404) {
			LdapResponseDTO ldapResponseDTO = new LdapResponseDTO();
			ldapResponseDTO.setStatusCode(503);
			ldapResponseDTO.setLdapDTO(null);
			ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(503, new Date(), "Servicio no disponible - LDAP");
			ldapResponseDTO.setErrorMessageDTO(errorMessageDTO);
			log.info("Finaliza rest cliente LDAP - con 404 error");
			return ldapResponseDTO ;
		} else {
			log.info("Finaliza rest cliente LDAP - respuesta de SecurityAuth");
			return gson.fromJson(response.body(), LdapResponseDTO.class);
		}
	}

	private static final TrustManager MOCK_TRUST_MANAGER = new X509ExtendedTrustManager() {
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[0];
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
				throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
				throws CertificateException {

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
				throws CertificateException {
		}

	};

}
