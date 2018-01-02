package de.DatabaseConfigurations;

import de.domain.Spot;
import de.domainAux.Route;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory2", basePackages = { "de.repositories.postDBRepositories" }
)
public class PostDbConfiguration {

    @Bean(name = "dataSource2")
    @ConfigurationProperties(prefix = "spring.datasource2")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "entityManagerFactory2")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dataSource2") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        /*Properties properties1 = new Properties();
        properties1.put("hibernate.hbm2ddl.auto", "create-drop");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        System.out.println(Spot.class.getPackage().getName());
        factory.setPackagesToScan(Spot.class.getPackage().getName());
        factory.setDataSource(dataSource());
        factory.setJpaProperties(properties1);
        factory.setPersistenceUnitName("postdb");
        return factory;*/


        /*LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(
                new String[] { Route.class.getPackage().getName() });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        em.setJpaPropertyMap(properties);
        em.setPersistenceUnitName("postdb");

        return em;*/




        return builder
                .dataSource(dataSource)
                .packages(Route.class.getPackage().getName())
                .persistenceUnit("postdb")
                .properties(properties)
                .build();
    }

    @Bean(name = "transactionManager2")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory2") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
