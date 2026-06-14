package com.n23.foa.bankingaiassistant.config;

import com.n23.foa.bankingaiassistant.ai.CustomerExtractor;
import com.n23.foa.bankingaiassistant.ai_service.BankingContentRetriever;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerInfoExtractorConfig {

    @Bean CustomerExtractor customerExtractor(
            ChatModel chatModel,
            BankingContentRetriever bankingContentRetriever
    ){
        return AiServices.builder(CustomerExtractor.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .contentRetriever(bankingContentRetriever)
                .build();
    }
}
