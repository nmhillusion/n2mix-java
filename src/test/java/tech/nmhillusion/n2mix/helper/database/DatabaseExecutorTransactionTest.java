package tech.nmhillusion.n2mix.helper.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.TransactionTemplate;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseExecutor;
import tech.nmhillusion.n2mix.helper.database.query.executor.HibernateSessionDatabaseExecutor;
import tech.nmhillusion.n2mix.helper.database.query.executor.JdbcTemplateDatabaseExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the transaction contract of {@link DatabaseExecutor}: one call is one unit of work.
 * Everything here runs against an in-memory H2 database, so it also runs in CI.
 */
class DatabaseExecutorTransactionTest {
    private static final String JDBC_URL = "jdbc:h2:mem:n2mix_e1_tx;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String TABLE = "t_e1_tx";

    private static HikariDataSource dataSource;
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUpAll() {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(JDBC_URL);
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setMaximumPoolSize(4);
        dataSource = new HikariDataSource(hikariConfig);

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.url", JDBC_URL)
                .applySetting("hibernate.connection.username", "sa")
                .applySetting("hibernate.connection.password", "")
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @AfterAll
    static void tearDownAll() {
        if (null != sessionFactory) {
            sessionFactory.close();
        }
        if (null != dataSource) {
            dataSource.close();
        }
    }

    @BeforeEach
    void resetTable() throws SQLException {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            statement.execute("drop table if exists " + TABLE);
            statement.execute("create table " + TABLE + " (id int primary key)");
        }
    }

    private int countRows() {
        final Integer count = new JdbcTemplate(dataSource)
                .queryForObject("select count(*) from " + TABLE, Integer.class);
        return null == count ? 0 : count;
    }

    private static void insert(final DatabaseExecutor executor, final int id) throws Throwable {
        executor.doWork(statementExecutor -> statementExecutor.doPreparedStatement(
                "insert into " + TABLE + "(id) values (" + id + ")",
                preparedStatement -> preparedStatement.execute()));
    }

    private static boolean isCausedBy(Throwable thrown, Throwable expected) {
        Throwable current = thrown;
        for (int depth = 0; null != current && depth < 20; ++depth) {
            if (current == expected) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    @Test
    void jdbcCommitsWhenTheCallbackCompletes() throws Throwable {
        final DatabaseExecutor executor = new JdbcTemplateDatabaseExecutor(dataSource);

        executor.doWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (2)",
                    preparedStatement -> preparedStatement.execute());
        });

        assertEquals(2, countRows());
    }

    @Test
    void jdbcRollsBackWhenTheCallbackFails() {
        final DatabaseExecutor executor = new JdbcTemplateDatabaseExecutor(dataSource);
        final SQLException marker = new SQLException("boom", "42000", 1);

        final Throwable thrown = assertThrows(Throwable.class, () -> executor.doWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            throw marker;
        }));

        assertTrue(isCausedBy(thrown, marker), "the original failure must stay in the cause chain");
        assertEquals(0, countRows(), "the insert must be rolled back");
    }

    @Test
    void hibernateCommitsWhenTheCallbackCompletes() throws Throwable {
        final DatabaseExecutor executor = new HibernateSessionDatabaseExecutor(sessionFactory);

        insert(executor, 1);

        assertEquals(1, countRows());
    }

    @Test
    void hibernateRollsBackWhenTheCallbackFails() {
        final DatabaseExecutor executor = new HibernateSessionDatabaseExecutor(sessionFactory);
        final SQLException marker = new SQLException("boom", "42000", 1);

        final Throwable thrown = assertThrows(Throwable.class, () -> executor.doWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            throw marker;
        }));

        assertTrue(isCausedBy(thrown, marker), "the original failure must stay in the cause chain");
        assertEquals(0, countRows(), "the insert must be rolled back");
    }

    @Test
    void jdbcParticipatesInAnOuterSpringTransaction() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final TransactionTemplate outerTransaction =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));

        assertThrows(UnexpectedRollbackException.class, () -> outerTransaction.executeWithoutResult(status -> {
            jdbcTemplate.update("insert into " + TABLE + "(id) values (1)");

            final DatabaseExecutor executor = new JdbcTemplateDatabaseExecutor(dataSource);
            assertThrows(Throwable.class, () -> executor.doWork(statementExecutor -> {
                statementExecutor.doPreparedStatement(
                        "insert into " + TABLE + "(id) values (2)",
                        preparedStatement -> preparedStatement.execute());
                throw new SQLException("inner failure");
            }));
        }));

        assertEquals(0, countRows(), "the inner failure must mark the shared transaction rollback-only");
    }

    private static void insertUnchecked(final DatabaseExecutor executor, final int id) {
        try {
            insert(executor, id);
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        }
    }

    @Test
    void jdbcReturningWorkCommitsAndReturnsTheCallbackResult() throws Throwable {
        final DatabaseExecutor executor = new JdbcTemplateDatabaseExecutor(dataSource);

        final Integer result = executor.doReturningWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            return 42;
        });

        assertEquals(42, result);
        assertEquals(1, countRows(), "the insert must be committed");
    }

    @Test
    void hibernateReturningWorkCommitsAndReturnsTheCallbackResult() throws Throwable {
        final DatabaseExecutor executor = new HibernateSessionDatabaseExecutor(sessionFactory);

        final Integer result = executor.doReturningWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            return 42;
        });

        assertEquals(42, result);
        assertEquals(1, countRows(), "the insert must be committed");
    }

    @Test
    void jdbcReturningWorkRollsBackWhenTheCallbackFails() {
        final DatabaseExecutor executor = new JdbcTemplateDatabaseExecutor(dataSource);
        final SQLException marker = new SQLException("boom", "42000", 1);

        final Throwable thrown = assertThrows(Throwable.class, () -> executor.doReturningWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            throw marker;
        }));

        assertTrue(isCausedBy(thrown, marker), "the original failure must stay in the cause chain");
        assertEquals(0, countRows(), "the insert must be rolled back");
    }

    @Test
    void hibernateReturningWorkRollsBackWhenTheCallbackFails() {
        final DatabaseExecutor executor = new HibernateSessionDatabaseExecutor(sessionFactory);
        final SQLException marker = new SQLException("boom", "42000", 1);

        final Throwable thrown = assertThrows(Throwable.class, () -> executor.doReturningWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            throw marker;
        }));

        assertTrue(isCausedBy(thrown, marker), "the original failure must stay in the cause chain");
        assertEquals(0, countRows(), "the insert must be rolled back");
    }

    @Test
    void jdbcRollsBackAndPropagatesAnError() {
        final DatabaseExecutor executor = new JdbcTemplateDatabaseExecutor(dataSource);
        final AssertionError marker = new AssertionError("boom");

        final Throwable thrown = assertThrows(Throwable.class, () -> executor.doWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            throw marker;
        }));

        assertSame(marker, thrown, "an Error must propagate unchanged");
        assertEquals(0, countRows(), "the insert must be rolled back");
    }

    @Test
    void hibernateRollsBackAndPropagatesAnError() {
        final DatabaseExecutor executor = new HibernateSessionDatabaseExecutor(sessionFactory);
        final AssertionError marker = new AssertionError("boom");

        final Throwable thrown = assertThrows(Throwable.class, () -> executor.doWork(statementExecutor -> {
            statementExecutor.doPreparedStatement(
                    "insert into " + TABLE + "(id) values (1)",
                    preparedStatement -> preparedStatement.execute());
            throw marker;
        }));

        assertSame(marker, thrown, "an Error must propagate unchanged");
        assertEquals(0, countRows(), "the insert must be rolled back");
    }

    @Test
    void jdbcJoinsAnOuterSpringTransactionAndCommitsWithIt() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final TransactionTemplate outerTransaction =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));

        outerTransaction.executeWithoutResult(status -> {
            jdbcTemplate.update("insert into " + TABLE + "(id) values (1)");

            final DatabaseExecutor executor = new JdbcTemplateDatabaseExecutor(dataSource);
            insertUnchecked(executor, 2);
        });

        assertEquals(2, countRows(), "the inner work must commit together with the outer transaction");
    }

    @Test
    void hibernateWorkDoesNotJoinAnOuterSpringTransaction() {
        final TransactionTemplate outerTransaction =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        final DatabaseExecutor executor = new HibernateSessionDatabaseExecutor(sessionFactory);

        assertThrows(IllegalStateException.class, () -> outerTransaction.executeWithoutResult(status -> {
            insertUnchecked(executor, 1);

            throw new IllegalStateException("roll back the outer transaction");
        }));

        assertEquals(1, countRows(), "the Hibernate insert must commit independently of the outer transaction");
    }
}
