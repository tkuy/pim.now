package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Dashboard;
import fr.fastmarketeam.pimnow.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.fastmarketeam.pimnow.security.SecurityUtils;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Customer}.
 */
@RestController
@RequestMapping("/api")
public class DashboardResource {

    private final Logger log = LoggerFactory.getLogger(DashboardResource.class);
    private static final String ENTITY_NAME = "dashboard";
    private final DashboardService dashboardService ;

    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService ;
    }

    /**
     * {@code GET  /dashboard} : get all the customers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customers in body.
     */
    @GetMapping("/dashboard")
    public Dashboard getDashboard() {

        if(!SecurityUtils.isAuthenticated())
            return null ;

        return dashboardService.dataByUser() ;
    }

}
