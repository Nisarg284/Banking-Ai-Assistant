package com.n23.foa.bankingaiassistant;

import com.n23.foa.bankingaiassistant.dto.EligibilityResult;
import com.n23.foa.bankingaiassistant.tools.EligibilityTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EligibilityToolTest {

    private EligibilityTool eligibilityTool;

    @BeforeEach
    void setUp() {
        eligibilityTool = new EligibilityTool();
    }

    @Test
    void testEligibleCustomer() {
        EligibilityResult result = eligibilityTool.checkEligibility(28, 500000.0);
        assertNotNull(result);
        assertTrue(result.eligible());
        assertEquals("Customer is eligible for personal loan", result.reason());
    }

    @Test
    void testBoundaryMinAge() {
        EligibilityResult result21 = eligibilityTool.checkEligibility(21, 500000.0);
        assertTrue(result21.eligible());

        EligibilityResult result20 = eligibilityTool.checkEligibility(20, 500000.0);
        assertFalse(result20.eligible());
        assertTrue(result20.reason().contains("below the minimum required age"));
    }

    @Test
    void testBoundaryMaxAge() {
        EligibilityResult result60 = eligibilityTool.checkEligibility(60, 500000.0);
        assertTrue(result60.eligible());

        EligibilityResult result61 = eligibilityTool.checkEligibility(61, 500000.0);
        assertFalse(result61.eligible());
        assertTrue(result61.reason().contains("exceeds the maximum allowed age"));
    }

    @Test
    void testMaxLoanAmount() {
        EligibilityResult result20L = eligibilityTool.checkEligibility(35, 2000000.0);
        assertTrue(result20L.eligible());

        EligibilityResult resultAbove20L = eligibilityTool.checkEligibility(35, 2000001.0);
        assertFalse(resultAbove20L.eligible());
        assertTrue(resultAbove20L.reason().contains("exceeds the maximum allowed amount"));
    }

    @Test
    void testMissingOrNegativeInputs() {
        EligibilityResult nullAge = eligibilityTool.checkEligibility(null, 500000.0);
        assertFalse(nullAge.eligible());

        EligibilityResult nullAmount = eligibilityTool.checkEligibility(28, null);
        assertFalse(nullAmount.eligible());

        EligibilityResult negativeAge = eligibilityTool.checkEligibility(-5, 500000.0);
        assertFalse(negativeAge.eligible());

        EligibilityResult negativeAmount = eligibilityTool.checkEligibility(28, -100.0);
        assertFalse(negativeAmount.eligible());
    }
}
