package com.spring.core;

import java.util.List;


public interface ICommandExecuteHelper {
    Integer exec(String command, String directory);
    void kill(Integer pid);
    List<Integer> getPidList(String command);

}
