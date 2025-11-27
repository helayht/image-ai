package com.yht.image.ai.controller;

import com.yht.image.ai.controller.dto.UserRequestDTO;
import com.yht.image.ai.service.IUserService;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.model.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/25
 */
@RestController
@CrossOrigin
@RequestMapping("/image/ai/api/user")
public class UserController {
    @Resource
    private IUserService userService;

    @PostMapping("/login")
    public Response<Boolean> login(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            String username = userRequestDTO.getUsername();
            String password = userRequestDTO.getPassword();
            if(username == null || password == null){
                return Response.<Boolean>builder()
                        .code(Constants.ResponseCode.SUCCESS.getCode())
                        .info(Constants.ResponseCode.SUCCESS.getMessage())
                        .data(false)
                        .build();
            }
            if(userService.login(username,password)){
                return Response.<Boolean>builder()
                        .code(Constants.ResponseCode.SUCCESS.getCode())
                        .info(Constants.ResponseCode.SUCCESS.getMessage())
                        .data(true)
                        .build();
            }else{
                return Response.<Boolean>builder()
                        .code(Constants.ResponseCode.FAIL.getCode())
                        .info(Constants.ResponseCode.FAIL.getMessage())
                        .data(false)
                        .build();
            }
        } catch (Exception e) {
            return Response.<Boolean>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .data(false)
                    .build();
        }
    }

    @PostMapping("/register")
    public Response<Boolean> register(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            String username = userRequestDTO.getUsername();
            String password = userRequestDTO.getPassword();
            if(username == null || password == null){
                return Response.<Boolean>builder()
                        .code(Constants.ResponseCode.SUCCESS.getCode())
                        .info(Constants.ResponseCode.SUCCESS.getMessage())
                        .data(false)
                        .build();
            }
            if(userService.register(username,password)){
                return Response.<Boolean>builder()
                        .code(Constants.ResponseCode.SUCCESS.getCode())
                        .info(Constants.ResponseCode.SUCCESS.getMessage())
                        .data(true)
                        .build();
            }else{
                return Response.<Boolean>builder()
                        .code(Constants.ResponseCode.FAIL.getCode())
                        .info(Constants.ResponseCode.FAIL.getMessage())
                        .data(false)
                        .build();
            }
        }catch (Exception e){
            return Response.<Boolean>builder()
                    .code(Constants.ResponseCode.FAIL.getCode())
                    .info(Constants.ResponseCode.FAIL.getMessage())
                    .data(false)
                    .build();
        }
    }
}
