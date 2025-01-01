package com.grs.core.repo.projapoti;

import com.grs.core.domain.projapoti.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepo extends JpaRepository<Division, Integer> {
}
