package com.itheima.heimaai.controller;

import com.itheima.heimaai.util.VectorDistance;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.ai.document.Document;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vector")
public class VectorController {
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private OpenAiEmbeddingModel openAiEmbeddingModel;

    private final ChatClient chatClient;
    public VectorController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
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
    @GetMapping("/test-pdf")
    public Map<String, Object> testPdfDocumentReader(@RequestParam(required = false) String query) {
        Map<String, Object> response = new HashMap<>();

        // 1. 固定文件路径
        String filePath = "D:\\Edge\\综合混合像元分解与分形理论方法的遥感蚀变信息提取_乔锴.pdf";
        Resource resource = new FileSystemResource(filePath);

        // 2. 文件存在性校验
        if (!resource.exists()) {
            response.put("status", "error");
            response.put("message", "本地文件未找到，请确认路径: " + filePath);
            return response;
        }

        // 3. 入参 query 校验与设置（如果用户没传，设置默认业务查询）
        if (query == null || query.trim().isEmpty()) {
            query = "什么是分形理论方法？";
        }

        try {
            // 4. 读取 PDF
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                    .withPagesPerDocument(1)
                    .build();

            PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);

            // 注意：有些版本是 get()，有些是 read()，根据你之前的截图请使用 read()
            List<org.springframework.ai.document.Document> documents = reader.read();

            if (documents == null || documents.isEmpty()) {
                response.put("status", "error");
                response.put("message", "PDF解析失败，未提取到文本内容");
                return response;
            }

            // 5. 存入向量库
            vectorStore.add(documents);
            System.out.println(query);
            // 6. 执行搜索
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(2)
                    .similarityThreshold(0.5)
                    .build();

            List<org.springframework.ai.document.Document> resultDocs = vectorStore.similaritySearch(searchRequest);

            // 7. 组装结果，使用 getText() 替代 getContent()
            List<String> contentList = resultDocs.stream()
                    .map(doc -> doc.getText()) // 关键修改点
                    .collect(Collectors.toList());

            response.put("status", "success");
            response.put("file", filePath);
            response.put("query", query);
            response.put("results", contentList);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "处理失败: " + e.getMessage());
        }

        return response;
    }
}
