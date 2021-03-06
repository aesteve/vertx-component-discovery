package io.vertx.componentdiscovery.crawlers.impl;

import java.util.List;

import io.vertx.componentdiscovery.model.VertxVersion;
import io.vertx.test.core.VertxTestBase;

import org.junit.Test;

public class MavenCentralCrawlerTest extends VertxTestBase {

    private static MavenCentralCrawler crawler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testCentralCrawlerv2() {
        crawler = new MavenCentralCrawler(vertx, VertxVersion.V2);
        crawler.scan(scanReport -> {
            assertFalse(scanReport.hasFailed());
            assertTrue(scanReport.result() != null && ((List) scanReport.result()).size() > 20);// more than one page fetched
            testComplete();
        });
        await();
    }
}
