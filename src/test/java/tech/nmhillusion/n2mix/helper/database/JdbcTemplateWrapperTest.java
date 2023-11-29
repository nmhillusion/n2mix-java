package tech.nmhillusion.n2mix.helper.database;

import org.junit.jupiter.api.BeforeAll;
import tech.nmhillusion.n2mix.validator.StringValidator;

import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getGitHubRunId;
import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-11-29
 */
public class JdbcTemplateWrapperTest {
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

//    @Test
//    void testSimple() {
//        assumeFalse(isGitHubAction);
//
//        Assertions.assertDoesNotThrow(() -> {
//            final Pair<DataSource, SessionFactory> databaseData_ = getSessionFactory();
//            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
//                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
//                final DatabaseWorker dbWorker = databaseHelper.getWorker();
//
//                dbWorker.doWork(connectionWrapper -> {
//                    try (final PreparedStatement preparedStatement_ = connectionWrapper.buildPreparedStatement("""
//                              select * from t_document
//                               where id in (1, 2, 3)
//                               order by id asc
//                            """)) {
//                        try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
//                            final List<DocumentEntity> documentList_ = new ResultSetObjectBuilder(resultSet)
//                                    .buildList(DocumentEntity.class);
//
//                            LogHelper.getLogger(this).info("document list: " + documentList_);
//                        }
//                    }
//                });
//            }
//        });
//    }
}
