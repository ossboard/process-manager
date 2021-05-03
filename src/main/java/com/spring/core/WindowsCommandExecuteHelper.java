package com.spring.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

public class WindowsCommandExecuteHelper implements ICommandExecuteHelper {
    @Override
    public Integer exec(String command) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();


        List<Integer> oldList = getPidList(command);
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Integer> newList = getPidList(command);

        newList.removeAll(oldList);

        if (newList.size() > 0 ) {
            return  newList.get(0);
        }
        else {
            return -1;
        }
    }

    @Override
    public void kill(Integer pid) {
        try {
            Runtime.getRuntime().exec("taskkill /pid " + pid.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> getPidList(String command) {

        ArrayList<Integer> result = new ArrayList<>();
        try {
            Integer parameterStart = command.trim().indexOf(" ");

            String commandString = command.substring(0, parameterStart - 1).trim();
            String parameterString = command.substring(parameterStart).trim();

            String searchCondition = "";

            Boolean needCheck = false;

            if (command.trim().equals(commandString)) {
                searchCondition = commandString.replace("\\", "\\\\");
            }
            else {
                searchCondition = "%" + commandString.replace("\\", "\\\\");
                needCheck = true;
            }

            Process process = Runtime.getRuntime().exec("wmic process where \"CommandLine like '"+ searchCondition +"'\" get CommandLine, Handle");

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;


            while((line = br.readLine()) != null){
                line = line.trim();

                if (needCheck && !line.startsWith(commandString)) {
                    continue;
                }

                Integer pidIndex = line.lastIndexOf(" ");
                if (pidIndex > 0) {
                    pidIndex++;
                    Integer pid =  Integer.parseInt(line.substring(pidIndex));
                    result.add(pid);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }
}


