package com.n23.foa.bankingaiassistant.config;

import com.n23.foa.bankingaiassistant.ai.BankingAssistant;
import com.n23.foa.bankingaiassistant.ai_service.BankingContentRetriever;
import com.n23.foa.bankingaiassistant.tools.EligibilityTool;
import com.n23.foa.bankingaiassistant.tools.EmiTool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankingAssistantConfig {


    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.model-name}")
    private String modelName;

    @Value("${groq.base-url}")
    private String baseUrl;




    @Bean
    public ChatModel chatModel(){
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public BankingAssistant bankingAssistant(ChatModel chatModel
    , BankingContentRetriever bankingContentRetriever
    , EmiTool emiTool
            , EligibilityTool eligibilityTool){
        return AiServices.builder(BankingAssistant.class)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .chatModel(chatModel)
                .contentRetriever(bankingContentRetriever)
                .tools(emiTool,eligibilityTool)
                .build();
    }

//    @Bean
//    public ContentRetriever contentRetriever(
//            EmbeddingStore<TextSegment> store,
//            EmbeddingModel embeddingModel
//    ){
//        return EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(store)
//                .embeddingModel(embeddingModel)
//                .minScore(0.8)
//                .maxResults(3)
//                .build();
//    }
}
