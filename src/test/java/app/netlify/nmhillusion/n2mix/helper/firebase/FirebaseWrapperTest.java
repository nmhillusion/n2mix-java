package app.netlify.nmhillusion.n2mix.helper.firebase;

import app.netlify.nmhillusion.n2mix.helper.YamlReader;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FirebaseWrapperTest {

    private String getFirebaseConfig(String key) throws IOException {
        try (final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("app-config/firebase.yml")) {
            return new YamlReader(resourceAsStream).getProperty(key);
        }
    }

    @Test
    void runWithWrapper() {
        assertDoesNotThrow(() -> {
            FirebaseWrapper firebaseWrapper = new FirebaseWrapper();
            firebaseWrapper.setFirebaseConfig(new FirebaseConfig()
                    .setEnable(true)
                    .setServiceAccountConfig(new FirebaseConfig.ServiceAccountConfig()
                            .setProjectId(getFirebaseConfig("service-account.project-id"))
                            .setCredentialFilePath(getFirebaseConfig("service-account.path"))
                    )
            ).runWithWrapper(firebaseHelper -> {
                final Optional<Firestore> firestore = firebaseHelper.getFirestore();
                if (firestore.isPresent()) {
                    final Iterable<CollectionReference> collectionReferences = firestore.get().listCollections();
                    collectionReferences.forEach(collectionReference -> {
                        System.out.println("col: " + collectionReference.getId());
                    });
                }
            });
        }, "run firebase app with wrapper");
    }
}