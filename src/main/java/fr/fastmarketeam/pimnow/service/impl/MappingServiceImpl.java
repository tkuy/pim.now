package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.Association;
import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Mapping;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
import fr.fastmarketeam.pimnow.repository.*;
import fr.fastmarketeam.pimnow.service.MappingService;
import fr.fastmarketeam.pimnow.service.errors.MappingNotFoundException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class MappingServiceImpl implements MappingService {

    private final MappingRepository mappingRepository;
    private final AttributRepository attributRepository;

    public MappingServiceImpl(MappingRepository mappingRepository, AttributRepository attributRepository) {
        this.mappingRepository = mappingRepository;
        this.attributRepository = attributRepository;
    }

    @Override
    public Mapping getMapping(Long idMapping, Long customerId) {
        Optional<Mapping> mappingOptional = mappingRepository.findByIdAndCustomerId(idMapping, customerId);
        if(!mappingOptional.isPresent()){
            throw new MappingNotFoundException();
        }
        Mapping mapping = mappingOptional.get();
        checkIfAttributeExistAndIsNotResource(mapping, mapping.getCustomer().getId());

        return mapping;
    }

    /**
     * Check if every attributes of the mapping exist and is not of the type Resource
     * @param mapping
     */
    private void checkIfAttributeExistAndIsNotResource(Mapping mapping, Long customerId) {
        List<Optional<Attribut>> lst = mapping.getAssociations().stream().map(Association::getIdFAttribut).map(attr -> attributRepository.findByIdFIgnoreCaseAndCustomerId(attr, customerId)).collect(Collectors.toList());
        for (Optional<Attribut> optAttr: lst) {
            if (!optAttr.isPresent()) {
                continue;
            }
            Attribut attr = optAttr.get();
            if (attr.getType().equals(AttributType.RESSOURCE)) {
                throw new IllegalStateException("This attribute of type Resource cannot be present in a mapping");
            }
        }
    }

}
