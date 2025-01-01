package com.grs.core.repo.projapoti;

import com.grs.core.domain.projapoti.EmployeeOffice;
import com.grs.core.domain.projapoti.EmployeeRecord;
import com.grs.core.domain.projapoti.Office;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRecordRepo extends JpaRepository<EmployeeRecord, Long> {
    List<EmployeeRecord> findByEmployeeOfficesIn(List<EmployeeOffice> employeeOffices);
    List<EmployeeRecord> findByOffices(Office office);
    Page<EmployeeRecord> findDistinctByOfficesOrderByIdAsc(Office office, Pageable pageable);
    Page<EmployeeRecord> findByIdInOrderByIdAsc(List<Long> employeeRecordIdList, Pageable pageable);
}
