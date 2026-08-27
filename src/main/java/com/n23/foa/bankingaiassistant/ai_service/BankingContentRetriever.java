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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class BankingContentRetriever implements ContentRetriever {

    private static final Logger log = LoggerFactory.getLogger(BankingContentRetriever.class);
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final DepartmentClassifier departmentClassifier;



    public BankingContentRetriever(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore, DepartmentClassifier departmentClassifier) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.departmentClassifier = departmentClassifier;
    }

    @Override
    public List<Content> retrieve(Query query) {

        log.info("<--------------- inside Banking content retrieval-------------------->");
        String question = query.text();


        List<String> department = resolveDepartments(question);
        System.out.println(department);

        Embedding queryEmbedding = embeddingModel.embed(question)
                .content();

        Filter filter = MetadataFilterBuilder
                .metadataKey("department")
                .isIn(department);


        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .filter(filter)
                .maxResults(3)
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);

        List<Content> list = result.matches().stream()
                .map(match -> {
                    TextSegment segment = match.embedded();

                    Content content = Content.from(segment);


                    return content;
                }).toList();

        System.out.println("========= RETRIEVED =========");
        System.out.println("========= result matches size %s ========="+result.matches().size());


        result.matches().forEach(
                match -> {
                    System.out.println("-------->"+match.embedded().text());
                }
        );
        System.out.println("=============================");

        return list;
    }

    private List<String> resolveDepartments(String question) {
        Set<String> departments = new LinkedHashSet<>();
        try {
            String raw = departmentClassifier.classify(question);
            if (raw != null) {
                Arrays.stream(raw.split(","))
                        .map(value -> value.replaceAll("[\\[\\]\\\"]", "").trim().toUpperCase())
                        .filter(Set.of("FD", "LOAN", "KYC")::contains)
                        .forEach(departments::add);
            }
        } catch (RuntimeException ex) {
            log.warn("Department classification failed; using keyword fallback", ex);
        }

        String normalizedQuestion = question.toUpperCase();
        if (normalizedQuestion.contains("FD") || normalizedQuestion.contains("FIXED DEPOSIT")) departments.add("FD");
        if (normalizedQuestion.contains("LOAN") || normalizedQuestion.contains("EMI") || normalizedQuestion.contains("ELIGIB")) departments.add("LOAN");
        if (normalizedQuestion.contains("KYC") || normalizedQuestion.contains("DOCUMENT")) departments.add("KYC");

        return departments.isEmpty() ? List.of("FD", "LOAN", "KYC") : List.copyOf(departments);
    }
}
