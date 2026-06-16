package com.n23.foa.bankingaiassistant.controller;


import com.n23.foa.bankingaiassistant.ai.BankingAssistant;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assistant")
@CrossOrigin(origins = "*")
public class BankingAssistantController {

    private final BankingAssistant bankingAssistant;

    public BankingAssistantController(BankingAssistant bankingAssistant) {
        this.bankingAssistant = bankingAssistant;
    }

    @PostMapping("/ask")
    public Object ask(@RequestBody String question){
        return bankingAssistant.chat(question);
    }
}
