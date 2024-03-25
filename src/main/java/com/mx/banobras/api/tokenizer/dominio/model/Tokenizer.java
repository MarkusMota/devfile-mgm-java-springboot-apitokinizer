package com.mx.banobras.api.tokenizer.dominio.model;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.mx.banobras.api.tokenizer.infraestructure.config.dto.ErrorMessageDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerResponseDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;


@Component
@Data
public class Tokenizer {

	private String jwtToken ;
	private String username;
	private String password;
	private String roleUser; 
	private String apiBackend;
	private Integer active;
	
	
	
	
	public static final String SUPER_SECRET_KEY = "ZnJhc2VzbGFyZ2FzcGFyYWNvbG9jYXJjb21vY2xhdmVlbnVucHJvamVjdG9kZWVtZXBsb3BhcmF";
	static final long TOKEN_EXPIRATION_TIME = 900000; //  150000; // 15 MINUTOS
	static final String BEARER = "Bearer ";

	public TokenizerResponseDTO validaToken(TokenizerDTO tokenizerDTO) throws Exception {
		
		TokenizerResponseDTO tokenizerResponseDTO = null;
		Claims claims = null;

		try {
			
			System.out.println(" datoa entrada::: " + this.username + " ** Activo " + this.active + " - "  + tokenizerDTO.getUsername());
			
			if (this.active > 0) {

				BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
				
				boolean isValidPassword = bCrypt.matches(tokenizerDTO.getPassword(), this.password);
				
				System.out.println("tokenizerDTO.getPassword(): " + " - - " + tokenizerDTO.getPassword()+ "this.password . " + this.password  );
				
				if(isValidPassword) {
				
					final String jwtUsername = extractUsername(tokenizerDTO.getJwtToken().replace("Bearer ", ""));
		
					if (jwtUsername.contains(this.username)) {
		
						System.out.println("JWTValid");
						String jwtToken = tokenizerDTO.getJwtToken().replace("Bearer ", "");
		
						claims = Jwts.parserBuilder().setSigningKey(getSignedKey(SUPER_SECRET_KEY)).build()
								.parseClaimsJws(jwtToken).getBody();
		
						if (claims != null && claims.get("authorities") != null) {
							tokenizerResponseDTO = new TokenizerResponseDTO();
							tokenizerResponseDTO.setStatusCode(200);
							tokenizerResponseDTO.setTokenDTO(new TokenDTO(tokenizerDTO.getJwtToken(), null));
						}
					}else {
						Date date = new Date();
						ErrorMessageDTO message = new ErrorMessageDTO(403, date, "El usuario: " +  this.username + ", no coincide con el que fue creado token.");
						tokenizerResponseDTO = new TokenizerResponseDTO();
						tokenizerResponseDTO.setStatusCode(403);
						tokenizerResponseDTO.setErrorMessageDTO(message);
						
					}
				}else {
					Date date = new Date();
					ErrorMessageDTO message = new ErrorMessageDTO(403, date, "El password no coincide con el que está almacenado en base de datos.");
					tokenizerResponseDTO = new TokenizerResponseDTO();
					tokenizerResponseDTO.setStatusCode(403);
					tokenizerResponseDTO.setErrorMessageDTO(message);
				}
			} else {
				Date date = new Date();
				ErrorMessageDTO message = new ErrorMessageDTO(403, date,
						"El usuario: " + this.username + ", no está activo.");
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(403);
				tokenizerResponseDTO.setErrorMessageDTO(message);
			}
		} catch (ExpiredJwtException ex) {
			ex.printStackTrace();
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(403, date, "El token ya está expirado.aaaa");
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(403);
			tokenizerResponseDTO.setErrorMessageDTO(message);
			throw new ExpiredJwtException(null, claims, "El token ya está expiradossss.", ex);

		}catch (io.jsonwebtoken.MalformedJwtException ex) {
			ex.printStackTrace();
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(403, date, "El token no tiene el formato correcto.");
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(403);
			tokenizerResponseDTO.setErrorMessageDTO(message);
			throw new MalformedJwtException("El token no tiene el formato correctosss.", ex);

		}catch (Exception ex) {
			ex.printStackTrace();
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(403, date, ex.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(403);
			tokenizerResponseDTO.setErrorMessageDTO(message);
			throw new MalformedJwtException("No se que errore es: ", ex);
		}
		return tokenizerResponseDTO;

	}

	public TokenizerResponseDTO getToken(TokenizerDTO tokenizerDTO) {
		String token = null;
		TokenizerResponseDTO tokenizerResponseDTO = null;
		try {
			if (this.active > 0 ) {
				
				BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
				
				boolean isValidPassword = bCrypt.matches(tokenizerDTO.getPassword(), this.password);		
				
				if(isValidPassword) {
					token = Jwts.builder()
							.setId("banobras")
							.setSubject(this.username)
							.claim("username", this.username)
							.claim("password", this.password)
							.claim("role_user", this.roleUser)
							.claim("authorities", "[Optional[ROLE_" + this.roleUser +"]]")
							.setIssuedAt(new Date(System.currentTimeMillis()))
							.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
							.signWith(getSignedKey(SUPER_SECRET_KEY), SignatureAlgorithm.HS512).compact();
					tokenizerResponseDTO = new TokenizerResponseDTO();
					tokenizerResponseDTO.setStatusCode(200);
					tokenizerResponseDTO.setTokenDTO(new TokenDTO(BEARER+token, null));
					
				}else {
					Date date = new Date();
					ErrorMessageDTO message = new ErrorMessageDTO(403, date, "El password no coincide con el que que esta almacenado en base de datos.");
					tokenizerResponseDTO = new TokenizerResponseDTO();
					tokenizerResponseDTO.setStatusCode(403);
					tokenizerResponseDTO.setErrorMessageDTO(message);
				}
			}else {
				Date date = new Date();
				ErrorMessageDTO message = new ErrorMessageDTO(403, date, "El usuario: " +  this.username + ", no está activo.");
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(403);
				tokenizerResponseDTO.setErrorMessageDTO(message);
				
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(403, date, ex.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(403);
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}
		
		return tokenizerResponseDTO;
	}

	public static Key getSignedKey(String secretKey) {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignedKey(SUPER_SECRET_KEY)).build().parseClaimsJws(token)
				.getBody();
	}

}
