package com.n23.foa.bankingaiassistant;


import com.n23.foa.bankingaiassistant.ai_service.RetrievalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TestRunner implements CommandLineRunner {

    private final RetrievalService retrievalService;

    public TestRunner(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @Override
    public void run(String... args) throws IOException {
//        retrievalService.search("Requeried Documents for Kyc");
    }
}
