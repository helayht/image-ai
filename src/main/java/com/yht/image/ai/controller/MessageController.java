package com.yht.image.ai.controller;

import com.yht.image.ai.controller.dto.MessagesResponseDTO;
import com.yht.image.ai.mapper.po.Conversations;
import com.yht.image.ai.service.IMessagesService;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.model.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/25
 */
@RestController
@CrossOrigin
@RequestMapping("/image/ai/api/message")
public class MessageController {
    @Resource
    private IMessagesService messagesService;
    @GetMapping("/list/{conversationsId}")
    public Response<List<MessagesResponseDTO>> getMessageList(@PathVariable Integer conversationsId){
        try {
            List<MessagesResponseDTO> messagesResponseDTO = messagesService.getMessageList(conversationsId);
            return Response.<List<MessagesResponseDTO>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(messagesResponseDTO)
                    .build();
        } catch (Exception e) {
            return Response.<List<MessagesResponseDTO>>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .data(null)
                    .build();
        }
    }

    @GetMapping("/conversations_list")
    public Response<List<Conversations>> getConversationsList(){
        try {
            List<Conversations> conversations = messagesService.getConversationsList();
            return Response.<List<Conversations>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(conversations)
                    .build();
        } catch (Exception e) {
            return Response.<List<Conversations>>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .data(null)
                    .build();
        }
    }
}
