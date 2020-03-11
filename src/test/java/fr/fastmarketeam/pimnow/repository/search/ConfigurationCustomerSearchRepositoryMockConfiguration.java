package fr.fastmarketeam.pimnow.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ConfigurationCustomerSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ConfigurationCustomerSearchRepositoryMockConfiguration {

    @MockBean
    private ConfigurationCustomerSearchRepository mockConfigurationCustomerSearchRepository;

}
