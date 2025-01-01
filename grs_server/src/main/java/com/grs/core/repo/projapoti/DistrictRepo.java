package com.grs.core.repo.projapoti;

import com.grs.core.domain.projapoti.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepo extends JpaRepository<District, Integer> {
    List<District> findByDivisionId(Integer divisionId);
}
