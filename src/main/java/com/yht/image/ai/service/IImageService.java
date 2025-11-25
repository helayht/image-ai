package com.yht.image.ai.service;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.TaskResponseDTO;
import com.yht.image.ai.service.entity.ChatResultEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/5
 */
public interface IImageService {
    String textToImage(ChatRequestDTO chatRequestDTO);

    TaskResponseDTO queryResultByTaskId(String taskId);

    String imageToImage(ChatRequestDTO chatRequestDTO, MultipartFile imageFile) throws IOException;

    public String createImageUrl(MultipartFile imageFile) throws IOException;
}
