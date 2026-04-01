package com.yht.image.ai.controller;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.ChatResponseDTO;
import com.yht.image.ai.controller.dto.TaskResponseDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.ILLMService;
import com.yht.image.ai.service.IMessagesService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.model.Response;
import jakarta.annotation.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    @Resource
    private IMessagesService messagesService;
    @Resource
    private ILLMService llmService;
    @PostMapping("/text_to_image")
    public Response<ChatResponseDTO> TextToImage(@RequestBody ChatRequestDTO chatRequestDTO) {
        try{
            if(chatRequestDTO.getModels() == null || chatRequestDTO.getModels().size() == 0){
                return Response.<ChatResponseDTO>builder()
                        .code(Constants.ResponseCode.FAIL.getCode())
                        .info(Constants.ResponseCode.FAIL.getMessage())
                        .build();
            }
            Integer conversationsId = messagesService.addUserMessage(chatRequestDTO, null);
            chatRequestDTO.setConversationsId(conversationsId);
            chatRequestDTO.setPrompt(llmService.textPromptOptimization(chatRequestDTO.getPrompt()));
            String taskId = imageService.textToImage(chatRequestDTO);
            ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
            chatResponseDTO.setTaskId(taskId);
            chatResponseDTO.setConversationsId(conversationsId);
            return Response.<ChatResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(chatResponseDTO)
                    .build();
        }catch (Exception e){
            return Response.<ChatResponseDTO>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
    }

    @GetMapping("/tasks/{taskId}")
    public Response<TaskResponseDTO> getTask(@PathVariable String taskId) {
        try {
            TaskResponseDTO results = imageService.queryResultByTaskId(taskId);
            if("success".equals(results.getStatus()) && results.getConversationsId() != null){
                messagesService.addAssistantMessage(results.getChatResultEntityList(),taskId);
            }
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
    public Response<ChatResponseDTO> ImageToImage(@RequestPart("chatRequestDTO") ChatRequestDTO chatRequestDTO,@RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        try {
            if(chatRequestDTO.getModels() == null || chatRequestDTO.getModels().size() == 0){
                return Response.<ChatResponseDTO>builder()
                        .code(Constants.ResponseCode.FAIL.getCode())
                        .info(Constants.ResponseCode.FAIL.getMessage())
                        .build();
            }
            String imageUrl = imageService.createFileUrl(imageFile);
            Integer conversationsId = messagesService.addUserMessage(chatRequestDTO, imageUrl);
            String taskId = imageService.imageToImage(chatRequestDTO,imageUrl);
            ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
            chatResponseDTO.setTaskId(taskId);
            chatResponseDTO.setConversationsId(conversationsId);
            return Response.<ChatResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(chatResponseDTO)
                    .build();
        } catch (IOException e) {
            return Response.<ChatResponseDTO>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
    }

    @PostMapping("vedio_to_image")
    public Response<ChatResponseDTO> VedioToImage(@RequestPart("chatRequestDTO") ChatRequestDTO chatRequestDTO,@RequestPart("vedioFile") MultipartFile vedioFile) throws IOException {
        if(chatRequestDTO.getModels() == null || chatRequestDTO.getModels().size() == 0){
            return Response.<ChatResponseDTO>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
        String vedioUrl = imageService.createFileUrl(vedioFile);
        Integer conversationsId = messagesService.addUserMessage(chatRequestDTO, vedioUrl);
        String taskId = imageService.vedioToImage(chatRequestDTO,vedioUrl);
        ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
        chatResponseDTO.setTaskId(taskId);
        chatResponseDTO.setConversationsId(conversationsId);
        return Response.<ChatResponseDTO>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getMessage())
                .data(chatResponseDTO)
                .build();
    }

    @PostMapping("/template_image")
    public Response<ChatResponseDTO> templateImage(@RequestParam String templateCode,
                                                   @RequestParam String username,
                                                   @RequestPart("images") MultipartFile[] images) {
        try {
            String taskId = imageService.templateImage(templateCode, username, images);
            ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
            chatResponseDTO.setTaskId(taskId);
            return Response.<ChatResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(chatResponseDTO)
                    .build();
        } catch (Exception e) {
            return Response.<ChatResponseDTO>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
    }
}
