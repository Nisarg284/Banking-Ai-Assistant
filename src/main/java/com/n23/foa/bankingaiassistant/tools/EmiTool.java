package com.n23.foa.bankingaiassistant.tools;

import com.n23.foa.bankingaiassistant.dto.EmiResult;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
public class EmiTool {

    private static final Logger log = LoggerFactory.getLogger(EmiTool.class);

    @Tool("Calculates the Equated Monthly Installment (EMI) for a loan.")
    public EmiResult calculateEmi(
            @P("The total principal loan amount (e.g., 500000)") double principleAmount,
            @P("The annual interest rate in percentage (e.g., 10.0 for 10%)") double annualRate,
            @P("The total duration of the loan in years (e.g., 5)") int years
    ) {
        log.info("EmiTool invoked: principal={}, annualRate={}, years={}", principleAmount, annualRate, years);

        if (principleAmount <= 0 || years <= 0) {
            return new EmiResult(0.0, 0.0, 0.0);
        }

        int months = years * 12;
        double emi;

        if (annualRate <= 0) {
            emi = principleAmount / months;
        } else {
            double monthlyRate = annualRate / 12.0 / 100.0;
            double factor = Math.pow(1.0 + monthlyRate, months);
            emi = (principleAmount * monthlyRate * factor) / (factor - 1.0);
        }

        double totalAmount = emi * months;
        double totalInterest = totalAmount - principleAmount;

        return new EmiResult(round(emi), round(totalAmount), round(totalInterest));
    }

    @Tool("Provides today's current date.")
    public String currentDate() {
        return LocalDate.now().toString();
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
