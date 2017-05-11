package io.github.ensyb.biwaf.application.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface RequestParam {

	String value();	
}
