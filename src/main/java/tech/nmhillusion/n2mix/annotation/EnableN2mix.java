package tech.nmhillusion.n2mix.annotation;

import org.springframework.context.annotation.Import;
import tech.nmhillusion.n2mix.bootstrap.SharedConfigurationReference;

import java.lang.annotation.*;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-12-10
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SharedConfigurationReference.class})
public @interface EnableN2mix {
}
