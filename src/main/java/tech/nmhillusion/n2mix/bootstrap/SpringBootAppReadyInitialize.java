package tech.nmhillusion.n2mix.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
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
@Component
public class SpringBootAppReadyInitialize implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${spring.output.ansi.enabled}")
    private String outputAnsiEnabled;

    @Value("${logging.pattern.dateformat}")
    private String loggingPatternDateFormat;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        getLogger(this).info("initialize in n2mix for spring boot: " + event);

        setupLogging();
    }

    private void setupLogging() {
        getLogger(this).info("Setup for LogHelper");

        final LogConfigModel piLoggerConfig = LogHelper.getDefaultPiLoggerConfig();
        piLoggerConfig
                .setColoring("always".equalsIgnoreCase(outputAnsiEnabled));

        if (!StringValidator.isBlank(loggingPatternDateFormat)) {
            getLogger(this).info("setting logging timestamp pattern to: " + loggingPatternDateFormat);
            piLoggerConfig.setTimestampPattern(loggingPatternDateFormat);
        }

        LogHelper.setDefaultPiLoggerConfig(
                piLoggerConfig
        );
    }
}
