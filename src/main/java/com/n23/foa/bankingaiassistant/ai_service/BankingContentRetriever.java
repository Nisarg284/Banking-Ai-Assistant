package com.n23.foa.bankingaiassistant.ai_service;

import com.n23.foa.bankingaiassistant.ai.DepartmentClassifier;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component
public class BankingContentRetriever implements ContentRetriever {

    private static final Logger log = LoggerFactory.getLogger(BankingContentRetriever.class);

    private static final Pattern GREETING_PATTERN = Pattern.compile(
            "^(hi|hello|hey|good\\s*(morning|afternoon|evening|day)|greetings|howdy|sup|yo|thanks|thank\\s*you)\\b[!.? ]*$",
            Pattern.CASE_INSENSITIVE
    );

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final DepartmentClassifier departmentClassifier;

    // Cache for query embeddings to eliminate redundant embedding computation
    private final Map<String, Embedding> embeddingCache = new ConcurrentHashMap<>(256);

    // In-memory fallback map: department -> document contents
    private final Map<String, String> localDocumentCache = new ConcurrentHashMap<>();

    public BankingContentRetriever(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore, DepartmentClassifier departmentClassifier) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.departmentClassifier = departmentClassifier;
    }

    @PostConstruct
    public void initLocalCache() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:banking-docs/*.txt");
            for (Resource res : resources) {
                String filename = res.getFilename();
                if (filename != null) {
                    String dept = filename.replace(".txt", "").split("-")[0].toUpperCase();
                    String content = res.getContentAsString(StandardCharsets.UTF_8);
                    localDocumentCache.put(dept, content);
                    log.info("Preloaded local fallback document for department: {}", dept);
                }
            }
        } catch (IOException e) {
            log.warn("Could not preload local banking docs for fallback: {}", e.getMessage());
        }
    }

    @Override
    public List<Content> retrieve(Query query) {
        String question = query.text() != null ? query.text().trim() : "";
        if (question.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Short-circuit greetings instantly (0 ms latency, no vector search needed)
        if (isGreeting(question)) {
            log.debug("Query '{}' recognized as greeting. Skipping document retrieval.", question);
            return Collections.emptyList();
        }

        // 2. Resolve target departments using fast keyword matching first (sub-millisecond)
        List<String> departments = resolveDepartments(question);
        log.debug("Query '{}' resolved to departments: {}", question, departments);

        try {
            // 3. Obtain query embedding (with fast LRU/concurrent cache)
            String normalizedKey = question.toLowerCase();
            Embedding queryEmbedding = embeddingCache.computeIfAbsent(normalizedKey, q ->
                    embeddingModel.embed(question).content()
            );

            // 4. Construct embedding search request
            EmbeddingSearchRequest.EmbeddingSearchRequestBuilder requestBuilder = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(4);

            // Only apply department filter if not all departments are targeted
            if (departments.size() < 3) {
                Filter filter = MetadataFilterBuilder
                        .metadataKey("department")
                        .isIn(departments);
                requestBuilder.filter(filter);
            }

            EmbeddingSearchResult<TextSegment> result = embeddingStore.search(requestBuilder.build());

            if (result != null && result.matches() != null && !result.matches().isEmpty()) {
                log.debug("Retrieved {} matches from vector store", result.matches().size());
                return result.matches().stream()
                        .map(match -> Content.from(match.embedded()))
                        .toList();
            }
        } catch (Exception ex) {
            log.warn("Vector store retrieval encountered an issue: {}. Using resilient in-memory fallback.", ex.getMessage());
        }

        // 5. Fallback: Return preloaded local policy contents for the requested departments
        log.debug("Falling back to preloaded document text for departments: {}", departments);
        List<Content> fallbackContents = new ArrayList<>();
        for (String dept : departments) {
            String docText = localDocumentCache.get(dept);
            if (docText != null) {
                fallbackContents.add(Content.from(docText));
            }
        }

        return fallbackContents;
    }

    private boolean isGreeting(String question) {
        String cleaned = question.replaceAll("[^a-zA-Z ]", "").trim();
        return GREETING_PATTERN.matcher(cleaned).matches();
    }

    private List<String> resolveDepartments(String question) {
        Set<String> departments = new LinkedHashSet<>();
        String normalizedQuestion = question.toUpperCase();

        // High-speed keyword resolution (avoids expensive LLM roundtrip for 99% of queries)
        if (normalizedQuestion.contains("FD") || normalizedQuestion.contains("FIXED DEPOSIT") || normalizedQuestion.contains("DEPOSIT")) {
            departments.add("FD");
        }
        if (normalizedQuestion.contains("LOAN") || normalizedQuestion.contains("EMI") || normalizedQuestion.contains("ELIGIB")
                || normalizedQuestion.contains("INTEREST") || normalizedQuestion.contains("TENURE") || normalizedQuestion.contains("BORROW")) {
            departments.add("LOAN");
        }
        if (normalizedQuestion.contains("KYC") || normalizedQuestion.contains("DOCUMENT") || normalizedQuestion.contains("AADHAAR")
                || normalizedQuestion.contains("PAN") || normalizedQuestion.contains("PASSPORT") || normalizedQuestion.contains("VERIF")) {
            departments.add("KYC");
        }

        // If fast detection matched departments, return immediately without LLM overhead!
        if (!departments.isEmpty()) {
            return List.copyOf(departments);
        }

        // Fallback: Use LLM classifier only for ambiguous non-keyword questions
        try {
            String raw = departmentClassifier.classify(question);
            if (raw != null) {
                Arrays.stream(raw.split(","))
                        .map(value -> value.replaceAll("[\\[\\]\\\"]", "").trim().toUpperCase())
                        .filter(Set.of("FD", "LOAN", "KYC")::contains)
                        .forEach(departments::add);
            }
        } catch (Exception ex) {
            log.warn("Department classification fallback failed: {}", ex.getMessage());
        }

        return departments.isEmpty() ? List.of("FD", "LOAN", "KYC") : List.copyOf(departments);
    }
}
