package com.client.ws.rasmooplus.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


public class JacocoConfig {

    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE, METHOD, CONSTRUCTOR })
    public @interface Generated {
    }
}
