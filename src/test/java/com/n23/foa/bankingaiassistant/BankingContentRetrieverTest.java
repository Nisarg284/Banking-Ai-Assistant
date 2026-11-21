package com.n23.foa.bankingaiassistant;

import com.n23.foa.bankingaiassistant.ai.DepartmentClassifier;
import com.n23.foa.bankingaiassistant.ai_service.BankingContentRetriever;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankingContentRetrieverTest {

    private BankingContentRetriever retriever;
    private DepartmentClassifier mockClassifier;

    @BeforeEach
    void setUp() {
        mockClassifier = mock(DepartmentClassifier.class);
        retriever = new BankingContentRetriever(
                new AllMiniLmL6V2EmbeddingModel(),
                new InMemoryEmbeddingStore<>(),
                mockClassifier
        );
        retriever.initLocalCache();
    }

    @Test
    void testGreetingShortCircuit() {
        // Greetings must return empty immediately without calling the LLM classifier
        List<Content> results = retriever.retrieve(Query.from("Hello"));
        assertTrue(results.isEmpty());
        verifyNoInteractions(mockClassifier);

        List<Content> resultsHi = retriever.retrieve(Query.from("hi!"));
        assertTrue(resultsHi.isEmpty());
        verifyNoInteractions(mockClassifier);
    }

    @Test
    void testFastKeywordResolutionBypassesClassifier() {
        // Queries with clear keywords like "FD" or "loan" must NOT invoke mockClassifier
        List<Content> results = retriever.retrieve(Query.from("What is the FD policy?"));
        assertFalse(results.isEmpty());
        verifyNoInteractions(mockClassifier);
    }

    @Test
    void testLoanKeywordRetrieval() {
        List<Content> results = retriever.retrieve(Query.from("What are the loan eligibility criteria and EMI?"));
        assertFalse(results.isEmpty());
        verifyNoInteractions(mockClassifier);
    }

    @Test
    void testKycKeywordRetrieval() {
        List<Content> results = retriever.retrieve(Query.from("What KYC documents are required for opening account?"));
        assertFalse(results.isEmpty());
        verifyNoInteractions(mockClassifier);
    }
}
