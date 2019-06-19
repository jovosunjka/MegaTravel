package com.bsep_sbz.SIEMCenter.repository;

import java.util.List;

import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.UserEntity;
import org.springframework.data.repository.Repository;


public interface UserRepository extends Repository<UserEntity, Long> {
	
	UserEntity save(UserEntity user);
	List<UserEntity> findAll();
	UserEntity findById(Long id);
	UserEntity findByUsername(String username);
	UserEntity findByUsernameAndPassword(String username,String password);

}
