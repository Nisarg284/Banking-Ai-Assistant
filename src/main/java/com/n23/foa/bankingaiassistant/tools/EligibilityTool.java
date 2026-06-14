package com.n23.foa.bankingaiassistant.tools;

import com.n23.foa.bankingaiassistant.dto.EligibilityResult;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class EligibilityTool {

    @Tool("Check loan eligibility based on age and loan amount")
    public EligibilityResult checkEligibility(
            int currAge,
            int minAge,
            int maxAge,
            double currAmount,
            double maxAmount)
    {
        System.out.println("Inside check eligibility tool");

        if(currAge < minAge){
            return new EligibilityResult(
                    false,
                    "Age is below minimum age"
            );
        }


        if(currAge > maxAge){
            return new EligibilityResult(
                    false,
                    "Age exceeds  minimum allowed age"
            );
        }

        if(currAmount > maxAmount){
            return new EligibilityResult(false,"Requested loan amount exceed maximum allowed amount.");
        }
        return new EligibilityResult(true,"Customer is eligible");

    }
}
