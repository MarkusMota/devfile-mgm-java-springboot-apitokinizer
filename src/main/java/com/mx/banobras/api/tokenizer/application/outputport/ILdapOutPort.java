package com.mx.banobras.api.tokenizer.application.outputport;

import javax.naming.NamingException;

import com.mx.banobras.api.tokenizer.dominio.model.TokenizerDTO;
import com.mx.banobras.api.tokenizer.infraestructure.adapter.output.client.LdapVO;

/**
 * ILdapOutPort.java:
 * 
 * Interface de puerto de salida, para buscar el usuario 
 * 
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */
public interface ILdapOutPort {

	/**
	 * Metodo para buscar el usuario.
	 * 
	 * @param tokenizerDTO que contiene los datos de usuario y password a buscar
	 * @return regresa un valor booleano, si es verdadero, si existe el usuario 
	 * 
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public LdapVO findByUsername(TokenizerDTO tokenizerDTO) throws NamingException;
	
}
