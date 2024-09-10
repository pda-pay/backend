//package org.ofz.config;
//
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = "org.ofz.repository.asset",
//        entityManagerFactoryRef = "assetEntityManagerFactory",
//        transactionManagerRef = "assetTransactionManager"
//)
//public class AssetDataSourceConfig {
//
//    @Primary
//    @Bean(name = "assetDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.asset")
//    public DataSource assetDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Primary
//    @Bean(name = "assetEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean assetEntityManagerFactory(
//            EntityManagerFactoryBuilder builder, @Qualifier("assetDataSource") DataSource dataSource) {
//        return builder
//                .dataSource(dataSource)
//                .packages("com.example.entity.asset")
//                .persistenceUnit("asset")
//                .build();
//    }
//
//    @Primary
//    @Bean(name = "assetTransactionManager")
//    public PlatformTransactionManager assetTransactionManager(
//            @Qualifier("assetEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//}
