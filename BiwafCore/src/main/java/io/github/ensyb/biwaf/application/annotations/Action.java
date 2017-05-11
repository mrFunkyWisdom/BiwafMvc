package io.github.ensyb.biwaf.application.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Action {
}
