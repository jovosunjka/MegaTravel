package com.bsep_sbz.SIEMCenter.service;

import java.util.Set;

public interface RoleService {
    Set<String> getPermission(String roleName);
}
