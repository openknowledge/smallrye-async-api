package io.smallrye.asyncapi.core.runtime;

public enum AsyncApiFormat {
    JSON("application/json"),
    YAML("application/yaml");

    private final String mimeType;

    AsyncApiFormat(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
