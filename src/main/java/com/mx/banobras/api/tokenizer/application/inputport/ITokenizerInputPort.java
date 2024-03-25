package com.mx.banobras.api.tokenizer.application.inputport;

import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerResponseDTO;

public interface ITokenizerInputPort {

	public TokenizerResponseDTO createToken(TokenizerDTO tokenizerDTO) throws Exception;
	public TokenizerResponseDTO validateToken(TokenizerDTO tokenizerDTO) throws Exception;
	
}
