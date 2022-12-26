package com.linkcircle.maveninfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/20 15:00
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(name = {"org.springframework.cloud.gateway.config.GatewayAutoConfiguration"})
@ComponentScan("com.linkcircle.maveninfo")
public class MavenInfoAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Logger log = LoggerFactory.getLogger(MavenInfoAutoConfiguration.class);

    private static boolean springCloudGatewayPresent = ClassUtils.isPresent("org.springframework.cloud.gateway.config.GatewayAutoConfiguration", null);

    @PostConstruct
    public void init(){
        if(springCloudGatewayPresent){
            RouteDefinition routeDefinition = new RouteDefinition();
            routeDefinition.setId("spring_cloud_gateway_maveninfo");
            routeDefinition.setUri(URI.create("http://gateway"));
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            Map<String, String> args = new HashMap<>();
            args.put("_genkey_0","/getMavenInfo");
            predicateDefinition.setArgs(args);
            predicateDefinition.setName("Path");
            routeDefinition.setPredicates(Arrays.asList(predicateDefinition));
            RouteDefinitionRepository routeDefinitionRepository = applicationContext.getBean(RouteDefinitionRepository.class);
            routeDefinitionRepository.save(Mono.just(routeDefinition)).subscribe();
            log.info("spring cloud gateway maven路由信息添加成功");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
