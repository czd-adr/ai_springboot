package com.itheima.heimaai.constants;

import com.itheima.heimaai.entity.vo.AngryEvent;

import java.util.Arrays;
import java.util.List;

public class AngryEventConstant {
    public static final List<AngryEvent> ANGRY_EVENTS = Arrays.asList(
            new AngryEvent(1, "你竟然忘了今天是我们的恋爱纪念日！"),
            new AngryEvent(2,"刚才逛街的时候，你盯着那个穿短裙的女生看了3秒钟！"),
            new AngryEvent(3, "我给你发消息两个小时才回！你是不是不在乎我了？"),
            new AngryEvent(4,"说好陪我看电影，结果你又跟朋友开黑打游戏！"),
            new AngryEvent(5, "你刚才说小美的厨艺很好？怎么，嫌弃做饭不好吃？")
    );
}
