package com.itheima.heimaai.controller;

import com.itheima.heimaai.dto.ChatResponse;
import com.itheima.heimaai.dto.HistoryMessage;
import com.itheima.heimaai.dto.HistoryResponse;
import com.itheima.heimaai.repository.ChatHistoryRepository;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.http.ResponseEntity;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import com.itheima.heimaai.dto.ChatRequest;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/ai")
public class ChatController {

    private final InMemoryChatMemoryRepository chatMemoryRepository;

    private final ChatClient chatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    private final ChatMemory chatMemory;
    public ChatController(InMemoryChatMemoryRepository chatMemoryRepository, ChatClient chatClient, ChatHistoryRepository chatHistoryRepository, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatMemoryRepository = chatMemoryRepository;
        this.chatClient = chatClient;
        this.chatHistoryRepository = chatHistoryRepository;
    }
    @PostMapping("/chat")  // ✅ 明确是 POST 请求
    public Flux<String> chat(@RequestBody ChatRequest request) {


        //保存回话id
        chatHistoryRepository.save("chat", request.getConversationId());




        //请求模型
        return chatClient.prompt()
                .user(request.getPrompt())
                .advisors(a -> a.param("CHAT_MEMORY_CONVERSATION_ID_KEY", request.getConversationId()))
                .stream()
                .content();
    }
    @RequestMapping(value = "/chatFlux",produces = "text/html;charset=utf-8")//流式输出
        public Flux<String> chatFlux(String prompt, String conversationId) {
            return chatClient.prompt()
                    .user(prompt)
                    .advisors(a -> a.param("conversationId", conversationId))
                    .stream()
                    .content();
        }
}
