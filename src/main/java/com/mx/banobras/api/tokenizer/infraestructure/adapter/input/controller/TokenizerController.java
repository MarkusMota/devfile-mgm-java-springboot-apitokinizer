package com.mx.banobras.api.tokenizer.infraestructure.adapter.input.controller;


import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.banobras.api.tokenizer.application.inputport.ITokenizerInputPort;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.ErrorMessageDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerResponseDTO;


@RestController
@RequestMapping("/v1")
public class TokenizerController {

	
	private final ITokenizerInputPort iTokenizerInputPort;

	public TokenizerController(ITokenizerInputPort iJwtTokenizerInputPort) {
		this.iTokenizerInputPort = iJwtTokenizerInputPort;
	}

	@PostMapping("/token")
	public ResponseEntity<TokenizerResponseDTO> getToken(
			@RequestHeader(value = "username") String username,
			@RequestHeader(value = "password") String password,
			@RequestHeader(value = "api-backend") String apiBackend) throws Exception {

//		TokenizerDTO tokenizerDTO = new TokenizerDTO(null, username, password, null, apiBackend, 0);
//
//		iTokenizerInputPort.createToken(tokenizerDTO);
//
//		TokenizerResponseDTO tokenizerResponseDTO = iTokenizerInputPort.createToken(tokenizerDTO);
//		
//		if(tokenizerResponseDTO.getStatusCode() == 200) {
//			return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.OK);
//		}else {
//			return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.FORBIDDEN);
//		}
		TokenizerResponseDTO tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(1000);
		
		 TokenDTO tokenDTO = new TokenDTO();
		 
		 ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO() ;
		 errorMessageDTO.setMessage("hola hola");
		 errorMessageDTO.setStatusCode(2000);
		 Date date = new Date();
		 errorMessageDTO.setTimestamp(date);
		 
		 tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
		 tokenizerResponseDTO.setTokenDTO(tokenDTO);
		
		return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.OK);
	}
	
	
	@PostMapping("/valid")
	public ResponseEntity<TokenizerResponseDTO> validToken(
			@RequestHeader(value = "authorization") String authorization,
			@RequestHeader(value = "username") String username,
			@RequestHeader(value = "password") String password,
			@RequestHeader(value = "api-backend") String apiBackend) throws Exception {

//		TokenizerDTO tokenizerDTO = new TokenizerDTO(authorization, username, password, null, apiBackend, 0);
//
//		iTokenizerInputPort.createToken(tokenizerDTO);
//
//		TokenizerResponseDTO tokenizerResponseDTO = iTokenizerInputPort.validateToken(tokenizerDTO);
//		
//		if(tokenizerResponseDTO.getStatusCode() == 200) {
//			return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.OK);
//		}else {
//			return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.FORBIDDEN);
//		}
		
		TokenizerResponseDTO tokenizerResponseDTO = new TokenizerResponseDTO();
		tokenizerResponseDTO.setStatusCode(1000);

 TokenDTO tokenDTO = new TokenDTO();
 
 ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO() ;
 errorMessageDTO.setMessage("hola hola");
 errorMessageDTO.setStatusCode(2000);
 Date date = new Date();
 errorMessageDTO.setTimestamp(date);
 
 tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
 tokenizerResponseDTO.setTokenDTO(tokenDTO);

return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.OK);
		
		
	}

	
}
