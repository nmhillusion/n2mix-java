package tech.nmhillusion.n2mix.helper.database;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.database.query.ConnectionWrapper;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseHelper;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseWorker;
import tech.nmhillusion.n2mix.helper.database.query.SqlCallableStatementBuilder;
import tech.nmhillusion.n2mix.helper.database.result.WorkingResultSetHelper;
import tech.nmhillusion.n2mix.type.Pair;
import tech.nmhillusion.n2mix.validator.StringValidator;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getGitHubRunId;
import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getSessionFactory;
import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-11-08
 */
public class TestDumpData {
    private static boolean isGitHubAction;
    private volatile boolean finished;

    @BeforeAll
    static void init() {
        final String gitHubRunId = getGitHubRunId();
        isGitHubAction = !StringValidator.isBlank(gitHubRunId);

        if (isGitHubAction) {
            getLogger(ConnectionDbTest.class).warn("Ignore this test, because there is no Oracle database in github action!");
        } else {
            getLogger(ConnectionDbTest.class).info("Localhost Environment ==> Running testing database");
        }
    }

    private void insertMassData(Executor executor, DatabaseWorker databaseWorker) {
        final int maxRowCount_ = 1_000_000;
        for (int idx = 0; idx < maxRowCount_; idx++) {
            int finalIdx = idx;
            executor.execute(() -> {
                getLogger(this).info("start insert for " + finalIdx);
                try {
                    databaseWorker.doWork(conn -> {
                        try (final CallableStatement call_ = new SqlCallableStatementBuilder(conn)
                                .setProcedureName("pkg_base_utils.p_insert_dump_data")
                                .addArgument("i_type", Math.random() > 0.5f ? "INFO" : "DEBUG")
                                .addArgument("i_value", Math.round(Math.random() * maxRowCount_))
                                .build()) {
                            call_.execute();

                            if (maxRowCount_ == finalIdx + 1) {
                                finished = true;
                            }
                        }
                    });
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                getLogger(this).info("END insert for " + finalIdx);
            });
        }
    }

    private void massReadData(Executor executor, DatabaseWorker dbWorker) {
        final int maxReadCount_ = 100;
        for (int idx = 0; idx < maxReadCount_; idx++) {
            int finalIdx = idx;
            executor.execute(() -> {
                getLogger(this).info("start insert for " + finalIdx);
                try {
                    dbWorker.doWork(conn -> {
                        try (final CallableStatement call_ = new SqlCallableStatementBuilder(conn)
                                .setFunctionName("pkg_base_utils.f_read_dump_data")
                                .setReturnType(Types.REF_CURSOR)
                                .addArgument("i_type", Math.random() > 0.5f ? "INFO" : "DEBUG")
                                .build()) {
                            call_.execute();

                            new WorkingResultSetHelper()
                                    .setOutParameterNameOfResultSet(ConnectionWrapper.RESULT_PARAM_NAME)
                                    .setCallableStatement(call_)
                                    .doWorkOnResultSet(resultSet -> {
                                        while (resultSet.next()) {
                                            getLogger(this).info(
                                                    "row[" + resultSet.getRow() + "]: " +
                                                            resultSet.getString("type") + " - " +
                                                            resultSet.getLong("value")
                                            );
                                        }
                                    });

                            if (maxReadCount_ == finalIdx + 1) {
                                finished = true;
                            }
                        }
                    });
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                getLogger(this).info("END insert for " + finalIdx);
            });
        }
    }

    @Test
    void testMassData() {
        assumeFalse(isGitHubAction);

        final Executor executor = Executors.newWorkStealingPool();

        Assertions.assertDoesNotThrow(() -> {
            finished = false;
            final Pair<DataSource, SessionFactory> databaseData_ = getSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final DatabaseWorker dbWorker = databaseHelper.getWorker();
//                insertMassData(executor, dbWorker);

                massReadData(executor, dbWorker);

                while (!finished) ;
            }
        });

        System.gc();
    }
}
