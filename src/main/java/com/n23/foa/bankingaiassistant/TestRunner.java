package com.n23.foa.bankingaiassistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TestRunner.class);

    @Override
    public void run(String... args) {
        log.info("Banking AI Assistant application started successfully.");
    }
}
