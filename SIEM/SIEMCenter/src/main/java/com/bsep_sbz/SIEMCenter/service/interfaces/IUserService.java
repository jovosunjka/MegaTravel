package com.bsep_sbz.SIEMCenter.service.interfaces;


import com.bsep_sbz.SIEMCenter.model.authentication_and_authorization_entities.UserEntity;

public interface IUserService {

	UserEntity getLoggedUser() throws Exception;
	
    void save(UserEntity user) throws Exception;

    UserEntity getUser(String username);
    
    UserEntity getUser(String username, String password);

    boolean exists(String username);

	boolean save(String name, String username, String password);

}
