package com.rsons.application.connecting_thoughts.Report;

/**
 * Created by ankit on 3/20/2018.
 */

public class SingleReporterModel {

    String ReporterId,ReporterName,ReporterMailId,ReporterPhoneNumber,ReportType,ReportProblem;

    public SingleReporterModel(String reporterId, String reporterName, String reporterMailId, String reporterPhoneNumber, String reportType, String reportProblem) {
        ReporterId = reporterId;
        ReporterName = reporterName;
        ReporterMailId = reporterMailId;
        ReporterPhoneNumber = reporterPhoneNumber;
        ReportType = reportType;
        ReportProblem = reportProblem;
    }

    public String getReporterId() {
        return ReporterId;
    }

    public String getReporterName() {
        return ReporterName;
    }

    public String getReporterMailId() {
        return ReporterMailId;
    }

    public String getReporterPhoneNumber() {
        return ReporterPhoneNumber;
    }

    public String getReportType() {
        return ReportType;
    }

    public String getReportProblem() {
        return ReportProblem;
    }
}
