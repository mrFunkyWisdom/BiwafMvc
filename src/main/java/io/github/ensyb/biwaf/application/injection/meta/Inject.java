package io.github.ensyb.biwaf.application.injection.meta;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface Inject {

	public String reference() default "";
	public String value() default "";
	
}
