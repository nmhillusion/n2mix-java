package tech.nmhillusion.n2mix.helper.log;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

class LogHelperTest {
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
}