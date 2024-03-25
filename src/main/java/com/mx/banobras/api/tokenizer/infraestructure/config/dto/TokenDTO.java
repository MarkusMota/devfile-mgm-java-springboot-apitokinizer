package com.mx.banobras.api.tokenizer.infraestructure.config.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

	public String token;
	public String tokenRefresh;

	
}
