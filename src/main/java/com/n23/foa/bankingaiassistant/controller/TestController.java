package com.n23.foa.bankingaiassistant.controller;

import com.n23.foa.bankingaiassistant.ai.CustomerExtractor;
import com.n23.foa.bankingaiassistant.ai.LoanAssessmentAgent;
import com.n23.foa.bankingaiassistant.ai_service.DocumentIngestionService;
import com.n23.foa.bankingaiassistant.dto.CustomerInfo;
import com.n23.foa.bankingaiassistant.dto.LoanAssessment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    private final DocumentIngestionService documentIngestionService;
    private final CustomerExtractor customerExtractor;
    private final LoanAssessmentAgent loanAssessmentAgent;

    public TestController(
            DocumentIngestionService documentIngestionService,
            CustomerExtractor customerExtractor,
            LoanAssessmentAgent loanAssessmentAgent
    ) {
        this.documentIngestionService = documentIngestionService;
        this.customerExtractor = customerExtractor;
        this.loanAssessmentAgent = loanAssessmentAgent;
    }

    @GetMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingest() {
        log.info("Triggered document ingestion process via /ingest endpoint");
        try {
            Map<String, Object> result = documentIngestionService.ingestAllDocuments();
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Document ingestion failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/extract-customer")
    public ResponseEntity<CustomerInfo> extractCustomer(@RequestBody String text) {
        log.info("Extracting customer info from input text");
        CustomerInfo info = customerExtractor.extract(text);
        return ResponseEntity.ok(info);
    }

    @PostMapping("/assess-loan")
    public ResponseEntity<LoanAssessment> assessLoan(@RequestBody String application) {
        log.info("Assessing loan application");
        LoanAssessment assessment = loanAssessmentAgent.assess(application);
        return ResponseEntity.ok(assessment);
    }
}
