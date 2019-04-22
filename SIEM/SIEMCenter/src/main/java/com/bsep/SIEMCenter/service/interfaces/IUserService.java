package com.bsep.SIEMCenter.service.interfaces;

import com.bsep.SIEMCenter.model.User;


public interface IUserService {

	User getLoggedUser() throws Exception;
	
    void save(User user) throws Exception;

    User getUser(String username);
    
    User getUser(String username, String password);

    boolean exists(String username);

	boolean save(String name, String username, String password);

}
