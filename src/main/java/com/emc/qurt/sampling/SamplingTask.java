package com.emc.qurt.sampling;

import com.emc.qurt.CurtException;
import com.emc.qurt.domain.SystemSettings;
import com.emc.qurt.domain.VirtualMachineData;
import com.emc.qurt.repository.SystemConnectionInfoRepository;
import com.emc.qurt.repository.VmDataRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import retrofit.RetrofitError;

import javax.inject.Inject;
import java.util.List;

//import DatabaseInfo;

/**
 * Created by morand3 on 12/24/2014.
 */
public class SamplingTask {
    public final static String EVERY_MINUTE = "1 * * * * *";
    public final static String EVERY_DAY = "0 15 * * * * ";
    public final static String SCHEDULE = EVERY_DAY;

    private final Logger log = LoggerFactory.getLogger(SamplingTask.class);

    @Inject
    private VmDataRepository vmDataRepository;

    @Inject
    private SystemConnectionInfoRepository systemsRepository;

    @Scheduled(cron = SCHEDULE)
    public void sampleDataToDb() {
        List<SystemSettings> settingsList = systemsRepository.findAll();
        for (SystemSettings settings : settingsList) {
            Sampler sampler = new Sampler(settings, systemsRepository);
            try {
                List<VirtualMachineData> allData = sampler.getAllVms();
                log.info("Collected {} VMs data from system {}", allData.size(), settings.getSystemIp());
                vmDataRepository.save(allData);
                settings.setLastCollected(DateTime.now());
                settings.setTestResult(true);
            } catch (CurtException e) {
                log.error("Couldn't get info for {}", settings.getSystemIp());
                settings.setTestResult(false);
            } catch (RetrofitError e) {
                log.error("Couldn't get info for {}", settings.getSystemIp());
                settings.setTestResult(false);
            }
            systemsRepository.save(settings);
        }
        systemsRepository.flush();
        vmDataRepository.flush();
    }

}
