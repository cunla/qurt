package com.emc.qurt.web.rest;

import com.emc.qurt.domain.ReportLine;
import com.emc.qurt.domain.VmCount;
import com.emc.qurt.repository.VmDataRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by morand3 on 2/15/2015.
 */
@RestController
@RequestMapping("/app")
public class ReportResource {
    private final Logger log = LoggerFactory.getLogger(VmDataResource.class);

    @Inject
    private VmDataRepository vmDataRepository;


    @RequestMapping(value = "/rest/quarterReport/{year}/{quarter}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReportLine>> getQuarterlyReport(
            @PathVariable("year") Integer year,
            @PathVariable("quarter") Integer quarter) {
        log.info("REST request to get VMs report in quarter {}/Q{} ", year, quarter);
        List<ReportLine> report = getReportLines(year, quarter);
        return Optional.ofNullable(report)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/rest/quarterReportCsv/{year}/{quarter}",
            method = RequestMethod.GET,
            produces = "text/csv;charset=utf-8")
    public ResponseEntity<String> getQuarterlyReportCsv(
            @PathVariable("year") Integer year,
            @PathVariable("quarter") Integer quarter) {
        log.info("REST request to get VMs CSV report in quarter {}/Q{} ", year, quarter);
        List<ReportLine> report = getReportLines(year, quarter);
        StringBuffer res = new StringBuffer();
        //Build CSV
        res.append(ReportLine.csvTitle() + "\n");
        for (ReportLine line : report) {
            res.append(line.asCsv() + "\n");
        }

        return HttpTools.getCsvResponseEntity(res.toString());
    }

    private List<ReportLine> getReportLines(Integer year, Integer quarter) {
        DateTime fromDate = new DateTime(year, (quarter - 1) * 3 + 1, 1, 0, 0, 0);
        DateTime toDate = new DateTime(fromDate).plusMonths(3);
        List<VmCount> vmCounts = vmDataRepository.countVmsInPeriod(fromDate, toDate);
        return flattenReport(vmCounts);
    }

    private static List<ReportLine> flattenReport(List<VmCount> vmCounts) {
        Map<String, ReportLine> res = new HashMap<>();
        for (VmCount vm : vmCounts) {
            String key = String.format("%d/%d/%s",vm.getYear(),vm.getMonth(),vm.getCountry());
            ReportLine line = res.get(key);
            if(null == line) {
                line = new ReportLine(vm);
                res.put(key,line);
            }else{
                line.setState(vm.getState(),vm.getTotalVms());
            }
        }
        return new LinkedList<>(res.values());
    }
}
