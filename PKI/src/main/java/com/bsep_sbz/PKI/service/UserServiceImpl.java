package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.model.CA;
import com.bsep_sbz.PKI.model.RootCA;
import com.bsep_sbz.PKI.model.User;
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
