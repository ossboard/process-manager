package com.spring.core;

import java.util.List;


public interface ICommandExecuteHelper {
    Integer exec(String command);
    void kill(Integer pid);
    List<Integer> getPidList(String command);

}
