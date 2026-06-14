package com.n23.foa.bankingaiassistant.tools;


import com.n23.foa.bankingaiassistant.dto.EmiResult;
import dev.langchain4j.agent.tool.Tool;
import org.apache.james.mime4j.dom.datetime.DateTime;
import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class EmiTool {



    @Tool("Calculate EMI for a loan.")
    public EmiResult calculateEmi(
            double principleAmount,
            double annualRate,
            int years
    ){

        System.out.println("principle amount: "+principleAmount);
        System.out.println("annualRate : "+annualRate);
        System.out.println("years "+years);


        System.out.println("<----------------- Emi Tool called --------------------->");
        double monthlyRate = annualRate / 12 / 100;

        int months = years * 12;

        double emi = (principleAmount * monthlyRate * Math.pow(1 + monthlyRate,months))
                /
                (Math.pow(1 + monthlyRate,months) -1);

        double totalAmount = emi * months;

        double totalInterest = totalAmount - principleAmount;

        return new EmiResult(emi,totalAmount,totalInterest);
    }



    @Tool("Give correct date to user")
    public String currentDate(){

        System.out.println("<---------------inside currentDate tool----------------->");
        return LocalDate.now().toString();
    }
}
