package com.n23.foa.bankingaiassistant.config;


import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {

    @Value("${qdrant.host-name}")
    private String qdrantHost;

    @Value("${qdrant.port}")
    private int port;

    @Value("${qdrant.collection-name}")
    private String collectionName;

    @Bean
    public EmbeddingModel embeddingModel(){
        return new AllMiniLmL6V2EmbeddingModel();
    }


    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(){
        return QdrantEmbeddingStore.builder()
                .host(qdrantHost)
                .port(port)
                .collectionName(collectionName)
                .useTls(false)
                .build();
    }




    @Bean
    DocumentSplitter documentSplitter(){return DocumentSplitters.recursive(100,20);}
}
