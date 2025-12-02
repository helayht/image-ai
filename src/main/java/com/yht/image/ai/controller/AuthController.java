package com.yht.image.ai.controller;

import com.yht.image.ai.controller.dto.LoginResponseDTO;
import com.yht.image.ai.service.IAuthService;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.model.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 鉴权控制器
 * @Author hela
 * @Date 2025/12/1
 */
@RestController
@CrossOrigin
@RequestMapping("/image/ai/api/auth")
public class AuthController {
    @Resource
    private IAuthService authService;
    @PostMapping("/login")
    public Response<LoginResponseDTO> login(@RequestParam String code) {
        try {
            LoginResponseDTO token = authService.doLogin(code);
            if(token == null){
                return Response.<LoginResponseDTO>builder()
                        .code(Constants.ResponseCode.FAIL.getCode())
                        .info(Constants.ResponseCode.FAIL.getMessage())
                        .build();
            }
            return Response.<LoginResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getMessage())
                    .data(token)
                    .build();
        } catch (Exception e) {
            return Response.<LoginResponseDTO>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .build();
        }
    }
}
