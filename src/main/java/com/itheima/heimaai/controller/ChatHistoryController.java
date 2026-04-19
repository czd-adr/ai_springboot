package com.itheima.heimaai.controller;

import com.itheima.heimaai.entity.MessageVo;
import com.itheima.heimaai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {
    private  final ChatHistoryRepository chatHistoryRepository;
    private final ChatMemory chatMemory;

    @GetMapping("/{type}")
    public List<String> getChatIds(@PathVariable("type") String type) {
        return chatHistoryRepository.getChatIds(type);
    }
    @GetMapping("/{type}/{chatId}")
    public List<MessageVo> getChatHistory(@PathVariable("type") String type, @PathVariable("chatId") String chatId) {
        System.out.println(chatId);
            List<Message> messages = chatMemory.get(chatId);
        if (messages == null) {
            return List.of();
        }
        return messages.stream().map(MessageVo::new).toList();
    }
    @GetMapping("/{type}/{chatId}/debug")
    public Map<String, Object> debugChatHistory(@PathVariable String type,
                                                @PathVariable String chatId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 通过 ChatMemory 查询（当前返回空的）
        List<Message> memoryMessages = chatMemory.get(chatId);
        result.put("chatMemory.get()", memoryMessages);
        result.put("memoryMessages size", memoryMessages != null ? memoryMessages.size() : "null");



        // 3. 查询所有 chatId
        List<String> allChatIds = chatHistoryRepository.getChatIds(type);
        result.put("all chatIds for type", allChatIds);

        // 4. 检查 chatId 是否存在
        result.put("chatId exists", allChatIds.contains(chatId));

        return result;
    }
}
