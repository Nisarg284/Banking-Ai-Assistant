package com.n23.foa.bankingaiassistant;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddingModelTest {

    @Test
    void testAllMiniLmL6V2Embedding() {
        EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();
        Response<Embedding> response = model.embed("What is the interest rate for personal loan?");
        assertNotNull(response);
        assertNotNull(response.content());
        assertEquals(384, response.content().dimension());
        System.out.println("ONNX Embedding generated successfully with dimension: " + response.content().dimension());
    }
}
