package com.grs.grs_client.repo;

import com.grs.grs_client.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmTokenRepo extends JpaRepository<FcmToken, Long> {
    FcmToken findByDeviceToken(String deviceToken);
    List<FcmToken> findByUsername(String username);
    List<FcmToken> findByIdIn(List<Long> ids);
}
