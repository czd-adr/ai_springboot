package com.itheima.heimaai.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AngryEvent {
    private Integer id;

    private String reason;

    public AngryEvent(int i, String s) {
    }
}
