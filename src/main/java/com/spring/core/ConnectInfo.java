package com.spring.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ConnectInfo {

    @Value("${server.title}")
    private String title;

    @PostConstruct
    public void init() {

        StringBuffer sb = new StringBuffer();
        sb.append("--------------------------------------------").append("\n");
        sb.append(title).append("\n");
        sb.append("--------------------------------------------").append("\n");
        System.out.println(sb.toString());
    }

}