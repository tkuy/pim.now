package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public interface ProductService {
    Product createProduct(Product product, Set<AttributValue> attributValues, Customer customer, Family family, Set<Workflow> workflowsToLink, Set<Category> categoriesToLink, Map<String, MultipartFile> multipartFiles) throws IOException;
    List<AttributValue> findAttributValuesByProductId(Long productId, Long customerId);
    Product updateProduct(Product product, Set<AttributValue> attributValues, Set<Category> categoriesToLink, Map<String, MultipartFile> multipartFiles) throws IOException;
    Page<Product> getSearchProductPage(String query, Pageable pageable);
}
