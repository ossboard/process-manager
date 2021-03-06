package com.spring.service;

import com.spring.domain.ProcessHandle;
import org.apache.commons.io.FilenameUtils;
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
    private String rootPath = System.getProperty("rootPath");

    public RunService() throws Exception {
        processHandleList = new ArrayList<>();
        String filePath = FilenameUtils.normalize(rootPath + INI_FILE);
        File file = new File(filePath);
        if(file.exists() && file.isFile()) {
            Ini ini = new Ini(new FileReader(file));
            for (String sectionName : ini.keySet()) {
                String command = ini.get(sectionName, "command", String.class);
                String directory = ini.get(sectionName, "directory", String.class);
                String stdout_logfile = ini.get(sectionName, "stdout_logfile", String.class);
                String stderr_logfile = ini.get(sectionName, "stderr_logfile", String.class);
                Integer stdout_logfile_maxbytes = ini.get(sectionName, "stdout_logfile_maxbytes", Integer.class);
                Integer stderr_logfile_maxbytes = ini.get(sectionName, "stderr_logfile_maxbytes", Integer.class);
                Integer stdout_logfile_backups = ini.get(sectionName, "stdout_logfile_backups", Integer.class);
                Integer stderr_logfile_backups = ini.get(sectionName, "stderr_logfile_backups", Integer.class);
                Boolean autostart = ini.get(sectionName, "autostart", Boolean.class);
                Boolean autorestart = ini.get(sectionName, "autorestart", Boolean.class);
                Integer numprocs = ini.get(sectionName, "numprocs", Integer.class);
                String process_name = ini.get(sectionName, "process_name", String.class);
                String user = ini.get(sectionName, "user", String.class);

                if(directory == null) directory = "";
                if(numprocs == null) numprocs = 1;
                ProcessHandle processHandle = new ProcessHandle(command, directory, numprocs, autostart);
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