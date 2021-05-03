package com.spring.domain;

import com.spring.core.ICommandExecuteHelper;
import com.spring.core.ShellCommandExecuteHelper;
import com.spring.core.WindowsCommandExecuteHelper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessHandle {
    @Getter
    private String command = null;
    @Getter
    private Integer count = 0;
    @Getter
    private Boolean autoRestart = false;

    private Map<Integer, ProcessState> processStateMap;

    static private ICommandExecuteHelper executeHelper = null;

    static {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            executeHelper = new WindowsCommandExecuteHelper();
        } else {
            executeHelper = new ShellCommandExecuteHelper();
        }
    }


    public ProcessHandle() {
        processStateMap = new HashMap<>();
    }

    public ProcessHandle(String command, Integer count, Boolean autoRestart) {
        processStateMap = new HashMap<>();
        this.command = command;
        this.count = count;
        this.autoRestart = autoRestart;
    }

    public Boolean start() {
        processStateMap.clear();
        List<Integer> procList = executeHelper.getPidList(command);
        Integer addCount = count - procList.size();
        for (Integer pid : procList) {
            processStateMap.put(pid, ProcessState.Running);
        }
        for (int i = 0; i < addCount; i++) {
            System.out.println("> " + command);
            Integer pid = executeHelper.exec(command);
            if (pid > 0) {
                processStateMap.put(pid, ProcessState.Running);
            }
        }
        return processStateMap.size() == count;
    }

    public void stop() {

        for (Integer pid : processStateMap.keySet()) {
            ProcessState state = processStateMap.get(pid);

            if (state == ProcessState.Running) {
                executeHelper.kill(pid);
                processStateMap.replace(pid, ProcessState.Stoped);
            }
        }
    }

    public Boolean stop(Integer pid) {
        Boolean result = false;

        ProcessState state = null;

        if (pid != null && pid > 0) {
            state = processStateMap.get(pid);
        }

        if (state != null && state == ProcessState.Running) {
            executeHelper.kill(pid);
            processStateMap.replace(pid, ProcessState.Stoped);
            result = true;
        }

        return result;
    }


    public Boolean restart() {
        stop();
        return start();
    }

    public Boolean restart(Integer pid) {
        Boolean result = false;

        ProcessState state = null;

        if (pid != null && pid > 0) {
            state = processStateMap.get(pid);
        }

        if (state != null && state == ProcessState.Running) {
            executeHelper.kill(pid);
            processStateMap.replace(pid, state, ProcessState.Stoped);
        }

        if (state == null || (state != ProcessState.Starting && state != ProcessState.Running)) {
            Integer newPid = executeHelper.exec(command);

            if (newPid > 0) {
                if (pid != null) {
                    processStateMap.remove(pid);
                }
                processStateMap.put(newPid, ProcessState.Running);
                result = true;
            }
        }

        return result;
    }

    public ProcessState getState(Integer pid) {
        ProcessState result = null;
        if (pid != null) {
            result = processStateMap.get(pid);
        }
        return result;
    }

    public Map<Integer, ProcessState> getProcessState() {
        return new HashMap<>(processStateMap);
    }

    public void updateProcessState() {

        List<Integer> procList = executeHelper.getPidList(command);
        List<Integer> fatalProcList = new ArrayList<>();

        for (Integer pid : processStateMap.keySet()) {
            ProcessState state = processStateMap.get(pid);

            if (procList.contains(pid)) {
                if (state != ProcessState.Running) {
                    processStateMap.replace(pid, ProcessState.Running);
                }
            } else {
                processStateMap.replace(pid, ProcessState.Fatal);
                fatalProcList.add(pid);
            }

        }


        if (autoRestart && fatalProcList.size() > 0) {
            for (Integer pid : fatalProcList) {
                System.out.println("> " + command);
                Integer newPid = executeHelper.exec(command);
                if (newPid > 0) {
                    processStateMap.remove(pid);
                    processStateMap.put(newPid, ProcessState.Running);
                }
            }
        }

    }
}
