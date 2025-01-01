package com.grs.core.repo.grs;

import com.grs.core.domain.grs.Occupation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccupationRepo extends JpaRepository<Occupation, Long> {
    Page<Occupation> findByOrderByIdAsc(Pageable pageable);
    Integer countByOccupationBanglaAndOccupationEnglish(String occupationNameBng, String occcupationNameEng);
    List<Occupation> findByStatus(Boolean status);
}