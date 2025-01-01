package com.grs.core.repo.grs;

import com.grs.core.domain.grs.ServiceOrigin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOriginRepo extends JpaRepository<ServiceOrigin, Long> {
    //public List<ServiceOrigin> findByOfficesNotOrOfficesIsNull(Office office);
    Page<ServiceOrigin> findByOrderByIdAsc(Pageable pageable);

    ServiceOrigin findById(Long serviceId);

    List<ServiceOrigin> findByOfficeOriginId(Long officeOriginId);

    List<ServiceOrigin> findByOfficeOriginIdAndStatus(Long officeOriginId, Boolean status);
}
