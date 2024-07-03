package com.mx.banobras.api.tokenizer.application.inputport;

import com.mx.banobras.api.tokenizer.dominio.model.TokenizerDTO;
import com.mx.banobras.api.tokenizer.infraestructure.adapter.input.dto.TokenizerResponseDTO;

public interface ITokenizerInputPort {

	/**
	 * Metodo para obtener el Token, para el consumo de los microservicios.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para generar el token.
	 * @return regresa el objeto con el token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO createToken(TokenizerDTO tokenizerDTO);
	
	/**
	 * Metodo para validar el Token, para el consumo de los microservicios.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para validar el token.
	 * @return regresa el objeto TokenizerResponseDTO con los datos del token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO validateToken(TokenizerDTO tokenizerDTO);
	/**
	 * Metodo para obtener el Token, para el consumo de los microservicios.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para generar el token.
	 * @return regresa el objeto con el token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO createTokenPublic(TokenizerDTO tokenizerDTO);
	
	/**
	 * Metodo para validar el Token, para el consumo de los microservicios.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para validar el token.
	 * @return regresa el objeto TokenizerResponseDTO con los datos del token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO validateTokenPublic(TokenizerDTO tokenizerDTO);
	
	
}
