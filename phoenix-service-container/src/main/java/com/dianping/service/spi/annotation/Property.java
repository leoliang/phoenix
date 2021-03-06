package com.dianping.service.spi.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD })
public @interface Property {
	public String name() default "";

	public String title() default "";

	public String description() default "";

	public String defaultValue() default NOT_SPECIFIED;

	public boolean required() default true;

	public static final String NOT_SPECIFIED = "NOT_SPECIFIED";
}
