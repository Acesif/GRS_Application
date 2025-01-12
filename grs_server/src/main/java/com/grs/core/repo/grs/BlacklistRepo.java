package com.grs.core.repo.grs;

import com.grs.core.domain.grs.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface BlacklistRepo extends JpaRepository<Blacklist, Long> {
    List<Blacklist> findByComplainantId(Long complainantId);
    Blacklist findByComplainantIdAndOfficeId(Long complainantId, Long officeId);
    List<Blacklist> findByOfficeId(Long officeId);
    List<Blacklist> findByOfficeIdInAndAndBlacklistedOrOfficeIdInAndRequested(Collection<Long> blacklisterOfficeId, Boolean blacklisted, Collection<Long> blacklisterOfficeId2, Boolean requested);
}
