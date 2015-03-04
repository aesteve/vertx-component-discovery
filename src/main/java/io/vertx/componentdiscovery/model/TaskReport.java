package io.vertx.componentdiscovery.model;

import io.vertx.componentdiscovery.model.exceptions.ScanComponentException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskReport<T> implements ApiObject {
	private String name;
	private boolean failed = false;
	private Throwable failedCause;
	private Integer failedStatus;
	private List<T> result;
	private Integer totalTasks;
	private Long startTime;
	private Long endTime;
	private Integer tasksDone;
	public List<TaskReport<T>> subTasks;

	public TaskReport(String name) {
		this.name = name;
		subTasks = new ArrayList<TaskReport<T>>();
	}

	public String name() {
		return name;
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void addTask(TaskReport<T> report) {
		if (report != null) {
			subTasks.add(report);
			if (endTime == null) {
				endTime = report.endTime;
			} else {
				endTime = Math.max(endTime, report.endTime);
			}
		}
	}

	public long elapsedTime() {
		return endTime - startTime;
	}

	/**
	 * Nothing happened...
	 */
	public void end() {
		endTime = System.currentTimeMillis();
	}

	public Throwable failureCause() {
		if (subTasks == null || subTasks.size() == 0) {
			return this.failedCause;
		}
		List<TaskReport<T>> failedReports = subTasks.stream().filter(task -> task.failed).collect(Collectors.toList());
		if (failedReports == null || failedReports.size() == 0) {
			return null;
		}
		ScanComponentException ex = new ScanComponentException();
		failedReports.forEach(failedReport -> {
			ex.addSuppressed(failedReport.failedCause);
		});
		return ex;
	}

	public boolean hasFailed() {
		return this.failed;
	}

	public void fail(Throwable cause) {
		this.failed = true;
		this.failedCause = cause;
	}

	public void fail(int status) {
		this.failed = true;
		this.failedStatus = status;
	}

	public void tasksDone(int tasksDone) {
		this.tasksDone = tasksDone;
	}

	public void terminate(int tasksDone, List<T> result) {
		this.result = result;
		terminate(tasksDone);
	}

	public void terminate(int tasksDone) {
		tasksDone(tasksDone);
		endTime = System.currentTimeMillis();
	}

	public List<TaskReport<T>> subTasks() {
		return subTasks;
	}

	public int nbSubTasks() {
		if (subTasks == null) {
			return 0;
		}
		return subTasks.size();
	}

	public List<T> result() {
		return result;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject json = new JsonObject();
		json.put("failed", failed);
		json.put("failedCause", failedCause);
		json.put("failedStatus", failedStatus);
		json.put("totalTasks", totalTasks);
		json.put("startTime", startTime);
		json.put("endTime", endTime);
		json.put("tasksDone", tasksDone);
		JsonArray tasks = new JsonArray();
		subTasks.forEach(subTask -> {
			tasks.add(subTask.toJsonObject());
		});
		json.put("subTasks", tasks);
		return json;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TaskReport fromMap(Map<String, Object> map) {
		TaskReport report = new TaskReport((String) map.get("name"));
		if (map.get("failed") != null) {
			report.failed = (boolean) map.get("failed");
		}
		report.failedCause = (Throwable) map.get("failedCause");
		if (map.get("failedStatus") != null) {
			report.failedStatus = (int) map.get("failedStatus");
		}
		if (map.get("totalTasks") != null) {
			report.totalTasks = (int) map.get("totalTasks");
		}
		if (map.get("startTime") != null) {
			report.startTime = (long) map.get("startTime");
		}
		if (map.get("endTime") != null) {
			report.endTime = (long) map.get("endTime");
		}
		if (map.get("tasksDone") != null) {
			report.tasksDone = (int) map.get("tasksDone");
		}
		List<Map<String, Object>> subTasks = (List<Map<String, Object>>) map.get("subTasks");
		if (subTasks != null) {
			subTasks.forEach(subTask -> {
				report.subTasks.add(TaskReport.fromMap(subTask));
			});
		}
		return report;
	}
}
