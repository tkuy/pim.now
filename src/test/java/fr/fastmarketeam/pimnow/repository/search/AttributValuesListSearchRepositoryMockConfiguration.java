package fr.fastmarketeam.pimnow.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link AttributValuesListSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class AttributValuesListSearchRepositoryMockConfiguration {

    @MockBean
    private AttributValuesListSearchRepository mockAttributValuesListSearchRepository;

}
