package app.netlify.nmhillusion.n2mix.helper.log;

import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

public class LogHelper {
    private static final Map<String, MixLogger> logFactory = new TreeMap<>();

    private static MixLogger generateLogger(Object object) {
        Class<?> loggerClass = object instanceof Class ? (Class<?>) object : object.getClass();
        MixLogger mixLogger = new MixLogger(LoggerFactory.getLogger(loggerClass), loggerClass);
        logFactory.put(object.getClass().getName(), mixLogger);
        return mixLogger;
    }

    public static MixLogger getLog(Object object) {
        String logName = object instanceof Class ? ((Class<?>) object).getName() : object.getClass().getName();
        if (logFactory.containsKey(logName)) {
            return logFactory.get(logName);
        } else {
            return generateLogger(object);
        }
    }
}
