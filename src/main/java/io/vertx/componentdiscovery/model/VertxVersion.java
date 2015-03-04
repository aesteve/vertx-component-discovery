package io.vertx.componentdiscovery.model;

public enum VertxVersion {
    V2("mod"), V3("vertx");

    private String mvnClassifier;

    private VertxVersion(String mvnClassifier) {
        this.mvnClassifier = mvnClassifier;
    }

    public String mvnClassifier() {
        return mvnClassifier;
    }

}
