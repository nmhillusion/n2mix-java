package tech.nmhillusion.n2mix.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.exception.AppRuntimeException;
import tech.nmhillusion.n2mix.helper.log.LogHelper;

import java.io.FileNotFoundException;

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
    }

    private void throwExceptionMethod() throws FileNotFoundException {
        throw new FileNotFoundException("Cannot find the file with name: data.txt");
    }

    @Test
    void convertThrowableToString() {
        try {
            throwExceptionMethod();
        } catch (Throwable ex) {
            Assertions.assertDoesNotThrow(() -> {
                final String stackTraceContent = ExceptionUtil.convertThrowableToString(ex);
                LogHelper.getLogger(this).error("Error Stacktrace: " + stackTraceContent);
                Assertions.assertNotNull(stackTraceContent);
                Assertions.assertFalse(stackTraceContent.isEmpty());
            });
        }
    }
}