package tech.nmhillusion.n2mix.util;

import org.springframework.lang.NonNull;
import tech.nmhillusion.n2mix.exception.GeneralException;

import java.sql.SQLException;
import java.util.regex.Pattern;

public abstract class ExceptionUtil {
    private final static Pattern USER_ERROR_PATTERN = Pattern.compile("ORA-20\\d{3}:.*?", Pattern.DOTALL | Pattern.MULTILINE);
    
    public static Throwable getFurthestGeneralException(final Throwable ex) {
        Throwable finalException = ex;
        
        if (null == finalException) {
            finalException = new GeneralException("Exception is null");
        }
        
        while (finalException.getCause() instanceof GeneralException) {
            finalException = finalException.getCause();
        }
        return finalException;
    }
    
    public static GeneralException throwException(final Throwable ex) {
        final Throwable finalException = getFurthestGeneralException(ex);
        
        if (finalException instanceof SQLException) {
            return throwParsedSqlException(finalException);
        } else if (finalException instanceof GeneralException) {
            return (GeneralException) finalException;
        } else {
            return new GeneralException(ex);
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
    
    public static GeneralException throwParsedSqlException(final Throwable ex) {
        if (ex instanceof SQLException) {
            String message = ex.getMessage();
            
            message = truncateSqlUserError(message);
            
            if (message.contains(":")) {
                message = message.substring(message.indexOf(":") + 1);
            }
            return new GeneralException(message.trim(), ex);
        } else {
            final Throwable finalException = getFurthestGeneralException(ex);
            
            if (finalException instanceof GeneralException) {
                return (GeneralException) finalException;
            } else {
                return new GeneralException(ex.getMessage(), ex);
            }
        }
    }
}
