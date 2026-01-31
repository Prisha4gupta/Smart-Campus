package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.DashboardResponse;
import com.sca.smartcampusbackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Dashboard endpoints for student-specific aggregated information.
 *
 * Secured: only users with role STUDENT or ADMIN may access.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get dashboard information for a given student id.
     *
     * Example: GET /api/dashboard/42
     */
    @GetMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable("studentId") Long studentId) {
        DashboardResponse resp = dashboardService.getDashboardForStudent(studentId);
        return ResponseEntity.ok(resp);
    }
}
