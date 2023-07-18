package tech.nmhillusion.n2mix.helper.firebase;

import tech.nmhillusion.n2mix.helper.YamlReader;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FirebaseWrapperTest {
    private static boolean ableToTest = true;

    private static String getFirebaseConfig(String key) throws IOException {
        try (final InputStream resourceAsStream = FirebaseWrapperTest.class.getClassLoader().getResourceAsStream("app-config/firebase.yml")) {
            return new YamlReader(resourceAsStream).getProperty(key);
        }
    }

    @BeforeAll
    static void init() throws IOException {
        LogHelper.getLogger(FirebaseWrapperTest.class).debug("init for firebase wrapper");
        final String credentialFilePath = getFirebaseConfig("service-account.path");

        if (!new File(credentialFilePath).exists()) {
            LogHelper.getLogger(FirebaseWrapperTest.class).warn("Don't run this test because this environment does not have firebase service-account.path");
            ableToTest = false;
            return;
        }

        FirebaseWrapper.setFirebaseConfig(new FirebaseConfig()
                .setEnable(true)
                .setServiceAccountConfig(new FirebaseConfig.ServiceAccountConfig()
                        .setProjectId(getFirebaseConfig("service-account.project-id"))
                        .setCredentialFilePath(credentialFilePath)
                )
        );
    }

    @Test
    void runWithWrapper() {
        assumeTrue(ableToTest);

        assertDoesNotThrow(() -> {
            FirebaseWrapper firebaseWrapper = FirebaseWrapper.getInstance();

            firebaseWrapper.runWithWrapper(firebaseHelper -> {
                LogHelper.getLogger(this).info("running in wrapper: " + Thread.currentThread().getName());
                final Optional<Firestore> firestore = firebaseHelper.getFirestore();
                if (firestore.isPresent()) {
                    final Iterable<CollectionReference> collectionReferences = firestore.get().listCollections();
                    collectionReferences.forEach(collectionReference -> {
                        LogHelper.getLogger(this).info("col: " + collectionReference.getId());
                    });
                }
            });
        }, "run firebase app with wrapper");
    }

    @Test
    void runWithWrapperInThreads() {
        assumeTrue(ableToTest);

        assertDoesNotThrow(() -> {
            final List<Thread> threadList = new ArrayList<>();
            for (int thIdx = 0; thIdx < 10; thIdx++) {
                threadList.add(new Thread(this::runWithWrapper, "Thread FB #" + thIdx));
            }

            for (Thread th : threadList) {
                th.start();
            }

            for (Thread th : threadList) {
                th.join();
            }

        }, "run firebase app with wrapper in threads");
    }
}