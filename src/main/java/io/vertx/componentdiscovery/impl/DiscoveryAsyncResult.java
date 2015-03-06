package io.vertx.componentdiscovery.impl;

import io.vertx.componentdiscovery.model.TaskReport;
import io.vertx.core.AsyncResult;

public class DiscoveryAsyncResult implements AsyncResult<TaskReport> {

    private TaskReport report;

    public DiscoveryAsyncResult(TaskReport report) {
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
    public TaskReport result() {
        return report;
    }

    @Override
    public boolean succeeded() {
        return report.failureCause() == null;
    }

}
