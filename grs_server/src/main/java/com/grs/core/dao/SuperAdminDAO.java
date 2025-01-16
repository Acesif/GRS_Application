package com.grs.core.dao;

import com.grs.api.gateway.SuperAdminGateway;
import com.grs.core.domain.grs.SuperAdmin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service

public class SuperAdminDAO {
    @Autowired
    private SuperAdminGateway superAdminRepo;

    public SuperAdmin save(SuperAdmin superAdmin) {
        return this.superAdminRepo.save(superAdmin);
    }

    public SuperAdmin findOne(Long id) {
        return this.superAdminRepo.findOne(id);
    }

    public List<SuperAdmin> findAll() {
        return this.superAdminRepo.findAll();
    }

    public List<SuperAdmin> findByRole(long role) {
        return this.superAdminRepo.findByUserRoleId(role);
    }

    public SuperAdmin findByUsername(String username) {
        return this.superAdminRepo.findByUsername(username);
    }

    public SuperAdmin findByUsernameAndPassword(String username, String password) {
        return this.superAdminRepo.findByUsernameAndPassword(username, password);
    }

    public SuperAdmin findByPhoneNumber(String phoneNumber) {
        return this.superAdminRepo.findByPhoneNumber(phoneNumber);
    }

    public Integer countByUsername(String username){
        return this.superAdminRepo.countByUsername(username);
    }
}
