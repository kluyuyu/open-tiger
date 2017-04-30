package org.tiger.open.core.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 开放平台类注解
 * Created by fish on 17/3/5.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface OpenService {
}
