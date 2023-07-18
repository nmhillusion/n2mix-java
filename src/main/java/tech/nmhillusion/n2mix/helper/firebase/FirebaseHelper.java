package tech.nmhillusion.n2mix.helper.firebase;

import tech.nmhillusion.n2mix.exception.GeneralException;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
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

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * date: 2022-09-25
 * <p>
 * created-by: nmhillusion
 */
public class FirebaseHelper implements AutoCloseable {
    private static final int MAX_WAITING_TIME = 60_000;
    private static volatile boolean isUsing = false;
    private final FirebaseConfig firebaseConfig;
    private FirebaseApp firebaseApp = null;

    FirebaseHelper(@NotNull FirebaseConfig firebaseConfig) throws IOException, GeneralException {
        this.firebaseConfig = firebaseConfig;
        if (isEnable()) {
            long startTime = System.currentTimeMillis();
            while (isUsing && MAX_WAITING_TIME > System.currentTimeMillis() - startTime) {
                // waiting for another using of firebase
            }

            if (isUsing) {
                throw new GeneralException("Timeout for waiting another using of firebase app");
            }
            isUsing = true;

            initializeFirebase();
        } else {
            throw new GeneralException("Not enable firebase");
        }
    }

    private synchronized void instanceFirebase() throws IOException {
        try {
            this.firebaseApp = FirebaseApp.getInstance();
        } catch (IllegalStateException ex) {
            LogHelper.getLogger(this).error("Cannot get instance of firebase: IllegalStateException : " + ex.getMessage());
            initializeFirebase();
        }
    }

    private synchronized void initializeFirebase() throws IOException {
        final String serviceAccountPath = firebaseConfig.getServiceAccountConfig().getCredentialFilePath();
        final String serviceAccountProjectId = firebaseConfig.getServiceAccountConfig().getProjectId();

        LogHelper.getLogger(this).debugFormat("==> serviceAccountPath = %s", serviceAccountPath);
        LogHelper.getLogger(this).debugFormat("==> serviceAccountProjectId = %s", serviceAccountProjectId);

        LogHelper.getLogger(this).info("Initializing Firebase >>");
        try (final InputStream serviceAccInputStream = Files.newInputStream(new File(serviceAccountPath).toPath())) {
            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccInputStream))
                    .setProjectId(serviceAccountProjectId)
                    .setConnectTimeout(60_000)
                    .setReadTimeout(60_000)
                    .build();
            this.firebaseApp = FirebaseApp.initializeApp(options);
            LogHelper.getLogger(this).info("<< Initializing Firebase Success: " + firebaseApp);
        }
    }

    public boolean isEnable() {
        return firebaseConfig.getEnable();
    }

    public Optional<Firestore> getFirestore() throws IOException {
        if (isEnable() && null != firebaseApp) {
            return Optional.of(FirestoreClient.getFirestore(firebaseApp));
        }
        return Optional.empty();
    }

    public Optional<StorageClient> getStorage() throws IOException {
        if (isEnable() && null != firebaseApp) {
            return Optional.of(StorageClient.getInstance(firebaseApp));
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
                LogHelper.getLogger(this).error(ex);
            }
        });

        if (null != firebaseApp) {
            firebaseApp.delete();
        }

        isUsing = false;
        LogHelper.getLogger(this).info("close FirebaseApp");
    }
}
