package com.spring.service;

import org.ini4j.Ini;
import org.ini4j.Wini;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("developer")
class RunServiceTest {

    @Test
    void test() throws Exception {

        String INI_FILE = "config.ini";

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream(INI_FILE);

        Ini ini = new Ini(inputStream);
        for (String sectionName : ini.keySet()) {
            String command = ini.get(sectionName, "command", String.class);
            String directory = ini.get(sectionName, "directory", String.class);
            String stdout_logfile = ini.get(sectionName, "stdout_logfile", String.class);
            String stderr_logfile = ini.get(sectionName, "stderr_logfile", String.class);
            boolean autostart = ini.get(sectionName, "autostart", Boolean.class);
            int numprocs = ini.get(sectionName, "numprocs", Integer.class);
            String user = ini.get(sectionName, "user", String.class);

            System.out.println(command + directory + user);
        }

    }


}