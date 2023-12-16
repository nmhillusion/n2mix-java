package tech.nmhillusion.n2mix.bootstrap;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-30
 */
@Configuration
@ComponentScan({"tech.nmhillusion.n2mix.bootstrap", "tech.nmhillusion.n2mix.config"})
public class SharedConfigurationReference {
}
