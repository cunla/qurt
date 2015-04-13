package com.emc.qurt.report;

import com.emc.qurt.CurtException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

/**
 * Created by morand3 on 3/2/2015.
 */
class PeriodCluster {
    public LocalDate sampleDate;
    public LocalDate fromDate;
    public LocalDate toDate;
    public long clusterId;

    public PeriodCluster(LocalDate fromDate, LocalDate toDate, long clusterId) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.clusterId = clusterId;
    }

    public PeriodCluster(DateTime sample, long clusterId) {
        this.sampleDate=new LocalDate(sample);
        fromDate = new LocalDate(sample);
        while (DateTimeConstants.TUESDAY != fromDate.getDayOfWeek() &&
                fromDate.getMonthOfYear() == sample.getMonthOfYear()) {
            fromDate = fromDate.minusDays(1);
        }
        toDate = new LocalDate(sample);
        while (DateTimeConstants.MONDAY != toDate.getDayOfWeek() &&
                toDate.getMonthOfYear() == sample.getMonthOfYear()) {
            toDate = toDate.plusDays(1);
        }
        this.clusterId = clusterId;
    }

    public LocalDate getFridayInPeriod() {
        if (null == fromDate || null == toDate) {
            throw new CurtException("Period is not well defined");
        }
        LocalDate tmp = new LocalDate(fromDate);
        while (DateTimeConstants.FRIDAY != tmp.getDayOfWeek() && tmp.isBefore(toDate) &&
                tmp.getMonthOfYear() == fromDate.getMonthOfYear()) {
            tmp = tmp.plusDays(1);
        }
        return tmp;
    }

    public int getMonth() {
        return sampleDate.getMonthOfYear();
    }

    public int getYear() {
        return sampleDate.getYear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeriodCluster that = (PeriodCluster) o;

        if (clusterId != that.clusterId) return false;
        if (fromDate != null ? !fromDate.equals(that.fromDate) : that.fromDate != null) return false;
        if (toDate != null ? !toDate.equals(that.toDate) : that.toDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromDate != null ? fromDate.hashCode() : 0;
        result = 31 * result + (toDate != null ? toDate.hashCode() : 0);
        result = 31 * result + (int) (clusterId ^ (clusterId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "PeriodCluster{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", clusterId=" + clusterId +
                '}';
    }
}

