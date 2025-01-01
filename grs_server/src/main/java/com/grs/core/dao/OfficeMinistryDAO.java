package com.grs.core.dao;

import com.grs.core.domain.projapoti.OfficeMinistry;
import com.grs.core.repo.projapoti.OfficeMinistryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeMinistryDAO {

    @Autowired
    private OfficeMinistryRepo officeMinistryRepo;

    public OfficeMinistry findOne(Long id) {
        return officeMinistryRepo.findOne(id);
    }

    public List<OfficeMinistry> findAll() {
        return officeMinistryRepo.findAll();
    }

}
