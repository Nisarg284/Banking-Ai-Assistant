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
                
                Return ALL relevant departments.
                
                Rules:
                - Return comma separated values.
                - Return only department names.
                - No explanation.
                
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
    List<String> classify(String question);
}
