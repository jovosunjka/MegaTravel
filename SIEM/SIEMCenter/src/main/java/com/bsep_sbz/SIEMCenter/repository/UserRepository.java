package com.bsep_sbz.SIEMCenter.repository;

import java.util.List;

import com.bsep_sbz.SIEMCenter.model.User;
import org.springframework.data.repository.Repository;


public interface UserRepository extends Repository<User, Long> {
	
	User save(User user);
	List<User> findAll();
	User findById(Long id);
	User findByUsername(String username);
	User findByUsernameAndPassword(String username,String password);
	


}
