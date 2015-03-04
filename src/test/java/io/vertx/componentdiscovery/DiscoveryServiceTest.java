package io.vertx.componentdiscovery;

import java.util.List;

import org.junit.Test;

import io.vertx.componentdiscovery.model.FullScanReport;
import io.vertx.componentdiscovery.model.ScanReport;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;

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
                FullScanReport report = handler.result();
                assertNotNull(report);
                List<ScanReport> reports = report.reports();
                assertNotNull(reports);
                assertTrue(reports.size() == 1);
                ScanReport mvnCentralReport = reports.get(0);
                assertNotNull(mvnCentralReport);
                assertFalse(mvnCentralReport.failed);
                assertNull(mvnCentralReport.failedCause);
                assertNull(mvnCentralReport.failedStatus);
                assertNotNull(mvnCentralReport.result);
                assertTrue(mvnCentralReport.result.size() > 0);
                testComplete();
            });
        });
        await();
    }
}
