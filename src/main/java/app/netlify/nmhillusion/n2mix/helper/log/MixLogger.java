package app.netlify.nmhillusion.n2mix.helper.log;

import app.netlify.nmhillusion.n2mix.type.ChainMap;
import app.netlify.nmhillusion.n2mix.util.CollectionUtil;
import app.netlify.nmhillusion.pi_logger.PiLogger;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

import java.util.Calendar;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLog;

public class MixLogger {
    private final Logger logger;
    private final Class<?> mClass;
    private final boolean isPiLogger;

    public MixLogger(Logger logger, Class<?> mClass) {
        this.logger = logger;
        this.mClass = mClass;
        isPiLogger = logger instanceof PiLogger;
    }

    public static void main(String[] args) {
        try {
            getLog(MixLogger.class).infoFormat("test > $data > $value", new ChainMap<String, String>()
                    .chainPut("data", "myData")
                    .chainPut("value", "myValue"));
            getLog(MixLogger.class).info("hello world\nabc world");

            Long.parseLong("g");
            int val = 8 / 0;
            System.out.println("val: " + val);
        } catch (Exception ex) {
            getLog(MixLogger.class).info(ex);
        }
    }

    private StackTraceElement findTrace() {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        StackTraceElement result = null;

        for (StackTraceElement stackTraceElement : traces) {
            if (mClass.getName().contains(stackTraceElement.getClassName())) {
                result = stackTraceElement;
                break;
            }
        }

        if (null == result) {
            result = traces[traces.length - 1];
        }
        return result;
    }

    private String getTemplateLog(Object data) {
        StackTraceElement trace = findTrace();
        String optionalData = "\t{}\n";
        if (data instanceof Throwable) {
            optionalData = "";
        }
        return null != trace ? trace.getMethodName() + "()_lineNumber:" + trace.getLineNumber() + " " + optionalData : "<not_method>" + optionalData;
    }

    private String defaultMarker() {
        return logger.getName();
    }

    private Object doFormattedString(Object formatData, Object... params) {
        if (formatData instanceof String) {
            String formattedData = "";

            /// Mark: Format with %
            String textPattern = String.valueOf(formatData);
            /// TODO: 2022-11-27 check for log string with very big log text 
            textPattern = textPattern.replaceAll("%([^abcdefghnostx]+)", "%%$1");
            try {
                formattedData = String.format(textPattern, params);
            } catch (Exception ex) {
                System.out.println(
                        Calendar.getInstance().getTime()
                                + " [ERROR] Error when format string: "
                                + " [" + textPattern + "] "
                                + " --> "
                                + ex.getMessage()
                );
                formattedData = String.valueOf(formatData);
            }

            /// Mark: Format with $
            if (!CollectionUtil.isNullOrEmptyArgv(params)) {
                if (params[0] instanceof final ChainMap<?, ?> paramsChainMap) {
                    for (Object key : paramsChainMap.keySet()) {
                        final Object value = paramsChainMap.get(key);

                        formattedData = formattedData.replace("$" + key, "$" + key + ": " + value);
                    }
                }
            }

            return formattedData;
        }
        return formatData;
    }

    public void info(Object data) {
        infoFormat(data);
    }

    public void infoFormat(Object data, Object... params) {
        infoDetail(defaultMarker(), data, params);
    }

    public void infoDetail(String marker, Object data, Object... params) {
        if (!isPiLogger) {
            logger.info(MarkerFactory.getMarker(marker), getTemplateLog(data), doFormattedString(data, params));
        } else {
            logger.info(String.valueOf(data), params);
        }
    }

    public void debug(Object data) {
        debugFormat(data);
    }

    public void debugFormat(Object data, Object... params) {
        debugDetail(defaultMarker(), data, params);
    }

    public void debugDetail(String marker, Object data, Object... params) {
        if (!isPiLogger) {
            logger.debug(MarkerFactory.getMarker(marker), getTemplateLog(data), doFormattedString(data, params));
        } else {
            logger.debug(String.valueOf(data), params);
        }
    }

    public void warn(Object data) {
        warnFormat(data);
    }

    public void warnFormat(Object data, Object... params) {
        warnDetail(defaultMarker(), data, params);
    }

    public void warnDetail(String marker, Object data, Object... params) {
        if (!isPiLogger) {
            logger.warn(MarkerFactory.getMarker(marker), getTemplateLog(data), doFormattedString(data, params));
        } else {
            logger.warn(String.valueOf(data), params);
        }
    }

    public void trace(Object data) {
        traceFormat(data);
    }

    public void traceFormat(Object data, Object... params) {
        traceDetail(defaultMarker(), data, params);
    }

    public void traceDetail(String marker, Object data, Object... params) {
        if (!isPiLogger) {
            logger.trace(MarkerFactory.getMarker(marker), getTemplateLog(data), doFormattedString(data, params));
        } else {
            logger.trace(String.valueOf(data), params);
        }
    }

    public void error(Object data) {
        errorFormat(data);
    }

    public void errorFormat(Object data, Object... params) {
        errorDetail(defaultMarker(), data, params);
    }

    public void errorDetail(String marker, Object data, Object... params) {
        if (!isPiLogger) {
            logger.error(MarkerFactory.getMarker(marker), getTemplateLog(data), doFormattedString(data, params));
        } else {
            logger.error(String.valueOf(data), params);
        }
    }
}
