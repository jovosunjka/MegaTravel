package com.bsep.PKI.repository;


import com.bsep.PKI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
