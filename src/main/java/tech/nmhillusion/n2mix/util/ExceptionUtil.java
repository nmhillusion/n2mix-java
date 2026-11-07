package tech.nmhillusion.n2mix.util;

import org.springframework.lang.NonNull;
import tech.nmhillusion.n2mix.exception.AppRuntimeException;
import tech.nmhillusion.n2mix.exception.GeneralException;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class ExceptionUtil {
    private final static Pattern USER_ERROR_PATTERN = Pattern.compile("ORA-20\\d{3}:.*?", Pattern.DOTALL | Pattern.MULTILINE);

    private static <T extends Throwable> T traverseCauseChain(Throwable ex, Class<T> classToFind) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (classToFind.isInstance(cause)) {
                return CastUtil.safeCast(cause, classToFind);
            }
            cause = cause.getCause();
        }
        return null;
    }

    public static Throwable unwrapAppRuntimeException(final Throwable ex) {
        Throwable finalException = ex;

        if (null == finalException) {
            finalException = new GeneralException("Exception is null");
        }

        while (finalException instanceof AppRuntimeException) {
            if (Objects.nonNull(finalException.getCause())) {
                finalException = finalException.getCause();
            }
        }

        return finalException;
    }

    public static AppRuntimeException throwException(final Throwable ex) {
        final Throwable finalException = unwrapAppRuntimeException(ex);

        if (finalException instanceof SQLException) {
            return throwParsedSqlException(finalException);
        } else if (finalException instanceof GeneralException) {
            return new AppRuntimeException(finalException);
        } else {
            return new AppRuntimeException(ex);
        }
    }

    private static String truncateSqlUserError(@NonNull String message) {
        if (StringValidator.isBlank(message)) {
            message = StringUtil.EMPTY;
        } else if (USER_ERROR_PATTERN.matcher(message.trim()).matches()) {
            if (message.contains("\n")) {
                message = message.substring(0, message.indexOf("\n"));
            }
        }

        return message;
    }

    public static AppRuntimeException throwParsedSqlException(final Throwable ex) {
        if (ex instanceof SQLException) {
            String message = ex.getMessage();

            message = truncateSqlUserError(message);

            if (message.contains(":")) {
                message = message.substring(message.indexOf(":") + 1);
            }
            return new AppRuntimeException(message.trim(), ex);
        } else {
            final Throwable finalException = unwrapAppRuntimeException(ex);

            if (finalException instanceof AppRuntimeException) {
                return (AppRuntimeException) finalException;
            } else {
                return new AppRuntimeException(ex.getMessage(), ex);
            }
        }
    }

    public static String convertThrowableToString(Throwable ex_) throws IOException {
        if (null == ex_) {
            return StringUtil.EMPTY;
        }

        try (final ByteArrayOutputStream outputStream_ = new ByteArrayOutputStream();
             final PrintWriter printWriter_ = new PrintWriter(outputStream_, true)
        ) {
            ex_.printStackTrace(printWriter_);
            printWriter_.flush();
            return outputStream_.toString(StandardCharsets.UTF_8);
        }
    }

    public static <T extends Throwable> T getExceptionFromStacktrace(Throwable ex, Class<T> classExceptionToFind, T defaultValueIfNotFound) {
        // Handle null input
        if (ex == null) {
            return defaultValueIfNotFound;
        }

        // Check the exception itself
        if (classExceptionToFind.isInstance(ex)) {
            return CastUtil.safeCast(ex, classExceptionToFind);
        }

        // Check suppressed exceptions
        final Throwable[] suppressedList = ex.getSuppressed();
        for (Throwable throwable_ : suppressedList) {
            if (classExceptionToFind.isInstance(throwable_)) {
                return CastUtil.safeCast(throwable_, classExceptionToFind);
            }
        }

        // Traverse the cause chain using shared helper
        T foundInCause = traverseCauseChain(ex, classExceptionToFind);
        if (foundInCause != null) {
            return foundInCause;
        }

        return defaultValueIfNotFound;
    }
}
