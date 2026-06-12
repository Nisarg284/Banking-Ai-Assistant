package com.n23.foa.bankingaiassistant.ai_service;


import com.n23.foa.bankingaiassistant.ai.DepartmentClassifier;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RetrievalService {


    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;
    private final DepartmentClassifier departmentClassifier;
//    private List<String> allMetadata = new ArrayList<>();

    public RetrievalService(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> store, DepartmentClassifier departmentClassifier) {
        this.embeddingModel = embeddingModel;
        this.store = store;
        this.departmentClassifier = departmentClassifier;
    }

//    @PostConstruct
//    public void init() throws IOException {
//        this.allMetadata = getMetadata();
//    }
//
//
//
//    private List<String> getMetadata() throws IOException {
//
//        List<String> allMetadata = new ArrayList<>();
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//
//        Resource[] resources = resolver.getResources("classpath:banking-docs/*.txt");
//
//        for(Resource res : resources){
//            String currFileName = res.getFilename().replace(".txt","")
//                    .split("-")[0]
//                    .toUpperCase();
//
//            allMetadata.add(currFileName);
//        }
//        return allMetadata;
//    }





    public void search(String question) throws IOException {

        List<String> departmentExtractor = departmentClassifier.classify(question);

        // Step 1: User ki query ki embeddings nikalo
        Embedding queryEmbedding = embeddingModel
                                        .embed(question)
                                        .content();

        // step 2: phir uss embedding ki search request banao
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .filter(MetadataFilterBuilder
                        .metadataKey("department")
                        .isIn(departmentExtractor))
                .build();


        // step 3: then uss search request ko embedding store yaani vector store mai search karo
        EmbeddingSearchResult<TextSegment> result = store.search(request);


        result.matches().forEach(
                match -> {
                    System.out.println("Score: "+match.score());

                    System.out.println(match.embedded().text());

                    System.out.println("<-------------------------->");
                }
        );
    }
}
