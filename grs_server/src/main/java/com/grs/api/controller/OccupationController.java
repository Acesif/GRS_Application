package com.grs.api.controller;


import com.grs.api.model.response.OccupationDTO;
import com.grs.core.domain.grs.Occupation;
import com.grs.core.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Occupation")
public class OccupationController {

    private final OccupationService occupationService;

    @RequestMapping(value="/convertToOccupationDTO",method= RequestMethod.POST)
    public OccupationDTO convertToOccupationDTO(@RequestBody Occupation occupation){
       return occupationService.convertToOccupationDTO(occupation);
    }
    @RequestMapping(value="/findAllOccupations",method = RequestMethod.POST)
    public Page<OccupationDTO> findAllOccupations (@RequestBody Pageable pageable){
        return occupationService.findAllOccupations(pageable);
    }

    @RequestMapping(value="/saveAllOccupations",method = RequestMethod.POST)
    public Boolean saveAllOccupations(@RequestBody OccupationDTO occupationDTO){
        return occupationService.saveAllOccupations(occupationDTO);
    }

    @RequestMapping(value = "/getOccupation/{id}",method = RequestMethod.GET)
    public Occupation getOccupation(@PathVariable("id") Long id){
        return occupationService.getOccupation(id);
    }

    @RequestMapping(value = "/updateOccupation",method = RequestMethod.POST)
    public Boolean updateOccupation(@RequestParam Long occupationID,
                                    @RequestBody OccupationDTO occupationDTO){
        return occupationService.updateOccupation(occupationID,occupationDTO);

    }


}
