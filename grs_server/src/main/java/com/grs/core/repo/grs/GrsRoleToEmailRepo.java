package com.grs.core.repo.grs;

import com.grs.core.domain.grs.EmailTemplate;
import com.grs.core.domain.grs.GrsRoleToEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrsRoleToEmailRepo extends JpaRepository <GrsRoleToEmail, Long> {
    List<GrsRoleToEmail> findByEmailTemplate(EmailTemplate emailTemplate);
    List<GrsRoleToEmail> findByEmailTemplateAndStatus(EmailTemplate emailTemplate, Boolean status);
}
