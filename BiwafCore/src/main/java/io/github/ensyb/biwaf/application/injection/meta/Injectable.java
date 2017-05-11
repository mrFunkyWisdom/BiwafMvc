package io.github.ensyb.biwaf.application.injection.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Injectable {

	public String id() default "";
	public Scope scope() default Scope.PROTOTYPE;

	enum Scope {
		SINGLETON, PROTOTYPE
	}

}
