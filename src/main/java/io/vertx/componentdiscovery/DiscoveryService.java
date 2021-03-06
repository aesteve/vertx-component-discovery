package io.vertx.componentdiscovery;

import io.vertx.componentdiscovery.impl.DiscoveryServiceImpl;
import io.vertx.componentdiscovery.model.TaskReport;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;

public interface DiscoveryService {
	static DiscoveryService create(Vertx vertx, JsonArray crawlers) {
		return new DiscoveryServiceImpl(vertx, crawlers);
	}

	void start(Handler<AsyncResult<Void>> handler);

	void stop(Handler<AsyncResult<Void>> handler);

	/**
	 * Crawls all repositories configured in config file and scans artifacts matching
	 */
	void crawl(Handler<AsyncResult<TaskReport>> handler);
}
