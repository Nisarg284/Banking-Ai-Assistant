package com.n23.foa.bankingaiassistant.config;

import com.n23.foa.bankingaiassistant.ai.BankingAssistant;
import com.n23.foa.bankingaiassistant.ai_service.BankingContentRetriever;
import com.n23.foa.bankingaiassistant.tools.EligibilityTool;
import com.n23.foa.bankingaiassistant.tools.EmiTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BankingAssistantConfig {

    private static final Logger log = LoggerFactory.getLogger(BankingAssistantConfig.class);

    @Value("${groq.api-key:}")
    private String apiKey;

    @Value("${groq.model-name:llama-3.3-70b-versatile}")
    private String modelName;

    @Value("${groq.base-url:https://api.groq.com/openai/v1}")
    private String baseUrl;

    @Bean
    public ChatModel chatModel() {
        String effectiveApiKey = (apiKey == null || apiKey.isBlank()) ? "demo-key" : apiKey;
        String effectiveModelName = (modelName == null || modelName.isBlank()) ? "llama-3.3-70b-versatile" : modelName;
        String effectiveBaseUrl = (baseUrl == null || baseUrl.isBlank()) ? "https://api.groq.com/openai/v1" : baseUrl;

        log.info("Configuring ChatModel with baseUrl: {}, modelName: {}", effectiveBaseUrl, effectiveModelName);

        return OpenAiChatModel.builder()
                .apiKey(effectiveApiKey)
                .modelName(effectiveModelName)
                .baseUrl(effectiveBaseUrl)
                .temperature(0.1)
                .timeout(Duration.ofSeconds(30))
                .maxRetries(2)
                .build();
    }

    @Bean
    public BankingAssistant bankingAssistant(
            ChatModel chatModel,
            BankingContentRetriever bankingContentRetriever,
            EmiTool emiTool,
            EligibilityTool eligibilityTool
    ) {
        return AiServices.builder(BankingAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .build())
                .contentRetriever(bankingContentRetriever)
                .tools(emiTool, eligibilityTool)
                .build();
    }
}
