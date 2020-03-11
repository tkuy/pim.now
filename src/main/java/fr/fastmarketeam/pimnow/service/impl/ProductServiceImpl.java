package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.*;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
import fr.fastmarketeam.pimnow.repository.AttributRepository;
import fr.fastmarketeam.pimnow.repository.AttributValueRepository;
import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.repository.search.ProductSearchRepository;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.CategoryService;
import fr.fastmarketeam.pimnow.service.FileUploadService;
import fr.fastmarketeam.pimnow.service.ProductService;
import fr.fastmarketeam.pimnow.service.WorkflowService;
import fr.fastmarketeam.pimnow.service.errors.*;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final AttributValueRepository attributValueRepository;

    private final WorkflowService workflowService;

    private final CategoryService categoryService;

    private final FileUploadService fileUploadService;

    private final UserCustomerUtil userCustomerUtil;

    private final ProductSearchRepository productSearchRepository;

    private static final float IDF_BOOST_VALUE = 5.0f;
    private static final float NAME_BOOST_VALUE = 4.0f;
    private static final float DESCRIPTION_BOOST_VALUE = 1.0f;


    private static final Fuzziness IDF_FUZINESS_LEVEL = Fuzziness.ZERO;
    private static final Fuzziness NAME_FUZINESS_LEVEL = Fuzziness.AUTO;
    private static final Fuzziness DESCRIPTION_FUZINESS_LEVEL = Fuzziness.AUTO;

    private static final String SEARCH_DELIMITOR = ";";
    private static final String PROPERTY_IDF = "idF";
    private static final String PROPERTY_NAME = "nom";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_FAMILY = "family.nom";
    private static final String PROPERTY_CATEGORY = "categories.nom";

    private final AttributRepository attributRepository;

    public ProductServiceImpl(ProductRepository productRepository, AttributValueRepository attributValueRepository, WorkflowService workflowService, CategoryService categoryService, FileUploadService fileUploadService, ProductSearchRepository productSearchRepository, UserCustomerUtil userCustomerUtil, AttributRepository attributRepository) {
        this.productRepository = productRepository;
        this.attributValueRepository = attributValueRepository;
        this.workflowService = workflowService;
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
        this.productSearchRepository = productSearchRepository;
        this.userCustomerUtil = userCustomerUtil;
        this.attributRepository = attributRepository;
    }

    @Override
    public Page<Product> getSearchProductPage(String query, Pageable pageable) {
        if (query.contains("=")) {
            return getComplexSearchProductPage(query, pageable);
        } else {
            return getSimpleSearchProductPage(query, pageable);
        }
    }

    private Page<Product> getSimpleSearchProductPage(String query, Pageable pageable) {
        BoolQueryBuilder boolQuery = getSimpleQuery(query);
        return productSearchRepository.search(boolQuery, pageable).map(ProductsWithAttributes::getProduct);
    }

    private BoolQueryBuilder getSimpleQuery(String query) {
        Customer c = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        QueryStringQueryBuilder nameQuery = queryStringQuery("*" + query + "*").field("product.nom")
            .defaultOperator(Operator.AND)
            .fuzziness(NAME_FUZINESS_LEVEL).boost(NAME_BOOST_VALUE);
        QueryStringQueryBuilder idFQuery = queryStringQuery("*" + query + "*").field("product.idF")
            .defaultOperator(Operator.AND)
            .fuzziness(IDF_FUZINESS_LEVEL).boost(IDF_BOOST_VALUE);
        QueryStringQueryBuilder descriptionQuery = queryStringQuery("*" + query + "*").field("product.description")
            .defaultOperator(Operator.AND)
            .fuzziness(DESCRIPTION_FUZINESS_LEVEL).boost(DESCRIPTION_BOOST_VALUE);
        QueryStringQueryBuilder familyQuery = queryStringQuery("*" + query + "*").field("product.family.nom")
            .defaultOperator(Operator.AND)
            .fuzziness(DESCRIPTION_FUZINESS_LEVEL).boost(DESCRIPTION_BOOST_VALUE);
        QueryStringQueryBuilder categoryQuery = queryStringQuery("*" + query + "*").field("product.categories.nom")
            .defaultOperator(Operator.AND)
            .fuzziness(DESCRIPTION_FUZINESS_LEVEL).boost(DESCRIPTION_BOOST_VALUE);
        return boolQuery()
            .minimumShouldMatch(1)
            .should(familyQuery)
            .should(nameQuery)
            .should(descriptionQuery)
            .should(idFQuery)
            .should(categoryQuery)
            .must(termQuery("product.customer.id", c.getId()));
    }

    private Map<String, String> getMapOfProductProperties() {
        Customer c = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        HashMap<String, String> map = new HashMap<>();

        attributRepository.findByIdFIgnoreCaseAndCustomerId(Attribut.idFAttributIdF, c.getId()).ifPresent(attribut -> map.put(attribut.getNom().toLowerCase(), PROPERTY_IDF));
        attributRepository.findByIdFIgnoreCaseAndCustomerId(Attribut.idFAttributNom, c.getId()).ifPresent(attribut -> map.put(attribut.getNom().toLowerCase(), PROPERTY_NAME));
        attributRepository.findByIdFIgnoreCaseAndCustomerId(Attribut.idFAttributDescription, c.getId()).ifPresent(attribut -> map.put(attribut.getNom().toLowerCase(), PROPERTY_DESCRIPTION));
        attributRepository.findByIdFIgnoreCaseAndCustomerId(Attribut.idFAttributFamille, c.getId()).ifPresent(attribut -> map.put(attribut.getNom().toLowerCase(), PROPERTY_FAMILY));
        attributRepository.findByIdFIgnoreCaseAndCustomerId(Attribut.idFAttributCategorie, c.getId()).ifPresent(attribut -> map.put(attribut.getNom().toLowerCase(), PROPERTY_CATEGORY));

        return Collections.unmodifiableMap(map);
    }

    private Page<Product> getComplexSearchProductPage(String query, Pageable pageable) {
        Customer c = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        List<String> listOfQuery = Arrays.asList(query.split(SEARCH_DELIMITOR));
        Map<String, String> mapOfAttributQuery = new HashMap<>();
        List<String> listOfSimpleQuery = new ArrayList<>();
        for (String str: listOfQuery) {
            if (str.contains("=")) {
                String[] keyValue = str.split("=", 2);
                List<Attribut> listOfAttribut = attributRepository.findByNomIgnoreCaseAndCustomerId(keyValue[0].trim(), c.getId());
                if (listOfAttribut.size() == 0) {
                    throw new AttributeNotFoundException(keyValue[0]);
                }
                mapOfAttributQuery.compute(keyValue[0], (k, v) -> keyValue[1]);
            } else {
                listOfSimpleQuery.add(str);
            }
        }
        if (listOfSimpleQuery.size() > 1) {
            throw new WrongQueryException("There can be only one query without key");
        }

        BoolQueryBuilder boolQuery = boolQuery().must(termQuery("product.customer.id", c.getId()));
        for (Map.Entry<String, String> entry : mapOfAttributQuery.entrySet()){
            boolQuery.must(getQueryFromAttributeNameAndValue(entry.getKey(), entry.getValue()));
        }

        if (listOfSimpleQuery.size() == 1) {
            boolQuery.must(getSimpleQuery(listOfSimpleQuery.get(0)));
        }

        return productSearchRepository.search(boolQuery, pageable).map(ProductsWithAttributes::getProduct);
    }

    private QueryBuilder getQueryFromAttributeNameAndValue(String key, String value) {
        Map<String, String> mapOfProductProperties = getMapOfProductProperties();
        String loweredKey = key.toLowerCase();
        if (mapOfProductProperties.containsKey(loweredKey)) {
            String property = mapOfProductProperties.get(loweredKey);
            QueryStringQueryBuilder propertyQuery = queryStringQuery("*" + value + "*").field("product." + property).defaultOperator(Operator.AND);
            return propertyQuery;
        } else {
            QueryStringQueryBuilder valueQuery = queryStringQuery("*" + value + "*").field("attributValues.value").fuzziness(Fuzziness.AUTO).defaultOperator(Operator.AND);
            QueryStringQueryBuilder attributQuery = queryStringQuery(key).field("attributValues.attribut.nom").fuzziness(Fuzziness.ZERO);

            NestedQueryBuilder nestedQueryBuilder = nestedQuery("attributValues", boolQuery()
                .must(valueQuery)
                .must(attributQuery), ScoreMode.Avg);
            return nestedQueryBuilder;
        }
    }

    @Override
    public Product createProduct(
        Product product,
        Set<AttributValue> attributValues,
        Customer customer,
        Family family,
        Set<Workflow> workflowsToLink,
        Set<Category> categoriesToLink,
        Map<String, MultipartFile> multipartFiles) throws IOException {

        log.info("Create a product " + product);
        if (customer == null) {
            throw new CustomerRequiredActionException();
        }
        //Check
        Product productExample = new Product();
        productExample.setIdF(product.getIdF());
        productExample.setCustomer(customer);
        if (productRepository.findOne(Example.of(productExample)).isPresent()) {
            throw new FunctionalIDAlreadyUsedException();
        }
        product.setFamily(family);
        product.setCustomer(customer);
        // Add the product
        Product productSaved = productRepository.save(product);
        // Add the attributs values
        attributValues.forEach(attributValue -> attributValue.setProduct(productSaved));
        insertAttributValue(attributValues, multipartFiles, true);
        // Add the product to the first step of the workflow
        workflowsToLink.forEach(workflow -> workflowService.linkProductToWorkflow(workflow, productSaved));
        // Add the product to the categories
        linkCategoriesToProduct(categoriesToLink, customer, productSaved);

        // Adding the product in ES
        ProductsWithAttributes productsWithAttributes = new ProductsWithAttributes()
            .setProduct(productSaved)
            .setAttributValues(attributValues);
        productsWithAttributes.initId();
        productSearchRepository.save(productsWithAttributes);
        return productSaved;
    }

    @Override
    public Product updateProduct(
        Product product,
        Set<AttributValue> attributValues,
        Set<Category> categoriesToLink,
        Map<String, MultipartFile> multipartFiles) throws IOException {

        log.info("Update a product " + product);
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        Optional<Product> retrievedProduct = productRepository.findOneWithEagerRelationships(product.getId(), customer.getId());
        if(!retrievedProduct.isPresent()) {
            throw new IllegalStateException("The product can't be updated because it doesn't exist");
        } else {
            Product p = retrievedProduct.get();
            if(!p.getIdF().equals(product.getIdF())) {
                throw new IllegalStateException("The functional id cannot be changed");
            }
            product.setCustomer(p.getCustomer());
            product.setFamily(p.getFamily());
        }
        Product productExample = new Product();
        productExample.setIdF(product.getIdF());
        productExample.setCustomer(product.getCustomer());
        Optional<Product> productWithIdF = productRepository.findOne(Example.of(productExample));
        if (productWithIdF.isPresent() && !productWithIdF.get().getId().equals(product.getId())) {
            throw new FunctionalIDAlreadyUsedException();
        }
        // Add the product
        product.setCustomer(customer);
        Product productSaved = productRepository.save(product);
        // Add the attributs values
        attributValues.forEach(attributValue -> attributValue.setProduct(productSaved));
        insertAttributValue(attributValues, multipartFiles, false);
        deleteAttributValue(attributValues);
        // Add the product to the categories
        linkCategoriesToProduct(categoriesToLink, customer, productSaved);

        ProductsWithAttributes productsWithAttributes = new ProductsWithAttributes()
            .setProduct(productSaved)
            .setAttributValues(attributValues);
        productsWithAttributes.initId();
        productSearchRepository.save(productsWithAttributes);

        return productSaved;
    }

    private void deleteAttributValue(Set<AttributValue> attributValues) {

        List<Long> ids = attributValues.stream()
            .filter(attributValue -> attributValue.getId() != null && (attributValue.getValue().isEmpty() || attributValue.getValue() == null))
            .map(AttributValue::getId).collect(Collectors.toList());
        attributValueRepository.deleteByIdIn(ids);

    }

    private void linkCategoriesToProduct(Set<Category> categories, Customer customer, Product productSaved) {
        if(categories.isEmpty()) {
            Optional<Category> categoryRoot = categoryService.getCategoryByIdAndCustomer(customer, customer.getCategoryRoot());
            categoryRoot.ifPresent(category -> categoryService.addProduct(category, productSaved));
        } else {
            List<Category> retrievedCategories = categories.stream()
                .map(category -> categoryService.getCategoryByIdAndCustomer(customer, category.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
            retrievedCategories.forEach(category -> categoryService.addProduct(category, productSaved));
        }
    }


    @Override
    public List<AttributValue> findAttributValuesByProductId(Long productId, Long customerId) {
        List<AttributValue> attributValues = attributValueRepository.findAllByProduct_IdOrderByAttributIdF(productId);
        return attributValues.stream()
            .filter(attributValue -> {
                Product product = attributValue.getProduct();
                Customer customer = product.getCustomer();
                return customer.getId().equals(customerId);
            })
            .collect(Collectors.toList());
    }

    private void insertAttributValue(Set<AttributValue> attributValues, Map<String, MultipartFile> multipartFiles, boolean isNew) throws IOException {
        validate(attributValues, multipartFiles, isNew);
        for (AttributValue attributValue : attributValues) {
            AttributType type = attributValue.getAttribut().getType();
            String value = attributValue.getValue();
            if(value != null && !value.isEmpty()) {
                switch (type) {
                    case MULTIPLE_VALUE:
                    case TEXT:
                    case NUMBER:
                        attributValueRepository.save(attributValue);
                        break;
                    case RESSOURCE:
                        // insert only if the file has real name
                        if (!StringUtils.isBlank(value)) {
                            MultipartFile multipartFile = multipartFiles.get(attributValue.getValue());
                            if(multipartFile == null && isNew) {
                                throw new MissingFileException();
                            } else if(multipartFile != null) {
                                String newFileName = (generateDate() + "-" + multipartFile.getOriginalFilename())
                                    .replaceAll("\\s+", "");
                                fileUploadService.transferFile(multipartFile, newFileName);
                                attributValue.setValue(newFileName);
                                attributValueRepository.save(attributValue);
                            }
                        }
                        break;
                /*case VALUES_LIST:
                    throw new NotImplementedException("The values list are not implemented yet");*/
                }
            }
        }
    }

    private String generateDate() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return formater.format(new Date());
    }

    public boolean validateTypeFile(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        return
            contentType.equals("application/pdf") ||
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("text/plain") ||
            contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public void validate(Set<AttributValue> attributValues, Map<String, MultipartFile> multipartFiles, boolean isNew) throws NumberFormatException {
        for (AttributValue attributValue : attributValues) {
            String value = attributValue.getValue();
            if (value != null) {
                switch (attributValue.getAttribut().getType()) {
                    case TEXT:
                        break;
                    case RESSOURCE:
                        if (!StringUtil.isNullOrEmpty(value)) {
                            MultipartFile multipartFile = multipartFiles.get(value);
                            if(multipartFile == null && isNew) {
                                throw new MissingFileException();
                            }
                            if (multipartFile!=null && !validateTypeFile(multipartFile)) {
                                throw new UnsupportedFileException();
                            }
                        }
                        break;
                    /*case VALUES_LIST:
                        throw new NotImplementedException("The values list are not implemented yet");*/
                    case MULTIPLE_VALUE:
                        break;
                    case NUMBER:
                        if(!value.isEmpty()) {
                            double v = Double.parseDouble(value);
                        }
                        break;
                }
            }
        }
    }
}
