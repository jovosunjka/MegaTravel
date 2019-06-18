package com.bsep_sbz.PKI.service;

import java.util.Set;


public interface RoleService {
    Set<String> getPermission(String roleName);
}
