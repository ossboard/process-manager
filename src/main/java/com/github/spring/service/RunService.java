package com.github.spring.service;

import com.github.spring.domain.ProcessHandle;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("!developer")
public class RunService {

    private static Logger log = LoggerFactory.getLogger(RunService.class);

    private List<ProcessHandle> processHandleList;

    private String INI_FILE = "config.ini";

    public RunService() throws Exception {

        processHandleList = new ArrayList<>();
        File file = new File(INI_FILE);
        if(file.exists() && file.isFile()) {
            Ini ini = new Ini(new FileReader(file));
            for (String sectionName : ini.keySet()) {
                String command = ini.get(sectionName, "command", String.class);
                String directory = ini.get(sectionName, "directory", String.class);
                String stdout_logfile = ini.get(sectionName, "stdout_logfile", String.class);
                String stderr_logfile = ini.get(sectionName, "stderr_logfile", String.class);
                boolean autostart = ini.get(sectionName, "autostart", Boolean.class);
                int numprocs = ini.get(sectionName, "numprocs", Integer.class);
                String user = ini.get(sectionName, "user", String.class);

                ProcessHandle processHandle = new ProcessHandle(command, numprocs, autostart);
                processHandleList.add(processHandle);
            }
        } else {
            System.out.printf("File Not Found (" + file.getAbsolutePath() + " )\n");
        }
    }

    @PostConstruct
    public void start() {
        for (ProcessHandle processHandle : processHandleList) {
            processHandle.start();
        }
    }


    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    private void checkProcess() {
        for (ProcessHandle processHandle: processHandleList) {
            processHandle.updateProcessState();
        }
    }
}