package com.emc.qurt;

import com.emc.qurt.domain.SystemSettings;
import com.emc.qurt.domain.VirtualMachineData;
import com.emc.qurt.fal.Client;
import com.emc.qurt.sampling.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.List;

/**
 * * import org.slf4j.LoggerFactory;
 * * Created by morand3 on 9/28/2016.
 */
public class Script {
    private final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * Main method, used to run the application.
     */
    public static void main(String[] args) {

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        boolean paramsOk = true;
        if (!source.containsProperty("clusterIp")) {
            System.out.println("Missing param 'clusterIp'");
            paramsOk = false;
        }
        if (!source.containsProperty("user")) {
            System.out.println("Missing param 'user'");
            paramsOk = false;
        }
        if (!source.containsProperty("password")) {
            System.out.println("Missing param 'password'");
            paramsOk = false;
        }
//        if (!source.containsProperty("clusterId")) {
//            System.out.println("Missing param 'clusterId'");
//            paramsOk = false;
//        }
        if (!paramsOk) {
            System.out.println("Usage: ");
            System.out.println("java com.emc.qurt.Script --clusterIp=xx.xx.xx.xx --user=admin --password=password --clusterId XXXXXX");
            System.exit(1);
        }
        String ip = source.getProperty("clusterIp");
        String user = source.getProperty("user");
        String password = source.getProperty("password");
//        long clusterId = Long.valueOf(source.getProperty("clusterId"));
        SystemSettings systemSettings = new SystemSettings(ip, user, password);
        Client client = new Client(systemSettings);
        Sampler sampler = new Sampler(client);
        List<VirtualMachineData> res = sampler.getAllVms();
        System.out.println(VirtualMachineData.simpleCsvTitle());
        for (VirtualMachineData vm : res) {
            System.out.println(vm.asSimpleCsv());
        }

    }
}
