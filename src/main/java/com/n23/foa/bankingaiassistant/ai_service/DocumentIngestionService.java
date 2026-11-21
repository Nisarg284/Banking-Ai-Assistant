package com.n23.foa.bankingaiassistant.ai_service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;
    private final DocumentSplitter documentSplitter;

    public DocumentIngestionService(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> store, DocumentSplitter documentSplitter) {
        this.embeddingModel = embeddingModel;
        this.store = store;
        this.documentSplitter = documentSplitter;
    }

    public Map<String, Object> ingestAllDocuments() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:banking-docs/*.txt");

        List<TextSegment> allSegments = new ArrayList<>();
        int documentsProcessed = 0;

        for (Resource res : resources) {
            String currFileName = res.getFilename();
            if (currFileName == null) continue;

            String dept = currFileName.replace(".txt", "")
                    .split("-")[0]
                    .toUpperCase();

            Metadata metadata = new Metadata();
            metadata.put("department", dept);

            String content = res.getContentAsString(StandardCharsets.UTF_8);
            Document document = Document.from(content, metadata);

            List<TextSegment> segments = documentSplitter.split(document);
            allSegments.addAll(segments);
            documentsProcessed++;

            log.info("Document '{}' split into {} segments for department '{}'", currFileName, segments.size(), dept);
        }

        if (!allSegments.isEmpty()) {
            log.info("Batch embedding {} segments...", allSegments.size());
            Response<List<Embedding>> response = embeddingModel.embedAll(allSegments);
            List<Embedding> embeddings = response.content();

            log.info("Batch storing {} embeddings into vector store...", embeddings.size());
            store.addAll(embeddings, allSegments);
            log.info("Successfully ingested {} segments across {} documents", allSegments.size(), documentsProcessed);
        }

        return Map.of(
                "status", "success",
                "documentsProcessed", documentsProcessed,
                "totalSegmentsIngested", allSegments.size()
        );
    }
}
