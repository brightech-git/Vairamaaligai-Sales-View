package com.VTM.application.storedProcedure;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {
        "https://sales.nkvairamaaligai.com",
        "http://localhost:3000"
})

public class DashboardController {

    private final DashboardRepository dashboardRepository;

    public DashboardController(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false, defaultValue = "") String costId,
            @RequestParam(required = false, defaultValue = "VAIADMINDB") String adminDB,
            @RequestParam(required = false, defaultValue = "VAIT2526") String transDB,
            @RequestParam(required = false, defaultValue = "VAISH0708") String schemeDB
    ) {
        return dashboardRepository.getDashboardData(
                fromDate, toDate, costId, adminDB, transDB, schemeDB
        );
    }
}
