package com.n23.foa.bankingaiassistant;

import com.n23.foa.bankingaiassistant.ai_service.DocumentIngestionService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class BankingAiAssistantApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(BankingAiAssistantApplication.class, args);



    }

}
