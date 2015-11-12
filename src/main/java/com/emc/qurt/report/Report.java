package com.emc.qurt.report;

import com.emc.qurt.domain.ReportLine;

import java.util.List;

/**
 * Created by morand3 on 3/2/2015.
 */
public class Report {
    private List<ReportLine> reportLines;
    private List<String> errors;

    public Report(List<ReportLine> lines, List<String> reportErrors) {
        this.reportLines = lines;
        this.errors = reportErrors;
    }

    public List<ReportLine> getReportLines() {
        return reportLines;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String asCsv() {
        StringBuffer res = new StringBuffer();
        //Build CSV
        res.append(ReportLine.csvTitle() + "\n");
        for (ReportLine line : reportLines) {
            res.append(line.asCsv() + "\n");
        }
        res.append("Errors\n");
        for (String error : errors) {
            res.append(error + "\n");
        }
        return res.toString();
    }
}
