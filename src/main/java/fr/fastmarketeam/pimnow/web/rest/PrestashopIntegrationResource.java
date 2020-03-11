package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.config.Constants;
import fr.fastmarketeam.pimnow.domain.*;
import fr.fastmarketeam.pimnow.repository.*;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.integrations.PrestaShop;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing integration with Prestashop.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
public class PrestashopIntegrationResource {
    private final Logger log = LoggerFactory.getLogger(PrestashopProductResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${sftp.host-resource-directory}")
    private String hostResourceDirectory;

    private final ConfigurationCustomerRepository configurationCustomerRepository;

    private final UserCustomerUtil userCustomerUtil;

    private final PrestashopProductRepository prestashopProductRepository;

    private final ProductRepository productRepository;

    private final AttributRepository attributRepository;

    private final AttributValueRepository attributValueRepository;

    public PrestashopIntegrationResource(ConfigurationCustomerRepository configurationCustomerRepository, UserCustomerUtil userCustomerUtil, PrestashopProductRepository prestashopProductRepository, ProductRepository productRepository, AttributRepository attributRepository, AttributValueRepository attributValueRepository) {
        this.configurationCustomerRepository = configurationCustomerRepository;
        this.userCustomerUtil = userCustomerUtil;
        this.prestashopProductRepository = prestashopProductRepository;
        this.productRepository = productRepository;
        this.attributRepository = attributRepository;
        this.attributValueRepository = attributValueRepository;
    }

    @PostMapping("/integrate/products")
    @Transactional
    public ResponseEntity<Void> integratePrestashopProduct(@RequestBody Product[] productsSelected) throws IOException, ParserConfigurationException {
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        Optional<ConfigurationCustomer> configurationCustomerOptional = configurationCustomerRepository.findById(customer.getId());
        if(!configurationCustomerOptional.isPresent()){
            throw new BadRequestAlertException("No configuration for integration", "ConfigurationCustomer", "noconfiguration");
        }
        if(StringUtils.isBlank(configurationCustomerOptional.get().getApiKeyPrestashop()) || StringUtils.isBlank(configurationCustomerOptional.get().getUrlPrestashop())){
            throw new BadRequestAlertException("No configuration for integration", "ConfigurationCustomer", "noconfiguration");
        }
        ConfigurationCustomer configurationCustomer = configurationCustomerOptional.get();
        List<PrestashopProduct> prestashopProductsToSave = new ArrayList<>();
        PrestaShop prestaShop = new PrestaShop(configurationCustomer.getUrlPrestashop(), configurationCustomer.getApiKeyPrestashop(), true);
        Optional<Attribut> attributPrice = attributRepository.findByIdFIgnoreCaseAndCustomerId("ATTR_PRIX", customer.getId());
        Optional<Attribut> attributStock = attributRepository.findByIdFIgnoreCaseAndCustomerId("ATTR_STOCK", customer.getId());
        if(!attributPrice.isPresent() || !attributStock.isPresent()){
            throw new BadRequestAlertException("Attribut stock or price not existing", "ConfigurationCustomer", "nopriceorstock");
        }
        for(Product product : productsSelected){
            Optional<AttributValue> attributValuePrice = attributValueRepository.findByAttributIdAndProductId(attributPrice.get().getId(), product.getId());
            Optional<AttributValue> attributValueStock = attributValueRepository.findByAttributIdAndProductId(attributStock.get().getId(), product.getId());
            Optional<PrestashopProduct> prestashopProduct = prestashopProductRepository.findById(product.getId());
            String priceToSetOnPrestashop = "0";
            String stockToSetOnPrestashop = "0";
            if(attributValuePrice.isPresent()){
                priceToSetOnPrestashop = attributValuePrice.get().getValue();
            }
            if(attributValueStock.isPresent()){
                stockToSetOnPrestashop = attributValueStock.get().getValue().split("\\.")[0];
            }
            List<String> ressources = new ArrayList<>();
            String descriptionToSave = createDescription(product, prestaShop, ressources);
            if(prestashopProduct.isPresent()){
                boolean result = prestaShop.editProduct(prestashopProduct.get().getPrestashopProductId().intValue(), product.getNom(), descriptionToSave, priceToSetOnPrestashop);
                if(!result){
                    throw new BadRequestAlertException("Could not update on PrestaShop the product with functional id : " + product.getIdF(), "UpdatePrestashopProduct", "failedPrestashopUpdate");
                }
                ressources.forEach(r -> {
                    try {
                        prestaShop.addImage(hostResourceDirectory + r, prestashopProduct.get().getId().intValue());
                    } catch (IOException e) {
                        throw new BadRequestAlertException("Could not update image on product with idF : " + product.getIdF(), "UpdatePrestashopProduct", "failedPrestashopUpdate");
                    }
                });
            } else {
                int prestashopProductId = prestaShop.createProduct(product.getNom(), descriptionToSave, priceToSetOnPrestashop, stockToSetOnPrestashop);
                Optional<Product> productOnBase = productRepository.findById(product.getId());
                if(productOnBase.isPresent()){
                    PrestashopProduct prestashopProductToSave = new PrestashopProduct();
                    prestashopProductToSave.setProductPim(productOnBase.get());
                    prestashopProductToSave.setPrestashopProductId((long) prestashopProductId);
                    prestashopProductsToSave.add(prestashopProductToSave);
                }
                ressources.forEach(r -> {
                    try {
                        prestaShop.addImage(hostResourceDirectory + r, prestashopProductId);
                    } catch (IOException e) {
                        throw new BadRequestAlertException("Could not update image on product with idF : " + product.getIdF(), "UpdatePrestashopProduct", "failedPrestashopUpdate");
                    }
                });
            }
        }
        prestashopProductRepository.saveAll(prestashopProductsToSave);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "pimnowApp.prestashopProduct.integrated", "")).build();
    }

    private String createDescription(Product product, PrestaShop prestaShop, List<String> ressources){
        StringBuilder descriptionToSave = new StringBuilder();
        descriptionToSave.append(product.getDescription()).append("<br /><br />");
        attributValueRepository.findAllByProduct_IdOrderByAttributIdF(product.getId()).forEach(attVal -> {
            if(!attVal.getAttribut().getIdF().equals(Attribut.idFAttributIdF) && !attVal.getAttribut().getIdF().equals(Attribut.idFAttributNom) && !attVal.getAttribut().getIdF().equals(Attribut.idFAttributDescription) && !attVal.getAttribut().getIdF().equals(Attribut.idFAttributCategorie) && !attVal.getAttribut().getIdF().equals(Attribut.idFAttributFamille) && !attVal.getAttribut().getIdF().equals(Attribut.idFAttributPrix) && !attVal.getAttribut().getIdF().equals(Attribut.idFAttributStock)){
                switch (attVal.getAttribut().getType()){
                    case TEXT:
                    case NUMBER:
                        descriptionToSave.append("<b>").append(attVal.getAttribut().getNom()).append(" : </b>").append(attVal.getValue()).append("<br />");
                        break;
                    case MULTIPLE_VALUE:
                        String[] values = attVal.getValue().split(Constants.STANDARD_SEPARATOR);
                        descriptionToSave.append("<b>").append(attVal.getAttribut().getNom()).append(" : </b>");
                        Arrays.stream(values).forEach(v -> {
                            descriptionToSave.append(v).append(", ");
                        });
                        descriptionToSave.setLength(descriptionToSave.length() - 2);
                        descriptionToSave.append("<br />");
                        break;
                    case RESSOURCE:
                        String[] verifyExtension = attVal.getValue().split("\\.");
                        if(verifyExtension[verifyExtension.length - 1].equals("jpeg") || verifyExtension[verifyExtension.length - 1].equals("jpg") || verifyExtension[verifyExtension.length - 1].equals("png")){
                            ressources.add(attVal.getValue());
                        }
                    default:
                        break;
                }
            }
        });
        return descriptionToSave.toString();
    }

    @PostMapping("/integrate/products/delete")
    @Transactional
    public ResponseEntity<Void> deletePrestashopProducts(@RequestBody Product[] productsSelected){
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        Optional<ConfigurationCustomer> configurationCustomerOptional = configurationCustomerRepository.findById(customer.getId());
        if(!configurationCustomerOptional.isPresent()){
            throw new BadRequestAlertException("No configuration for integration", "ConfigurationCustomer", "noconfiguration");
        }
        if(StringUtils.isBlank(configurationCustomerOptional.get().getApiKeyPrestashop()) || StringUtils.isBlank(configurationCustomerOptional.get().getUrlPrestashop())){
            throw new BadRequestAlertException("No configuration for integration", "ConfigurationCustomer", "noconfiguration");
        }
        ConfigurationCustomer configurationCustomer = configurationCustomerOptional.get();
        PrestaShop prestaShop = new PrestaShop(configurationCustomer.getUrlPrestashop(), configurationCustomer.getApiKeyPrestashop(), false);
        for(Product product : productsSelected){
            Optional<PrestashopProduct> prestashopProduct = prestashopProductRepository.findById(product.getId());
            if(prestashopProduct.isPresent()){
                prestaShop.deleteProduct(prestashopProduct.get().getPrestashopProductId().intValue());
                prestashopProductRepository.deleteById(prestashopProduct.get().getId());
            }
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "pimnowApp.prestashopProduct.integratedRemoved", "")).build();
    }
}
