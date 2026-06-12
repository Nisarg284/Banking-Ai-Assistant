package com.n23.foa.bankingaiassistant.ai;

import dev.langchain4j.service.SystemMessage;

import java.util.List;

public interface DepartmentClassifier {

    @SystemMessage("""
            You are an professional Department extractor.
            Your task is to extract department from user query and return it to Uppercase.
            
            Department names are like:  kyc, fd, loan.
            return only department name no other text or sentences.
            """)
    List<String> classify(String question);
}
