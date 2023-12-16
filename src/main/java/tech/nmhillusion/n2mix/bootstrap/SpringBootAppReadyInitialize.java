package tech.nmhillusion.n2mix.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.validator.StringValidator;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-30
 */
@Configuration
public class SpringBootAppReadyInitialize implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        getLogger(this).info("initialize in n2mix for spring boot: " + event);
    }
}
