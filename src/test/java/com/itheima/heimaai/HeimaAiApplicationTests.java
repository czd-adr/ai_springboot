package com.itheima.heimaai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HeimaAiApplicationTests {
    @Autowired
    private OpenAiEmbeddingModel openAiEmbeddingModel;
    @Test
    void contextLoads() {
        float[] textVector = openAiEmbeddingModel.embed("贝壳里隐藏什么依赖?");
        System.out.println(textVector);
    }

}
