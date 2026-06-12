package com.n23.foa.bankingaiassistant.controller;


import com.n23.foa.bankingaiassistant.ai.BankingAssistant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistant")
public class BankingAssistantController {

    private final BankingAssistant bankingAssistant;

    public BankingAssistantController(BankingAssistant bankingAssistant) {
        this.bankingAssistant = bankingAssistant;
    }

    @GetMapping("/ask")
    public String ask(@RequestBody String question){
        return bankingAssistant.chat(question);
    }
}
