package com.bsep.PKI.service;

import com.bsep.PKI.model.CA;
import com.bsep.PKI.model.RootCA;
import com.bsep.PKI.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getLoggedUser() {
        CA ca = new RootCA( "root", "MegaTravel",
                "MegaTravelRootCA", "RS", "http://localhost:8080/pki/certificate/create");
        User rootAdmin = new User("admin", "admin", ca);
        return rootAdmin;
    }
}
