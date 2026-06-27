package tech.nmhillusion.n2mix.helper.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;
import tech.nmhillusion.pi_logger.PiLogger;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class LogHelper {
    private static final Map<String, MixLogger> logFactory = new ConcurrentHashMap<>();
    private static final LogConfigModel piLogConfigModel = PiLoggerFactory.getDefaultLogConfig()
            .setColoring(true)
            .setLogLevel(LogLevel.DEBUG)
            .setIsOutputToFile(false)
            .setTimestampPattern("yyyy-MM-dd HH:mm:ss");
    private static boolean usePiLogger = true;

    public static LogConfigModel getDefaultPiLoggerConfig() {
        return piLogConfigModel;
    }

    public static void setDefaultPiLoggerConfig(LogConfigModel logConfig) {
        PiLoggerFactory.getDefaultLogConfig()
                .setColoring(logConfig.getColoring())
                .setIsOutputToFile(logConfig.isOutputToFile())
                .setLogFilePath(logConfig.getLogFilePath())
                .setLogLevel(logConfig.getLogLevel())
                .setOnChangeConfig(logConfig.getOnChangeConfig())
                .setTimestampPattern(logConfig.getTimestampPattern())
        ;
    }

    public static void setUsePiLogger(boolean usePiLogger) {
        LogHelper.usePiLogger = usePiLogger;
        logFactory.clear();
    }

    private static PiLogger defaultLogger(Object object_) {
        return PiLoggerFactory.getLogger(object_);
    }

    private static MixLogger generateLogger(Object object_, boolean usePiLogger) {
        final Class<?> loggerClass = object_ instanceof Class ? (Class<?>) object_ : object_.getClass();

        Logger logger_;

        if (!usePiLogger) {
            logger_ = LoggerFactory.getLogger(loggerClass);

            if (logger_ instanceof NOPLogger) {
                logger_ = defaultLogger(object_);
            }
        } else {
            logger_ = defaultLogger(object_);
        }

        final MixLogger mixLogger = new MixLogger(logger_, loggerClass);
        logFactory.put(object_.getClass().getName(), mixLogger);
        return mixLogger;
    }

    public static MixLogger getLogger(Object object_) {
        return getLogger(object_, usePiLogger);
    }

    public static MixLogger getLogger(Object object_, boolean usePiLogger) {
        String logName = object_ instanceof Class ? ((Class<?>) object_).getName() : object_.getClass().getName();
        if (logFactory.containsKey(logName)) {
            return logFactory.get(logName);
        } else {
            return generateLogger(object_, usePiLogger);
        }
    }

    public static List<Future<Void>> flush() {
        return logFactory.values()
                .stream()
                .map(MixLogger::flush)
                .toList();
    }

    public static void forceFlush() {
        for (MixLogger mixLogger : logFactory.values()) {
            mixLogger.forceFlush();
        }
    }
}
