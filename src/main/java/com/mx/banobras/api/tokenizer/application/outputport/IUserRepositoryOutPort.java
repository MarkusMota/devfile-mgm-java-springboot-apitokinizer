package com.mx.banobras.api.tokenizer.application.outputport;

import com.mx.banobras.api.tokenizer.dominio.model.User;


public interface IUserRepositoryOutPort {
	
	public User findByUsername(String username);
	public User findByPassword(String password);

	

}
