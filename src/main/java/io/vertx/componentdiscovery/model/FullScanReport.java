package io.vertx.componentdiscovery.model;

import io.vertx.componentdiscovery.model.exceptions.ScanComponentException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FullScanReport {
    private long start;
    private long end;
    private List<ScanReport> reports;

    public FullScanReport() {
        reports = new ArrayList<ScanReport>();
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void addReport(ScanReport report) {
        reports.add(report);
        end = Math.max(end, report.endTime);
    }

    public List<ScanReport> reports() {
        return reports;
    }

    public long elapsedTime() {
        return end - start;
    }

    public int nbReports() {
        return reports.size();
    }

    /**
     * Nothing happened...
     */
    public void end() {
        end = System.currentTimeMillis();
    }

    public Throwable failureCause() {
        if (reports == null || reports.size() == 0) {
            return null;
        }
        List<ScanReport> failedReports = reports.stream().filter(report -> report.failed).collect(Collectors.toList());
        if (failedReports == null || failedReports.size() == 0) {
            return null;
        }
        ScanComponentException ex = new ScanComponentException();
        failedReports.forEach(failedReport -> {
            ex.addSuppressed(failedReport.failedCause);
        });
        return ex;
    }
}
