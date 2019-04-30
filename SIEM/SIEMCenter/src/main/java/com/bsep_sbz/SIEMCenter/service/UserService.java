package com.bsep_sbz.SIEMCenter.service;


import com.bsep_sbz.SIEMCenter.model.User;
import com.bsep_sbz.SIEMCenter.repository.UserRepository;
import com.bsep_sbz.SIEMCenter.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;



	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	@Override
	public User getLoggedUser() throws Exception {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try {
			org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth
					.getPrincipal();
			return userRepository.findByUsername(user.getUsername());
		} catch (Exception e) {
			throw new Exception("User not found!");
		}

	}



	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void save(User user) throws Exception {
		userRepository.save(user);
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	@Override
	public User getUser(String username) {
		return userRepository.findByUsername(username);
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	@Override
	public User getUser(String username, String password) {
		return userRepository.findByUsernameAndPassword(username, password);
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	@Override
	public boolean exists(String username) {
		User u = userRepository.findByUsername(username);
		return u != null;
	}


	@Override
	public boolean save(String name, String username, String password) {
		if (name == null || username == null || password == null)
			return false;
		if (name.equals("") || username.equals("") || password.equals(""))
			return false;

		if (exists(username))
			return false;

		User user = new User(name, username, passwordEncoder.encode(password));
		try {
			userRepository.save(user);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}