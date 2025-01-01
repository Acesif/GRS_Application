package com.grs.core.repo.projapoti;

import com.grs.core.domain.projapoti.CityCorporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityCorporationRepo extends JpaRepository<CityCorporation, Integer> {
    List<CityCorporation> findByDivisionIdAndDistrictId(Integer divisionId, Integer districtId);
}
