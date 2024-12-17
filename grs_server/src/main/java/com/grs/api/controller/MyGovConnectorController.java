package com.grs.api.controller;


import com.grs.api.model.request.ComplainantDTO;
import com.grs.api.myGov.MyGovTokenResponse;
import com.grs.api.myGov.MyGovUser;
import com.grs.core.service.MyGovConnectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myGovConnect")
public class MyGovConnectorController {

    private final MyGovConnectorService myGovConnectorService;

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ComplainantDTO createUser(
            @RequestBody ComplainantDTO complainantDTO) throws Exception {
        return myGovConnectorService.createUser(complainantDTO);
    }
    @RequestMapping(value = "/getMyGovToken", method = RequestMethod.POST)
    public MyGovTokenResponse getMyGovToken(@RequestParam String code) {
        return myGovConnectorService.getMyGovToken(code);
    }
    @RequestMapping(value = "/getMyGovLoginUser", method = RequestMethod.POST)
    public MyGovUser getMyGovLoginUser(@RequestParam String accessToken) {
        return myGovConnectorService.getMyGovLoginUser(accessToken);
    }


}
