package com.n23.foa.bankingaiassistant.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class GreetingTool {

    @Tool("Returns a friendly banking greeting message.")
    public String greet(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            return "Hello! Welcome to our bank. How may I assist you today?";
        }
        return "Hello " + customerName + "! Welcome to our bank. How may I assist you today?";
    }
}
