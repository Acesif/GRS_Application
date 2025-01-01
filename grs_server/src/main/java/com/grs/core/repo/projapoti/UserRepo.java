package com.grs.core.repo.projapoti;

import com.grs.core.domain.projapoti.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String Username);
    User findByEmployeeRecordId(Long id);
}
