package io.vertx.componentdiscovery;

import io.vertx.componentdiscovery.model.TaskReport;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import java.util.List;

import org.junit.Test;

public class DiscoveryServiceTest extends VertxTestBase {
    private static DiscoveryService service;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        JsonObject config = new JsonObject();
        JsonArray crawlers = new JsonArray();
        JsonObject mvnConfig = new JsonObject();
        mvnConfig.put("name", "mavenCentral");
        crawlers.add(mvnConfig);
        config.put("crawlers", crawlers);
        service = DiscoveryService.create(vertx, config);
    }

    @Test
    public void testCrawling() {
        service.start(startHandler -> {
            assertTrue(startHandler.succeeded());
            service.crawl(handler -> {
                assertTrue(handler.succeeded());
                TaskReport report = handler.result();
                assertNotNull(report);
                List<TaskReport> reports = report.subTasks();
                assertNotNull(reports);
                assertTrue(reports.size() == 1);
                TaskReport mvnCentralReport = reports.get(0);
                assertNotNull(mvnCentralReport);
                assertFalse(mvnCentralReport.hasFailed());
                assertNull(mvnCentralReport.failureCause());
                assertNotNull(mvnCentralReport.result());
                assertTrue(((List) mvnCentralReport.result()).size() > 0);
                testComplete();
            });
        });
        await();
    }
}
