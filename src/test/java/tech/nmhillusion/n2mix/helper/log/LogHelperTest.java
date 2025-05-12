package tech.nmhillusion.n2mix.helper.log;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.type.ChainList;
import tech.nmhillusion.n2mix.type.ChainMap;
import tech.nmhillusion.pi_logger.constant.LogLevel;

import java.sql.SQLException;
import java.util.Map;

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

    @Test
    void testWithDoubleBrackets() {
        final StateModel state1 = new StateModel()
                .setName("Lisa")
                .setTitle("Manager")
                .setItems(new ChainList<Item>()
                        .chainAdd(new Item().setName("name").setValue("Lisa"))
                        .chainAdd(new Item().setName("title").setValue("Manager"))
                );
        final Map<String, ?> state2 = new ChainMap<String, Object>()
                .chainPut("name", "Johnson")
                .chainPut("title", "Employee");
        getLogger(this).info("execution state1 = {}, state2 = {}", state1, state2);
    }

    @Test
    void testWithComplexText() {
        final String text = """
                ExecutionState{listeners=[tech.nmhillusion.jParrotDataSelectorApp.Main$$Lambda/0x000001d1be013640@48e62fd3], datasourceModel=mMySQL, sqlData=SELECT * FROM t_user;
                SELECT * FROM t_user tu\s
                where tu.enabled = 1
                order by tu.username
                ;}
                """;

        getLogger(this).info("exec state: {}", text);
    }
}