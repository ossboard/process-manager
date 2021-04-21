package com.github.spring;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

    private static String mode;

    public static void main(String[] args) throws Exception {

        if (rootCheck()) {
            System.out.println("root 권한으로 실행할 수 없습니다");
            return;
        }
        File file = appInit();
        List<String> list = new ArrayList(Arrays.asList(args));
        if (file.exists()) {
            String configSet = String.format("--spring.config.location=file:%s", file.getAbsolutePath());
            boolean found = false;
            for (String s : list) {
                if (StringUtils.startsWith(s, "--spring.config.location")) {
                    found = true;
                }
            }
            if (!found) {
                list.add(configSet);
            }
            String[] newArgs = new String[args.length + 1];
            newArgs[newArgs.length - 1] = configSet;
        }
        args = list.toArray(new String[0]);

        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

    public static File appInit() throws Exception {
        String OS = System.getProperty("os.name").toLowerCase();
        String startup = ((OS.indexOf("win") >= 0)) ? "startup.bat" : "startup.sh";
        String commandPath = System.getProperty("sun.java.command");
        commandPath = StringUtils.substringBefore(commandPath, " ");
        String path = FilenameUtils.getPath(commandPath);
        path = (StringUtils.isEmpty(path)) ? "." : path;
        String filePath1 = FilenameUtils.normalize(path + "/application.yml");
        String filePath2 = FilenameUtils.normalize(path + "/config.ini");
        String filePath3 = FilenameUtils.normalize(path + "/logs");
        String filePath4 = FilenameUtils.normalize(path + "/" + startup);
        System.setProperty("logging.file.path", filePath3); // = ${LOG_PATH}
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        File file3 = new File(filePath3);
        File file4 = new File(filePath4);

        if (!file1.exists()) {
            InputStream in = Application.class.getResourceAsStream("/application.yml");
            OutputStream out = new FileOutputStream(file1);
            FileCopyUtils.copy(in, out);
        }

        if (!file2.exists()) {
            InputStream in = Application.class.getResourceAsStream("/config.ini");
            OutputStream out = new FileOutputStream(file2);
            FileCopyUtils.copy(in, out);
        }

        if (!file3.isDirectory()) {
            file3.mkdirs();
        }

        if (!file4.exists()) {
            InputStream in = Application.class.getResourceAsStream("/runscript/" + startup);
            OutputStream out = new FileOutputStream(file4);
            FileCopyUtils.copy(in, out);
            if (OS.indexOf("win") >= 0) { /* windows */
                in = Application.class.getResourceAsStream("/runscript/tail.exe");
                out = new FileOutputStream(new File(file4.getParentFile(), "tail.exe"));
                FileCopyUtils.copy(in, out);
            }
        }
        return file1;
    }

    public static boolean rootCheck() throws Exception {
        String userName = System.getProperty("user.name");
        return StringUtils.equals(userName, "root");
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
