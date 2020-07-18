package com.example.hodgepodge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class HodgepodgeApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(HodgepodgeApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("# NonOptionArgs: " + args.getNonOptionArgs().size());
        log.info("NonOptionArgs:");
        args.getNonOptionArgs().forEach(log::info);
        log.info("# OptionArgs: " + args.getOptionNames().size());
        log.info("NoOptionArgs:");
        args.getOptionNames().forEach(
                optionName -> log.info(optionName + " = " + args.getOptionValues(optionName))
        );
    }
}
