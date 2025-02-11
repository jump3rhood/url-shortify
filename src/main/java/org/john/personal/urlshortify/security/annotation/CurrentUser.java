package org.john.personal.urlshortify.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)  // Can only be used on method parameters
@Retention(RetentionPolicy.RUNTIME)  // Available at runtime for reflection
public @interface CurrentUser {
}