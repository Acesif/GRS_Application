package com.grs.core.repo.grs;

import com.grs.core.domain.grs.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuperAdminRepo extends JpaRepository<SuperAdmin, Long>  {
    SuperAdmin findByUsername(String Username);
    SuperAdmin findByUsernameAndPassword(String Username, String password);

    @Query(value = "select c.*\n"+
            " from grs_users as c where c.user_role_id = ?1",
            nativeQuery = true)
    List<SuperAdmin> findByUserRoleId(long role);

    SuperAdmin findByPhoneNumber(String phoneNumber);
    Integer countByUsername(String Username);
}
