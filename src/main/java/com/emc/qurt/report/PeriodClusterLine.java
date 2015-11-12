package com.emc.qurt.report;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * Created by morand3 on 3/2/2015.
 */
public class PeriodClusterLine {
    private String country;
    private PeriodCluster periodCluster;

    private DateTime sampleTime;
    private int stateRemote;
    private int stateLocal;
    private int stateSource;

    public PeriodClusterLine(DateTime sampleTime, long clusterId) {
        this.sampleTime = sampleTime;
        periodCluster = new PeriodCluster(sampleTime, clusterId);
    }


    public PeriodClusterLine(PeriodCluster periodCluster, Sample sample) {
        this.periodCluster = periodCluster;
        this.sampleTime = sample.getSampleDate();
        this.country = sample.getCountry();
        this.stateSource = sample.getSourceVms();
        this.stateLocal = sample.getLocalVms();
        this.stateRemote = sample.getRemoteVms();
    }

    public PeriodCluster getPeriodCluster() {
        return periodCluster;
    }

    public DateTime getSampleTime() {
        return sampleTime;
    }

    public int getStateRemote() {
        return stateRemote;
    }

    public int getStateLocal() {
        return stateLocal;
    }

    public int getStateSource() {
        return stateSource;
    }

    public String getCountry() {
        return country;
    }

    public boolean setSample(Sample sample) {
        DateTime friday = periodCluster.getFridayInPeriod().toDateTime(new LocalTime(11, 0));
        long currentDist = Math.abs(friday.getMillis() - sampleTime.getMillis());
        long sampleDist = Math.abs(friday.getMillis() - sample.getSampleDate().getMillis());
        if (sampleDist > currentDist) {
            return false;
        } else {
            this.sampleTime = sample.getSampleDate();
            this.stateSource = sample.getSourceVms();
            this.stateLocal = sample.getLocalVms();
            this.stateRemote = sample.getRemoteVms();
            return true;
        }
    }

    @Override
    public String toString() {
        return "PeriodClusterLine{" +
                "periodCluster=" + periodCluster +
                ", stateRemote=" + stateRemote +
                ", stateLocal=" + stateLocal +
                ", stateSource=" + stateSource +
                '}';
    }
}
