package org.tiger.open.core.annotations;

import org.springframework.web.bind.annotation.RequestMethod;
import org.tiger.open.core.defaults.OpenParams;

import java.lang.annotation.*;

/**
 * name-group-version全局唯一
 * <p>
 * 开发平台方法注解
 * <p>
 * Created by fish on 17/3/5.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenMethod {

    /**
     * 方法名称,全局唯一
     */
    String name();

    /**
     * 分组
     */
    String group() default OpenParams.DEFAULT_GROUP;

    /**
     * 版本
     */
    String version() default OpenParams.DEFAULT_VERSION;

    /**
     * 请求方法的限制
     */
    RequestMethod[] httpMethods() default {};

    /**
     * 请求参数
     */
    String[] paramNames() default {};

    /**
     * 是否需要会话
     */
    boolean isSession() default false;

    /**
     * 是否需要签名
     */
    boolean isSign() default true;

}
