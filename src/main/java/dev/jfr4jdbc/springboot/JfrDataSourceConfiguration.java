package dev.jfr4jdbc.springboot;

import com.zaxxer.hikari.HikariDataSource;
import dev.jfr4jdbc.Jfr4JdbcDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class JfrDataSourceConfiguration {

    @ConditionalOnClass({HikariDataSource.class})
    @ConditionalOnProperty(
            name = {"spring.datasource.type"},
            havingValue = "com.zaxxer.hikari.HikariDataSource",
            matchIfMissing = true
    )
    static class JfrDataSource {
        JfrDataSource() {
        }

        @Bean
        @ConfigurationProperties(
                prefix = "spring.datasource.hikari"
        )
        DataSource dataSource(DataSourceProperties properties) {
            HikariDataSource dataSource = (HikariDataSource) JfrDataSourceConfiguration.createDataSource(properties, HikariDataSource.class);
            if (StringUtils.hasText(properties.getName())) {
                dataSource.setPoolName(properties.getName());
            }
            return new Jfr4JdbcDataSource(dataSource);
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
        return (T) properties.initializeDataSourceBuilder().type(type).build();
    }
}