package app.netlify.nmhillusion.n2mix.helper.log;

import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

public class LogHelper {
    private static final Map<String, ChubbLogger> logFactory = new TreeMap<>();

    private static ChubbLogger generateLogger(Object object) {
        Class<?> loggerClass = object instanceof Class ? (Class<?>) object : object.getClass();
        ChubbLogger chubbLogger = new ChubbLogger(LoggerFactory.getLogger(loggerClass), loggerClass);
        logFactory.put(object.getClass().getName(), chubbLogger);
        return chubbLogger;
    }

    public static ChubbLogger getLog(Object object) {
        String logName = object instanceof Class ? ((Class<?>) object).getName() : object.getClass().getName();
        if (logFactory.containsKey(logName)) {
            return logFactory.get(logName);
        } else {
            return generateLogger(object);
        }
    }
}
