// src/main/java/com/itheima/heimaai/dto/ChatResponse.java
package com.itheima.heimaai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String content;             // AI 回复内容
    private String conversationId;      // 会话 ID（前端需保存）
    private boolean isNewSession;       // 是否是新会话
}