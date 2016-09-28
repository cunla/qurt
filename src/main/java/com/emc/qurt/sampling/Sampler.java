package com.emc.qurt.sampling;

import com.emc.qurt.domain.SystemSettings;
import com.emc.qurt.domain.VirtualMachineData;
import com.emc.qurt.fal.Client;
import com.emc.qurt.repository.SystemConnectionInfoRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by morand3 on 2/12/2015.
 */
public class Sampler {
    private final Logger log = LoggerFactory.getLogger(Sampler.class);
    private final Client falClient;
    private final SystemConnectionInfoRepository systemsRepository;

    public Sampler(Client falClient) {
        this.falClient = falClient;
        this.systemsRepository = null;
    }

    public Sampler(Client falClient, SystemConnectionInfoRepository systemsRepository) {
        this.falClient = falClient;
        this.systemsRepository = systemsRepository;
    }

    public Sampler(SystemSettings settings, SystemConnectionInfoRepository systemsRepository) {
        this.falClient = new Client(settings);
        this.systemsRepository = systemsRepository;
    }

    public List<VirtualMachineData> getAllVms() {
        DateTime sampleTime = DateTime.now();
        Map<Long, String> clustersNames = falClient.getClusterNames();
        Map<Long, Map<String, String>> vmNames = falClient.getVmNamesAllClusters();
        Map<String, String> vmState = falClient.getVmState();

        List<VirtualMachineData> res = new LinkedList<>();
        for (Long clusterId : clustersNames.keySet()) {
            String clusterCountry = systemsRepository == null ? "null" : systemsRepository.clusterCountry(clusterId);
            Map<String, String> vms = vmNames.get(clusterId);
            if (null == vms) {
                return Collections.emptyList();
            }
            for (String vmId : vms.keySet()) {
                String role = vmState.get(vmId);
                if (null == role) {
//                    log.info("vm {}({}) doesn't have a state... vRPA?", vmId, vms.get(vmId));
                    role = "NONE";
                }
                VirtualMachineData vmData = new VirtualMachineData(sampleTime, clusterId,
                        clustersNames.get(clusterId), vmId, vms.get(vmId), clusterCountry, role);
                res.add(vmData);

            }
        }
        return res;
    }
}
