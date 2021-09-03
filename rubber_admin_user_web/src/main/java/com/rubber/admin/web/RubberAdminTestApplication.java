package com.rubber.admin.web;

import com.rubber.admin.core.plugins.cache.ICacheProvider;
import com.rubber.admin.core.plugins.cache.LocalCacheProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


/**
 * 启动类
 * @author luffyu
 * Created on 2019-10-31
 */
@ComponentScan("com.rubber.*")
@MapperScan("com.rubber.admin.**.mapper.**")
@SpringBootApplication
public class RubberAdminTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RubberAdminTestApplication.class, args);
    }


    /**
     * 默认的缓存信息
     */
    @Bean
    @ConditionalOnMissingBean(ICacheProvider.class)
    public ICacheProvider cacheProvider() {
        return new LocalCacheProvider();
    }

}
