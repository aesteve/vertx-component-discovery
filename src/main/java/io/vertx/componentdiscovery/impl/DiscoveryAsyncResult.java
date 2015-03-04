package io.vertx.componentdiscovery.impl;

import io.vertx.componentdiscovery.model.FullScanReport;
import io.vertx.core.AsyncResult;

public class DiscoveryAsyncResult implements AsyncResult<FullScanReport> {

    private FullScanReport report;

    public DiscoveryAsyncResult(FullScanReport report) {
        this.report = report;
    }

    @Override
    public Throwable cause() {
        return report.failureCause();
    }

    @Override
    public boolean failed() {
        return report.failureCause() != null;
    }

    @Override
    public FullScanReport result() {
        return report;
    }

    @Override
    public boolean succeeded() {
        return report.failureCause() == null;
    }

}
