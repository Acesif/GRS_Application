package com.grs.api.controller;


import com.grs.core.service.GrievanceMigratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrievanceMigratorController {

    @Autowired
    private  GrievanceMigratorService grievanceMigratorService;
}
