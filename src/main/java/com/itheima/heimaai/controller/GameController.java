package com.itheima.heimaai.controller;

import com.itheima.heimaai.constants.AngryEventConstant;
import com.itheima.heimaai.dto.ChatRequest;
import com.itheima.heimaai.entity.vo.AngryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/")
public class GameController {
    private final ChatClient gameChatClient;
    @PostMapping("/game")  // ✅ 明确是 POST 请求
    public Flux<String> chat(@RequestBody ChatRequest request) {

        //请求模型
        return gameChatClient.prompt()
                .user(request.getPrompt())
                .advisors(a -> a.param("CHAT_MEMORY_CONVERSATION_ID_KEY", request.getConversationId()))
                .stream()
                .content();
    }
    @GetMapping("/angry-events")
    public List<AngryEvent> getAngryEvents() {
        System.out.println("213");
        List<AngryEvent> events = new ArrayList<>(AngryEventConstant.ANGRY_EVENTS);
        Collections.shuffle(events);
        return events;
    }
}
