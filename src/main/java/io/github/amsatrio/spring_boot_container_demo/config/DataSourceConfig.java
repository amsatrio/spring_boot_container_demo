package io.github.amsatrio.spring_boot_container_demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    
    @Primary
    @Bean(name = "db1DataSourceProperties")
    @ConfigurationProperties("spring.datasource.db1")
    public DataSourceProperties db1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "db1DataSource")
    public DataSource db1DataSource() {
        return db1DataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "db1JdbcTemplate")
    public JdbcTemplate db1JdbcTemplate(@Qualifier("db1DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    

    @Bean(name = "db2DataSourceProperties")
    @ConfigurationProperties("spring.datasource.db2")
    public DataSourceProperties db2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "db2DataSource")
    public DataSource db2DataSource() {
        return db2DataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "db2JdbcTemplate")
    public JdbcTemplate db2JdbcTemplate(@Qualifier("db2DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}