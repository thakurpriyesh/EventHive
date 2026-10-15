package com.app.eventhive.controller;

import com.app.eventhive.dto.OrganizerStatsResponse;
import com.app.eventhive.security.SecurityUtils;
import com.app.eventhive.service.OrganizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerControllerTest {

    @Mock
    private OrganizerService organizerService;

    @Mock
    private SecurityUtils securityUtils;

    private OrganizerController organizerController;

    @BeforeEach
    void setUp() {
        organizerController = new OrganizerController(organizerService, securityUtils);
    }

    @Test
    void testGetStats() {
        when(securityUtils.getCurrentUserId()).thenReturn(10L);
        when(organizerService.getOrganizerStats(10L))
                .thenReturn(new OrganizerStatsResponse(3, 15, BigDecimal.valueOf(1500.00)));

        ResponseEntity<OrganizerStatsResponse> response = organizerController.getStats();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().totalEvents());
        assertEquals(15, response.getBody().totalBookings());
        assertEquals(BigDecimal.valueOf(1500.00), response.getBody().totalRevenue());
    }
}
