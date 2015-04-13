package com.emc.qurt.report;

import com.emc.qurt.domain.ClusterSettings;
import com.emc.qurt.domain.ReportLine;
import com.emc.qurt.domain.ReportLinePerCluster;
import com.emc.qurt.repository.SystemConnectionInfoRepository;
import com.emc.qurt.repository.VmDataRepository;
import com.emc.qurt.web.rest.HttpTools;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
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
 * Created by morand3 on 3/2/2015.
 */
@RestController
@RequestMapping("/report")
public class ReportLogic {
    private final Logger log = LoggerFactory.getLogger(ReportLogic.class);
    @Inject
    private SystemConnectionInfoRepository systemsRepo;
    @Inject
    private VmDataRepository dataRepo;

    @RequestMapping(value = "/quarterReport/{year}/{quarter}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Report> getQuarterlyReport(@PathVariable("year") Integer year,
                                                     @PathVariable("quarter") Integer quarter) {
        log.info("REST request to get VMs report in quarter {}/Q{} ", year, quarter);
        Report report = getReportLines(year, quarter);
        return Optional.ofNullable(report).map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElse(
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/quarterReportCsv/{year}/{quarter}",
                    method = RequestMethod.GET,
                    produces = "text/csv;charset=utf-8")
    public ResponseEntity<String> getQuarterlyReportCsv(@PathVariable("year") Integer year,
                                                        @PathVariable("quarter") Integer quarter) {
        log.info("REST request to get VMs CSV report in quarter {}/Q{} ", year, quarter);
        Report report = getReportLines(year, quarter);
        return HttpTools.getCsvResponseEntity(report.asCsv());
    }

    private Report getReportLines(Integer year, Integer quarter) {
        DateTime fromDate = new DateTime(year, (quarter - 1) * 3 + 1, 1, 0, 0, 0);
        while (fromDate.getDayOfWeek() < DateTimeConstants.TUESDAY) {
            fromDate.plusDays(1);
        }
        DateTime toDate = new DateTime(fromDate).plusMonths(3).minusDays(1);
        List<Long> clusters = systemsRepo.clusterIds();

        List<Sample> samples = dataRepo.samplesInPeriod(fromDate, toDate);
        List<String> reportErrors =
                getReportErrors(getPeriods(fromDate.toLocalDate(), toDate.toLocalDate(), clusters), samples);

        //Take best sample per period
        Map<PeriodCluster, PeriodClusterLine> periods = new HashMap<>();
        PeriodCluster periodCluster = null;
        for (Sample sample : samples) {
            if (null == periodCluster || periodCluster.clusterId != sample.getClusterId() ||
                    periodCluster.toDate.toDateTimeAtStartOfDay().isBefore(sample.getSampleDate()) ||
                    periodCluster.fromDate.toDateTimeAtStartOfDay().isAfter(sample.getSampleDate())) {
                periodCluster = new PeriodCluster(sample.getSampleDate(), sample.getClusterId());
            }
            PeriodClusterLine line = periods.get(periodCluster);
            if (null == line) {
                line = new PeriodClusterLine(periodCluster, sample);
                periods.put(periodCluster, line);
            } else {
                Boolean lineUsed = line.setSample(sample);
            }
        }
        return generateReport(periods.values(), reportErrors);
    }

    private Report generateReport(Collection<PeriodClusterLine> periods, List<String> reportErrors) {
        Map<String, Integer> numberOfSamples = new HashMap<>();
        Map<String, ReportLinePerCluster> reportLinesPerCluster = new HashMap<>();

        for (PeriodClusterLine sample : periods) {
            String country = sample.getCountry();
            long clusterId = sample.getPeriodCluster().clusterId;
            int year = sample.getPeriodCluster().getYear();
            int month = sample.getPeriodCluster().getMonth();
            String key = String.format("%d/%d/%d", year, month, clusterId);
            ReportLinePerCluster reportLine = reportLinesPerCluster.get(key);
            if (null == reportLine) {
                reportLine = new ReportLinePerCluster(country, year, month, clusterId, sample.getStateSource(),
                        sample.getStateLocal(), sample.getStateRemote());
                reportLinesPerCluster.put(key, reportLine);
                numberOfSamples.put(key, 1);
            } else {
                int sampleNumber = numberOfSamples.get(key) + 1;
                reportLine.addPeriodClusterSample(sampleNumber, sample.getStateSource(), sample.getStateLocal(),
                        sample.getStateRemote());
                numberOfSamples.put(key, sampleNumber);
            }
        }

        //Flatten clusters
        Map<String, ReportLine> sumClusters = new HashMap<>();
        for (ReportLinePerCluster line : reportLinesPerCluster.values()) {
            String country = line.getCountry();
            int year = line.getYear();
            int month = line.getMonth();
            String key = String.format("%d/%d/%s", year, month, country);
            ReportLine reportLine = sumClusters.get(key);
            if (null == reportLine) {
                reportLine = new ReportLine(year, month, country, line.getStateSource(), line.getStateLocal(),
                        line.getStateRemote());
                sumClusters.put(key, reportLine);
            } else {
                reportLine.addCluster(line);
            }
        }


        List<ReportLine> lines = new LinkedList<>(sumClusters.values());
        Report report = new Report(lines, reportErrors);
        return report;
    }


    private List<String> getReportErrors(Map<PeriodCluster, Boolean> periods, List<Sample> samples) {
        List<String> errors = new LinkedList<>();
        PeriodCluster periodCluster = null;
        for (Sample sample : samples) {
            if (null == periodCluster || periodCluster.clusterId != sample.getClusterId() ||
                    !periodCluster.containDate(sample.getSampleDate())
                   ) {
                periodCluster = new PeriodCluster(sample.getSampleDate(), sample.getClusterId());
            }
            Boolean res = periods.get(periodCluster);
            if(null==res){
                log.warn("Can't find period for cluster "+periodCluster.toString());
            }
            periods.put(periodCluster, true);

        }
        for (Map.Entry<PeriodCluster, Boolean> periodSampled : periods.entrySet()) {
            if (!periodSampled.getValue()) {
                ClusterSettings cluster = systemsRepo.clusterById(periodSampled.getKey().clusterId);
                String errorStr = String.format("Data for cluster %s (%s) was not collected on Friday %s",
                        cluster.getFriendlyName() == null ? "" : cluster.getFriendlyName(), cluster.getClusterName(),
                        periodSampled.getKey().getFridayInPeriod());
                errors.add(errorStr);
            }
        }
        return errors.isEmpty() ? null : errors;
    }

    private Map<PeriodCluster, Boolean> getPeriods(LocalDate fromDate, LocalDate toDate, List<Long> clusters) {
        Map<PeriodCluster, Boolean> periods = new HashMap<>();
        LocalDate tmp = new LocalDate(fromDate);
        while (toDate.isAfter(tmp)) {
            LocalDate fDate = tmp;
            while (DateTimeConstants.TUESDAY != fDate.getDayOfWeek()
                    && fDate.getMonthOfYear()==tmp.getMonthOfYear()) {
                fDate = fDate.minusDays(1);
            }
            LocalDate tDate = tmp;
            while (DateTimeConstants.MONDAY != tDate.getDayOfWeek()
                    && tDate.getMonthOfYear() == tmp.getMonthOfYear()) {
                tDate = tDate.plusDays(1);
            }
            tmp = tDate.plusDays(1);
            for (Long clusterId : clusters) {
                PeriodCluster periodCluster = new PeriodCluster(fDate, tDate, clusterId);
                periods.put(periodCluster, false);
            }
        }
        return periods;
    }


}
