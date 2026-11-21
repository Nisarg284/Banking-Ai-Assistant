package com.n23.foa.bankingaiassistant.ai_service;

import com.n23.foa.bankingaiassistant.ai.DepartmentClassifier;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class RetrievalService {

    private static final Logger log = LoggerFactory.getLogger(RetrievalService.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;
    private final DepartmentClassifier departmentClassifier;

    public RetrievalService(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> store, DepartmentClassifier departmentClassifier) {
        this.embeddingModel = embeddingModel;
        this.store = store;
        this.departmentClassifier = departmentClassifier;
    }

    public void search(String question) throws IOException {
        List<String> departmentExtractor = Arrays.stream(departmentClassifier.classify(question).split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(value -> !value.isBlank())
                .toList();

        Embedding queryEmbedding = embeddingModel.embed(question).content();

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .filter(MetadataFilterBuilder.metadataKey("department").isIn(departmentExtractor))
                .build();

        EmbeddingSearchResult<TextSegment> result = store.search(request);

        result.matches().forEach(match -> {
            log.info("Score: {}", match.score());
            log.info("Text: {}", match.embedded().text());
        });
    }
}
