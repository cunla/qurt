package com.emc.qurt.web.rest;

import com.emc.qurt.domain.VirtualMachineData;
import com.emc.qurt.repository.VmDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by morand3 on 12/23/2014.
 */
@RestController
@RequestMapping("/app")
public class VmDataResource {
    private final Logger log = LoggerFactory.getLogger(VmDataResource.class);

    @Inject
    private VmDataRepository vmDataRepository;

    /**
     * GET  /rest/vmData -> get all vm data list.
     */
    @RequestMapping(value = "/rest/dbdump",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VirtualMachineData>> getAllVmData() {
        log.debug("REST request to get all sampled data in json form");
        return Optional.ofNullable(vmDataRepository.findAll()).map(
                user -> new ResponseEntity<>(user, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /rest/dbdump -> get all sampled data in csv form
     */
    @RequestMapping(value = "/rest/dbdumpCsv",
                    method = RequestMethod.GET,
                    produces = "text/csv;charset=utf-8")
    public ResponseEntity<String> getDbDump() {
        log.debug("REST request to get sampled data in csv form");
        List<VirtualMachineData> allData = vmDataRepository.findAll();
        StringBuffer res = new StringBuffer();
        res.append(VirtualMachineData.csvTitle() + "\n");
        for (VirtualMachineData vmData : allData) {
            res.append(vmData.asCsv() + "\n");
        }
        return HttpTools.getCsvResponseEntity(res.toString());
    }

//    /**
//     * GET  /rest/vmcount -> get number of VMs in period
//     */
//    @RequestMapping(value = "/rest/reportDates",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<VmCount>> getVmCount(
//            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime fromDate,
//            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime toDate) {
//        log.debug("REST request to get VMs report in period");
//        if (null == fromDate) {
//            fromDate = DateTime.now().minusMonths(3);
//        }
//        if (null == toDate) {
//            toDate = DateTime.now();
//        }
//        List<VmCount> vmCounts = vmDataRepository.countVmsInPeriod(fromDate, toDate);
//        return Optional.ofNullable(vmCounts)
//                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
//                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }

//    /**
//     * GET  /rest/vmcount -> get number of VMs in period
//     */
//    @RequestMapping(value = "/rest/vmCountReport/{year}/{quarter}",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<VmCount>> getQuarterlyReport(
//            @PathVariable("year") Integer year,
//            @PathVariable("quarter") Integer quarter) {
//        log.info("REST request to get VMs report in quarter {}/Q{} ", year, quarter);
//        DateTime fromDate = new DateTime(year, (quarter - 1) * 3 + 1, 1, 0, 0, 0);
//        DateTime toDate = new DateTime(fromDate).plusMonths(3);
//        return getVmCount(fromDate, toDate);
//    }

//    @RequestMapping(value = "/rest/vmCountReportCsv/{year}/{quarter}",
//            method = RequestMethod.GET,
//            produces = "text/csv;charset=utf-8")
//    public ResponseEntity<String> getQuarterlyReportCsv(
//            @PathVariable("year") Integer year,
//            @PathVariable("quarter") Integer quarter) {
//        log.info("REST request to get VMs CSV report in quarter {}/Q{} ", year, quarter);
//
//        DateTime fromDate = new DateTime(year, (quarter - 1) * 3 + 1, 1, 0, 0, 0);
//        DateTime toDate = new DateTime(fromDate).plusMonths(3);
//        List<VmCount> report = getVmCount(fromDate, toDate).getBody();
//        StringBuffer res = new StringBuffer();
//
//        //Build CSV
//        res.append(VmCount.csvTitle() + "\n");
//        for (VmCount vmCount : report) {
//            res.append(vmCount.asCsv() + "\n");
//        }
//
//        return HttpTools.getCsvResponseEntity(res);
//    }

}
