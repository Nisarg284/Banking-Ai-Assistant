package com.n23.foa.bankingaiassistant.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingConfig.class);

    @Value("${qdrant.host-name:localhost}")
    private String qdrantHost;

    @Value("${qdrant.port:6334}")
    private int port;

    @Value("${qdrant.collection-name:banking-docs}")
    private String collectionName;

    @Value("${qdrant.api-key:}")
    private String qdrantApiKey;

    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("Using ultra-fast, 100% free in-process AllMiniLmL6V2EmbeddingModel (ONNX runtime, 384 dimensions)");
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        String effectiveHost = (qdrantHost == null || qdrantHost.isBlank()) ? "localhost" : qdrantHost;
        int effectivePort = port > 0 ? port : 6334;
        String effectiveCollection = (collectionName == null || collectionName.isBlank()) ? "banking-docs" : collectionName;

        QdrantEmbeddingStore.Builder builder = QdrantEmbeddingStore.builder()
                .host(effectiveHost)
                .port(effectivePort)
                .collectionName(effectiveCollection);

        if (qdrantApiKey != null && !qdrantApiKey.isBlank() && !qdrantApiKey.startsWith("${")) {
            builder.apiKey(qdrantApiKey).useTls(true);
        } else {
            builder.useTls(false);
        }

        return builder.build();
    }

    @Bean
    public DocumentSplitter documentSplitter() {
        return DocumentSplitters.recursive(250, 40);
    }
}
