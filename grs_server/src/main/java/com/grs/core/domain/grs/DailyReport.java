package com.grs.core.domain.grs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_report")
public class DailyReport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "office_id")
    private Long officeId;

    @Column(name = "office_name")
    private String officeName;

    @Column(name = "report_date")
    private Date reportDate;

    @Column(name = "online_submission")
    private Long onlineSubmissionCount;

    @Column(name = "conventional_method_submission")
    private Long conventionalMethodSubmissionCount;

    @Column(name = "self_motivated_accusation")
    private Long selfMotivatedAccusationCount;

    @Column(name = "inherited_from_last_month")
    private Long inheritedFromLastMonthCount;

    @Column(name = "total")
    private Long totalCount;

    @Column(name = "sent_to_other")
    private Long sentToOtherCount;

    @Column(name = "resolved")
    private Long resolvedCount;

    @Column(name = "time_expired")
    private Long timeExpiredCount;

    @Column(name = "running")
    private Long runningCount;

    @Column(name = "resolve_rate")
    private Double resolveRate;

    @Column(name = "appeal_online_submission")
    private Long appealOnlineSubmissionCount;

    @Column(name = "appeal_inherited_from_last_month")
    private Long appealInheritedFromLastMonthCount;

    @Column(name = "appeal_total")
    private Long appealTotalCount;

    @Column(name = "appeal_resolved")
    private Long appealResolvedCount;

    @Column(name = "appeal_time_expired")
    private Long appealTimeExpiredCount;

    @Column(name = "appeal_running")
    private Long appealRunningCount;

    @Column(name = "appeal_resolve_rate")
    private Double appealResolveRate;
}
