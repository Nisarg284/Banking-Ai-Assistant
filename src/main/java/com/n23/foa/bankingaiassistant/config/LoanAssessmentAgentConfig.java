package com.n23.foa.bankingaiassistant.config;


import com.n23.foa.bankingaiassistant.ai.LoanAssessmentAgent;
import com.n23.foa.bankingaiassistant.ai_service.BankingContentRetriever;
import com.n23.foa.bankingaiassistant.tools.EligibilityTool;
import com.n23.foa.bankingaiassistant.tools.EmiTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanAssessmentAgentConfig {

    @Bean
    public LoanAssessmentAgent loanAssessmentAgent(ChatModel chatModel, BankingContentRetriever contentRetriever,
                                                   EligibilityTool eligibilityTool, EmiTool emiTool){
        return AiServices.builder(LoanAssessmentAgent.class)
                .contentRetriever(contentRetriever)
                .chatModel(chatModel)
                .tools(emiTool,eligibilityTool)
                .build();
    }
}
