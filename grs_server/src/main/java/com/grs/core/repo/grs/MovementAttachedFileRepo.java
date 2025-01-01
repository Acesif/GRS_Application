package com.grs.core.repo.grs;

import com.grs.core.domain.grs.MovementAttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MovementAttachedFileRepo extends JpaRepository<MovementAttachedFile, Long> {
    List<MovementAttachedFile> findByIdIn(List<Long> ids);
}
