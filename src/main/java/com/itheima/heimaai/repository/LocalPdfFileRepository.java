package com.itheima.heimaai.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalPdfFileRepository implements FileRepository{
    private final VectorStore vectorStore;

    private final Properties chatFiles = new Properties();

    @Override
    public boolean save(String chatId, Resource resource) {
        //保存文件到本地磁盘
        String filename = resource.getFilename();
        File target = new File(Objects.requireNonNull(filename));
        if(!target.exists()){
            try{
                Files.copy(resource.getInputStream(),target.toPath());
            } catch(IOException e) {
                log.error("failed to save file", e);
                return false;
            }
        }
        chatFiles.put(chatId, filename);
        return true;
    }
    @Override
    public Resource getFile(String chatId) {
        return  new FileSystemResource(chatFiles.getProperty(chatId));
    }
    @PostConstruct
    private void init() {
        FileSystemResource pdfResource = new FileSystemResource("chat-pdf-properties");
        if(pdfResource.exists()){
            try {
                chatFiles.load(new BufferedReader(new InputStreamReader(pdfResource.getInputStream())));
            } catch (IOException e) {
                throw new RuntimeException("failed to load pdf properties", e);
            }
        }
        FileSystemResource vectorStoreResource = new FileSystemResource("chat-pdf.json");
        if(vectorStoreResource.exists()){
            SimpleVectorStore simpleVectorStore = (SimpleVectorStore)vectorStore;
            simpleVectorStore.load(vectorStoreResource);
        }
    }
    @PreDestroy
    private void presistent(){

    }
}
