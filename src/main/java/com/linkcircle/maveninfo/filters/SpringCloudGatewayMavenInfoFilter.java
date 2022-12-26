package com.linkcircle.maveninfo.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkcircle.maveninfo.conditions.SpringCloudGatewayCondition;
import com.linkcircle.maveninfo.util.MavenInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 获取spring cloud gateway 的maven项目信息过滤器
 * @createTime 2022/12/20 11:28
 */
@Conditional(SpringCloudGatewayCondition.class)
@Component
public class SpringCloudGatewayMavenInfoFilter  implements GlobalFilter, Ordered,ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Logger log = LoggerFactory.getLogger(SpringCloudGatewayMavenInfoFilter.class);
    private PathMatcher pathMatcher = new AntPathMatcher();
    private ObjectMapper objectMapper = new ObjectMapper();

    public SpringCloudGatewayMavenInfoFilter(){
        log.info("---------------------SpringCloudGatewayMavenInfoFilter init--------------------");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        if(pathMatcher.match("/getMavenInfo",url)){
            Map mavenInfo = MavenInfoUtil.getMavenInfo(applicationContext);
            return getVoidMono(exchange.getResponse(),mavenInfo);
        }
        return chain.filter(exchange);
    }
    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, Map mavenInfo) {
        String resultStr = "";
        try{
            resultStr = objectMapper.writeValueAsString(mavenInfo);
        }catch (Exception e){
            log.error("getVoidMono error",e);
        }
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(resultStr.getBytes(Charset.forName("UTF-8")));
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
