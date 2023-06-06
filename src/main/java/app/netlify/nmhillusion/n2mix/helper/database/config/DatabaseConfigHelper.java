package app.netlify.nmhillusion.n2mix.helper.database.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.SharedCacheMode;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

@Configuration
@EnableTransactionManagement
public class DatabaseConfigHelper {
    public static final DatabaseConfigHelper INSTANCE = new DatabaseConfigHelper();
    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 5 + 2;

    @Bean("defaultPlatformTransactionManager")
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("defaultDataSourceTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    public SessionFactory generateSessionFactory(DataSourceProperties dataSourceProperties) throws IOException {
        final LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(generateDataSource(dataSourceProperties));

        final Properties hibernateProperties = new Properties();
        hibernateProperties.putAll(getHibernateProperties(dataSourceProperties));
        sessionFactoryBean.setHibernateProperties(hibernateProperties);

        sessionFactoryBean.afterPropertiesSet();
        return sessionFactoryBean.getObject();
    }

    public DataSource generateDataSource(DataSourceProperties dataSourceProperties) {
        final HikariDataSource dataSource = new HikariDataSource();
        final String dataSourceName = dataSourceProperties.getDataSourceName();

        dataSource.setDriverClassName(dataSourceProperties.getConnection().getDriverClassName());
        dataSource.setJdbcUrl(dataSourceProperties.getConnection().getJdbcUrl());
        dataSource.setUsername(dataSourceProperties.getConnection().getUsername());
        dataSource.setPassword(dataSourceProperties.getConnection().getPassword());
        dataSource.setPoolName(dataSourceName);

        dataSource.setMaximumPoolSize(POOL_SIZE);
        dataSource.setLeakDetectionThreshold(Integer.parseInt(dataSourceProperties.getConnection().getLeakDetectionThreshold()));
        dataSource.setAutoCommit(Boolean.parseBoolean(dataSourceProperties.getConnection().getAutocommit()));
        dataSource.setIdleTimeout(Integer.parseInt(dataSourceProperties.getConnection().getIdleTimeout()));
        dataSource.setMinimumIdle(Integer.parseInt(dataSourceProperties.getConnection().getMinimumIdle()));
        dataSource.setConnectionTimeout(Integer.parseInt(dataSourceProperties.getConnection().getConnectionTimeout()));
        dataSource.setMaxLifetime(Integer.parseInt(dataSourceProperties.getConnection().getMaxLifetime()));

        dataSource.setRegisterMbeans(Boolean.parseBoolean(dataSourceProperties.getRegisterMbeans()));

        return dataSource;
    }

    public LocalContainerEntityManagerFactoryBean generateEntityManagerFactoryBean(String persistenceUnitName, DataSourceProperties dataSourceProperties, Class<?> mainClassToScan) {
        return generateEntityManagerFactoryBean(persistenceUnitName, generateDataSource(dataSourceProperties), dataSourceProperties, mainClassToScan);
    }

    public LocalContainerEntityManagerFactoryBean generateEntityManagerFactoryBean(String persistenceUnitName,
                                                                                   DataSource dataSource,
                                                                                   DataSourceProperties dataSourceProperties,
                                                                                   Class<?> mainClassToScan) {
        final LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setPersistenceProviderClass(org.hibernate.jpa.HibernatePersistenceProvider.class);
        entityManager.setPackagesToScan(mainClassToScan.getPackage().getName()); //The packages to search for Entities, line required to avoid looking into the persistence.xml
        entityManager.setPersistenceUnitName(persistenceUnitName);
        entityManager.setSharedCacheMode(SharedCacheMode.ALL);
        entityManager.setDataSource(dataSource);
        entityManager.setJpaPropertyMap(getHibernateProperties(dataSourceProperties));
        entityManager.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver()); //required unless you know what your doing
        entityManager.afterPropertiesSet();

        return entityManager;
    }

    private Map<String, String> getHibernateProperties(DataSourceProperties dataSourceProperties) {
        final Map<String, String> jpaProperties = new HashMap<>();

        /// Mark: Basic
        jpaProperties.put("hibernate.connection.pool_size", String.valueOf(POOL_SIZE));
        jpaProperties.put("hibernate.connection.autocommit", dataSourceProperties.getConnection().getAutocommit());

        /// Mark: Connection
        jpaProperties.put("hibernate.dialect", dataSourceProperties.getDialectClass());
        jpaProperties.put("hibernate.transaction.coordinator_class", dataSourceProperties.getCoordinatorClass());
        jpaProperties.put("hibernate.transaction.auto_close_session", dataSourceProperties.getAutoCloseSession());
        jpaProperties.put("hibernate.hbm2ddl.auto", dataSourceProperties.getHbm2ddlAuto());
        jpaProperties.put("hibernate.current_session_context_class", dataSourceProperties.getCurrentSessionContextClass());

        /// Mark: Show SQL
        jpaProperties.put("hibernate.format_sql", dataSourceProperties.getShowSql());
        jpaProperties.put("hibernate.show_sql", dataSourceProperties.getShowSql());
        jpaProperties.put("hibernate.use_sql_comments", dataSourceProperties.getShowSql());
        jpaProperties.put("hibernate.generate_statistics", dataSourceProperties.getGenerateStatistics());

        /// Mark: Cache
        jpaProperties.put("hibernate.cache.use_minimal_puts", dataSourceProperties.getCached().getEnabled());
        jpaProperties.put("hibernate.cache.use_query_cache", dataSourceProperties.getCached().getEnabled());
        jpaProperties.put("hibernate.cache.use_second_level_cache", dataSourceProperties.getCached().getEnabled());
        jpaProperties.put("hibernate.cache.provider_class", dataSourceProperties.getCached().getCacheClass());
        jpaProperties.put("hibernate.cache.region.factory_class", dataSourceProperties.getCached().getRegionFactoryClass());
        jpaProperties.put("hibernate.javax.cache.missing_cache_strategy", dataSourceProperties.getCached().getMissingCacheStrategy());

        return jpaProperties;
    }

}
