package tech.nmhillusion.n2mix.helper.firebase;

import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;

import java.util.concurrent.CopyOnWriteArrayList;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * date: 2022-12-09
 * <p>
 * created-by: nmhillusion
 */

public class FirebaseWrapper {
    private static final FirebaseWrapper instance = new FirebaseWrapper();
    private static FirebaseConfig firebaseConfig;
    private final CopyOnWriteArrayList<ThrowableVoidFunction<FirebaseHelper>> jobsQueue = new CopyOnWriteArrayList<>();
    private volatile boolean isRunning = false;

    private FirebaseWrapper() {
    }

    public static FirebaseWrapper getInstance() {
        return instance;
    }

    public static void setFirebaseConfig(FirebaseConfig firebaseConfig) {
        FirebaseWrapper.firebaseConfig = firebaseConfig;
    }

    public void runWithWrapper(ThrowableVoidFunction<FirebaseHelper> funcWithFirebase) throws Throwable {
        if (null == firebaseConfig) {
            throw new IllegalArgumentException("Missing firebaseConfig value");
        }

        jobsQueue.add(funcWithFirebase);
        triggerWorker();
    }

    private void triggerWorker() throws Throwable {
        if (isRunning) {
            LogHelper.getLogger(this).debug("no need to trigger, because queue is running now");
            return;
        }

        isRunning = true;

        try (FirebaseHelper firebaseHelper = new FirebaseHelper(firebaseConfig)) {
            if (!firebaseHelper.isEnable()) {
                return;
            }

            while (!jobsQueue.isEmpty()) {
                LogHelper.getLogger(this).debug("current job queue size: " + jobsQueue.size());

                final ThrowableVoidFunction<FirebaseHelper> firstJob = jobsQueue.remove(0);
                firstJob.throwableVoidApply(firebaseHelper);
            }
        } finally {
            isRunning = false;
        }
    }
}
