package io.vertx.componentdiscovery;

import io.vertx.componentdiscovery.impl.DiscoveryServiceImpl;
import io.vertx.componentdiscovery.model.FullScanReport;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface DiscoveryService {
    static DiscoveryService create(Vertx vertx, JsonObject config) {
        return new DiscoveryServiceImpl(vertx, config);
    }

    void start(Handler<AsyncResult<Void>> handler);

    void stop(Handler<AsyncResult<Void>> handler);

    /**
     * Crawls all repositories configured in config file and scans artifacts matching
     */
    void crawl(Handler<AsyncResult<FullScanReport>> handler);
}
