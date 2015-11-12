package com.emc.qurt.report;

import org.joda.time.DateTime;

/**
 * Created by morand3 on 3/2/2015.
 */
public class Sample {
    private DateTime sampleDate;
    private long clusterId;
    private String country;
    private Integer sourceVms;
    private Integer localVms;
    private Integer remoteVms;


    public Sample(DateTime sampleDate, long clusterId, String country, Long sourceVms, Long localVms, Long remoteVms) {
        this.sampleDate = sampleDate;
        this.clusterId = clusterId;
        this.country=country;
        this.sourceVms = sourceVms.intValue();
        this.localVms = localVms.intValue();
        this.remoteVms = remoteVms.intValue();
    }

    public DateTime getSampleDate() {
        return sampleDate;
    }

    public long getClusterId() {
        return clusterId;
    }

    public String getCountry() {
        return country;
    }

    public Integer getSourceVms() {
        return sourceVms;
    }

    public Integer getLocalVms() {
        return localVms;
    }

    public Integer getRemoteVms() {
        return remoteVms;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "sampleDate=" + sampleDate +
                ", clusterId=" + clusterId +
                ", sourceVms=" + sourceVms +
                ", localVms=" + localVms +
                ", remoteVms=" + remoteVms +
                '}';
    }
}
