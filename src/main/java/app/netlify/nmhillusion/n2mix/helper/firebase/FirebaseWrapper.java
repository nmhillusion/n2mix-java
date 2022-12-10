package app.netlify.nmhillusion.n2mix.helper.firebase;

import app.netlify.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

/**
 * date: 2022-12-09
 * <p>
 * created-by: nmhillusion
 */

public class FirebaseWrapper {
    private static final FirebaseWrapper instance = new FirebaseWrapper();
    private static FirebaseConfig firebaseConfig;

    private FirebaseWrapper() {
    }

    public static FirebaseWrapper getInstance() {
        return instance;
    }

    public static void setFirebaseConfig(FirebaseConfig firebaseConfig) {
        FirebaseWrapper.firebaseConfig = firebaseConfig;
    }

    public synchronized void runWithWrapper(ThrowableVoidFunction<FirebaseHelper> funcWithFirebase) throws Throwable {
        if (null == firebaseConfig) {
            throw new IllegalArgumentException("Missing firebaseConfig value");
        }

        try (FirebaseHelper firebaseHelper = new FirebaseHelper(firebaseConfig)) {
            if (!firebaseHelper.isEnable()) {
                return;
            }

            funcWithFirebase.throwableVoidApply(firebaseHelper);
        }
    }
}
