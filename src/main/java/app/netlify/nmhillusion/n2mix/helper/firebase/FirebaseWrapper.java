package app.netlify.nmhillusion.n2mix.helper.firebase;

import app.netlify.nmhillusion.n2mix.type.function.ThrowableVoidFunction;
import org.springframework.stereotype.Component;

/**
 * date: 2022-12-09
 * <p>
 * created-by: nmhillusion
 */

@Component
public class FirebaseWrapper {
    private FirebaseConfig firebaseConfig;

    public FirebaseWrapper setFirebaseConfig(FirebaseConfig firebaseConfig) {
        this.firebaseConfig = firebaseConfig;
        return this;
    }

    public void runWithWrapper(ThrowableVoidFunction<FirebaseHelper> funcWithFirebase) throws Throwable {
        if (null == firebaseConfig) {
            throw new IllegalArgumentException("Missing firebaseConfig value");
        }

        try (FirebaseHelper firebaseHelper = new FirebaseHelper(this.firebaseConfig)) {
            if (!firebaseHelper.isEnable()) {
                return;
            }

            funcWithFirebase.throwableVoidApply(firebaseHelper);
        }
    }
}
