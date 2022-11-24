package app.netlify.nmhillusion.n2mix.helper.firebase;

/**
 * date: 2022-11-25
 * <p>
 * created-by: nmhillusion
 */

public class FirebaseConfig {
    private boolean enable;
    private ServiceAccountConfig serviceAccountConfig;

    public boolean getEnable() {
        return enable;
    }

    public FirebaseConfig setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public ServiceAccountConfig getServiceAccountConfig() {
        return serviceAccountConfig;
    }

    public FirebaseConfig setServiceAccountConfig(ServiceAccountConfig serviceAccountConfig) {
        this.serviceAccountConfig = serviceAccountConfig;
        return this;
    }

    public static class ServiceAccountConfig {
        private String projectId;
        private String credentialFilePath;

        public String getProjectId() {
            return projectId;
        }

        public ServiceAccountConfig setProjectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public String getCredentialFilePath() {
            return credentialFilePath;
        }

        public ServiceAccountConfig setCredentialFilePath(String credentialFilePath) {
            this.credentialFilePath = credentialFilePath;
            return this;
        }
    }
}
