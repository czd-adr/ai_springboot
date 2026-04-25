package com.itheima.heimaai.controller;

import com.itheima.heimaai.entity.Result;
import com.itheima.heimaai.repository.ChatHistoryRepository;
import com.itheima.heimaai.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/pdf")
public class PdfContrioller {
    private final FileRepository fileRepository;

    private final VectorStore vectorStore;

    private final ChatClient pdfChatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat(String prompt, String chatId){
        Resource file = fileRepository.getFile(chatId);
        if(!file.exists()){
            throw new RuntimeException("会话文件不存在！");
        }
        chatHistoryRepository.save("pdf",chatId);
        return pdfChatClient.prompt()
                .user(prompt)
                .advisors(a->a.param("CHAT_MEMORY_CONVERSATION_ID_KEY",chatId))
                .advisors(a->a.param("FILTER_EXPRESSION","file_name =='"+file.getFilename()+"'"))
                .stream()
                .content();
    }
    //上传
    @RequestMapping("/upload/{chatId}")
    public Result upload(@PathVariable("chatId") String chatId,@RequestParam MultipartFile file) {
        try {
            if(!Objects.equals(file.getContentType(),"application/pdf")){
                return Result.fail("请上传pdf！");
            }
            // 保存文件
            boolean success = fileRepository.save(chatId, file.getResource());
            if(!success){
                return Result.fail("保存失败");
            }
            //写入向量库
            this.writeToVectorStore(file.getResource());
            return Result.ok();
        } catch (Exception e) {
            log.error("fail upload",e);
            return Result.fail("上传失败");
        }
    }
    @GetMapping("/file/{chatId}")
    public ResponseEntity<Resource> download(@PathVariable("chatId") String chatId) throws IOException {
        Resource resource = fileRepository.getFile(chatId);
        if(!resource.exists()){
            return ResponseEntity.notFound().build();
        }
        String filename = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition","attachment; filename=\"")
                .body(resource);
    }
    private void writeToVectorStore(Resource resource) {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                        resource,
                        PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1)
                        .build()
                );
        List<Document> documents = reader.read();
        vectorStore.add(documents);
    }
}
