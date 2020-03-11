package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface CustomerService {
    /**
     * This function create the roots of the customer (family and category). It is used when creating a new customer.
     * @param newCustomer
     */
    Customer addRootFamilyAndCategoryToNewCustomer(Customer newCustomer);
}
