package com.grs.core.repo.grs;

import com.grs.core.domain.grs.PermissionsToUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionsToUsersRepo extends JpaRepository <PermissionsToUsers, Long> {
    List<PermissionsToUsers> findByUserIdAndUserType(Long userId, String userType);
}
