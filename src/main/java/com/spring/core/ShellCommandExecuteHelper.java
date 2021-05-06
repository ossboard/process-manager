package com.spring.core;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShellCommandExecuteHelper implements ICommandExecuteHelper {

    @Override
    public Integer exec(String command, String directory) {
        List<Integer> oldList = getPidList(command);
        try {
            String[] cmd = StringUtils.split(command, " ");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            if(StringUtils.isNotEmpty(directory)) {
                builder.directory(new File(directory));
            }
//            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Integer> newList = getPidList(command);
        newList.removeAll(oldList);
        if (newList.size() > 0) {
            return newList.get(0);
        } else {
            return -1;
        }
    }

    @Override
    public void kill(Integer pid) {
        try {
            Runtime.getRuntime().exec("kill " + pid.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> getPidList(String command) {
        ArrayList<Integer> result = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("ps -eo pid,command");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                Integer endIndex = line.indexOf(command);
                if (endIndex > -1) {
                    String pidString = line.substring(0, endIndex).trim();
                    Integer pid = Integer.parseInt(pidString);
                    if (pid != null) {
                        result.add(pid);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
