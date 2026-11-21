package com.n23.foa.bankingaiassistant.config;

import com.n23.foa.bankingaiassistant.ai.CustomerExtractor;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerInfoExtractorConfig {

    @Bean
    public CustomerExtractor customerExtractor(ChatModel chatModel) {
        return AiServices.builder(CustomerExtractor.class)
                .chatModel(chatModel)
                .build();
    }
}
