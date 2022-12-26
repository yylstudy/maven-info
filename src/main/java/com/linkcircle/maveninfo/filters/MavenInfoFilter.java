package com.linkcircle.maveninfo.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkcircle.maveninfo.conditions.NoSpringCloudGatewayCondition;
import com.linkcircle.maveninfo.util.MavenInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 获取非spring cloud gateway的maven项目信息
 * @createTime 2022/12/20 11:28
 */
@Component
@Conditional(NoSpringCloudGatewayCondition.class)
public class MavenInfoFilter extends HttpFilter implements ApplicationContextAware,Ordered {

    private Logger log = LoggerFactory.getLogger(MavenInfoFilter.class);

    private PathMatcher pathMatcher = new AntPathMatcher();

    public MavenInfoFilter(){
        log.info("---------------------MavenInfoFilter init--------------------");
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    private ApplicationContext applicationContext;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String url = request.getRequestURI();
        if(pathMatcher.match("/getMavenInfo",url)){
            Map mavenInfo = MavenInfoUtil.getMavenInfo(applicationContext);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(objectMapper.writeValueAsString(mavenInfo));
            response.getWriter().flush();
        }else{
            super.doFilter(request, response, chain);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
