package org.geektimes.configuration.microprofile.config.annotation;

import java.lang.annotation.*;

/**
 * @Author: 项峥
 * @Date: 2021/8/11 0:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigSources {
    ConfigSource[] value();
}
