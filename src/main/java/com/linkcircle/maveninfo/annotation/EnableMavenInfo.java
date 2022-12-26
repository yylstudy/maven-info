package com.linkcircle.maveninfo.annotation;

import com.linkcircle.maveninfo.filters.MavenInfoFilter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022/12/19 16:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(MavenInfoFilter.class)
public @interface EnableMavenInfo {
}
