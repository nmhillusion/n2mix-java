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

    @Test
    void getExceptionFromStacktrace_directMatch() {
        final SQLException sqlException = new SQLException("Direct SQL error");
        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(sqlException, SQLException.class, null);
        Assertions.assertSame(sqlException, found);
    }

    @Test
    void getExceptionFromStacktrace_wrappedByOneLevel() {
        final SQLException sqlException = new SQLException("Wrapped SQL error", "23505", 1);
        final RuntimeException wrapper = new RuntimeException(sqlException);
        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(wrapper, SQLException.class, null);
        Assertions.assertSame(sqlException, found);
        Assertions.assertEquals("23505", found.getSQLState());
        Assertions.assertEquals(1, found.getErrorCode());
    }

    @Test
    void getExceptionFromStacktrace_deeplyNestedCauseChain() {
        final SQLException sqlException = new SQLException("Deep nested SQL error", "23000", 2291);
        final RuntimeException inner = new RuntimeException(sqlException);
        final RuntimeException middle = new RuntimeException(inner);
        final RuntimeException outer = new RuntimeException(middle);
        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(outer, SQLException.class, null);
        Assertions.assertSame(sqlException, found);
        Assertions.assertEquals("23000", found.getSQLState());
        Assertions.assertEquals(2291, found.getErrorCode());
    }

    @Test
    void getExceptionFromStacktrace_suppressedException() {
        final SQLException sqlException = new SQLException("Suppressed SQL error");
        final RuntimeException main = new RuntimeException("Main error");
        main.addSuppressed(sqlException);
        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(main, SQLException.class, null);
        Assertions.assertSame(sqlException, found);
    }

    @Test
    void getExceptionFromStacktrace_notFoundReturnsDefault() {
        final RuntimeException ex = new RuntimeException("No SQL here");
        final SQLException defaultValue = new SQLException("Default fallback");
        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(ex, SQLException.class, defaultValue);
        Assertions.assertSame(defaultValue, found);
    }

    @Test
    void getExceptionFromStacktrace_hibernateStyleWrapping() {
        // Simulates HibernateException -> JDBCException -> SQLException chain
        final SQLException sqlException = new SQLException("ORA-00001: unique constraint violated", "23000", 1);
        final RuntimeException jdbcException = new RuntimeException("Could not execute statement", sqlException);
        final RuntimeException hibernateException = new RuntimeException("Hibernate operation failed", jdbcException);

        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(hibernateException, SQLException.class, null);
        Assertions.assertSame(sqlException, found);
        Assertions.assertEquals("23000", found.getSQLState());
        Assertions.assertEquals(1, found.getErrorCode());
        Assertions.assertTrue(found.getMessage().contains("ORA-00001"));
    }

    @Test
    void getExceptionFromStacktrace_nullCause() {
        final RuntimeException ex = new RuntimeException("No cause");
        final SQLException defaultValue = new SQLException("Default");
        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(ex, SQLException.class, defaultValue);
        Assertions.assertSame(defaultValue, found);
    }

    @Test
    void getExceptionFromStacktrace_nullException() {
        final SQLException defaultValue = new SQLException("Default");
        final SQLException found = ExceptionUtil.getExceptionFromStacktrace(null, SQLException.class, defaultValue);
        Assertions.assertSame(defaultValue, found);
    }
}