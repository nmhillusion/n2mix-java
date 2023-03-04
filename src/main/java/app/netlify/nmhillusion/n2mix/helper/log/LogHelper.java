package app.netlify.nmhillusion.n2mix.helper.log;

import app.netlify.nmhillusion.pi_logger.PiLogger;
import app.netlify.nmhillusion.pi_logger.PiLoggerFactory;
import app.netlify.nmhillusion.pi_logger.constant.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.util.Map;
import java.util.TreeMap;

public class LogHelper {
    private static final Map<String, MixLogger> logFactory = new TreeMap<>();
    private static boolean usePiLogger = true;

    static {
        PiLoggerFactory.getDefaultLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true)
                .setLogLevel(LogLevel.DEBUG)
                .setIsOutputToFile(false)
                .setTimestampPattern("yyyy-MM-dd HH:mm:ss")
        ;
    }

    public static void setUsePiLogger(boolean usePiLogger) {
        LogHelper.usePiLogger = usePiLogger;
    }

    private static PiLogger defaultLogger(Object object_) {
        return PiLoggerFactory.getLog(object_);
    }

    private static MixLogger generateLogger(Object object_, boolean usePiLogger) {
        final Class<?> loggerClass = object_ instanceof Class ? (Class<?>) object_ : object_.getClass();

        Logger logger_ = defaultLogger(object_);

        if (!usePiLogger) {
            logger_ = LoggerFactory.getLogger(loggerClass);

            if (logger_ instanceof NOPLogger) {
                logger_ = defaultLogger(object_);
            }
        }

        final MixLogger mixLogger = new MixLogger(logger_, loggerClass);
        logFactory.put(object_.getClass().getName(), mixLogger);
        return mixLogger;
    }

    public static MixLogger getLog(Object object_) {
        return getLog(object_, usePiLogger);
    }

    public static MixLogger getLog(Object object_, boolean usePiLogger) {
        String logName = object_ instanceof Class ? ((Class<?>) object_).getName() : object_.getClass().getName();
        if (logFactory.containsKey(logName)) {
            return logFactory.get(logName);
        } else {
            return generateLogger(object_, usePiLogger);
        }
    }
}
