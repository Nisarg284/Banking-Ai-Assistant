package com.n23.foa.bankingaiassistant.tools;

import com.n23.foa.bankingaiassistant.dto.EligibilityResult;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class EligibilityTool {

    private static final int MIN_AGE = 21;
    private static final int MAX_AGE = 60;
    private static final double MAX_LOAN_AMOUNT = 20_00_000;

    @Tool("""
            Check loan eligibility.
            
            Call this tool only when customer's age
            and requested loan amount are known.
            """)
    public EligibilityResult checkEligibility(
            Integer currAge,
            Double currAmount
    ) {

        System.out.println("Inside check eligibility tool");

        if (currAge == null) {
            return new EligibilityResult(
                    false,
                    "Customer age is missing"
            );
        }

        if (currAmount == null) {
            return new EligibilityResult(
                    false,
                    "Requested loan amount is missing"
            );
        }

        if (currAge < MIN_AGE) {
            return new EligibilityResult(
                    false,
                    "Age is below minimum age"
            );
        }

        if (currAge > MAX_AGE) {
            return new EligibilityResult(
                    false,
                    "Age exceeds maximum allowed age"
            );
        }

        if (currAmount > MAX_LOAN_AMOUNT) {
            return new EligibilityResult(
                    false,
                    "Requested loan amount exceeds maximum allowed amount"
            );
        }

        return new EligibilityResult(
                true,
                "Customer is eligible"
        );
    }
}