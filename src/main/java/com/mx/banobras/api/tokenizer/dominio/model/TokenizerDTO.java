package com.mx.banobras.api.tokenizer.dominio.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenizerDTO {
	
	private String credentials;
	private String userName;
	private String password;
	private String jwtToken;
	private String consumerId; 
	private String functionalId;
	private String transactionalId;
	private Integer refreshToken;
	

}
