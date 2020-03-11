package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Category;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Product;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category addProduct(Category category, Product product);
    Category createCategory(Category category, Customer customer) ;
    Optional<Category> getCategoryByIdAndCustomer(Customer customer, long id) ;
    boolean deleteCategoryById(Long id);
}
