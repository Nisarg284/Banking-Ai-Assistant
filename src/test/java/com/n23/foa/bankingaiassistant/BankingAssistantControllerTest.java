package com.n23.foa.bankingaiassistant;

import com.n23.foa.bankingaiassistant.ai.BankingAssistant;
import com.n23.foa.bankingaiassistant.controller.BankingAssistantController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankingAssistantController.class)
class BankingAssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankingAssistant bankingAssistant;

    @Test
    void testAskEndpointWithPlainText() throws Exception {
        when(bankingAssistant.chat(eq("default-session"), eq("What is the FD policy?")))
                .thenReturn("The minimum FD amount is 10,000.");

        mockMvc.perform(post("/assistant/ask")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("What is the FD policy?"))
                .andExpect(status().isOk())
                .andExpect(content().string("The minimum FD amount is 10,000."));
    }

    @Test
    void testAskEndpointWithJsonObject() throws Exception {
        when(bankingAssistant.chat(eq("custom-sess-123"), eq("What is the loan interest?")))
                .thenReturn("Personal loan interest rate is 10% per annum.");

        mockMvc.perform(post("/assistant/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Session-Id", "custom-sess-123")
                        .content("{\"question\": \"What is the loan interest?\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Personal loan interest rate is 10% per annum."));
    }

    @Test
    void testAskEndpointEmptyBody() throws Exception {
        mockMvc.perform(post("/assistant/ask")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}
