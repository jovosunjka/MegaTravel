package com.bsep.SIEMCenter.service;
	
	import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bsep.SIEMCentar.service.repository.UserRepository;
import com.bsep.SIEMCenter.model.User;


	@Primary // Intellij pronalazi vise implementacija za UserDetailService,
	// zato koristimo ovu anotaciju, da bi ova implementacija imala prednost u odnosu na druge,
	// i da bi @Autowired znao koju implementaciju da izabere
	@Service
	public class UserDetailsServiceImpl implements UserDetailsService {

	    private final Logger logger = LogManager.getLogger(this.getClass());

	    @Autowired
	    private UserRepository userRepository;

	    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        User user = userRepository.findByUsername(username);

	        if(user == null) {
	            String message = String.format("No user found with username '%s' in database.", username);
	            logger.error(message);
	            throw new UsernameNotFoundException(message);
	        }

	        /*if(user.getUserStatus() != UserStatus.ACTIVATED) {
	            String message = String.format("No user found with username '%s' in database.", username);
	            logger.error(message);
	            throw new UsernameNotFoundException(message);
	        }*/

	        List<GrantedAuthority> garantedAuthorities = null;
	      /*  if(user.getUserType() == UserType.PASSENGER) {
	            Passenger passenger = (Passenger) user;
	            if(passenger.getPassengerType() != PassengerType.OTHER) {
	                if (passenger.getExpirationDate() == null) {
	                    resetPassengerType(passenger, false);
	                }
	                else if(LocalDate.now().isAfter(passenger.getExpirationDate())) {
	                    resetPassengerType(passenger, true);
	                }
	            }
	            

	        
	        }*/
	        //else {
	            garantedAuthorities = AuthorityUtils.createAuthorityList(user.getUserType().toString());
	       // }

	        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), garantedAuthorities);
	    }

	   

	}
