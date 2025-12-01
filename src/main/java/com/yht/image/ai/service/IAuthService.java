package com.yht.image.ai.service;

import com.yht.image.ai.service.entity.UserMessageEntity;

/**
 * @Description
 * @Author hela
 * @Date 2025/12/1
 */
public interface IAuthService {
    String createVerifyCode(UserMessageEntity userMessageEntity);

    String doLogin(String openId);
}
