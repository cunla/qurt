package com.emc.qurt.domain;

import com.emc.qurt.StatesConsts;

/**
 * Created by morand3 on 2/19/2015.
 */
public class ReportLinePerCluster {
    private final String country;
    private long clusterId;
    private int year;
    private int month;
    private int stateRemote;
    private int stateLocal;
    private int stateSource;

    public ReportLinePerCluster(String country, long clusterId, int year, int month) {
        this.country = country;
        this.clusterId = clusterId;
        this.year = year;
        this.month = month;
    }


    public ReportLinePerCluster(String country, int year, int month, long clusterId, int stateSource, int stateLocal,
                                int stateRemote) {
        this(country, clusterId, year, month);
        this.stateSource = stateSource;
        this.stateLocal = stateLocal;
        this.stateRemote = stateRemote;
    }


    public void setState(String state, int count) {
        if (StatesConsts.STATE_SOURCE.equals(state)) setStateSource(count);
        else if (StatesConsts.STATE_LOCAL.equals(state)) setStateLocal(count);
        else if (StatesConsts.STATE_REMOTE.equals(state)) setStateRemote(count);
    }

    public void setStateRemote(int stateRemote) {
        this.stateRemote = stateRemote;
    }

    public void setStateLocal(int stateLocal) {
        this.stateLocal = stateLocal;
    }

    public void setStateSource(int stateSource) {
        this.stateSource = stateSource;
    }

    public static String csvTitle() {
        return "month,year,country,source,local,remote";
    }

    public String asCsv() {
        return String.format("%d,%d,%s,%d,%d,%d", month, year, clusterId, stateSource, stateLocal, stateRemote);
    }

    public String getCountry() {
        return country;
    }

    public long getClusterId() {
        return clusterId;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportLinePerCluster that = (ReportLinePerCluster) o;

        if (clusterId != that.clusterId) return false;
        if (year != that.year) return false;
        if (month != that.month) return false;
        if (stateRemote != that.stateRemote) return false;
        if (stateLocal != that.stateLocal) return false;
        return stateSource == that.stateSource;

    }

    @Override
    public int hashCode() {
        int result = (int) (clusterId ^ (clusterId >>> 32));
        result = 31 * result + year;
        result = 31 * result + month;
        result = 31 * result + stateRemote;
        result = 31 * result + stateLocal;
        result = 31 * result + stateSource;
        return result;
    }

    public void addPeriodClusterSample(int sampleNumber, int stateSource, int stateLocal, int stateRemote) {
        this.stateSource = (this.stateSource * (sampleNumber - 1) + stateSource) / sampleNumber;
        this.stateLocal = (this.stateLocal * (sampleNumber - 1) + stateLocal) / sampleNumber;
        this.stateRemote = (this.stateRemote * (sampleNumber - 1) + stateRemote) / sampleNumber;
    }
}
