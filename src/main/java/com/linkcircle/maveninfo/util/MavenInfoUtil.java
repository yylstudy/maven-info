package com.linkcircle.maveninfo.util;

import com.linkcircle.maveninfo.annotation.EnableMavenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/20 14:46
 */

public class MavenInfoUtil {

    private static Logger log = LoggerFactory.getLogger(MavenInfoUtil.class);
    private static boolean springBootPresent = ClassUtils.isPresent("org.springframework.boot.autoconfigure.SpringBootApplication", null);

    public static Map getMavenInfo(ApplicationContext applicationContext){
        Map<String,Object> map = new HashMap<>();
        map.put("code","500");
        try{
            Map<String, Object> mainObject;
            if(springBootPresent){
                mainObject = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
            }else{
                mainObject = applicationContext.getBeansWithAnnotation(EnableMavenInfo.class);
            }
            Optional<Object> optional = mainObject.values().stream().findFirst();
            if(!optional.isPresent()){
                map.put("message","请先配置启动注解：SpringBootApplication或EnableJarVersion");
                return map;
            }
            Object startObject = optional.get();
            Class clazz = startObject.getClass();
            Class configurationClass = Class.forName("org.springframework.context.annotation.ConfigurationClassEnhancer$EnhancedConfiguration");
            //cglib代理或者@Configuration类
            boolean cglib = AopUtils.isCglibProxy(startObject);
            if(cglib){
                clazz = AopUtils.getTargetClass(startObject);
            }else if(configurationClass.isAssignableFrom(clazz)){
                clazz = clazz.getSuperclass();
            }
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(clazz.getClassLoader());
            Resource[] resources = resourcePatternResolver.getResources("META-INF/maven/**/pom.properties");
            if(resources.length!=1){
                for(Resource resource:resources){
                    log.info("resource:{}",resource.getFilename());
                }
                map.put("message","未找到或存在多个pom.properties");
                return map;
            }
            Properties properties = new Properties();
            properties.load(resources[0].getInputStream());
            map.put("result",properties);
            map.put("message","获取项目maven信息成功");
            map.put("code","0");
            return map;
        }catch (Exception e){
            map.put("message","获取maven信息异常");
            log.error("获取maven信息异常",e);
            return map;
        }

    }
}
