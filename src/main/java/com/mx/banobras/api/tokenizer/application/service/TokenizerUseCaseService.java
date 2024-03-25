package com.mx.banobras.api.tokenizer.application.service;


import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.mx.banobras.api.tokenizer.application.inputport.ITokenizerInputPort;
import com.mx.banobras.api.tokenizer.application.outputport.IUserRepositoryOutPort;
import com.mx.banobras.api.tokenizer.dominio.model.Tokenizer;
import com.mx.banobras.api.tokenizer.dominio.model.User;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.ErrorMessageDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerDTO;
import com.mx.banobras.api.tokenizer.infraestructure.config.dto.TokenizerResponseDTO;


@Service
public class TokenizerUseCaseService  implements ITokenizerInputPort{
	
	private final IUserRepositoryOutPort iUserRepositoryOutPort;
	private final Tokenizer tokenizer;
	
	public TokenizerUseCaseService(IUserRepositoryOutPort iUserRepositoryOutPort, Tokenizer tokenizer) {
		this.iUserRepositoryOutPort = iUserRepositoryOutPort;
		this.tokenizer = tokenizer;
	}
	

	@Override
	public TokenizerResponseDTO createToken(TokenizerDTO tokenizerDTO) {
		// TODO Auto-generated method stub
		
		TokenizerResponseDTO tokenizerResponseDTO = null;
		
		try {
			User user = iUserRepositoryOutPort.findByUsername(tokenizerDTO.getUsername());
			
			
			tokenizer.setUsername(user.getUsername());
			tokenizer.setPassword(user.getPassword());
			tokenizer.setApiBackend(user.getApiBackend());
			tokenizer.setActive(user.getActive());
			tokenizerResponseDTO = tokenizer.getToken(tokenizerDTO);
	
		} catch (Exception ex) {
			Date date = new Date();
			ErrorMessageDTO message = new ErrorMessageDTO(403, date, ex.getMessage());
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(403);
			tokenizerResponseDTO.setErrorMessageDTO(message);
		}

		return tokenizerResponseDTO;
	}



	@Override
	public TokenizerResponseDTO validateToken(TokenizerDTO tokenizerDTO) throws Exception {
		TokenizerResponseDTO tokenizerResponseDTO = null;
		
		//try {
			User user = iUserRepositoryOutPort.findByUsername(tokenizerDTO.getUsername());
			tokenizer.setUsername(user.getUsername());
			tokenizer.setPassword(user.getPassword());
			tokenizer.setApiBackend(user.getApiBackend());
			tokenizer.setActive(user.getActive());
			
			System.out.println(user.getUsername() + " Activo " + user.getActive());
			System.out.println(tokenizer.getUsername() + " Activo " + tokenizer.getActive());
			
			
			tokenizerResponseDTO = tokenizer.validaToken(tokenizerDTO);
	
//		}catch (NoSuchElementException ex) {
//			Date date = new Date();
//			ErrorMessageDTO message = new ErrorMessageDTO(403, date, ex.getMessage());
//			tokenizerResponseDTO = new TokenizerResponseDTO();
//			tokenizerResponseDTO.setStatusCode(403);
//			tokenizerResponseDTO.setErrorMessageDTO(message);
//		} 
//		catch (Exception ex) {
//			Date date = new Date();
//			ErrorMessageDTO message = new ErrorMessageDTO(403, date, ex.getMessage());
//			tokenizerResponseDTO = new TokenizerResponseDTO();
//			tokenizerResponseDTO.setStatusCode(403);
//			tokenizerResponseDTO.setErrorMessageDTO(message);
//		}

		return tokenizerResponseDTO;
	}

	


}
