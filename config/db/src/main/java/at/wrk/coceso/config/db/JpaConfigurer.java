package at.wrk.coceso.config.db;

import at.wrk.coceso.config.DbConfig;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("at.wrk.coceso")
class JpaConfigurer {

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("at.wrk.coceso");

    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    em.setPersistenceUnitName("at.wrk.coceso");
    em.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
    em.setJpaProperties(additionalProperties());

    return em;
  }

  @Bean(destroyMethod = "close")
  public DataSource dataSource(DbConfig config) {
    DataSource dataSource = new DataSource();
    dataSource.setDriverClassName(config.getDriver());
    dataSource.setUrl(config.getUrl());
    dataSource.setUsername(config.getUsername());
    dataSource.setPassword(config.getPassword());
    dataSource.setMaxActive(80);
    dataSource.setMaxIdle(80);
    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);

    return transactionManager;
  }

  @Bean
  public PersistenceAnnotationBeanPostProcessor annotationBean() {
    return new PersistenceAnnotationBeanPostProcessor();
  }

  private Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", JsonPostgreSQLDialect.class.getName());
    properties.setProperty("hibernate.hbm2ddl.auto", "validate");
    properties.setProperty("hibernate.connection.charSet", "UTF-8");
    properties.setProperty("hibernate.show_sql", "false");
    properties.setProperty("hibernate.formate_sql", "false");
    properties.setProperty("hibernate.archive.autodetection", "class, hbm");
    return properties;
  }

}
