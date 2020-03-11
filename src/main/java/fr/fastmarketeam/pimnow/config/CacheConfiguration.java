package fr.fastmarketeam.pimnow.config;

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, fr.fastmarketeam.pimnow.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, fr.fastmarketeam.pimnow.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, fr.fastmarketeam.pimnow.domain.User.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Authority.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.User.class.getName() + ".authorities");
            createCache(cm, fr.fastmarketeam.pimnow.domain.Family.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Family.class.getName() + ".successors");
            createCache(cm, fr.fastmarketeam.pimnow.domain.Family.class.getName() + ".attributes");
            createCache(cm, fr.fastmarketeam.pimnow.domain.Attribut.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Attribut.class.getName() + ".families");
            createCache(cm, fr.fastmarketeam.pimnow.domain.Category.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Product.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.AttributValue.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.UserExtra.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Customer.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Workflow.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Workflow.class.getName() + ".steps");
            createCache(cm, fr.fastmarketeam.pimnow.domain.WorkflowStep.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Mapping.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Association.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.ConfigurationCustomer.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.ValuesList.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.ValuesList.class.getName() + ".items");
            createCache(cm, fr.fastmarketeam.pimnow.domain.ValuesListItem.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.WorkflowState.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.WorkflowState.class.getName() + ".steps");
            createCache(cm, fr.fastmarketeam.pimnow.domain.AttributValuesList.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.PrestashopProduct.class.getName());
            createCache(cm, fr.fastmarketeam.pimnow.domain.Mapping.class.getName() + ".associations");
            createCache(cm, fr.fastmarketeam.pimnow.domain.Category.class.getName() + ".products");
            createCache(cm, fr.fastmarketeam.pimnow.domain.Product.class.getName() + ".categories");
            createCache(cm, fr.fastmarketeam.pimnow.domain.Category.class.getName() + ".successors");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
