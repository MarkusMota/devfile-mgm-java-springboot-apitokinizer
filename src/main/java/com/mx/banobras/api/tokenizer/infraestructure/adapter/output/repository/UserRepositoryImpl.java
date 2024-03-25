package com.mx.banobras.api.tokenizer.infraestructure.adapter.output.repository;


import org.springframework.stereotype.Repository;

import com.mx.banobras.api.tokenizer.application.outputport.IUserRepositoryOutPort;
import com.mx.banobras.api.tokenizer.dominio.model.User;
//import com.mx.banobras.api.tokenizer.infraestructure.mapper.UserMapper;

@Repository
public class UserRepositoryImpl implements IUserRepositoryOutPort{
	
//	private final IUserCrudRepository iUserCrudRepository;
//    
//
//    public UserRepositoryImpl(IUserCrudRepository iUserCrudRepository) {
//        this.iUserCrudRepository = iUserCrudRepository;
//        
//    }
	

	@Override
	public User findByUsername(String username) {
		User user = new User();
		
		user.setId(1);
		user.setFirstName("marcos");
		
		return new User();
		
	}

	@Override
	public User findByPassword(String password) {
		User user = new User();
		user.setId(1);
		user.setFirstName("marcos");
		
		return new User();
		
	}

	

	
	
	
}
