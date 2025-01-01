package com.grs.core.repo.grs;

import com.grs.core.domain.grs.SpProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpProgrammeRepo extends JpaRepository <SpProgramme, Integer> {
    Optional<SpProgramme> findById(Integer id);
    Integer countByNameEnOrNameBn(String nameEn, String nameBn);
    List<SpProgramme> findAllByStatus(Boolean status);
}
