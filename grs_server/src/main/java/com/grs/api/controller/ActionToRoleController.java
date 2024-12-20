package com.grs.api.controller;

import com.grs.api.model.OISFUserType;
import com.grs.api.model.wrapperDTO.GrievanceStatusAndRoleAndActionDTO;
import com.grs.api.model.wrapperDTO.GrievanceStatusAndRoleTypeDTO;
import com.grs.core.domain.grs.Action;
import com.grs.core.domain.grs.ActionToRole;
import com.grs.core.domain.grs.GrievanceStatus;
import com.grs.core.domain.grs.GrsRole;
import com.grs.core.service.ActionToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.WeakHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/actionToRole")
public class ActionToRoleController {

    private final ActionToRoleService actiontoRoleService;

    @RequestMapping(value = "/grsRole",method = RequestMethod.POST)
    public GrsRole findGrsRole(
            @RequestBody OISFUserType oisfUserType){
        return this.actiontoRoleService.findGrsRole(oisfUserType);

    }

    @RequestMapping(value = "/findByGrievanceStatusAndRoleType",method = RequestMethod.POST)
    public List<ActionToRole> findByGrievanceStatusAndRoleType(@RequestBody GrievanceStatusAndRoleTypeDTO grievanceStatusAndRoleTypeDTO){
        return this.actiontoRoleService.findByGrievanceStatusAndRoleType(
                grievanceStatusAndRoleTypeDTO.getGrievanceStatus(),
                grievanceStatusAndRoleTypeDTO.getRole());
    }

    @RequestMapping(value = "/findByGrievanceStatusAndRoleAndAction",method = RequestMethod.POST)
    public ActionToRole findByGrievanceStatusAndRoleAndAction(
            @RequestBody GrievanceStatusAndRoleAndActionDTO grievanceStatusAndRoleAndActionDTO){
        return this.actiontoRoleService.findByGrievanceStatusAndRoleAndAction(
                grievanceStatusAndRoleAndActionDTO.getGrievanceStatus(),
                grievanceStatusAndRoleAndActionDTO.getGrsRole(),
                grievanceStatusAndRoleAndActionDTO.getAction());
    }

    @RequestMapping(value = "/findRoleByRoleName",method = RequestMethod.POST)
    public GrsRole getRoleByRoleName(@RequestParam String role){
        return this.actiontoRoleService.getRolebyRoleName(role);
    }

    @RequestMapping(value = "/findByName",method = RequestMethod.POST)
    public GrievanceStatus findByName(@RequestParam String statusName){
        return this.actiontoRoleService.findByName(statusName);
    }

    @RequestMapping(value = "/findByActionId/{actionID}",method = RequestMethod.GET)
    public Action findByActionId(@PathVariable("actionID") Long actionID){
        return this.actiontoRoleService.findByActionId(actionID);
    }

    @RequestMapping(value = "/findDistinctGRSRoleByGrievanceStatus",method = RequestMethod.POST)
    public WeakHashMap<String, String> findDistinctGRSRoleByGrievanceStatus(
            @RequestParam String grievanceStatus){
        return this.actiontoRoleService.findDistinctGRSRoleByGrievanceStatus(grievanceStatus);
    }

    @RequestMapping(value = "/findActionsByGrievanceStatusAndGrsRole",method = RequestMethod.POST)
    public WeakHashMap<String, String> findActionsByGrievanceStatusAndGrsRole(
           @RequestParam String grievanceStatus,
           @RequestParam String grsRole){

        return this.actiontoRoleService.findActionsByGrievanceStatusAndGrsRole(grievanceStatus,grsRole);
    }


}
