package com.grs.core.repo.projapoti;

import com.grs.core.domain.projapoti.Thana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanaRepo extends JpaRepository<Thana, Integer> {
    List<Thana> findByDivisionIdAndDistrictId(Integer divisionId, Integer districtId);
    List<Thana> findByDivisionId(Integer divisionId);
}