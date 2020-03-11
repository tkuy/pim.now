package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.Category;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Product;
import fr.fastmarketeam.pimnow.repository.CategoryRepository;
import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.CategoryService;
import fr.fastmarketeam.pimnow.service.errors.*;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private CategoryRepository categoryRepository;
    private final UserCustomerUtil userCustomerUtil;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserCustomerUtil userCustomerUtil, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.userCustomerUtil = userCustomerUtil;
        this.productRepository = productRepository;
    }

    @Override
    public Category addProduct(Category category, Product product) {
        category.addProduct(product);
        return categoryRepository.save(category);
    }

    @Override
    public Category createCategory(Category category, Customer customer) {
        if(customer == null) {
            throw new CustomerRequiredActionException();
        }
        Category predecessor;
        if(category.getPredecessor() == null) {
            predecessor = categoryRepository.findById(Long.valueOf(customer.getCategoryRoot())).get();
        } else {
            predecessor = categoryRepository.findById(Long.valueOf(category.getPredecessor().getId())).get();
        }
        Category categoryExample = new Category();
        categoryExample.setIdF(category.getIdF());
        categoryExample.setCustomer(customer);
        if(categoryRepository.findOne(Example.of(categoryExample)).isPresent()) {
            throw new FunctionalIDAlreadyUsedException();
        }

        category.setCustomer(customer);
        // save the category
        Category result = categoryRepository.save(category);
        predecessor.addSuccessors(category);
        // save the predecessor
        categoryRepository.save(predecessor);
        return result;
    }

    @Override
    public Optional<Category> getCategoryByIdAndCustomer(Customer customer, long id) {
        Optional<Category> category = categoryRepository.findById(id) ;
        if(!category.isPresent()) {
            throw new IllegalStateException("The category is not found") ;
        }
        if(!category.get().getCustomer().getId().equals(customer.getId())){
            throw new IllegalStateException("Not your entity") ;
        }
        return category ;
    }


    @Override
    public boolean deleteCategoryById(Long id) {
        Optional<Category> optC = categoryRepository.findByIdAndCustomerWithEagerRelationship(id);

        if (!optC.isPresent()) {
            return true;
        }

        Category c = optC.get();
        Customer userCustomer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        if (!userCustomer.getId().equals(c.getCustomer().getId())) {
            return true;
        }

        if (id.longValue() == userCustomer.getFamilyRoot().longValue()) {
            throw new RootCategoryException();
        }

        if (c.getSuccessors().stream().filter(fam -> !fam.isDeleted()).count() != 0) {
            throw new SuccessorCategoryStillAliveException();
        }

        if (!productRepository.findAllByCustomerIdAndCategoryIdAndIsDeletedIsFalse(userCustomer.getId(), id).isEmpty()) {
            throw new ProductStillAttachedToCategoryException();
        }

        c.setDeleted(true);
        categoryRepository.save(c);
        return false;
    }

}
