package com.scsa.attend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource({"classpath:/application.properties"})
@MapperScan(basePackages = {"com.scsa.attend.mapper"})
//@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUser;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        log.error(jdbcUrl);
        config.setDriverClassName(jdbcDriver);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUser);
        config.setPassword(jdbcPassword);

        config.setConnectionTimeout(30000); //풀에서 연결을 가져오기 위해 대기할 최대 시간(밀리초). 기본값은 30,000ms (30초)
        config.setMinimumIdle(1); //풀의 최소 연결 크기입니다. 기본값은 5입니다
        config.setMaximumPoolSize(5);//풀의 최대 크기입니다. 기본값은 10입니다.
        config.setIdleTimeout(600000); //연결이 유휴 상태로 유지될 최대 시간(밀리초)입니다. 기본값은 600,000ms (10분)입니다.
        config.setMaxLifetime(1800000);//풀의 연결이 최대 유지될 시간(밀리초)입니다. 기본값은 1,800,000ms (30분)입니다.
        config.setAutoCommit(true);//커넥션의 자동 커밋 여부를 설정합니다. 기본값은 true입니다.

        return new HikariDataSource(config);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));

// (2)모든 mapper용 xml
//        sessionFactory.setMapperLocations(new
//                PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml"));
        return sessionFactory.getObject();
    }

    /**
     * SqlSessionFactory를 직접사용하면 트랜잭션 관리를 직접 구현해야 합니다.
     * SqlSessionTemplate를 사용하면 SqlSessionFactory를 사용하여 생성된 SqlSession을 래핑하며, 트랜잭션 관리와 예외 처리를 자동으로 처리합니다.
     *
     * @param sqlSessionFactory
     * @return
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}