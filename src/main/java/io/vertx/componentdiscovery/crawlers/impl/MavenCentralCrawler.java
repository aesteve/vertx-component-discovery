package io.vertx.componentdiscovery.crawlers.impl;

import io.vertx.componentdiscovery.crawlers.Crawler;
import io.vertx.componentdiscovery.model.Artifact;
import io.vertx.componentdiscovery.model.TaskReport;
import io.vertx.componentdiscovery.model.VertxVersion;
import io.vertx.componentdiscovery.utils.FetchCounter;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class MavenCentralCrawler implements Crawler {

	private VertxVersion version;
	private HttpClient client;
	private List<Artifact> artifacts;
	private FetchCounter counter;
	private TaskReport currentReport;

	public MavenCentralCrawler(Vertx vertx, VertxVersion version) {
		HttpClientOptions options = new HttpClientOptions();
		this.version = version;
		options.setDefaultHost("search.maven.org");
		this.client = vertx.createHttpClient(options);
		this.counter = new FetchCounter();
	}

	@Override
	public void scan(Consumer<TaskReport> imDone) {
		System.out.println("Start scanning maven central");
		artifacts = new ArrayList<Artifact>();
		currentReport = new TaskReport("Scan maven central");
		currentReport.start();
		try {
			fetchCurrentPage(imDone); // recursive
		} catch (Throwable t) {
			failWithCause(imDone, t);
		}
	}

	public String buildSearchPath() throws UnsupportedEncodingException {
		StringBuilder requestPath = new StringBuilder("/solrsearch/select");
		requestPath.append("?q=l%3A" + URLEncoder.encode(version.mvnClassifier(), "UTF-8"));
		requestPath.append("&wt=json");
		requestPath.append("&rows=100");
		requestPath.append("&start=" + Integer.toString(counter.getFetched() + 1));
		return requestPath.toString();
	}

	@SuppressWarnings("unchecked")
	public void fetchCurrentPage(Consumer<TaskReport> imDone) throws Throwable {
		String path = buildSearchPath();
		System.out.println("Fetching : " + path);
		client.get(path, response -> {
			if (response.statusCode() != 200) {
				failWithStatus(imDone, response.statusCode());
				return;
			}
			Buffer buffer = Buffer.buffer();
			response.handler(responseBuf -> {
				buffer.appendBuffer(responseBuf);
			});
			response.endHandler(handler -> {
				String json = buffer.toString("UTF-8");
				if (json == null || "".equals(json)) {
					scanSuccess(imDone); // nothing to fetch
					return;
				}
				JsonObject jsonResp = new JsonObject(json);
				JsonObject jsonObj = jsonResp.getJsonObject("response");
				counter.setCount(jsonObj.getInteger("numFound"));
				JsonArray array = jsonObj.getJsonArray("docs");
				List<Map<String, Object>> list = array.getList();
				if (list.size() == 0) {
					scanSuccess(imDone);
					return;
				}
				counter.addFetched(list.size());
				handle(list);
				if (counter.getFetched() < counter.getCount()) {
					try {
						fetchCurrentPage(imDone);
					} catch (Throwable e) {
						failWithCause(imDone, e);
						return;
					}
				} else {
					scanSuccess(imDone);
				}
			});
		}).putHeader("Accept", "application/json").end();
	}

	private void failWithCause(Consumer<TaskReport> handler, Throwable t) {
		currentReport.fail(t);
		handler.accept(currentReport);
	}

	private void scanSuccess(Consumer<TaskReport> handler) {
		currentReport.terminate(counter.getFetched(), artifacts);
		handler.accept(currentReport);
	}

	private void failWithStatus(Consumer<TaskReport> handler, int status) {
		currentReport.fail(status);
		handler.accept(currentReport);
	}

	/**
	 * Add if new, merge if existing
	 * 
	 * @param rawArtifacts the artifacts as returned by solrsearch
	 */
	private void handle(List<Map<String, Object>> rawArtifacts) {
		rawArtifacts.forEach(mvnArtifact -> {
			// try to get it from current list
				Artifact newArtifact = Artifact.fromCentral(mvnArtifact);
				Optional<Artifact> existing = artifacts.stream().filter(inList -> {
					return inList.fullId().equals(newArtifact.fullId());
				}).findFirst();
				if (existing.isPresent()) {
					// merge
					// System.out.println("merge : " + existing.get().fullId() + " with " + newArtifact.fullId());
					existing.get().merge(newArtifact);
				} else {
					artifacts.add(newArtifact);
				}
			});
	}
}
