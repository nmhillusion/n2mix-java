package tech.nmhillusion.n2mix.util;

import org.springframework.lang.NonNull;
import tech.nmhillusion.n2mix.exception.AppRuntimeException;
import tech.nmhillusion.n2mix.exception.GeneralException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.regex.Pattern;

public abstract class ExceptionUtil {
    private final static Pattern USER_ERROR_PATTERN = Pattern.compile("ORA-20\\d{3}:.*?", Pattern.DOTALL | Pattern.MULTILINE);

    public static Throwable getFurthestGeneralException(final Throwable ex) {
        Throwable finalException = ex;

        if (null == finalException) {
            finalException = new GeneralException("Exception is null");
        }

        while (finalException.getCause() instanceof AppRuntimeException) {
            finalException = finalException.getCause();
        }
        return finalException;
    }

    public static AppRuntimeException throwException(final Throwable ex) {
        final Throwable finalException = getFurthestGeneralException(ex);

        if (finalException instanceof SQLException) {
            return throwParsedSqlException(finalException);
        } else if (finalException instanceof GeneralException) {
            return (AppRuntimeException) finalException;
        } else {
            return new AppRuntimeException(ex);
        }
    }

    private static String truncateSqlUserError(@NonNull String message) {
        if (USER_ERROR_PATTERN.matcher(message.trim()).matches()) {
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
            final Throwable finalException = getFurthestGeneralException(ex);

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
}
