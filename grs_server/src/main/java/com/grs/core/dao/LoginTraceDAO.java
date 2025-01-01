package com.grs.core.dao;

import com.grs.api.model.UserInformation;
import com.grs.core.domain.grs.LoginTrace;
import com.grs.core.repo.grs.LoginTraceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginTraceDAO {
    @Autowired
    private LoginTraceRepo loginTraceRepo;

    public LoginTrace findOne(Long id) {
        return loginTraceRepo.findById(id);
    }

    public List<LoginTrace> findAll() {
        return this.loginTraceRepo.findAll();
    }

    public LoginTrace save(LoginTrace loginTrace) {
        return loginTraceRepo.save(loginTrace);
    }

}
