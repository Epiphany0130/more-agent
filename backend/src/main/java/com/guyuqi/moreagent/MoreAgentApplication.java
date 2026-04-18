package com.guyuqi.moreagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class MoreAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoreAgentApplication.class, args);
    }

}
