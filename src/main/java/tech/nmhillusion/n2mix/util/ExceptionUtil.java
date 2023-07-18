package tech.nmhillusion.n2mix.util;

import tech.nmhillusion.n2mix.constant.ErrorType;
import tech.nmhillusion.n2mix.exception.ApiResponseException;
import tech.nmhillusion.n2mix.model.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.sql.SQLException;
import java.util.regex.Pattern;

public abstract class ExceptionUtil {
    private final static Pattern USER_ERROR_PATTERN = Pattern.compile("ORA-20\\d{3}:.*?", Pattern.DOTALL | Pattern.MULTILINE);

    public static ApiResponseException throwException(final Throwable ex) {
        return throwException(ex, HttpStatus.BAD_REQUEST);
    }

    public static Throwable getFurthestChubbCustomException(final Throwable ex) {
        Throwable finalException = ex;

        if (null == finalException) {
            finalException = new ApiResponseException("Exception is empty.");
        }

        while (finalException.getCause() instanceof ApiResponseException) {
            finalException = finalException.getCause();
        }
        return finalException;
    }

    public static ApiResponseException throwException(final Throwable ex, HttpStatus httpStatus) {
        final Throwable finalException = getFurthestChubbCustomException(ex);

        if (finalException instanceof ApiResponseException) {
            return (ApiResponseException) finalException;
        } else {
            return new ApiResponseException(new ApiErrorResponse(httpStatus, ex.getMessage(), ex.getMessage()), ex);
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

    public static ApiResponseException throwParsedSqlException(final Throwable ex) {
        if (ex instanceof SQLException) {
            String message = ex.getMessage();

            message = truncateSqlUserError(message);

            if (message.contains(":")) {
                message = message.substring(message.indexOf(":") + 1);
            }
            return new ApiResponseException(message.trim());
        } else {
            final Throwable finalException = getFurthestChubbCustomException(ex);

            if (finalException instanceof ApiResponseException) {
                return (ApiResponseException) finalException;
            } else {
                return new ApiResponseException(
                        new ApiErrorResponse(HttpStatus.valueOf(ErrorType.SQL_ERROR.getErrorCode()),
                                ErrorType.SQL_ERROR.getErrorName(),
                                ex.getMessage()
                        ),
                        ex
                );
            }
        }
    }
}
