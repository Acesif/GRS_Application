package com.grs.core.repo.grs;

import com.grs.core.domain.grs.GrievanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrievanceStatusRepo extends JpaRepository<GrievanceStatus, Long> {
    GrievanceStatus findByStatusName(String statusName);
}
