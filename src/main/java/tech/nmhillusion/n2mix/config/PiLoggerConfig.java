package tech.nmhillusion.n2mix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.util.SelectUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import javax.annotation.PostConstruct;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-16
 */
@Configuration
public class PiLoggerConfig {
    @Value("${logging.piLogger.enabled:true}")
    private boolean usePiLogger;

    @Value("${logging.piLogger.displayLineNumber:true}")
    private boolean displayLineNumber;

    @Value("${spring.output.ansi.enabled:NEVER}")
    private String outputAnsiEnabled;

    @Value("${logging.file.name}")
    private String loggingFileName;

    @Value("${logging.file.path}")
    private String loggingFilePath;

    @Value("${logging.pattern.dateformat:yyyy-MM-dd'T'HH:mm:ss.SSS}")
    private String loggingPatternDateFormat;

    @PostConstruct
    private void setupLogging() {
        getLogger(this).info("Setup for PiLogHelper");

        if (!usePiLogger) {
            getLogger(this).info("No using PiLogHelper");
            return;
        }


        final LogConfigModel piLoggerConfig = LogHelper.getDefaultPiLoggerConfig();
        piLoggerConfig
                .setColoring("always".equalsIgnoreCase(outputAnsiEnabled))
        ;

        if (!StringValidator.isBlank(loggingPatternDateFormat)) {
            getLogger(this).info("setting logging timestamp pattern to: " + loggingPatternDateFormat);
            piLoggerConfig.setTimestampPattern(loggingPatternDateFormat);
        }

        {
            //-- Mark: Set for file output
            final String outputFilePath = SelectUtil.getFirstValueNotNullArgv(loggingFileName, loggingFilePath);

            piLoggerConfig
                    .setIsOutputToFile(!StringValidator.isBlank(outputFilePath))
                    .setLogFilePath(outputFilePath)
            ;
        }

        LogHelper.setDefaultPiLoggerConfig(
                piLoggerConfig
        );
    }
}
