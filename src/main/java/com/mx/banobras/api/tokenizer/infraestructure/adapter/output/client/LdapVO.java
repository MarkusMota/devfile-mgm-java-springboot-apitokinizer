package com.mx.banobras.api.tokenizer.infraestructure.adapter.output.client;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LdapVO {
	private String expediente; 
	private String nombre;
	private String area;
	private String extension;
	private String activo;
	private String emailPrincipal;
	private String email;


}
