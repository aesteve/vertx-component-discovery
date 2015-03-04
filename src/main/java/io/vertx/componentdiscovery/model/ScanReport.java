package io.vertx.componentdiscovery.model;

import java.util.List;

public class ScanReport {
    public boolean failed;
    public Throwable failedCause;
    public Integer failedStatus;
    public List<Artifact> result;
    public Integer total;
    public Long startTime;
    public Long endTime;
    public int itemsScanned;
}
