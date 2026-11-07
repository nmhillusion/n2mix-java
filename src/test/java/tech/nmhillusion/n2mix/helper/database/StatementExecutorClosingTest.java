package tech.nmhillusion.n2mix.helper.database;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.jdbc.ReturningWork;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that every statement created on the Hibernate path is closed before
 * the executor call returns, on both success and failure paths.
 * Uses JDK {@link java.lang.reflect.Proxy} fakes — no database, no extra dependency.
 */
class StatementExecutorClosingTest {

    private interface Tracker {
        void mark(String event);
        void clear();
        List<String> events();
    }

    private static final Tracker tracker = new Tracker() {
        final List<String> events = new ArrayList<>();

        @Override public void mark(String event) { events.add(event); }
        @Override public void clear() { events.clear(); }
        @Override public List<String> events() { return new ArrayList<>(events); }
    };

    @BeforeEach void resetTracker() { tracker.clear(); }

    @AfterEach void verifyNoSurprise() {
        List<String> events = tracker.events();
        // The executor closes StatementExecutor after every call, so we should
        // see exactly one statement close per method call in the success path.
        // Failure path tests do their own assertions.
    }

    /** Creates a fake {@link Session} whose doWork/doReturningWork invoke the
     * supplied Work/ReturningWork with a fake {@link Connection}. */
    @SuppressWarnings("unchecked")
    private static Session fakeSession() {
        return (Session) Proxy.newProxyInstance(
                StatementExecutorClosingTest.class.getClassLoader(),
                new Class<?>[]{Session.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("doWork".equals(name)) {
                        Work work = (Work) args[0];
                        work.execute(fakeConnection());
                        return null;
                    }
                    if ("doReturningWork".equals(name)) {
                        ReturningWork<?> rw = (ReturningWork<?>) args[0];
                        return rw.execute(fakeConnection());
                    }
                    if ("close".equals(name)) { return null; }
                    return method.getReturnType().isPrimitive() ? 0 : null;
                });
    }

    /** Creates a fake {@link Connection} whose prepareStatement/prepareCall
     * return a fake statement that records close(). */
    private static Connection fakeConnection() {
        return (Connection) Proxy.newProxyInstance(
                StatementExecutorClosingTest.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("prepareStatement".equals(name)) {
                        return fakePreparedStatement();
                    }
                    if ("prepareCall".equals(name)) {
                        return fakeCallableStatement();
                    }
                    if ("close".equals(name)) { return null; }
                    return method.getReturnType().isPrimitive() ? 0 : null;
                });
    }

    private static PreparedStatement fakePreparedStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                StatementExecutorClosingTest.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class},
                new TrackingHandler("PreparedStatement"));
    }

    private static CallableStatement fakeCallableStatement() {
        return (CallableStatement) Proxy.newProxyInstance(
                StatementExecutorClosingTest.class.getClassLoader(),
                new Class<?>[]{CallableStatement.class},
                new TrackingHandler("CallableStatement"));
    }

    private static class TrackingHandler implements InvocationHandler {
        private final String type;
        TrackingHandler(String type) { this.type = type; }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("close".equals(name)) {
                tracker.mark(type + ".close");
                return null;
            }
            if ("registerOutParameter".equals(name)) { return null; }
            if ("execute".equals(name) || "executeQuery".equals(name) || "executeUpdate".equals(name)) {
                return method.getReturnType().isPrimitive() ? 0 : null;
            }
            return method.getReturnType().isPrimitive() ? 0 : null;
        }
    }

    // --- 8 success-path tests ------------------------------------------------

    @Test void doPreparedStatement_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doPreparedStatement("select 1", ps -> {});
        assertTrue(tracker.events().contains("PreparedStatement.close"),
                "PreparedStatement.close was not called; events: " + tracker.events());
    }

    @Test void doReturningPreparedStatement_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doReturningPreparedStatement("select 1", ps -> "ok");
        assertTrue(tracker.events().contains("PreparedStatement.close"),
                "PreparedStatement.close was not called; events: " + tracker.events());
    }

    @Test void doReturningCallableStatement_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doReturningCallableStatement("func()", java.sql.Types.INTEGER, cs -> 1);
        assertTrue(tracker.events().contains("CallableStatement.close"),
                "CallableStatement.close was not called; events: " + tracker.events());
    }

    @Test void doCallableStatement_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doCallableStatement("proc()", java.sql.Types.INTEGER, cs -> {});
        assertTrue(tracker.events().contains("CallableStatement.close"),
                "CallableStatement.close was not called; events: " + tracker.events());
    }

    @Test void doReturningPureCallableStatement_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doReturningPureCallableStatement("func()", cs -> 1);
        assertTrue(tracker.events().contains("CallableStatement.close"),
                "CallableStatement.close was not called; events: " + tracker.events());
    }

    @Test void doPureCallableStatement_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doPureCallableStatement("proc()", cs -> {});
        assertTrue(tracker.events().contains("CallableStatement.close"),
                "CallableStatement.close was not called; events: " + tracker.events());
    }

    @Test void doReturningCallableStatementNamed_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doReturningCallableStatementNamed("func()", java.sql.Types.INTEGER, cs -> 1);
        assertTrue(tracker.events().contains("CallableStatement.close"),
                "CallableStatement.close was not called; events: " + tracker.events());
    }

    @Test void doCallableStatementNamed_closesStatement() throws Exception {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        se.doCallableStatementNamed("proc()", java.sql.Types.INTEGER, cs -> {});
        assertTrue(tracker.events().contains("CallableStatement.close"),
                "CallableStatement.close was not called; events: " + tracker.events());
    }

    // --- 1 failure-path test -------------------------------------------------

    @Test void doPreparedStatement_closesStatementEvenWhenCallbackThrows() {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        SQLException thrown = assertThrows(SQLException.class, () ->
                se.doPreparedStatement("select 1", ps -> { throw new RuntimeException("boom"); }));
        assertTrue(tracker.events().contains("PreparedStatement.close"),
                "PreparedStatement.close was not called even though callback threw; events: " + tracker.events());
        assertTrue(thrown.getMessage().contains("boom") || thrown.getCause() != null,
                "Original exception should surface");
    }

    // --- E7: Constructor null validation ------------------------------------

    @Test void constructor_nullSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor((Session) null));
    }

    @Test void constructor_nullJdbcTemplate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(
                        (org.springframework.jdbc.core.JdbcTemplate) null));
    }

    // --- E6: Error instances are rethrown unchanged --------------------------

    @Test void doPreparedStatement_Error_rethrownUnchanged() {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        OutOfMemoryError thrown = assertThrows(OutOfMemoryError.class, () ->
                se.doPreparedStatement("select 1", ps -> { throw new OutOfMemoryError("OOM"); }));
        assertEquals("OOM", thrown.getMessage());
    }

    @Test void doReturningPreparedStatement_Error_rethrownUnchanged() {
        tech.nmhillusion.n2mix.helper.database.query.StatementExecutor se =
                new tech.nmhillusion.n2mix.helper.database.query.StatementExecutor(fakeSession());
        StackOverflowError thrown = assertThrows(StackOverflowError.class, () ->
                se.doReturningPreparedStatement("select 1", ps -> { throw new StackOverflowError("SO"); }));
        assertEquals("SO", thrown.getMessage());
    }
}