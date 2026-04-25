package com.itheima.heimaai.config;

import com.itheima.heimaai.constants.SystemConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {
    @Bean
    public InMemoryChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    // ✅ 2️⃣ ChatMemory 注入上面的 Repository Bean（共用同一个实例！）
    @Bean
    public ChatMemory chatMemory(InMemoryChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)  // ✅ 注入 Bean，不要 new 新的！
                .maxMessages(20)  // ✅ 可选：限制每个会话保留的消息数
                .build();
    }
    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel openAiEmbeddingModel) {
        return SimpleVectorStore.builder(openAiEmbeddingModel).build();
    }
    @Bean
    public ChatClient chatClient(OllamaChatModel model ,ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem("你是一个热心的智能助手，你的名字叫做小霸王，喜欢健身和打炉石传说，头尖尖的，请以小霸王的身份和语气回答问题。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        // ✅ 绑定记忆顾问，启用多轮对话
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model , ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        // ✅ 绑定记忆顾问，启用多轮对话
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel chatModel,
                                    ChatMemory chatMemory,
                                    VectorStore vectorStore) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你是一个专门处理遥感影像分类和湿地研究的助手。")
                .defaultAdvisors(
                        // 聊天记忆
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),

                        // RAG Advisor：如果这里依然报错，请检查上面提到的 Maven 依赖
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(0.6)
                                        .topK(2)
                                        .build())
                                .build()
                )
                .build();
    }
}
