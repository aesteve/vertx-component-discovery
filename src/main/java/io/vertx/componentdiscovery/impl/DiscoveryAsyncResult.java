package io.vertx.componentdiscovery.impl;

import io.vertx.componentdiscovery.model.Artifact;
import io.vertx.componentdiscovery.model.TaskReport;
import io.vertx.core.AsyncResult;

public class DiscoveryAsyncResult implements AsyncResult<TaskReport<Artifact>> {

	private TaskReport<Artifact> report;

	public DiscoveryAsyncResult(TaskReport<Artifact> report) {
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
	public TaskReport<Artifact> result() {
		return report;
	}

	@Override
	public boolean succeeded() {
		return report.failureCause() == null;
	}

}
