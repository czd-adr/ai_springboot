// HistoryMessage.java
package com.itheima.heimaai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryMessage {
    private String role;        // "user" 或 "assistant"
    private String content;     // 消息内容
    private long timestamp;     // 时间戳（用于显示时间）
}