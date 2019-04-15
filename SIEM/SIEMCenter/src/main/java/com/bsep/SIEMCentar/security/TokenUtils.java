package com.bsep.SIEMCentar.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtils {

	@Value("myXAuthSecret")
	private String secret;

	//@Value("60")
	@Value("18000") //in seconds (5 hours)
	private Long expiration;
	
	public String getUsernameFromToken(String token) {
		String username = null;
		try {
			Claims claims = this.getClaimsFromToken(token);
			if(claims != null) username = claims.getSubject();

		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(this.secret)
					.parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}
	
	public Date getExpirationDateFromToken(String token) {
		Date expirationDate = null;
		try {
			final Claims claims = this.getClaimsFromToken(token);
			if(claims != null) expirationDate = claims.getExpiration();
		} catch (Exception e) {
			expirationDate = null;
		}
		return expirationDate;
	}
	
	private boolean isTokenExpired(String token) {
	    final Date expirationDate = this.getExpirationDateFromToken(token);
	    return expirationDate.before(new Date(System.currentTimeMillis()));
	  }
	
	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return username.equals(userDetails.getUsername())
				&& !isTokenExpired(token);
	}
	
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sub", userDetails.getUsername());
		claims.put("created", new Date(System.currentTimeMillis()));

		List<HashMap<String,String>> roles = userDetails.getAuthorities().stream()
				.map(a -> {
				    HashMap<String, String> hm = new HashMap<>();
                    hm.put("authority", ((GrantedAuthority) a).getAuthority());
                    return hm;
                })
				.collect(Collectors.toList());
		claims.put("roles", roles);

		return Jwts.builder().setClaims(claims)
				.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}


}
