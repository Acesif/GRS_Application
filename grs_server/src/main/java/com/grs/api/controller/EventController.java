package com.grs.api.controller;


import com.grs.api.model.EventSubscriber;
import com.grs.core.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public void publish(
            @RequestParam String event,
            @RequestParam Object... args) {
        this.eventService.publish(event, args);
    }
}
