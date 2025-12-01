package com.yht.image.ai.config;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yht.image.ai.service.impl.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author hela
 * @Date 2025/12/1
 */
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取携带的 token 进行验证, 这里自定义
        String token = request.getHeader("token");
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // token 验证
            boolean verify = AuthService.isVerify(token);
            if(!verify){
                responseMap.put("msg", "token 无效");
                throw new RuntimeException();
            }
            responseMap.put("state", true);
            responseMap.put("msg", "请求成功");
            responseMap.put("data", "获取的数据");
            // 验证通过
            return true;
        } catch (SignatureVerificationException e) {
            log.error("签名无线: {}", e.getMessage());
            responseMap.put("msg", "签名无效");
        } catch (TokenExpiredException e2) {
            log.error("token 过期: {}", e2.getMessage());
            responseMap.put("msg", "token 过期");
            throw new RuntimeException(e2);
        } catch (AlgorithmMismatchException | IncorrectClaimException e3) {
            log.error("token 无效: {}", e3.getMessage());
            responseMap.put("msg", "token 无效");
        } catch (Exception e4) {
            log.error("签名无线: {}", e4.getMessage());
            responseMap.put("msg", "签名无效");
        }
        responseMap.put("state", false);

        // 将结果写入响应体返回
        String json = new ObjectMapper().writeValueAsString(responseMap);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // application/json
        PrintWriter writer = response.getWriter();
        writer.println(json);
        return false;
    }
}
