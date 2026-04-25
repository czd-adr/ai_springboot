package com.itheima.heimaai.repository;

import org.springframework.core.io.Resource;

public interface FileRepository {
    boolean save(String chatId, Resource resource);//保存文件
    Resource getFile(String chatId);//根据会话id找文件

}
