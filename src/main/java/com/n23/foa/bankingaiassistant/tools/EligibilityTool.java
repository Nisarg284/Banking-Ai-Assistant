package com.n23.foa.bankingaiassistant.tools;

import com.n23.foa.bankingaiassistant.dto.EligibilityResult;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EligibilityTool {

    private static final Logger log = LoggerFactory.getLogger(EligibilityTool.class);

    private static final int MIN_AGE = 21;
    private static final int MAX_AGE = 60;
    private static final double MAX_LOAN_AMOUNT = 20_00_000.0;

    @Tool("""
            Check personal loan eligibility based on the applicant's age and requested loan amount.
            Call this tool only when customer's age and requested loan amount are known.
            """)
    public EligibilityResult checkEligibility(
            @P("The age of the customer in years (e.g. 28)") Integer currAge,
            @P("The requested loan amount in rupees (e.g. 500000)") Double currAmount
    ) {
        log.info("EligibilityTool invoked: age={}, amount={}", currAge, currAmount);

        if (currAge == null) {
            return new EligibilityResult(false, "Customer age is missing");
        }

        if (currAmount == null) {
            return new EligibilityResult(false, "Requested loan amount is missing");
        }

        if (currAge <= 0) {
            return new EligibilityResult(false, "Customer age must be a positive number");
        }

        if (currAmount <= 0) {
            return new EligibilityResult(false, "Requested loan amount must be greater than zero");
        }

        if (currAge < MIN_AGE) {
            return new EligibilityResult(
                    false,
                    "Age is below the minimum required age of " + MIN_AGE + " years"
            );
        }

        if (currAge > MAX_AGE) {
            return new EligibilityResult(
                    false,
                    "Age exceeds the maximum allowed age of " + MAX_AGE + " years"
            );
        }

        if (currAmount > MAX_LOAN_AMOUNT) {
            return new EligibilityResult(
                    false,
                    "Requested loan amount exceeds the maximum allowed amount of ₹" + String.format("%,.0f", MAX_LOAN_AMOUNT)
            );
        }

        return new EligibilityResult(true, "Customer is eligible for personal loan");
    }
}
