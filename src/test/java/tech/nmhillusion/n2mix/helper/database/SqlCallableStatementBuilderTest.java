package tech.nmhillusion.n2mix.helper.database;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseHelper;
import tech.nmhillusion.n2mix.helper.database.query.SqlCallableStatementBuilder;
import tech.nmhillusion.n2mix.helper.database.query.StatementExecutor;
import tech.nmhillusion.n2mix.helper.database.result.ResultSetObjectBuilder;
import tech.nmhillusion.n2mix.helper.database.result.WorkingResultSetHelper;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.model.DocumentEntity;
import tech.nmhillusion.n2mix.type.Pair;
import tech.nmhillusion.n2mix.validator.StringValidator;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getDataSourceAndSessionFactory;
import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getGitHubRunId;
import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: chubb
 * <p>
 * created date: 2023-12-02
 */
public class SqlCallableStatementBuilderTest {
    private static boolean isGitHubAction;

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

    private long createDocument(StatementExecutor statementExecutor, String title, String insertBy) throws SQLException {
        return new SqlCallableStatementBuilder(statementExecutor)
                .setFunctionName("pkg_document_utils.f_insert_new_document")
                .setReturnType(Types.NUMERIC)
                .addArgument("i_title", title)
                .addArgument("i_insert_by", insertBy)
                .buildReturning(callableStatement -> {
                    callableStatement.execute();
                    return callableStatement.getLong(StatementExecutor.RESULT_PARAM_NAME);
                });
    }

    private void deleteDocument(StatementExecutor statementExecutor, String docId) throws SQLException {
        new SqlCallableStatementBuilder(statementExecutor)
                .setProcedureName("pkg_document_utils.p_delete_document")
                .addArgument("i_doc_id", docId)
                .build(PreparedStatement::execute);
    }

    private DocumentEntity getDocumentById(StatementExecutor conn, String docId) throws SQLException {
        return new SqlCallableStatementBuilder(conn)
                .setFunctionName("pkg_document_utils.f_get_document_by_id")
                .setReturnType(Types.REF_CURSOR)
                .addArgument("i_doc_id", docId)
                .buildReturning(callableStatement -> {
                    callableStatement.execute();

                    try {
                        return new WorkingResultSetHelper()
                                .setCallableStatement(callableStatement)
                                .setOutParameterNameOfResultSet(StatementExecutor.RESULT_PARAM_NAME)
                                .doReturningWorkOnResultSet(resultSet -> {
                                    if (resultSet.next()) {
                                        return new ResultSetObjectBuilder(resultSet)
                                                .buildCurrent(DocumentEntity.class);
                                    } else {
                                        throw new SQLException("Missing value of ResultSet");
                                    }
                                });
                    } catch (Throwable ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    @Test
    void testCreateAndDeleteDocument() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final var dbWorker = databaseHelper.getExecutor();

                dbWorker.doWork(statementExecutor -> {
                    var createdId = createDocument(statementExecutor, "Temp Document", "root");

                    getLogger(this).info("created doc id = " + createdId);
                    var createdDocument = getDocumentById(statementExecutor, String.valueOf(createdId));

                    LogHelper.getLogger(this).info("created document = " + createdDocument);

                    deleteDocument(statementExecutor, String.valueOf(createdId));
                });
            }
        });
    }

    @Test
    void testGetDocumentById() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final var dbWorker = databaseHelper.getExecutor();

                var result = dbWorker.doReturningWork(conn -> getDocumentById(conn, "1"));

                Assertions.assertNotNull(result);
                getLogger(this).info("load data for document: %s".formatted(result));
            }
        });
    }
}
