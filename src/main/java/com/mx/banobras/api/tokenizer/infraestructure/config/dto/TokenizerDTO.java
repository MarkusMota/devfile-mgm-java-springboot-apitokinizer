package com.mx.banobras.api.tokenizer.infraestructure.config.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenizerDTO {
	private String jwtToken ;
	private String username;
	private String password;
	private String roleUser; 
	private String apiBackend;
	private Integer active;
	
}
