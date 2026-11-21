package com.n23.foa.bankingaiassistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.SystemMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LangchainImportsTest {
    @Test
    void testImports() {
        assertNotNull(MemoryId.class);
        assertNotNull(UserMessage.class);
        assertNotNull(SystemMessage.class);
    }
}
