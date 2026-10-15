package com.app.eventhive.controller;

import com.app.eventhive.dto.OrganizerStatsResponse;
import com.app.eventhive.security.SecurityUtils;
import com.app.eventhive.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final SecurityUtils securityUtils;

    public OrganizerController(OrganizerService organizerService, SecurityUtils securityUtils) {
        this.organizerService = organizerService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<OrganizerStatsResponse> getStats() {
        Long organizerId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(organizerService.getOrganizerStats(organizerId));
    }
}
