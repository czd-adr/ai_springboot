package com.itheima.heimaai.controller;

import com.itheima.heimaai.util.VectorDistance;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/vector")
public class VectorController {
    @Autowired
    private OpenAiEmbeddingModel openAiEmbeddingModel;
    @GetMapping("/test-rs-ranking")
    public List<Map<String, Object>> testRemoteSensingRanking() {
        // 1. 定义基准词
        String target = "遥感 remote sensing";
        float[] targetVector = openAiEmbeddingModel.embed(target);

        // 2. 准备对比文本（将基准词自身加入列表的第一项）
        List<String> texts = new ArrayList<>();
        texts.add(target); // 额外加入自身
        texts.add("遥感技术是通过非接触式传感器获取地物信息的过程，广泛应用于地形测绘、环境监测及资源调查。");
        texts.add("利用卫星或航空器搭载的成像雷达与多光谱相机，可以实现对地球表面大面积、远距离的动态观测。");
        texts.add("在现代农业中，精准农业依赖卫星影像分析作物生长状态，通过植被指数评估农田的水分与病虫害。");
        texts.add("虽然摄影测量与远距离传感都涉及影像采集，但遥感更侧重于电磁波特性的定量分析与地物分类。");
        texts.add("深夜的厨房里，厨师正专注地通过文火慢炖出一锅浓郁的汤底，食材的香气在空间内弥漫。");

        // 3. 循环计算距离并封装结果
        List<Map<String, Object>> results = new ArrayList<>();
        for (String text : texts) {
            float[] vector = openAiEmbeddingModel.embed(text);

            double cosine = VectorDistance.cosineSimilarity(targetVector, vector);
            double euclidean = VectorDistance.euclideanDistance(targetVector, vector);

            Map<String, Object> item = new HashMap<>();
            // 标记是否为自身对比
            item.put("text", text.equals(target) ? text + " [自身对比]" : text);
            item.put("cosineSimilarity", cosine);
            item.put("euclideanDistance", euclidean);
            results.add(item);
        }

        // 4. 排序：按余弦相似度从高到低排序
        results.sort((a, b) -> Double.compare((double) b.get("cosineSimilarity"), (double) a.get("cosineSimilarity")));

        return results;
    }
}
