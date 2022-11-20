package app.netlify.nmhillusion.n2mix.helper.firebase;

import app.netlify.nmhillusion.n2mix.helper.YamlReader;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLog;

/**
 * date: 2022-09-25
 * <p>
 * created-by: nmhillusion
 */
public class FirebaseHelper implements AutoCloseable {
    private Optional<FirebaseApp> firebaseApp;

    private static <T> T getConfig(String configKey, Class<T> classToCast) throws IOException {
        try (final InputStream firebaseConfig = FirebaseHelper.class.getClassLoader().getResourceAsStream("app-config/firebase.yml")) {
            final YamlReader yamlReader = new YamlReader(firebaseConfig);
            return yamlReader.getProperty(configKey, classToCast);
        }
    }

    public static boolean isEnable() {
        try {
            return getConfig("config.enable", Boolean.class);
        } catch (Exception ex) {
            getLog(FirebaseHelper.class).error(ex);
            return false;
        }
    }

    public Optional<FirebaseHelper> newsInstance() throws IOException {
        if (isEnable()) {
            final String serviceAccountPath = getConfig("service-account.path", String.class);
            final String serviceAccountProjectId = getConfig("service-account.project-id", String.class);

            getLog(this).infoFormat("==> serviceAccountPath = %s", serviceAccountPath);
            getLog(this).infoFormat("==> serviceAccountProjectId = %s", serviceAccountProjectId);

            getLog(this).info("Initializing Firebase >>");
            try (final InputStream serviceAccInputStream = Files.newInputStream(new File(serviceAccountPath).toPath())) {
                final FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccInputStream))
                        .setProjectId(serviceAccountProjectId)
                        .build();
                this.firebaseApp = Optional.of(FirebaseApp.initializeApp(options));
                getLog(this).info("<< Initializing Firebase Success: " + firebaseApp);
                return Optional.of(this);
            }
        } else {
            getLog(this).error("Not enable firebase");
        }
        return Optional.empty();
    }

    public Optional<Firestore> getFirestore() throws IOException {
        if (isEnable() && firebaseApp.isPresent()) {
            return Optional.of(FirestoreClient.getFirestore(firebaseApp.get()));
        }
        return Optional.empty();
    }

    @Override
    public void close() throws IOException {
        getFirestore().ifPresent(_firestore -> {
            try {
                _firestore.close();
                _firestore.shutdown();
            } catch (Throwable ex) {
                getLog(this).error(ex);
            }
        });

        firebaseApp.ifPresent(FirebaseApp::delete);
    }
}
