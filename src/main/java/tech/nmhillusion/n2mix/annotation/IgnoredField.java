package tech.nmhillusion.n2mix.annotation;

import java.lang.annotation.*;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-25
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoredField {
}
