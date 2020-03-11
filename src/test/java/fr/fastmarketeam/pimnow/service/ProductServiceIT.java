package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.*;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.repository.search.ProductSearchRepository;
import fr.fastmarketeam.pimnow.service.errors.FunctionalIDAlreadyUsedException;
import fr.fastmarketeam.pimnow.service.impl.ProductServiceImpl;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link UserService}.
 */
@SpringBootTest(classes = PimnowApp.class)
@Transactional
public class ProductServiceIT {

    private static final String DEFAULT_IDF = "IDF";

    private static final String DEFAULT_NOM = "Mobile Phone";

    private static final String DEFAULT_DESCRIPTION = "One Plus 6T";

    private static final String DEFAULT_FAMILY_NAME = "Mobile Phone";

    private static final String DEFAULT_NAME_CUSTOMER = "ADIDAS";

    private static final Integer DEFAULT_CATEGORY_ROOT_ID_CUSTOMER = 0;

    private static final Integer DEFAULT_FAMILY_ROOT_ID_CUSTOMER = 0;

    @Mock
    private UserCustomerUtil userCustomerUtil;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    private Customer customer;

    private Category categoryRoot;

    private Family familyRoot;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    public void init() {
        product = new Product();
        product.setIdF(DEFAULT_IDF);
        product.setNom(DEFAULT_NOM);
        product.setDescription(DEFAULT_DESCRIPTION);
        customer = new Customer();
        customer.setFamilyRoot(DEFAULT_FAMILY_ROOT_ID_CUSTOMER);
        customer.setCategoryRoot(DEFAULT_CATEGORY_ROOT_ID_CUSTOMER);
        customer.setName(DEFAULT_NAME_CUSTOMER);
        product.setCustomer(customer);
        categoryRoot = new Category();
        categoryRoot.setIdF("CATEGORY_ROOT");
        categoryRoot.setNom("CATEGORY ROOT");
        familyRoot = new Family().idF("FAMILY_ROOT").nom("FAMILY ROOT");
    }

    @Test
    @Transactional
    public void assertThatTheFilesHaveTheRightFormat() {
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.jpg", "a.jpg", "image/jpeg", new byte[0]))).isTrue();
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.jpeg", "a.jpeg", "image/jpeg", new byte[0]))).isTrue();
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.png", "a.png", "image/png", new byte[0]))).isTrue();
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.pdf", "a.pdf", "application/pdf", new byte[0]))).isTrue();
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.txt", "a.txt", "text/plain", new byte[0]))).isTrue();
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.xlsx", "a.xslx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]))).isTrue();
    }

    @Test
    @Transactional
    public void assertThatTheFilesHaveAnUnsupportedException() {
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.xls", "a.xls", "application/vnd.ms-excel", new byte[0]))).isFalse();
        assertThat(productService.validateTypeFile(new MockMultipartFile("empty", "empty", "multipart/form-data", new byte[0]))).isFalse();
        assertThat(productService.validateTypeFile(new MockMultipartFile("emptyExtension.", "emptyExtension.","multipart/form-data", new byte[0]))).isFalse();
        assertThat(productService.validateTypeFile(new MockMultipartFile("a", "a", "multipart/form-data",new byte[0]))).isFalse();
        assertThat(productService.validateTypeFile(new MockMultipartFile("a.csv", "a.csv","text/csv", new byte[0]))).isFalse();
        assertThatThrownBy(() -> productService.validateTypeFile(new MockMultipartFile("", new byte[0]))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    public void assertThatNumberHasRightNumberFormat() {
        Attribut attribut = new Attribut();
        attribut.setType(AttributType.NUMBER);
        Map<String, MultipartFile> map = new HashMap<>();
        AttributValue attributValue = new AttributValue();
        attributValue.setAttribut(attribut);
        HashSet<AttributValue> attributValues = new HashSet<>();
        attributValues.add(attributValue);
        attributValue.setValue("0");
        assertThatCode(() -> productService.validate(attributValues, map, true)).doesNotThrowAnyException();
        attributValue.setValue("0.3");
        assertThatCode(() -> productService.validate(attributValues, map, true)).doesNotThrowAnyException();
        attributValue.setValue("-1");
        assertThatCode(() -> productService.validate(attributValues, map, true)).doesNotThrowAnyException();
        attributValue.setValue("-1234.5342");
        assertThatCode(() -> productService.validate(attributValues, map, true)).doesNotThrowAnyException();
        attributValue.setValue("12345.5342");
        assertThatCode(() -> productService.validate(attributValues, map, true)).doesNotThrowAnyException();
        attributValue.setValue("12345.0");
        assertThatCode(() -> productService.validate(attributValues, map, true)).doesNotThrowAnyException();
        attributValue.setValue("12345.");
        assertThatCode(() -> productService.validate(attributValues, map, true)).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    public void assertThatNumberIsNotValidatedWithSpace() {
        Attribut attribut = new Attribut();
        attribut.setType(AttributType.NUMBER);
        Map<String, MultipartFile> map = new HashMap<>();
        AttributValue attributValue = new AttributValue();
        attributValue.setAttribut(attribut);
        HashSet<AttributValue> attributValues = new HashSet<>();
        attributValues.add(attributValue);
        attributValue.setValue("135 123");
        assertThatThrownBy(() -> productService.validate(attributValues, map, true)).isInstanceOf(NumberFormatException.class);
    }

    @Test
    @Transactional
    public void assertThatThrownNotANumberExceptionWhenCommaIsUsed() {
        Attribut attribut = new Attribut();
        attribut.setType(AttributType.NUMBER);
        Map<String, MultipartFile> map = new HashMap<>();
        AttributValue attributValue = new AttributValue();
        attributValue.setAttribut(attribut);
        HashSet<AttributValue> attributValues = new HashSet<>();
        attributValues.add(attributValue);
        attributValue.setValue("0,3");
        assertThatThrownBy(() -> productService.validate(attributValues, map, true)).isInstanceOf(NumberFormatException.class);
    }

    @Test
    @Transactional
    public void assertThatThrownNotANumberException() {
        Attribut attribut = new Attribut();
        attribut.setType(AttributType.NUMBER);
        Map<String, MultipartFile> map = new HashMap<>();
        AttributValue attributValue = new AttributValue();
        attributValue.setAttribut(attribut);
        HashSet<AttributValue> attributValues = new HashSet<>();
        attributValues.add(attributValue);
        attributValue.setValue("A");
        assertThatThrownBy(() -> productService.validate(attributValues, map, true)).isInstanceOf(NumberFormatException.class);
    }

    @Test
    @Transactional
    public void assertThatTheFileIsFound() {
        Attribut attribut = new Attribut();
        attribut.setId(1L);
        attribut.setType(AttributType.RESSOURCE);
        Map<String, MultipartFile> files = new HashMap<>();
        MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", new byte[0]);
        files.put("file.txt", file);
        AttributValue attributValue = new AttributValue();
        attributValue.setAttribut(attribut);
        HashSet<AttributValue> attributValues = new HashSet<>();
        attributValues.add(attributValue);
        attributValue.setValue(file.getName());
        assertThatCode(() -> productService.validate(attributValues, files, true)).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    public void assertThatTheFileIsNotSearchedWhenNoNameIsGiven() {
        Attribut attribut = new Attribut();
        attribut.setId(1L);
        attribut.setType(AttributType.RESSOURCE);
        Map<String, MultipartFile> files = new HashMap<>();
        File file = new File("");
        AttributValue attributValue = new AttributValue();
        attributValue.setAttribut(attribut);
        HashSet<AttributValue> attributValues = new HashSet<>();
        attributValues.add(attributValue);
        attributValue.setValue(file.getName());
        assertThatCode(() -> productService.validate(attributValues, files, true)).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    public void assertCreateSimpleProductWithNoAttributs() throws IOException {
        when(productRepository.findOne(any())).thenReturn(Optional.empty());
        when(productRepository.save(any())).then(invocationOnMock -> {
            product.setId(1L);
            return product;
        });
        when(productSearchRepository.save(any())).thenReturn(null);
        when(userCustomerUtil.findCustomer(any())).thenReturn(customer);
        when(categoryService.getCategoryByIdAndCustomer(any(Customer.class), any(Long.class))).thenReturn(Optional.of(categoryRoot));
        assertThat(productService.createProduct(
            product,
            new HashSet<>(),
            customer,
            new Family(),
            new HashSet<>(),
            new HashSet<>(),
            new HashMap<>()
        )).isEqualTo(product);
    }

    @Test
    @Transactional
    public void assertThatTheProductHasFunctionalIDAlreadyTaken() {
        when(productRepository.findOne(any())).thenReturn(Optional.of(new Product().idF(DEFAULT_IDF)));
        when(productRepository.save(any())).then(invocationOnMock -> {
            product.setId(1L);
            return product;
        });
        assertThatThrownBy(() -> productService.createProduct(
            product,
            new HashSet<>(),
            new Customer(),
            new Family(),
            new HashSet<>(),
            new HashSet<>(),
            new HashMap<>()
        )).isInstanceOf(FunctionalIDAlreadyUsedException.class);
    }

    @Test
    @Transactional
    public void assertThatTheProductIsLinkedToFamily() throws IOException {
        when(productRepository.findOne(any())).thenReturn(Optional.empty());
        when(productRepository.save(any())).then(invocationOnMock -> {
            product.setId(1L);
            return product;
        });
        product.setCustomer(customer);

        when(userCustomerUtil.findCustomer(any())).thenReturn(customer);
        when(categoryService.getCategoryByIdAndCustomer(any(Customer.class), any(Long.class))).thenReturn(Optional.of(categoryRoot));
        Family family = new Family();
        family.setId(2L);
        family.setIdF(DEFAULT_IDF);
        family.setNom(DEFAULT_FAMILY_NAME);
        Family familyInProduct = productService.createProduct(
            product,
            new HashSet<>(),
            customer,
            family,
            new HashSet<>(),
            new HashSet<>(),
            new HashMap<>()
        ).getFamily();
        assertThat(familyInProduct).isEqualTo(family);
    }

    @Test
    @Transactional
    public void assertThatTheProductIsLinkedToCustomer() throws IOException {
        when(productRepository.findOne(any())).thenReturn(Optional.empty());
        when(productRepository.save(any())).then(invocationOnMock -> {
            product.setId(1L);
            return product;
        });
        when(userCustomerUtil.findCustomer(any())).thenReturn(customer);
        when(categoryService.getCategoryByIdAndCustomer(any(Customer.class), any(Long.class))).thenReturn(Optional.of(categoryRoot));
        customer.setId(3L);
        assertThat(productService.createProduct(
            product,
            new HashSet<>(),
            customer,
            new Family(),
            new HashSet<>(),
            new HashSet<>(),
            new HashMap<>()
        ).getCustomer().getId()).isEqualTo(3L);
    }

//    @Test
//    @Transactional
    public void assertThatTheProductIsAddedToTheCategories() throws IOException {
        when(productRepository.findOne(any())).thenReturn(Optional.empty());
        when(productRepository.save(any())).then(invocationOnMock -> {
            product.setId(1L);
            return product;
        });
        HashSet<Category> categories = new HashSet<>();
        Category category = new Category();
        category.setId(10L);
        categories.add(category);
        Category category2 = new Category();
        category.setId(11L);
        categories.add(category);
        categories.add(category2);
        Product product = productService.createProduct(
            this.product,
            new HashSet<>(),
            new Customer(),
            new Family(),
            new HashSet<>(),
            categories,
            new HashMap<>()
        );
        assertThat(product.getCategories().containsAll(categories)).isTrue();
        categories.forEach(c -> assertThat(c.getProducts().contains(product)).isTrue());
    }
}
