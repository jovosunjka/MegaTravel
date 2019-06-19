package com.bsep_sbz.PKI.security;


import com.bsep_sbz.PKI.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@ComponentScan(value = "com.bsep_sbz.PKI.service")
public class MyAuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Autowired
    private RoleService roleService;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<GrantedAuthority> garantedAuthorities = new HashSet<GrantedAuthority>();

        collection.stream()
                .forEach(role -> {
                    Set<String> permissions = roleService.getPermission(((GrantedAuthority) role).getAuthority());
                    Set<GrantedAuthority> simpleGrantedAuthorities = permissions.stream()
                            .map(per -> new SimpleGrantedAuthority(per))
                            .collect(Collectors.toSet());
                    //simpleGrantedAuthorities.add(role);
                    garantedAuthorities.addAll(simpleGrantedAuthorities);
                });

        return garantedAuthorities;
    }

    @Bean
    public MyAuthoritiesMapper myAuthoritiesMapperBean() {
        return new MyAuthoritiesMapper();
    }
}
