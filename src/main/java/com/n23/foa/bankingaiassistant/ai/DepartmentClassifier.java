package com.n23.foa.bankingaiassistant.ai;

import dev.langchain4j.service.SystemMessage;

import java.util.List;

public interface DepartmentClassifier {

    @SystemMessage("""
                You are a banking department classifier.
                
                Available departments:
                
                FD
                LOAN
                KYC
                
                Return ALL relevant departments as one comma-separated string.
                
                Rules:
                - Return only department names: FD, LOAN, and/or KYC.
                - Separate multiple department names with commas.
                - Do not return JSON, brackets, or an explanation.
                
                Examples:
                
                Question:
                Compare FD and Loan Policies
                
                Answer:
                FD,LOAN
                
                Question:
                Required documents for KYC
                
                Answer:
                KYC
                
                Question:
                Compare FD, KYC and Loan policies
                
                Answer:
                FD,KYC,LOAN
                """)
    String classify(String question);
}
