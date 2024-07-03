package com.mx.banobras.api.tokenizer.infraestructure.adapter.input.dto;

import lombok.Data;

@Data
public class TokenizerResponseDTO {
	private Integer statusCode;
	private TokenDTO tokenDTO;
	
	private ErrorMessageDTO errorMessageDTO;
}


