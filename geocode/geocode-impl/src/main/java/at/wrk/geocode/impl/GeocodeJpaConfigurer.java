package at.wrk.geocode.impl;

import at.wrk.geocode.GeocodeConfig;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(value = "at.wrk.geocode", entityManagerFactoryRef = "geocodeEntityManagerFactory", transactionManagerRef = "geocodeTransactionManager")
class GeocodeJpaConfigurer {

  @Bean(name = "geocodeEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("geocodeDataSource") DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("at.wrk.geocode");

    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    em.setPersistenceUnitName("at.wrk.geocode");
    em.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
    em.setJpaProperties(additionalProperties());

    return em;
  }

  @Bean(name = "geocodeDataSource", destroyMethod = "close")
  public DataSource dataSource(GeocodeConfig config) {
    DataSource dataSource = new DataSource();
    dataSource.setDriverClassName(config.getDriver());
    dataSource.setUrl(config.getUrl());
    dataSource.setUsername(config.getUsername());
    dataSource.setPassword(config.getPassword());
    dataSource.setMaxActive(80);
    dataSource.setMaxIdle(80);
    return dataSource;
  }

  @Bean(name = "geocodeTransactionManager")
  public PlatformTransactionManager geocodeTransactionManager(@Qualifier("geocodeEntityManagerFactory") EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);

    return transactionManager;
  }

  private Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", PostgreSQL9Dialect.class.getName());
    properties.setProperty("hibernate.hbm2ddl.auto", "validate");
    properties.setProperty("hibernate.connection.charSet", "UTF-8");
    properties.setProperty("hibernate.show_sql", "false");
    properties.setProperty("hibernate.formate_sql", "false");
    properties.setProperty("hibernate.archive.autodetection", "class, hbm");
    return properties;
  }

}
