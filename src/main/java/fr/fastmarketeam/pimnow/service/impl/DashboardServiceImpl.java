package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Dashboard;
import fr.fastmarketeam.pimnow.domain.User;
import fr.fastmarketeam.pimnow.repository.*;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.DashboardService;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final ProductRepository productRepository;
    private final PrestashopProductRepository prestashopProductRepository;
    private final UserExtraRepository userExtraRepository;
    private final UserRepository userRepository;
    private final UserCustomerUtil userCustomerUtil;
    private final CustomerRepository customerRepository ;

    public DashboardServiceImpl(CustomerRepository customerRepository, PrestashopProductRepository prestashopProductRepository, ProductRepository productRepository, UserExtraRepository userExtraRepository, UserRepository userRepository, UserCustomerUtil userCustomerUtil) {
        this.productRepository = productRepository;
        this.userExtraRepository = userExtraRepository;
        this.userRepository = userRepository;
        this.userCustomerUtil = userCustomerUtil;
        this.prestashopProductRepository = prestashopProductRepository ;
        this.customerRepository = customerRepository ;
    }

    @Override
    public Dashboard dataByUserId(Long userId) {
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        int nbCustomerActive = -1 ;
        int nbCustomerInactive = -1 ;
        int nbUserInactive = -1 ;
        int nbUserActive = -1 ;
        int nbProductIntegrated = prestashopProductRepository.countPrestashopProductByProductPimCustomerId(customer.getId()) ;
        int nbProductCreated = productRepository.countProductsByCustomerIdAndIsDeletedIsFalse(customer.getId());
        int nbProductDeleted = productRepository.countProductsByCustomerIdAndIsDeletedIsTrue(customer.getId());

        return new Dashboard(customer, nbCustomerActive, nbCustomerInactive, nbProductCreated, nbProductDeleted, nbProductIntegrated, nbUserInactive, nbUserActive);
    }

    @Override
    public Dashboard dataByAdminFoncId(Long adminId) {

        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        int nbCustomerActive = -1 ;
        int nbCustomerInactive = -1 ;
        int nbUserInactive = userExtraRepository.countByCustomerIdAndUserActivated(customer.getId(), false) ;
        int nbUserActive = userExtraRepository.countByCustomerIdAndUserActivated(customer.getId(), true) ;
        int nbProductIntegrated = prestashopProductRepository.countPrestashopProductByProductPimCustomerId(customer.getId()) ;
        int nbProductCreated = productRepository.countProductsByCustomerIdAndIsDeletedIsFalse(customer.getId());
        int nbProductDeleted = productRepository.countProductsByCustomerIdAndIsDeletedIsTrue(customer.getId());

        return new Dashboard(customer, nbCustomerActive, nbCustomerInactive, nbProductCreated, nbProductDeleted, nbProductIntegrated, nbUserInactive, nbUserActive);

    }

    @Override
    public Dashboard dataByAdminId(Long adminId) {
        int nbCustomerActive = customerRepository.countAllByIsDeleted(false) ;
        int nbCustomerInactive = customerRepository.countAllByIsDeleted(true) ;
        int nbUserInactive = userRepository.countAllByActivated(false) ;
        int nbUserActive = userRepository.countAllByActivated(true) ;
        Long nbProductIntegrated = prestashopProductRepository.count() ;
        int nbProductCreated = productRepository.countAllByIsDeleted(false) ;
        int nbProductDeleted = productRepository.countAllByIsDeleted(true) ;
        return new Dashboard(null, nbCustomerActive, nbCustomerInactive, nbProductCreated, nbProductDeleted, nbProductIntegrated.intValue(), nbUserInactive, nbUserActive);
    }

    @Override
    public Dashboard dataByUser() {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get() ;

        if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.USER)) {
            return this.dataByUserId(user.getId()) ;
        }   else if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN_FONC)) {
            return this.dataByAdminFoncId(user.getId()) ;
        }   else if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            return this.dataByAdminId(user.getId()) ;
        }

        return null ;
    }
}
