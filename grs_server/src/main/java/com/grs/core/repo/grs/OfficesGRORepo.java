package com.grs.core.repo.grs;

import com.grs.core.domain.grs.OfficesGRO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface OfficesGRORepo extends JpaRepository<OfficesGRO, Long> {
    List<OfficesGRO> findAll(Specification specification);

    OfficesGRO findByOfficeId(Long id);

    @Query(nativeQuery = true, value = "select office_id from offices_gro")
    List<BigInteger> findOfficeIds();

    List<OfficesGRO> findByGroOfficeUnitOrganogramIdIsNull();

    List<OfficesGRO> findByAppealOfficerOfficeUnitOrganogramIdIsNull();

    List<OfficesGRO> findByAdminOfficeUnitOrganogramIdIsNull();

    List<OfficesGRO> findByAppealOfficerOfficeUnitOrganogramId(Long officeUnitOrganogramId);

    List<OfficesGRO> findByAdminOfficeUnitOrganogramId(Long officeUnitOrganogramId);

    Integer countByOfficeId(Long officeId);

    @Query(nativeQuery = true, value = "select office_id from offices_gro where office_id in ?1")
    List<BigInteger> findGRSEnabledOfficeIdIn(List<Long> officeIdList);

    List<OfficesGRO> findByAppealOfficeIdAndAppealOfficerOfficeUnitOrganogramId(Long officeId, Long officeUnitOrganogramId);
}
