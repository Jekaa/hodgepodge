package com.example.hodgepodge.scheduler;

import com.example.hodgepodge.nio2.AsyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Scheduler {

    @Autowired
    private AsyncClient client;

    @Scheduled(fixedDelay = 3000)
    public void reportCurrentTime() {
        log.info("Scheduler run client.doRequest()");
        client.doRequest();
    }
}
