package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.model.authentication_and_authorization_entities.PermissionEntity;
import com.bsep_sbz.PKI.model.authentication_and_authorization_entities.RoleEntity;
import com.bsep_sbz.PKI.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public Set<String> getPermission(String roleName) {
        RoleEntity roleEntity = roleRepository.findByName(roleName);
        if(roleEntity == null) return new HashSet<>();

        return roleEntity.getPermissions().stream()
                .map(per -> per.getPermissionName())
                .collect(Collectors.toSet());
    }
}
