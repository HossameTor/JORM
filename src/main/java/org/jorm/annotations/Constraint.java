package org.jorm.annotations;

import org.jorm.ConstraintType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Constraint {
    ConstraintType[] types() default {};
    String def() default "";
}
