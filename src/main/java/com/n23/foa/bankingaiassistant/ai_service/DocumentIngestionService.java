package com.n23.foa.bankingaiassistant.ai_service;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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


    public void ingestAllDocuments() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath:banking-docs/*.txt");

        for(Resource res : resources){
            String currFileName = res.getFilename();
            log.info("<----------------- {} is ingested>",currFileName);
            ingest(currFileName);
        }
    }

    private void ingest(String currFileName) throws IOException {


        // Step 1: Import All Resources
        Resource resource = new ClassPathResource(
                "banking-docs/"+currFileName
        );


        Metadata metadata = new Metadata();

        String metaInfo = currFileName.replace(".txt","")
                        .split("-")[0]
                .toUpperCase();

        metadata.put("department",metaInfo);

        // step 2: now fetching all data from that files and store it into string
        String content = resource.getContentAsString(StandardCharsets.UTF_8);

        // step 3: Store that string into document
        Document document = Document.from(content,metadata);

        // step 4: now split that documents
//        DocumentSplitter splitter = DocumentSplitters.recursive(
//                100,
//                20
//        );

        // step 5: Now store that splitted document into list of text segments
        List<TextSegment> segments = documentSplitter.split(document);

        System.out.println("<---------Size of list---------->"+segments.size());

        segments.forEach(segment ->
        {
            System.out.println("<------------------------------------------->");
            System.out.println(segment.metadata());
        });

        // step 6: convert this text segments into vector
        // we need EmbeddingModel for that
        for(TextSegment segment : segments){
            Embedding embedding =
                    embeddingModel.embed(segment.text())
                            .content();

            store.add(
                    embedding,
                    segment
            );

            System.out.println("-------------------> stored: "+segment.text());
        }

    }
}
