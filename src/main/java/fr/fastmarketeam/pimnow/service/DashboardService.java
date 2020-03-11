package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Dashboard;

public interface DashboardService {
    Dashboard dataByUserId(Long userId);

    Dashboard dataByAdminFoncId(Long adminId);

    Dashboard dataByAdminId(Long adminId);
    Dashboard dataByUser();
}
