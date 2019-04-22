package com.bsep.SIEMCenter.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.bsep.SIEMCenter.model.User;


public interface UserRepository extends Repository<User, Long> {
	
	User save(User user);
	List<User> findAll();
	User findById(Long id);
	User findByUsername(String username);
	User findByUsernameAndPassword(String username,String password);
	


}
