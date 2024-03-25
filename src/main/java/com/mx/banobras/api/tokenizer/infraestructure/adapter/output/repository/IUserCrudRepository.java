package com.mx.banobras.api.tokenizer.infraestructure.adapter.output.repository;


//import org.springframework.data.repository.CrudRepository;

//import com.mx.banobras.api.tokenizer.infraestructure.entity.UserEntity;

import java.util.Optional;

import com.mx.banobras.api.tokenizer.dominio.model.User;

public interface IUserCrudRepository  {//extends CrudRepository<UserEntity,Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPassword(String password);
}
