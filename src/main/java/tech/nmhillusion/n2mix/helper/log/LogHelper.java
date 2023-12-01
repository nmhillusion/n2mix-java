package tech.nmhillusion.n2mix.helper.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;
import tech.nmhillusion.pi_logger.PiLogger;
import tech.nmhillusion.pi_logger.PiLoggerFactory;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import java.util.Map;
import java.util.TreeMap;

public class LogHelper {
    private static final LogHelper instance = new LogHelper();
    private final Map<String, MixLogger> logFactory = new TreeMap<>();
    private final LogConfigModel piLogConfigModel = PiLoggerFactory.getDefaultLogConfig()
            .setColoring(true)
            .setDisplayLineNumber(true)
            .setLogLevel(LogLevel.DEBUG)
            .setIsOutputToFile(false)
            .setTimestampPattern("yyyy-MM-dd HH:mm:ss")
            .clone();
    
    private static boolean usePiLogger = true;
    
    public static LogConfigModel getDefaultPiLoggerConfig() {
        return instance.piLogConfigModel;
    }
    
    public static void setDefaultPiLoggerConfig(LogConfigModel logConfig) {
        PiLoggerFactory.getDefaultLogConfig()
                .setColoring(logConfig.getColoring())
                .setDisplayLineNumber(logConfig.getDisplayLineNumber())
                .setIsOutputToFile(logConfig.isOutputToFile())
                .setLogFilePath(logConfig.getLogFilePath())
                .setLogLevel(logConfig.getLogLevel())
                .setOnChangeConfig(logConfig.getOnChangeConfig())
                .setTimestampPattern(logConfig.getTimestampPattern())
        ;
    }
    
    public static void setUsePiLogger(boolean usePiLogger) {
        LogHelper.usePiLogger = usePiLogger;
        instance.logFactory.clear();
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
        instance.logFactory.put(object_.getClass().getName(), mixLogger);
        return mixLogger;
    }
    
    public static MixLogger getLogger(Object object_) {
        return getLogger(object_, usePiLogger);
    }
    
    public static MixLogger getLogger(Object object_, boolean usePiLogger) {
        String logName = object_ instanceof Class ? ((Class<?>) object_).getName() : object_.getClass().getName();
        if (instance.logFactory.containsKey(logName)) {
            return instance.logFactory.get(logName);
        } else {
            return generateLogger(object_, usePiLogger);
        }
    }
}
