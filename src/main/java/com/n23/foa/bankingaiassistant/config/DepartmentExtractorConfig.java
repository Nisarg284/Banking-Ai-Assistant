package com.n23.foa.bankingaiassistant.config;


import com.n23.foa.bankingaiassistant.ai.DepartmentClassifier;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepartmentExtractorConfig {


    @Bean
    public DepartmentClassifier departmentClassifier(ChatModel chatModel){
        return AiServices.builder(DepartmentClassifier.class)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .chatModel(chatModel)
                .build();
    }
}
