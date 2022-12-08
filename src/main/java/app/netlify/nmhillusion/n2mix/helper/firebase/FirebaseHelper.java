package app.netlify.nmhillusion.n2mix.helper.firebase;

import app.netlify.nmhillusion.n2mix.exception.GeneralException;
import app.netlify.nmhillusion.n2mix.helper.log.LogHelper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.jetbrains.annotations.NotNull;

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
    private static final int MAX_WAITING_TIME = 60_000;
    private static volatile boolean isUsing = false;
    private final FirebaseConfig firebaseConfig;
    private Optional<FirebaseApp> firebaseAppOpt;

    public FirebaseHelper(@NotNull FirebaseConfig firebaseConfig) throws IOException, GeneralException {
        this.firebaseConfig = firebaseConfig;
        if (isEnable()) {
            long startTime = System.currentTimeMillis();
            while (isUsing && MAX_WAITING_TIME > System.currentTimeMillis() - startTime) {
                // waiting for another using of firebase
            }

            if (isUsing) {
                throw new GeneralException("Timeout for waiting another using of firebase app");
            }

            initializeFirebase();
        } else {
            throw new GeneralException("Not enable firebase");
        }
    }

    private synchronized void instanceFirebase() throws IOException {
        try {
            this.firebaseAppOpt = Optional.of(FirebaseApp.getInstance());
        } catch (IllegalStateException ex) {
            LogHelper.getLog(this).error("Cannot get instance of firebase: IllegalStateException : " + ex.getMessage());
            initializeFirebase();
        }
    }

    private synchronized void initializeFirebase() throws IOException {
        final String serviceAccountPath = firebaseConfig.getServiceAccountConfig().getCredentialFilePath();
        final String serviceAccountProjectId = firebaseConfig.getServiceAccountConfig().getProjectId();

        getLog(this).debugFormat("==> serviceAccountPath = %s", serviceAccountPath);
        getLog(this).debugFormat("==> serviceAccountProjectId = %s", serviceAccountProjectId);

        getLog(this).info("Initializing Firebase >>");
        try (final InputStream serviceAccInputStream = Files.newInputStream(new File(serviceAccountPath).toPath())) {
            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccInputStream))
                    .setProjectId(serviceAccountProjectId)
                    .setConnectTimeout(60_000)
                    .setReadTimeout(60_000)
                    .build();
            this.firebaseAppOpt = Optional.of(FirebaseApp.initializeApp(options));
            getLog(this).info("<< Initializing Firebase Success: " + firebaseAppOpt);
        }
    }

    public boolean isEnable() {
        return firebaseConfig.getEnable();
    }

    public Optional<Firestore> getFirestore() throws IOException {
        if (isEnable() && firebaseAppOpt.isPresent()) {
            return Optional.of(FirestoreClient.getFirestore(firebaseAppOpt.get()));
        }
        return Optional.empty();
    }

    public Optional<StorageClient> getStorage() throws IOException {
        if (isEnable() && firebaseAppOpt.isPresent()) {
            return Optional.of(StorageClient.getInstance(firebaseAppOpt.get()));
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

        firebaseAppOpt.ifPresent(FirebaseApp::delete);

        isUsing = false;
        getLog(this).info("close FirebaseApp");
    }
}
