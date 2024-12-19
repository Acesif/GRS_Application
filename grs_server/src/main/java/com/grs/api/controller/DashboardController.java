package com.grs.api.controller;

import com.grs.api.model.SafetyNetProgramReportResponse;
import com.grs.api.model.SafetyNetSummaryResponse;
import com.grs.api.model.UserInformation;
import com.grs.api.model.request.AddCentralDashboardRecipientDTO;
import com.grs.api.model.response.CentralDashboardRecipientDTO;
import com.grs.api.model.response.NudgeableGrievanceDTO;
import com.grs.api.model.response.RegisterDTO;
import com.grs.api.model.response.dashboard.*;
import com.grs.api.model.response.dashboard.latest.*;
import com.grs.core.dao.GRSStatisticsDAO;
import com.grs.core.domain.MediumOfSubmission;
import com.grs.core.domain.grs.Grievance;
import com.grs.core.service.DashboardService;
import com.grs.utils.CacheUtil;
import com.grs.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private GRSStatisticsDAO grsStatisticsDAO;

    @GetMapping("/office/{office_id}")
    public DashboardDataDTO getDashboardData(Authentication authentication, @PathVariable("office_id") Long officeId) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        DashboardDataDTO dashboardDataDTO = DashboardDataDTO.builder()
                .groDashboardData(dashboardService.getGrievanceDataForGRODashboard(officeId, true))
                .build();
        if (dashboardService.hasAccessToAoAndSubOfficesDashboard(userInformation, officeId)) {
            dashboardDataDTO.setAoDashboardData(dashboardService.getGrievanceDataForAODashboard(officeId, true));
            if (!userInformation.getIsMobileLogin()) {
                dashboardDataDTO.setListOfChildOfficesData(dashboardService.getListOfChildOffices(officeId));
            }
        }
        return dashboardDataDTO;
    }

    @GetMapping("/office/{office_id}/grs-enabled-child-offices")
    public List<ChildOfficesDashboardNavigatorDTO> getListOfChildOffices(@PathVariable("office_id") Long officeId) {
        List<ChildOfficesDashboardNavigatorDTO> childOffices = dashboardService.getListOfChildOffices(officeId);
        return (childOffices != null && childOffices.size() > 0) ? childOffices : new ArrayList();
    }

    @GetMapping("/office/{office_id}/gro-aggregated-data")
    public GeneralDashboardDataDTO getAggregatedDataForGRO(@PathVariable("office_id") Long officeId) {
        return dashboardService.getSubOfficesAggregatedDataForGrievances(officeId);
    }

    @GetMapping("/office/{office_id}/ao-aggregated-data")
    public GeneralDashboardDataDTO getAggregatedDataForAO(@PathVariable("office_id") Long officeId) {
        return dashboardService.getSubOfficesAggregatedDataForAppeals(officeId);
    }

    @GetMapping("/office/{office_id}/grievances/medium-of-submission")
    public NameValuePairListDTO getGrievancesMediumOfSubmissionCounts(@PathVariable("office_id") Long officeId) {
        return NameValuePairListDTO.builder()
                .nameValuePairList(dashboardService.getGrievanceCountByMediumOfSubmission(officeId, 0L))
                .build();
    }

    @GetMapping("/office/{office_id}/appeals/medium-of-submission")
    public NameValuePairListDTO getAppealsMediumOfSubmissionCounts(@PathVariable("office_id") Long officeId) {
        return NameValuePairListDTO.builder()
                .nameValuePairList(dashboardService.getAppealCountByMediumOfSubmission(officeId, 0L))
                .build();
    }

    @PostMapping("/dashboard/feedbackForDashboardData")
    public void getFeedbackForDashboardData(@RequestBody Grievance grievance){
        dashboardService.getFeedbackForDashboardData(grievance);
    }


    @PostMapping("/dashboard/appealFeedbackForDashboardData")
    public void getAppealFeedbackForDashboardData(@RequestBody Grievance grievance){
        dashboardService.getAppealFeedbackForDashboardData(grievance);
    }

    @GetMapping("/dashboard/citizenCharterServicesByComplaintFrequency")
    public List<ItemIdNameCountDTO> getCitizenCharterServicesByComplaintFrequency(){
        return dashboardService.getCitizenCharterServicesByComplaintFrequency();
    }

    @GetMapping("/office/{office_id}/grievances/compare-with-last-month")
    public CompareWithLastMonthDTO getGrievancesComparisonDataWithLastMonth(@PathVariable("office_id") Long officeId) {
        return CompareWithLastMonthDTO.builder()
                .thisMonth(dashboardService.getMonthAndTypeWiseCountOfGrievanceByOfficeIdWithMonthDiff(officeId, 0))
                .lastMonth(dashboardService.getMonthAndTypeWiseCountOfGrievanceByOfficeIdWithMonthDiff(officeId, -1))
                .build();
    }

    @GetMapping("/office/{office_id}/appeals/compare-with-last-month")
    public CompareWithLastMonthDTO getAppealsComparisonDataWithLastMonth(@PathVariable("office_id") Long officeId) {
        return CompareWithLastMonthDTO.builder()
                .thisMonth(dashboardService.getMonthAndTypeWiseCountOfAppealByOfficeIdWithMonthDiff(officeId, 0))
                .lastMonth(dashboardService.getMonthAndTypeWiseCountOfAppealByOfficeIdWithMonthDiff(officeId, -1))
                .build();
    }

    @GetMapping("/office/{office_id}/grievances/count-by-sections")
    public ListOfGrievanceCountByItemDTO getGrievanceCountBySections(@PathVariable("office_id") Long officeId) {
        return ListOfGrievanceCountByItemDTO.builder()
                .list(dashboardService.countGrievanceOfAnOfficeByOfficeUnit(officeId))
                .build();
    }

    @GetMapping("/office/{office_id}/grievances/count-by-services")
    public ListOfGrievanceCountByItemDTO getGrievanceCountByServices(@PathVariable("office_id") Long officeId) {
        return ListOfGrievanceCountByItemDTO.builder()
                .list(dashboardService.countGrievanceOfAnOfficeByService(officeId))
                .build();
    }

    @GetMapping("/office/{office_id}/appeals/count-by-child-offices")
    public ListOfAppealCountByChildOfficesDTO getAppealsCountOfChildOffices(@PathVariable("office_id") Long officeId) {
        return ListOfAppealCountByChildOfficesDTO.builder()
                .list(dashboardService.getCountOfAppealsBySourceOffices(officeId))
                .build();
    }

    @GetMapping("/office/{office_id}/grievances/resolution-statistics")
    public GrievanceResolutionStatisticsDTO getStatisticsOfGrievanceResolution(@PathVariable("office_id") Long officeId) {
        return GrievanceResolutionStatisticsDTO.builder()
                .resolutionTypeInfo(dashboardService.getResolutionTypeInfo(officeId))
                .unacceptedGrievancesCounts(dashboardService.getUnacceptedGrievancesInfo(officeId))
                .currentMonthResolutions(dashboardService.getResolutionsInCurrentMonth(officeId, 0L))
                .build();
    }

    @GetMapping("/office/{office_id}/grievances/resolution-types")
    public ResolutionTypeInfoDTO getTypeOfGrievanceResolution(@PathVariable("office_id") Long officeId) {
        return dashboardService.getResolutionTypeInfo(officeId);
    }

    @GetMapping("/office/{office_id}/grievances/declined-counts")
    public UnacceptedGrievancesCountDTO getStatisticsOfDeclinedGrievances(@PathVariable("office_id") Long officeId) {
        return dashboardService.getUnacceptedGrievancesInfo(officeId);
    }

    @GetMapping("/office/{office_id}/grievances/current-month-resolution")
    public List<MonthlyGrievanceResolutionDTO> getGrievanceResolutionsOfCurrentMonth(@PathVariable("office_id") Long officeId) {
        return dashboardService.getResolutionsInCurrentMonth(officeId, 0L);
    }

    @GetMapping("/office/{office_id}/appeals/resolution-statistics")
    public List<MonthlyGrievanceResolutionDTO> getStatisticsOfAppealResolution(@PathVariable("office_id") Long officeId) {
        return dashboardService.getAppealResolutionsInCurrentMonth(officeId, 0L);
    }

    @GetMapping("/office/{office_id}/grievances/expired")
    public List<NudgeableGrievanceDTO> getExpiredGrievancesByOffice(@PathVariable("office_id") Long officeId) {
        return dashboardService.getTimeExpiredGrievanceDTOList(officeId);
    }

    @GetMapping("/office/{office_id}/appeals/expired")
    public List<NudgeableGrievanceDTO> getExpiredAppealsByOffice(@PathVariable("office_id") Long officeId) {
        return dashboardService.getTimeExpiredAppealDTOList(officeId);
    }


    @GetMapping("/office/{office_id}/{month_diff}/grievanceAscertainCountOfPreviousMonthV2")
    public Long getGrievanceAscertainCountOfPreviousMonthV2(@PathVariable("office_id") Long officeId,
                                                            @PathVariable("month_diff") Long monthDiff){
        return dashboardService.getGrievanceAscertainCountOfPreviousMonthV2(officeId,monthDiff);
    }

    @GetMapping("/central-data")
    public CentralDashboardDataDTO getCentralDashboardData() {
        return dashboardService.getCentralDashboardData();
    }

    @GetMapping("/central-data/{year}/{month}")
    public CentralDashboardDataDTO getCentralDashboardData(@PathVariable("year") Integer year, @PathVariable("month") Integer month) {
        return dashboardService.getCentralDashboardData(year, month);
    }

    @GetMapping("/yearly-data")
    public YearlyCounts getYearlyCount() {
        return dashboardService.getYearlyCounts();
    }

    @GetMapping("/yearly-data/{year}")
    public YearlyCounts getYearlyCount(@PathVariable("year") Integer year) {
        return dashboardService.getYearlyCounts(year);
    }

    @GetMapping("/central-dashboard/services/{service_id}/count-by-office")
    public List<ItemIdNameCountDTO> getServicesCountWithOfficeNameByServiceId(@PathVariable("service_id") Long serviceId) {
        return dashboardService.getServicesCountWithOfficeNameByServiceId(serviceId);
    }

    @GetMapping("/central-dashboard/count-by-all-ministries")
    public List<TotalAndResolvedCountDTO> getTotalSubmittedAndResolvedCountsOfMinistries() {
        return dashboardService.getTotalSubmittedAndResolvedCountsOfMinistries();
    }

    @GetMapping("/central-dashboard/count-by-all-ministries/{year}/{month}")
    public List<TotalAndResolvedCountDTO> getTotalSubmittedAndResolvedCountsOfMinistries(@PathVariable("year") Integer year, @PathVariable("month") Integer month) {
        return dashboardService.getTotalSubmittedAndResolvedCountsOfMinistries(year, month);
    }

    @GetMapping("/central-dashboard/subordinate-stat-all-ministries")
    public List<TotalAndResolvedCountDTO> getSubordinateTotalSubmittedAndResolvedCountsOfMinistries() {
        return dashboardService.getSubordinateTotalSubmittedAndResolvedCountsOfMinistries(true);
    }

    @GetMapping("/central-dashboard/subordinate-stat-all-ministries/{year}/{month}")
    public List<TotalAndResolvedCountDTO> getSubordinateTotalSubmittedAndResolvedCountsOfMinistries(@PathVariable("year") Integer year, @PathVariable("month") Integer month) {
        return dashboardService.getSubordinateTotalSubmittedAndResolvedCountsOfMinistries(year, month, true);
    }

    @GetMapping("/central-dashboard-recipients")
    public List<CentralDashboardRecipientDTO> getAllCentralDashboardRecipients() {
        return dashboardService.getAllCentralDashboardRecipients();
    }

    @PostMapping("/central-dashboard-recipients")
    public CentralDashboardRecipientDTO addNewCentralDashboardRecipients(
            @RequestBody AddCentralDashboardRecipientDTO dashboardRecipientDTO) {
        return dashboardService.addNewCentralDashboardRecipients(dashboardRecipientDTO);
    }


    @GetMapping("/{office_id}/{year}/{month}/countResolvedComplaintsByOfficeIdAndYearAndMonth")
    public Long countResolvedComplaintsByOfficeIdAndYearAndMonth(
           @PathVariable("office_id") Long officeId,
           @PathVariable("year") String year,
           @PathVariable("month") String month){
        return dashboardService.countResolvedComplaintsByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countTimeExpiredComplaintsByOfficeIdAndYearAndMonth")
    public Long countTimeExpiredComplaintsByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countTimeExpiredComplaintsByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countRunningGrievancesByOfficeIdAndYearAndMonth")
    public Long countRunningGrievancesByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countRunningGrievancesByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countTotalComplaintsByOfficeIdAndYearAndMonth")
    public Long countTotalComplaintsByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countTotalComplaintsByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countDeclinedGrievancesByOfficeIdAndYearAndMonth")
    public Long countDeclinedGrievancesByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countDeclinedGrievancesByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/getGrievanceAscertainCountbyOfficeIdAndYearAndMonth")
    public Long getGrievanceAscertainCountbyOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.getGrievanceAscertainCountbyOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/getMonthlyComplaintsCountByOfficeIdAndMediumOfSubmissionAndYearAndMonth")
    public Long getMonthlyComplaintsCountByOfficeIdAndMediumOfSubmissionAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @RequestParam MediumOfSubmission mediumOfSubmission,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.getMonthlyComplaintsCountByOfficeIdAndMediumOfSubmissionAndYearAndMonth(officeId,mediumOfSubmission,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countResolvedAppealByOfficeIdAndYearAndMonth")
    public Long countResolvedAppealByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countResolvedAppealByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countTimeExpiredAppealByOfficeIdAndYearAndMonth")
    public Long countTimeExpiredAppealByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countTimeExpiredAppealByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countRunningAppealByOfficeIdAndYearAndMonth")
    public Long countRunningAppealByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countRunningAppealByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/countTotalAppealByOfficeIdAndYearAndMonth")
    public Long countTotalAppealByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.countTotalAppealByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/getAppealAscertainCountByOfficeIdAndYearAndMonth")
    public Long getAppealAscertainCountByOfficeIdAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.getAppealAscertainCountByOfficeIdAndYearAndMonth(officeId,year,month);
    }

    @GetMapping("/{office_id}/{year}/{month}/getMonthlyAppealCountByOfficeIdAndMediumOfSubmissionAndYearAndMonth")
    public Long getMonthlyAppealCountByOfficeIdAndMediumOfSubmissionAndYearAndMonth(
            @PathVariable("office_id") Long officeId,
            @RequestParam MediumOfSubmission mediumOfSubmission,
            @PathVariable("year") String year,
            @PathVariable("month") String month){
        return dashboardService.getMonthlyAppealCountByOfficeIdAndMediumOfSubmissionAndYearAndMonth(officeId,mediumOfSubmission,year,month);
    }






    @PutMapping("/central-dashboard-recipients/{id}/status/{status}")
    public Boolean changeCentralDashboardRecipientStatus(@PathVariable("id") Long id, @PathVariable("status") Boolean status) {
        return dashboardService.changeCentralDashboardRecipientStatus(id, status);
    }

    @GetMapping("/{office_id}/{is_appeal}/complaintIdsContainRatingInCurrentMonth")
    public List<Long> getComplaintIdsContainRatingInCurrentMonth(@PathVariable("office_id") Long officeId,
                                                                 @PathVariable("is_appeal") Boolean isAppeal){
        return dashboardService.getComplaintIdsContainRatingInCurrentMonth(officeId,isAppeal);
    }

    @GetMapping("/{office_id}/{month_diff}/monthlyComplaintsCountByOfficeIdAndMediumOfSubmission")
    public Long getMonthlyComplaintsCountByOfficeIdAndMediumOfSubmission(
           @PathVariable("office_id") Long officeId,
           @RequestParam MediumOfSubmission mediumOfSubmission,
           @PathVariable("month_diff") Long monthDiff){
        return dashboardService.getMonthlyComplaintsCountByOfficeIdAndMediumOfSubmission(
                officeId,mediumOfSubmission,monthDiff);
    }


    @GetMapping("/{office_id}/{month_diff}/monthlyAppealsCountByOfficeIdAndMediumOfSubmission")
    public Long getMonthlyAppealsCountByOfficeIdAndMediumOfSubmission(
            @PathVariable("office_id") Long officeId,
            @RequestParam   MediumOfSubmission mediumOfSubmission,
            @PathVariable("month_diff") Long monthDiff){

        return dashboardService.getMonthlyAppealsCountByOfficeIdAndMediumOfSubmission(
                officeId,mediumOfSubmission,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/appealAscertainCountOfPreviousMonth")
    public Long getAppealAscertainCountOfPreviousMonth(@PathVariable("office_id") Long officeId,
                                                       @PathVariable("month_diff") Long monthDiff){
        return dashboardService.getAppealAscertainCountOfPreviousMonth(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/monthlyAppealsCountByOfficeIdAndMediumOfSubmissionV2")
    public Long getMonthlyAppealsCountByOfficeIdAndMediumOfSubmissionV2(
            @PathVariable("office_id") Long officeId,
            @RequestParam   MediumOfSubmission mediumOfSubmission,
            @PathVariable("month_diff") Long monthDiff){

        return dashboardService.getMonthlyAppealsCountByOfficeIdAndMediumOfSubmissionV2(
                officeId,mediumOfSubmission,monthDiff);
    }


    @GetMapping("/{office_id}/{month_diff}/countResolvedComplaintsByOfficeId")
    public Long countResolvedComplaintsByOfficeId(@PathVariable("office_id") Long officeId,
                                                  @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countResolvedComplaintsByOfficeId(officeId,monthDiff);
    }


    @GetMapping("/{office_id}/{month_diff}/countTimeExpiredComplaintsByOfficeId")
    public Long countTimeExpiredComplaintsByOfficeId(@PathVariable("office_id") Long officeId,
                                                  @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countTimeExpiredComplaintsByOfficeId(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countRunningGrievancesByOfficeId")
    public Long countRunningGrievancesByOfficeId(@PathVariable("office_id") Long officeId,
                                                     @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countRunningGrievancesByOfficeId(officeId,monthDiff);
    }


    @GetMapping("/{office_id}/{month_diff}/countDeclinedGrievancesByOfficeId")
    public Long countDeclinedGrievancesByOfficeId(@PathVariable("office_id") Long officeId,
                                                 @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countDeclinedGrievancesByOfficeId(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countRunningAppealsByOfficeId")
    public Long countRunningAppealsByOfficeId(@PathVariable("office_id") Long officeId,
                                                  @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countRunningAppealsByOfficeId(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countTimeExpiredAppealsByOfficeId")
    public Long countTimeExpiredAppealsByOfficeId(@PathVariable("office_id") Long officeId,
                                              @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countTimeExpiredAppealsByOfficeId(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countTotalComplaintsByOfficeIdV2")
    public Long countTotalComplaintsByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                                  @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countTotalComplaintsByOfficeIdV2(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countResolvedComplaintsByOfficeIdV2")
    public Long countResolvedComplaintsByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                                 @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countResolvedComplaintsByOfficeIdV2(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countTimeExpiredComplaintsByOfficeIdV3")
    public Long countTimeExpiredComplaintsByOfficeIdV3(@PathVariable("office_id") Long officeId,
                                                    @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countTimeExpiredComplaintsByOfficeIdV3(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countRunningGrievancesByOfficeIdV2")
    public Long countRunningGrievancesByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                                       @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countRunningGrievancesByOfficeIdV2(officeId,monthDiff);
    }


    @GetMapping("/{office_id}/{month_diff}/countInheritedComplaintsByOfficeId")
    public Long countInheritedComplaintsByOfficeId(@PathVariable("office_id") Long officeId,
                                                   @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countInheritedComplaintsByOfficeId(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countForwardedGrievancesByOfficeIdV2")
    public Long countForwardedGrievancesByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                                   @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countForwardedGrievancesByOfficeIdV2(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countTotalAppealsByOfficeIdV2")
    public Long countTotalAppealsByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                                     @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countTotalAppealsByOfficeIdV2(officeId,monthDiff);
    }


    @GetMapping("/{office_id}/{month_diff}/countResolvedAppealsByOfficeIdV2")
    public Long countResolvedAppealsByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                              @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countResolvedAppealsByOfficeIdV2(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countTimeExpiredAppealsByOfficeIdV2")
    public Long countTimeExpiredAppealsByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                                 @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countTimeExpiredAppealsByOfficeIdV2(officeId,monthDiff);
    }

    @GetMapping("/{office_id}/{month_diff}/countRunningAppealsByOfficeIdV2")
    public Long countRunningAppealsByOfficeIdV2(@PathVariable("office_id") Long officeId,
                                                    @PathVariable("month_diff") Long monthDiff){
        return dashboardService.countRunningAppealsByOfficeIdV2(officeId,monthDiff);
    }




    @DeleteMapping("/central-dashboard-recipients/{id}")
    public Boolean deleteCentralDashboardRecipient(@PathVariable("id") Long id) {
        return dashboardService.deleteCentralDashboardRecipient(id);
    }

    @GetMapping("/offices/{office_id}/current-month-register")
    public List<RegisterDTO> getDashboardDataForGrievanceRegister(@PathVariable("office_id") Long officeId) {
        return dashboardService.getDashboardDataForGrievanceRegister(officeId);
    }

    @GetMapping("/offices/{office_id}/current-month-appeal-register")
    public List<RegisterDTO> getDashboardDataForAppealRegister(@PathVariable("office_id") Long officeId) {
        return dashboardService.getDashboardDataForAppealRegister(officeId);
    }

    @GetMapping("/offices/{office_id}/register")
    public Page<RegisterDTO> getPaginatedDashboardDataForGrievanceRegister(
            @PathVariable("office_id") Long officeId,
            @RequestParam(value = "trackingNumber", required = false) String trackingNumber,
            Pageable pageable) {
        return dashboardService.getPageableDashboardDataForGrievanceRegister(officeId, trackingNumber, pageable);
    }

    @GetMapping("/offices/{office_id}/appeal-register")
    public Page<RegisterDTO> getPaginatedDashboardDataForAppealRegister(@PathVariable("office_id") Long officeId, Pageable pageable) {
        return dashboardService.getPageableDashboardDataForAppealRegister(officeId, pageable);
    }

    @GetMapping("/offices/{office_id}/appealed-complaints")
    public Page<RegisterDTO> getPaginatedDashboardDataForAppealedComplaints(@PathVariable("office_id") Long officeId, Pageable pageable) {
        return dashboardService.getPageableDashboardDataForAppealedComplaints(officeId, pageable);
    }

    @GetMapping("/tagid/list")
    public Page<NudgeDTO> getPaginatedDashboardDataForTagidList(Authentication authentication, Pageable pageable) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return dashboardService.getPageableDashboardDataForTagidList(userInformation, pageable);
    }

    /***************    New HOO Dashboard    *******************/

    @GetMapping("/offices/{office_id}/hoo-dashboard-data")
    public GRSStatisticDTO getCurrentMonthGRSStatisticsDTO(Authentication authentication, @PathVariable("office_id") Long officeId) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        return grsStatisticsDAO.getGRSStatistics(userInformation, officeId, year, month);
    }


    @GetMapping("/offices/{office_id}/hoo-dashboard-data/{year_month}")
    public GRSStatisticDTO getCurrentMonthGRSStatistics(Authentication authentication, @PathVariable("office_id") Long officeId, @PathVariable(value = "year_month") String key) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        String[] values = key.split("_");
        if (values.length < 2) {
            return new GRSStatisticDTO();
        }
        int year = Integer.parseInt(values[0]);
        int month = Integer.parseInt(values[1]);
        return grsStatisticsDAO.getGRSStatistics(userInformation, officeId, year, month);
    }

    @GetMapping("/offices/{office_id}/hoo-child-offices-data")
    public List<SubOfficesStatisticsDTO> getChildOfficesStatisticsForCurrentMonth(@PathVariable("office_id") Long officeId) {
        return grsStatisticsDAO.getCurrentMonthChildOfficesStatistics(officeId);
    }

    @GetMapping("/cell-dashboard-data")
    public GRSStatisticDTO getCabinetCellStatisticsForCurrentMonth(Authentication authentication) {
        try {
            if (!Utility.isCellGRO(authentication)) {
                throw new Exception("Access denied for viewing cell dashboard data");
            }
            return grsStatisticsDAO.getGRSStatisticsOfCabinetCell();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    @GetMapping("/offices/{office_id}/grievances/count-by-status")
    public GeneralDashboardDataDTO getGrievanceCountByCurrentStatus(@PathVariable("office_id") Long officeId) {
        return dashboardService.getGrievanceDataForGRODashboard(officeId, false);
    }

    @GetMapping("/layer/{layerLevel}/officeOrigin/{officeOrigin}/offices/{office_id}/year/{year}/month/{month}/grievances/count-by-status")
    public GeneralDashboardDataDTO getGrievanceCountByCurrentStatus(
            @PathVariable("layerLevel") Integer layerLevel,
            @PathVariable("officeOrigin") Long officeOrigin,
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") Integer year,
            @PathVariable("month") Integer month,
            @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled
    ) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        if (year < 0) year = null;
        if (month < 1 || month > 12) month = null;

        if (layerLevel >= 0 && layerLevel <= 2) {
            officeOrigin = null;
        } else {
            if (officeOrigin.equals(CacheUtil.SELECT_ALL_OPTION_VALUE)) {
                officeId = CacheUtil.SELECT_ALL_OPTION_VALUE;
            }
        }

        if (year != null && month != null && year == currentYear && month == currentMonth + 1) {

            return dashboardService.getGrievanceDataForGRODashboard(layerLevel, officeOrigin, officeId, false, grsEnabled);
        } else {
            return dashboardService.getTotalSummaryGrievancesByOfficeAndYearAndMonth(layerLevel, officeOrigin, officeId, year, month, grsEnabled);

        }

    }

    @GetMapping("/offices/{office_id}/grievances/current-year-total-resolved")
    public List<TotalResolvedByMonth> getTotalResolvedGrievancesByMonthOfCurrentYear(@PathVariable("office_id") Long officeId) {
        return dashboardService.getTotalResolvedGrievancesByMonthOfCurrentYear(officeId, null, null);
    }

    @GetMapping("/layer/{layerLevel}/officeOrigin/{officeOrigin}/offices/{office_id}/year/{year}/month/{month}/grievances/current-year-total-resolved")
    public List<TotalResolvedByMonth> getTotalResolvedGrievancesByMonthOfGivenYear(
            @PathVariable("layerLevel") Integer layerLevel,
            @PathVariable("officeOrigin") Long officeOrigin,
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") Integer year,
            @PathVariable("month") Integer month,
            @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled
    ) {
        if (year < 0) year = null;
        if (month < 1 || month > 12) month = null;

        if (layerLevel >= 0 && layerLevel <= 2) {
            officeOrigin = null;
        } else {
            if (officeOrigin.equals(CacheUtil.SELECT_ALL_OPTION_VALUE)) {
                officeId = CacheUtil.SELECT_ALL_OPTION_VALUE;
            }
        }
        return dashboardService.getTotalResolvedGrievancesByMonthOfCurrentYear(layerLevel, officeOrigin, officeId, year, month, grsEnabled);
    }

    @GetMapping("/offices/{office_id}/grievances/all-year-total-resolved")
    public List<TotalResolvedByYear> getTotalResolvedGrievancesByYearOfAllYear(@PathVariable("office_id") Long officeId) {
        return dashboardService.getTotalResolvedGrievancesByYearOfAllYear(officeId, null);
    }


    @GetMapping("/offices/{office_id}/year/{year}/grievances/all-year-total-resolved")
    public List<TotalResolvedByYear> getTotalResolvedGrievancesByYearOfAllYear(@PathVariable("office_id") Long officeId, @PathVariable("year") Integer year) {
        if (year < 0) year = null;
        return dashboardService.getTotalResolvedGrievancesByYearOfAllYear(officeId, year);
    }

    //appeals
    @GetMapping("/offices/{office_id}/appeals/count-by-status")
    public GeneralDashboardDataDTO getAppealCountByCurrentStatus(@PathVariable("office_id") Long officeId) {
        return dashboardService.getGrievanceDataForAODashboard(officeId, false);
    }

    @GetMapping("/layer/{layerLevel}/officeOrigin/{officeOrigin}/offices/{office_id}/year/{year}/month/{month}/appeals/count-by-status")
    public GeneralDashboardDataDTO getAppealCountByCurrentStatus(
            @PathVariable("layerLevel") Integer layerLevel,
            @PathVariable("officeOrigin") Long officeOrigin,
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") Integer year,
            @PathVariable("month") Integer month,
            @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled
    ) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        if (year < 0) year = null;
        if (month < 1 || month > 12) month = null;

        if (layerLevel >= 0 && layerLevel <= 2) {
            officeOrigin = null;
        } else {
            if (officeOrigin.equals(CacheUtil.SELECT_ALL_OPTION_VALUE)) {
                officeId = CacheUtil.SELECT_ALL_OPTION_VALUE;
            }
        }

        if (year != null && month != null && year == currentYear && month == currentMonth + 1) {

            return dashboardService.getGrievanceDataForAODashboard(layerLevel, officeOrigin, officeId, false, grsEnabled);
        } else {
            return dashboardService.getTotalSummaryAppealByOfficeAndYearAndMonth(layerLevel, officeOrigin, officeId, year, month, grsEnabled);

        }

    }

    @GetMapping("/offices/{office_id}/appeals/current-year-total-resolved")
    public List<TotalResolvedByMonth> getTotalResolvedAppealsByMonthOfCurrentYear(@PathVariable("office_id") Long officeId) {
        return dashboardService.getTotalResolvedAppealsByMonthOfCurrentYear(officeId);
    }

    @GetMapping("/layer/{layerLevel}/officeOrigin/{officeOrigin}/offices/{office_id}/year/{year}/month/{month}/appeals/current-year-total-resolved")
    public List<TotalResolvedByMonth> getTotalResolvedAppealsByMonthOfCurrentYear(
            @PathVariable("layerLevel") Integer layerLevel,
            @PathVariable("officeOrigin") Long officeOrigin,
            @PathVariable("office_id") Long officeId,
            @PathVariable("year") Integer year,
            @PathVariable("month") Integer month,
            @RequestParam(value = "grsEnabled", defaultValue = "true") Boolean grsEnabled
    ) {
        if (year < 0) year = null;
        if (month < 1 || month > 12) month = null;

        if (layerLevel >= 0 && layerLevel <= 2) {
            officeOrigin = null;
        } else {
            if (officeOrigin.equals(CacheUtil.SELECT_ALL_OPTION_VALUE)) {
                officeId = CacheUtil.SELECT_ALL_OPTION_VALUE;
            }
        }
        return dashboardService.getTotalResolvedAppealByMonthOfCurrentYear(layerLevel, officeOrigin, officeId, year, month, grsEnabled);
    }

    @GetMapping("/field-coordinator-data")
    public List<SubOfficesStatisticsDTO> getGrievanceRelatedStatisticsForFieldCoordinator(Authentication authentication) {
        return grsStatisticsDAO.getOfficesStatisticsForFieldCoordinator(authentication);
    }

    @GetMapping("/field-coordinator-appeal-data")
    public ComplaintsCountByTypeDTO getAppealRelatedStatisticsForFieldCoordinator(Authentication authentication) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return grsStatisticsDAO.getDivisionAppealStatisticsForFieldCoordinator(userInformation);
    }

    @GetMapping("/safety-net-program-report/{from_date}/{to_date}")
    public SafetyNetProgramReportResponse safetyNetProgramReport(Authentication authentication, @PathVariable(name = "from_date") String fromDate, @PathVariable(name = "to_date") String toDate) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return this.dashboardService.safetyNetProgramReport(userInformation, fromDate, toDate);
    }

    @GetMapping("/safety-net-program-report/{from_date}/{to_date}/{program_id}")
    public SafetyNetProgramReportResponse safetyNetProgramReportByProgramId(Authentication authentication, @PathVariable(name = "from_date") String fromDate, @PathVariable(name = "to_date") String toDate, @PathVariable(name = "program_id") Integer programId) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return this.dashboardService.safetyNetProgramReportByProgramId(userInformation, fromDate, toDate, programId);
    }

    @PostMapping("/safety-net-program/{programId}/safetyNetSummary")
    public SafetyNetSummaryResponse getSafetyNetSummary(
            @RequestBody UserInformation userInformation,
            @PathVariable("programId") Integer programId){
        return this.dashboardService.getSafetyNetSummary(userInformation,programId);
    }

}
