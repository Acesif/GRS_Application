package com.grs.core.repo.grs;

import com.grs.core.domain.grs.LoginTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginTraceRepo extends JpaRepository <LoginTrace, Long> {
    LoginTrace findById(Long loginTraceId);
}
