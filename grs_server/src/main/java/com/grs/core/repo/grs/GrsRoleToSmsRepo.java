package com.grs.core.repo.grs;

import com.grs.core.domain.grs.GrsRoleToSms;
import com.grs.core.domain.grs.SmsTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrsRoleToSmsRepo extends JpaRepository <GrsRoleToSms,Long> {
    List<GrsRoleToSms> findBySmsTemplateAndStatus(SmsTemplate smsTemplate, Boolean status);
    List<GrsRoleToSms> findBySmsTemplate(SmsTemplate smsTemplate);
}
