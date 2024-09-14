package tech.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.exception.AppRuntimeException;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-22
 */
class ExceptionUtilTest {

    @Test
    void throwException() {
        final String exceptionMessage = "Missing parameter";
        final AppRuntimeException missingParameterException = ExceptionUtil.throwException(new IllegalArgumentException(exceptionMessage));
        Assertions.assertInstanceOf(AppRuntimeException.class, missingParameterException);
        Assertions.assertEquals(IllegalArgumentException.class.getName() + ": " + exceptionMessage, missingParameterException.getMessage());
    }

    @Test
    void throwParsedSqlException() {
        final String sqlErrorMessage = "invalid number";
        try {
            throw new SQLException("ORA-01722: " + sqlErrorMessage);
        } catch (Throwable ex) {
            final AppRuntimeException sqlException = ExceptionUtil.throwParsedSqlException(ex);
            Assertions.assertNotNull(sqlException);
            Assertions.assertEquals(sqlErrorMessage, sqlException.getMessage());
        }
    }

    @Test
    void convertThrowableToString() {
        try {
            throw new FileNotFoundException("Cannot find the file with name: data.txt");
        } catch (Throwable ex) {
            Assertions.assertDoesNotThrow(() -> {
                final String stackTraceContent = ExceptionUtil.convertThrowableToString(ex);
                getLogger(this).error("Error Stacktrace: " + stackTraceContent);
                Assertions.assertNotNull(stackTraceContent);
                Assertions.assertFalse(stackTraceContent.isEmpty());
            });
        }
    }

    @Test
    void testSqlExceptionWithStacktrace() {
        final String sqlErrorMessage = "Cannot find program unit amk.pkg_app_base.f_get_name";
        try {
            throw new AppRuntimeException(
                    new SQLException("ORA-01722: " + sqlErrorMessage)
            );
        } catch (Throwable ex) {
            final AppRuntimeException sqlException = ExceptionUtil.throwParsedSqlException(ex);
            Assertions.assertNotNull(sqlException);
            Assertions.assertTrue(
                    sqlException.getMessage()
                            .contains(sqlErrorMessage)
            );
            getLogger(this).error(ex);
        }
    }
}