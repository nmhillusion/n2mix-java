package tech.nmhillusion.n2mix.helper.log;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.constant.LogLevel;

import java.sql.SQLException;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

class LogHelperTest {

    @BeforeAll
    static void setupLogger() {
        final tech.nmhillusion.pi_logger.model.LogConfigModel updatedConfig = LogHelper.getDefaultPiLoggerConfig()
                .setLogLevel(LogLevel.TRACE);
        LogHelper.setDefaultPiLoggerConfig(
                updatedConfig
        )
        ;
    }

    void throwExceptionFunc() throws SQLException {
        throw new SQLException("Invalid column name.");
    }

    @Test
    void testWithException() {
        try {
            throwExceptionFunc();
        } catch (Throwable ex) {
            getLogger(this).error(ex);
        }
    }

    @Test
    void testInfo() {
        getLogger(this).info("test info log");

        getLogger(this).info("hello, {}, {} is a teacher.", "Jim", "Jim");

        getLogger(this).info("hello, {1}, {0} is a teacher.", "Jim", "Morgan");
    }

    @Test
    void testWarn() {
        getLogger(this).warn("test warn log");
    }

    @Test
    void testError() {
        getLogger(this).error("test error log");
    }

    @Test
    void testTrace() {
        getLogger(this).trace("test trace log");
    }
}