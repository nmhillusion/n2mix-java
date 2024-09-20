package tech.nmhillusion.n2mix.helper.log;

import org.slf4j.Logger;
import org.slf4j.MarkerFactory;
import tech.nmhillusion.n2mix.type.ChainMap;
import tech.nmhillusion.n2mix.util.CollectionUtil;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;
import tech.nmhillusion.pi_logger.PiLogger;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class MixLogger {
    private final static Pattern IS_FORMATTED_PATTERN = Pattern.compile("%|(\\$\\w+(\\s|\\b))");
    private final static Pattern INTERPOLATION_PATTERN = Pattern.compile("\\{(\\d*)}");
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
            LogHelper.getLogger(MixLogger.class).infoFormat("test > $data > $value", new ChainMap<String, String>()
                    .chainPut("data", "myData")
                    .chainPut("value", "myValue"));
            LogHelper.getLogger(MixLogger.class).info("hello world\nabc world");

            Long.parseLong("g");
            int val = 8 / 0;
            System.out.println("val: " + val);
        } catch (Exception ex) {
            LogHelper.getLogger(MixLogger.class).info(ex);
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
        return getTemplateLog(true, data);
    }

    private String getTemplateLog(boolean prefillPlaceholderForLogMessage, Object data) {
        String optionalData = "";

        if (prefillPlaceholderForLogMessage) {
            optionalData = "\t{}\n";
        }
        if (data instanceof Throwable) {
            optionalData = "";
        }
        return wrapperLogMessageWithTrace(optionalData);
    }

    private String defaultMarker() {
        return logger.getName();
    }

    private Object[] doFormattedString(Object formatData, Object... params) {
        final String trimmedFormatData = StringUtil.trimWithNull(formatData);
        final boolean foundFormattedPattern = formatData instanceof String
                && IS_FORMATTED_PATTERN.matcher(trimmedFormatData).find();

        if (foundFormattedPattern) {
            String formattedData = "";

            //-- Mark: Format with %
            String textPattern = String.valueOf(formatData);
            /// TODO: 2022-11-27 check for log string with very big log text 
            textPattern = textPattern.replaceAll("%([^abcdefghnostx]+)", "%%$1");
            try {
                formattedData = String.format(textPattern, params);
            } catch (Exception ex) {
                System.out.println(
                        ZonedDateTime.now()
                                + " [ERROR] Error when format string: "
                                + " [" + textPattern + "] "
                                + " --> "
                                + ex.getMessage()
                );
                formattedData = String.valueOf(formatData);
            }

            //-- Mark: Format with $
            if (!CollectionUtil.isNullOrEmptyArgv(params)) {
                if (params[0] instanceof final ChainMap<?, ?> paramsChainMap) {
                    for (Object key : paramsChainMap.keySet()) {
                        final Object value = paramsChainMap.get(key);

                        formattedData = formattedData.replace("$" + key, "$" + key + ": " + value);
                    }
                }
            }

            return new Object[]{formattedData};
        } else {
            final ArrayList<Object> paramObjects = new ArrayList<>(Collections.singleton(formatData));
            paramObjects.addAll(Arrays.asList(params));
            return paramObjects.toArray();
        }
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public void info(Object data) {
        if (data instanceof String) {
            infoFormat((String) data);
        } else {
            infoFormat(String.valueOf(data), data);
        }
    }

    private String formatForInterpolation(String format, Object... params) {
        final List<Object> paramObjects = Arrays.asList(params);

        final AtomicInteger count = new AtomicInteger();

        final String interpolationText = INTERPOLATION_PATTERN.matcher(format).replaceAll(match -> {
            final String matchedIndex = match.group(1);

            final int currentIndex = StringValidator.isBlank(matchedIndex)
                    ? count.getAndIncrement()
                    : Integer.parseInt(matchedIndex);

            if (params.length > currentIndex) {
                return String.valueOf(paramObjects.get(currentIndex));
            } else {
                return match.group();
            }
        });

        return wrapperLogMessageWithTrace(interpolationText);
    }

    private String wrapperLogMessageWithTrace(String message) {
        final StackTraceElement trace = findTrace();
        return null != trace ? trace.getMethodName() + "():" + trace.getLineNumber() + " " + message : "<not_method>" + message;
    }

    public void info(String format, Object... params) {
        logger.info(formatForInterpolation(format, params));
    }

    public void infoFormat(String format_, Object... params) {
        infoDetail(defaultMarker(), format_, params);
    }

    public void infoDetail(String marker, String format_, Object... params) {
        if (isInfoEnabled()) {
            if (!isPiLogger) {
                logger.info(MarkerFactory.getMarker(marker), getTemplateLog(format_), doFormattedString(format_, params));
            } else {
                logger.info(MarkerFactory.getMarker(marker), getTemplateLog(false, format_), doFormattedString(format_, params));
            }
        }
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void debug(Object data) {
        if (data instanceof String) {
            debugFormat((String) data);
        } else {
            debugFormat(String.valueOf(data), data);
        }
    }

    public void debug(String format, Object... params) {
        logger.debug(formatForInterpolation(format, params));
    }

    public void debugFormat(String format_, Object... params) {
        debugDetail(defaultMarker(), format_, params);
    }

    public void debugDetail(String marker, String format_, Object... params) {
        if (isDebugEnabled()) {
            if (!isPiLogger) {
                logger.debug(MarkerFactory.getMarker(marker), getTemplateLog(format_), doFormattedString(format_, params));
            } else {
                logger.debug(MarkerFactory.getMarker(marker), getTemplateLog(false, format_), doFormattedString(format_, params));
            }
        }
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public void warn(Object data) {
        if (data instanceof String) {
            warnFormat((String) data);
        } else {
            warnFormat(String.valueOf(data), data);
        }
    }

    public void warn(String format, Object... params) {
        logger.warn(formatForInterpolation(format, params));
    }

    public void warnFormat(String format_, Object... params) {
        warnDetail(defaultMarker(), format_, params);
    }

    public void warnDetail(String marker, String format_, Object... params) {
        if (isWarnEnabled()) {
            if (!isPiLogger) {
                logger.warn(MarkerFactory.getMarker(marker), getTemplateLog(format_), doFormattedString(format_, params));
            } else {
                logger.warn(MarkerFactory.getMarker(marker), getTemplateLog(false, format_), doFormattedString(format_, params));
            }
        }
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public void trace(Object data) {
        if (data instanceof String) {
            traceFormat((String) data);
        } else {
            traceFormat(String.valueOf(data), data);
        }
    }

    public void trace(String format, Object... params) {
        logger.trace(formatForInterpolation(format, params));
    }

    public void traceFormat(String format_, Object... params) {
        traceDetail(defaultMarker(), format_, params);
    }

    public void traceDetail(String marker, String format_, Object... params) {
        if (isTraceEnabled()) {
            if (!isPiLogger) {
                logger.trace(MarkerFactory.getMarker(marker), getTemplateLog(format_), doFormattedString(format_, params));
            } else {
                logger.trace(MarkerFactory.getMarker(marker), getTemplateLog(false, format_), doFormattedString(format_, params));
            }
        }
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public void error(Object data) {
        if (data instanceof String) {
            errorFormat((String) data);
        } else {
            errorFormat(String.valueOf(data), data);
        }
    }

    public void error(String format, Object... params) {
        logger.error(formatForInterpolation(format, params));
    }

    public void errorFormat(String format_, Object... params) {
        errorDetail(defaultMarker(), format_, params);
    }

    public void errorDetail(String marker, String format_, Object... params) {
        if (isErrorEnabled()) {
            if (!isPiLogger) {
                logger.error(MarkerFactory.getMarker(marker), getTemplateLog(format_), doFormattedString(format_, params));
            } else {
                logger.error(MarkerFactory.getMarker(marker), getTemplateLog(false, format_), doFormattedString(format_, params));
            }
        }
    }
}
