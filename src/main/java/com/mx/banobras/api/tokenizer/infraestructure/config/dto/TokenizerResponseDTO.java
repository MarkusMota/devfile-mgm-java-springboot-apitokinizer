package com.mx.banobras.api.tokenizer.infraestructure.config.dto;

import lombok.Data;

@Data
public class TokenizerResponseDTO {
	private int statusCode;
	private TokenDTO tokenDTO;
	private ErrorMessageDTO errorMessageDTO;
}


