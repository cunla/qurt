package com.emc.qurt.repo;

import com.emc.qurt.Application;
import com.emc.qurt.config.Constants;
import com.emc.qurt.domain.VirtualMachineData;
import com.emc.qurt.repository.VmDataRepository;
import com.emc.qurt.web.rest.VmDataResource;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by morand3 on 1/18/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(value = Constants.SPRING_PROFILE_DEVELOPMENT)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class TestVmDataRepository {
    @Inject
    private VmDataRepository vmDataRepository;
    @Inject
    private VmDataResource vmDataResource;
    private static final int NUBMER_OF_CLUSTERS = 2;
    private static final int NUBMER_OF_WEEKS = 12;

    @Test
    public void testGetAllData() {
        List<VirtualMachineData> vmDataBefore = vmDataResource.getAllVmData().getBody();
        int sampleDataSize = createAndSaveSampleData(NUBMER_OF_WEEKS).size();
        List<VirtualMachineData> vmDataAfter = vmDataResource.getAllVmData().getBody();
        assertTrue(vmDataAfter.size() == vmDataBefore.size() + sampleDataSize);

    }

//    @Test
//    public void testReportGeneration() {
//        DateTime fromDate = DateTime.now().minusWeeks(NUBMER_OF_WEEKS);
//        DateTime toDate = DateTime.now();
//        List<VmCount> before = vmDataResource.getVmCount(fromDate, toDate).getBody();
//        createAndSaveSampleData(NUBMER_OF_WEEKS);
//        List<VmCount> after = vmDataResource.getVmCount(fromDate, toDate).getBody();
//        //TODO
//    }


    private List<VirtualMachineData> createAndSaveSampleData(int numberOfWeeks) {
        List<VirtualMachineData> newVmData = createSamplingData(numberOfWeeks);
        vmDataRepository.save(newVmData);
        vmDataRepository.flush();
        return newVmData;
    }

    private List<VirtualMachineData> createSamplingData(int numberOfWeeks) {
        DateTime sampleDate = DateTime.now();
        List<VirtualMachineData> res = new LinkedList<>();
        for (int i = 1; i <= numberOfWeeks; ++i) {
            res.addAll(listOfVmsWithDate(sampleDate.minusWeeks(i), NUBMER_OF_WEEKS - i));
        }
        return res;
    }

    private List<VirtualMachineData> listOfVmsWithDate(DateTime sampleDate, int size) {
        List<VirtualMachineData> res = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            long clusterId = i % NUBMER_OF_CLUSTERS;
            String clusterName = String.format("Cluster%d", clusterId);
            String vmId = String.format("vmId %d", i);
            String vmName = String.format("vmName %d", i);
            String clusterCountry = "Israel";
            String role = "Source";
            VirtualMachineData vmData = new VirtualMachineData(sampleDate, clusterId, clusterName, vmId, vmName,
                    clusterCountry, role);
            res.add(vmData);
        }
        return res;
    }
}
