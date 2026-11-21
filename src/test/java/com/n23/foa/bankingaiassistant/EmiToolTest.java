package com.n23.foa.bankingaiassistant;

import com.n23.foa.bankingaiassistant.dto.EmiResult;
import com.n23.foa.bankingaiassistant.tools.EmiTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmiToolTest {

    private EmiTool emiTool;

    @BeforeEach
    void setUp() {
        emiTool = new EmiTool();
    }

    @Test
    void testStandardEmiCalculation() {
        // Principal 1,00,000, 10% annual rate, 1 year (12 months)
        EmiResult result = emiTool.calculateEmi(100000.0, 10.0, 1);
        assertNotNull(result);
        assertTrue(result.emi() > 0);
        assertTrue(result.totalAmount() > 100000.0);
        assertTrue(result.totalInterest() > 0);
        assertEquals(result.totalAmount(), Math.round((result.emi() * 12) * 100.0) / 100.0, 0.1);
    }

    @Test
    void testZeroInterestRate() {
        // Principal 12,000, 0% interest, 1 year -> EMI = 1,000
        EmiResult result = emiTool.calculateEmi(12000.0, 0.0, 1);
        assertNotNull(result);
        assertEquals(1000.0, result.emi(), 0.01);
        assertEquals(12000.0, result.totalAmount(), 0.01);
        assertEquals(0.0, result.totalInterest(), 0.01);
    }

    @Test
    void testInvalidInputs() {
        EmiResult resultNegativePrincipal = emiTool.calculateEmi(-50000.0, 10.0, 2);
        assertEquals(0.0, resultNegativePrincipal.emi());

        EmiResult resultZeroYears = emiTool.calculateEmi(50000.0, 10.0, 0);
        assertEquals(0.0, resultZeroYears.emi());
    }

    @Test
    void testCurrentDate() {
        String date = emiTool.currentDate();
        assertNotNull(date);
        assertFalse(date.isBlank());
    }
}
