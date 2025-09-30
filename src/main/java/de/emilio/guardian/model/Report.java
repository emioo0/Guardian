package de.emilio.guardian.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Report {

    private final int id;
    private final UUID reporterUuid;
    private final String reporterName;
    private final UUID reportedUuid;
    private final String reportedName;
    private final String reason;
    private final long timestamp;
    private String status;

    public Report(int id, UUID reporterUuid, String reporterName, UUID reportedUuid, String reportedName, String reason, long timestamp, String status) {
        this.id = id;
        this.reporterUuid = reporterUuid;
        this.reporterName = reporterName;
        this.reportedUuid = reportedUuid;
        this.reportedName = reportedName;
        this.reason = reason;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getId() { return id; }
    public UUID getReporterUuid() { return reporterUuid; }
    public String getReporterName() { return reporterName; }
    public UUID getReportedUuid() { return reportedUuid; }
    public String getReportedName() { return reportedName; }
    public String getReason() { return reason; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String,Object> serialize() {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("reporter-uuid", reporterUuid.toString());
        m.put("reporter-name", reporterName);
        m.put("reported-uuid", reportedUuid.toString());
        m.put("reported-name", reportedName);
        m.put("reason", reason);
        m.put("timestamp", timestamp);
        m.put("status", status);
        return m;
    }

    public static Report deserialize(int id, Map<String,Object> m) {
        UUID reporterUuid = UUID.fromString(String.valueOf(m.get("reporter-uuid")));
        String reporterName = String.valueOf(m.get("reporter-name"));
        UUID reportedUuid = UUID.fromString(String.valueOf(m.get("reported-uuid")));
        String reportedName = String.valueOf(m.get("reported-name"));
        String reason = String.valueOf(m.get("reason"));
        long timestamp = Long.parseLong(String.valueOf(m.get("timestamp")));
        String status = String.valueOf(m.get("status"));
        return new Report(id, reporterUuid, reporterName, reportedUuid, reportedName, reason, timestamp, status);
    }
}
