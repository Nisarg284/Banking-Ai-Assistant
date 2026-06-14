package com.n23.foa.bankingaiassistant.controller;


import com.n23.foa.bankingaiassistant.ai.CustomerExtractor;
import com.n23.foa.bankingaiassistant.ai_service.DocumentIngestionService;
import com.n23.foa.bankingaiassistant.dto.CustomerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    private final DocumentIngestionService documentIngestionService;

    private final CustomerExtractor extractor;

    public TestController(DocumentIngestionService documentIngestionService, CustomerExtractor extractor) {
        this.documentIngestionService = documentIngestionService;
        this.extractor = extractor;
    }


    @GetMapping("/ingest")
    public void ingest() throws IOException {

        documentIngestionService.ingestAllDocuments();
//        log.info(str);

    }


}
