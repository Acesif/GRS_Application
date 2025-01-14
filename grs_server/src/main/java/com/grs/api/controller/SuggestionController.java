package com.grs.api.controller;

import com.grs.api.model.request.SuggestionDetailsDTO;
import com.grs.api.model.request.SuggestionRequestDTO;
import com.grs.api.model.response.GenericResponse;
import com.grs.api.model.response.SuggestionResponseDTO;
import com.grs.core.domain.grs.Suggestion;
import com.grs.core.service.*;
import com.grs.utils.StringUtil;
import com.grs.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//TODO backend checking of field of suggestion form
public class SuggestionController {
    @Autowired
    private SuggestionService suggestionService;
    @Autowired
    private ModelViewService modelViewService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private ComplainantService complainantService;
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/api/suggestion", method = RequestMethod.POST)
    public GenericResponse addSuggestionDetail(@RequestBody SuggestionRequestDTO suggestionRequestDTO, BindingResult bindingResult) {
        String message;
        boolean success = false;
        if(suggestionRequestDTO.getOfficeId() == null) {
            message = messageService.isCurrentLanguageInEnglish() ? "Error! Office selection is not valid" : "দুঃখিত দপ্তর নির্বাচন সঠিক নয়";
        } else if(suggestionRequestDTO.getOfficeServiceId() == null && !StringUtil.isValidString(suggestionRequestDTO.getOfficeServiceName())) {
            message = messageService.isCurrentLanguageInEnglish() ? "Error! Service selection is not valid" : "দুঃখিত সেবা নির্বাচন সঠিক নয়";
        } else {
            Suggestion suggestion = suggestionService.addSuggestion(suggestionRequestDTO);
            if (suggestion == null) {
                message = messageService.isCurrentLanguageInEnglish() ? "Error! Cannot submit suggestion" : "দুঃখিত! পরামর্শ প্রদান করা যাচ্ছেনা";
            } else {
                message = messageService.isCurrentLanguageInEnglish() ? "Your suggestions has been submitted" : "আপনার মতামত গৃহীত হয়েছে";
                success = true;
            }
        }
        return GenericResponse.builder().success(success).message(message).build();
    }

    @RequestMapping(value = "/api/offices/{office_id}/suggestions", method = RequestMethod.GET)
    public Page<SuggestionResponseDTO> getSuggestions(Authentication authentication,
                                                      @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable,
                                                      @PathVariable("office_id") Long officeId){
        log.info("View Page Request : /api/offices/{}/suggestions", officeId);
        if(authentication != null && (Utility.isUserAHOOUser(authentication) || Utility.isUserAnGROUser(authentication) || Utility.isUserACentralDashboardRecipient(authentication))) {
            return suggestionService.getSuggestionByOfficeId(officeId, pageable);
        }
        return null;
    }

    @RequestMapping(value = "/api/suggestion/{id}", method = RequestMethod.GET)
    public SuggestionDetailsDTO getSuggestionDetails(Authentication authentication,
                                                     @PathVariable("id") Long id) {
        if(authentication != null && (Utility.isUserAHOOUser(authentication) || Utility.isUserAnGROUser(authentication) || Utility.isUserACentralDashboardRecipient(authentication))) {
            return suggestionService.getSuggestionDetails(id);
        }
        return null;

    }
}
