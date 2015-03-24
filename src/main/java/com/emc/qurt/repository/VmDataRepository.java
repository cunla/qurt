package com.emc.qurt.repository;

import com.emc.qurt.StatesConsts;
import com.emc.qurt.domain.VirtualMachineData;
import com.emc.qurt.report.Sample;
import com.emc.qurt.domain.VmCount;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by morand3 on 12/23/2014.
 */
@RepositoryRestResource(collectionResourceRel = "vms", path = "vms")
public interface VmDataRepository extends JpaRepository<VirtualMachineData, Long> {

    @Query("select vm from VirtualMachineData vm where vm.sampleTime = ?1")
    List<VirtualMachineData> findAllWithTimeStamp(DateTime dateTime);

    @Query("select distinct vm.sampleTime from VirtualMachineData vm")
    List<DateTime> findAllSampleDates();

    @Query("select vm from VirtualMachineData vm where vm.sampleTime >= ?1 and vm.clusterName = ?2")
    List<VirtualMachineData> findForCluster(DateTime fromDate, String clusterName);

    @Query("select vm from VirtualMachineData vm where vm.sampleTime >= ?1 and DAY_OF_WEEK(vm.sampleTime) = ?2")
    List<VirtualMachineData> findForDayOfWeek(DateTime fromDate, int dayOfWeek);

    @Query("select new com.emc.qurt.domain.VmCount(" +
            "YEAR(vm.sampleTime)," +
            "MONTH(vm.sampleTime), " +
            "vm.clusterCountry, " +
            "vm.role, " +
            "COUNT(*)/COUNT(DISTINCT vm.sampleTime)) " +
            "from VirtualMachineData vm " +
            "where vm.sampleTime >= ?1 and vm.sampleTime <= ?2 " +
            "group by YEAR(vm.sampleTime),MONTH(vm.sampleTime), vm.clusterCountry, vm.role " +
            "order by YEAR(vm.sampleTime),MONTH(vm.sampleTime), vm.clusterCountry, vm.role")
    List<VmCount> countVmsInPeriod(DateTime fromDate, DateTime toDate);

    @Query("select new com.emc.qurt.report.Sample(" +
                   "vm.sampleTime," +
                   "vm.clusterId, " +
                   "vm.clusterCountry, " +
                   "SUM(CASE WHEN vm.role='"+ StatesConsts.STATE_SOURCE+"' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN vm.role='"+StatesConsts.STATE_LOCAL+"' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN vm.role='"+StatesConsts.STATE_REMOTE+"' THEN 1 ELSE 0 END) " +
                   ")" +
                   "from VirtualMachineData vm " +
                   "where vm.sampleTime >= ?1 and vm.sampleTime <= ?2 " +
                   "group by vm.sampleTime, vm.clusterCountry, vm.role " +
                   "order by vm.sampleTime, vm.clusterCountry, vm.role")
    List<Sample> samplesInPeriod(DateTime fromDate, DateTime toDate);

    @Query("select vm from VirtualMachineData vm " +
                   "where vm.sampleTime >= ?1 " +
                   "and vm.sampleTime <= ?2 "+
                   "order by vm.sampleTime")
    List<VirtualMachineData> findBetweenDates(DateTime fromDate, DateTime toDate);

}
