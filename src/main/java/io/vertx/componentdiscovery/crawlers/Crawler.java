package io.vertx.componentdiscovery.crawlers;

import io.vertx.componentdiscovery.crawlers.impl.MavenCentralCrawler;
import io.vertx.componentdiscovery.model.ScanReport;
import io.vertx.componentdiscovery.model.VertxVersion;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.function.Consumer;

public interface Crawler {
    /**
     * TODO : read config and find the right type of crawler to instantiate
     * 
     * @param config
     * @return
     */
    public static Crawler fromConfig(JsonObject config, Vertx vertx) {
        return new MavenCentralCrawler(vertx, VertxVersion.V2);
    }

    void scan(Consumer<ScanReport> handler);

    // TODO : should we add other methods ? Or organize it diferrently so that crawlers can share some common behaviour ?
    // It could be interesting to factorize some code (e.g. pagination, httpClient creating, ... between crawlers)

}
