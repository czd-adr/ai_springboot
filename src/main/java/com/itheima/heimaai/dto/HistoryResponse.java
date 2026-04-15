// HistoryResponse.java
package com.itheima.heimaai.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    private String conversationId;          // 会话 ID
    @Singular
    private List<HistoryMessage> messages;  // 历史消息列表
}