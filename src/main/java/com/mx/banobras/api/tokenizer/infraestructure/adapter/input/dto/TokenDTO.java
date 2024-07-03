package com.mx.banobras.api.tokenizer.infraestructure.adapter.input.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
	public boolean valid;
	public String token;
	public String refreshToken;
	
	
}
