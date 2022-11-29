package me.vicasong.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Path format validator
 *
 * @author vicasong
 * @since 2022-08-09 14:25
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {PathFormatValidator.class})
public @interface PathFormat {

	String message() default "path format error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};


	boolean blankAllow() default false;


	int length() default 0;


	boolean throwInvalidError() default true;

	boolean dependsOnOs() default false;
}
