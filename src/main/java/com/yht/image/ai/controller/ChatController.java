package com.yht.image.ai.controller;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.TaskResponseDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.model.Response;
import jakarta.annotation.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
@RestController
@CrossOrigin
@RequestMapping("/image/ai/api/chat")
public class ChatController {
    @Resource
    private IImageService imageService;
    @PostMapping("/text_to_image")
    public Response<String> TextToImage(@RequestBody ChatRequestDTO chatRequestDTO) {
        try{
            if(chatRequestDTO.getModels() == null || chatRequestDTO.getModels().size() == 0){
                return Response.<String>builder()
                        .code(Constants.ResponseCode.FAIL.getCode())
                        .info(Constants.ResponseCode.FAIL.getMessage())
                        .build();
            }
            String taskId = imageService.textToImage(chatRequestDTO);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(taskId)
                    .build();
        }catch (Exception e){
            return Response.<String>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
    }

    @GetMapping("/tasks/{taskId}")
    public Response<TaskResponseDTO> getTask(@PathVariable String taskId) {
        try {
            TaskResponseDTO results = imageService.queryResultByTaskId(taskId);
            return Response.<TaskResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(results)
                    .build();
        } catch (Exception e) {
            return Response.<TaskResponseDTO>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
    }

    @PostMapping("/image_to_image")
    public Response<String> ImageToImage(@RequestPart("chatRequestDTO") ChatRequestDTO chatRequestDTO,@RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        if(chatRequestDTO.getModels() == null || chatRequestDTO.getModels().size() == 0){
            return Response.<String>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
        String taskId = imageService.imageToImage(chatRequestDTO,imageFile);
        return Response.<String>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getMessage())
                .data(taskId)
                .build();
    }
}
