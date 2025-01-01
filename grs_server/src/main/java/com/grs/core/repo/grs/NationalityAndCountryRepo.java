package com.grs.core.repo.grs;

import com.grs.core.domain.grs.CountryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationalityAndCountryRepo extends JpaRepository<CountryInfo, Long>{

}
