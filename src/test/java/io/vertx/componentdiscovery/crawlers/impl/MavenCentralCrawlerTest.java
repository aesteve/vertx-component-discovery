package io.vertx.componentdiscovery.crawlers.impl;

import org.junit.Test;

import io.vertx.componentdiscovery.model.VertxVersion;
import io.vertx.test.core.VertxTestBase;

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
            System.out.println(scanReport.failed);
            System.out.println(scanReport.failedCause);
            assertFalse(scanReport.failed);
            assertTrue(scanReport.result != null && scanReport.result.size() > 20);// more than one page fetched
            System.out.println("Fetched : " + scanReport.result.size());
            testComplete();
        });
        await();
    }
}
