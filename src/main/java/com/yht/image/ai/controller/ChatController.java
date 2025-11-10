package com.yht.image.ai.controller;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.TaskResponseDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.model.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            String taskId = imageService.TextToImage(chatRequestDTO);
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
        TaskResponseDTO results = imageService.queryResultByTaskId(taskId);
        try {
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
}
