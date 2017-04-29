package io.github.ensyb.biwaf.application.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Request {

	String value() default "";
	HttpMethod method() default HttpMethod.GET;
	
	//only get and post for now
	enum HttpMethod{GET , POST};
	

}
