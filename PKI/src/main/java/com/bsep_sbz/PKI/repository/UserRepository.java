package com.bsep_sbz.PKI.repository;


import com.bsep_sbz.PKI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
