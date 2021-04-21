package com.github.spring.core;

import com.github.spring.domain.ProcessHandle;

import java.util.List;


public interface ICommandExecuteHelper {
    Integer exec(String command);
    void kill(Integer pid);
    List<Integer> getPidList(String command);

}
