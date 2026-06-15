package com.n23.foa.bankingaiassistant.ai;

import com.n23.foa.bankingaiassistant.dto.CustomerInfo;
import dev.langchain4j.service.SystemMessage;

public interface CustomerExtractor {


    @SystemMessage("""
            Extract customer information from the given text.
            """)
    CustomerInfo extract(String text);
}
