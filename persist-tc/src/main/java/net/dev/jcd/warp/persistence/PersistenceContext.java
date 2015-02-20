package net.dev.jcd.warp.persistence;

import java.sql.Driver;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import net.dev.jcd.warp.log.Loggable;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource("classpath:application.properties")
class PersistenceContext {
	@Loggable
	private Logger logger;
	
	@Autowired
    Environment env;
	
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "net.dev.jcd.warp.data" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

//	@Bean(destroyMethod = "close")
	@Bean	
	DataSource dataSource() {
//		logger.debug("create data source");
		try {
			SimpleDriverDataSource dataSourceConfig = new SimpleDriverDataSource();

			String driverClass = env.getRequiredProperty("db.driver");
			Class<?> clz = Class.forName(driverClass);
			Driver driver = (Driver) clz.newInstance();

			dataSourceConfig.setDriver(driver);
			dataSourceConfig.setDriverClass((Class<? extends Driver>) clz);
			dataSourceConfig.setUrl(env.getRequiredProperty("db.url"));
			dataSourceConfig.setUsername(env.getRequiredProperty("db.username"));
			dataSourceConfig.setPassword(env.getRequiredProperty("db.password"));

			return dataSourceConfig;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException x) {
			logger.error("Failed to create data source ", x);
		}
		return null;
	}

	   @Bean
	   public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
	      JpaTransactionManager transactionManager = new JpaTransactionManager();
	      transactionManager.setEntityManagerFactory(emf);
	 
	      return transactionManager;
	   }
	 
	   @Bean
	   public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
	      return new PersistenceExceptionTranslationPostProcessor();
	   }
	   
	   Properties additionalProperties() {
		      Properties properties = new Properties();
		      properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		      properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		      return properties;
		   }
	 
}
