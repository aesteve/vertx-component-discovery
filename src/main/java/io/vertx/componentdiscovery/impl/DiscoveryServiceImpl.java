package io.vertx.componentdiscovery.impl;

import io.vertx.componentdiscovery.DiscoveryService;
import io.vertx.componentdiscovery.crawlers.Crawler;
import io.vertx.componentdiscovery.model.TaskReport;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryServiceImpl implements DiscoveryService {
	private final Vertx vertx;

	private List<Crawler> crawlers;
	private JsonArray configuratedCrawlers;

	public DiscoveryServiceImpl(Vertx vertx, JsonArray configuratedCrawlers) {
		this.vertx = vertx;
		this.configuratedCrawlers = configuratedCrawlers;
		this.crawlers = new ArrayList<Crawler>();
	}

	@Override
	public void start(Handler<AsyncResult<Void>> handler) {
		configuratedCrawlers.forEach(action -> {
			JsonObject mapConfig = (JsonObject) action;
			crawlers.add(Crawler.fromConfig(mapConfig, vertx));
		});
		handler.handle(new VoidResult());
	}

	@Override
	public void stop(Handler<AsyncResult<Void>> handler) {
		crawlers = new ArrayList<Crawler>();
		handler.handle(new VoidResult());
	}

	@Override
	public void crawl(Handler<AsyncResult<TaskReport>> reports) {
		System.out.println("Start crawling");
		TaskReport fullReport = new TaskReport("Components discovery");
		fullReport.start();
		if (crawlers.isEmpty()) {
			fullReport.end();
			reports.handle(new TaskAsyncResult(fullReport));
		}
		crawlers.forEach(crawler -> {
			crawler.scan(singleReport -> {
				fullReport.addTask(singleReport);
				if (fullReport.nbSubTasks() == crawlers.size()) {
					reports.handle(new TaskAsyncResult(fullReport));
				}
			});
		});
	}
}
