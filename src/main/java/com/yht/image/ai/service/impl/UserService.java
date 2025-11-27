package com.yht.image.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yht.image.ai.mapper.UsersMapper;
import com.yht.image.ai.mapper.po.Users;
import com.yht.image.ai.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/26
 */
@Service
public class UserService implements IUserService {
    @Resource
    private UsersMapper usersMapper;
    @Override
    public boolean login(String username, String password) {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUsername,username).eq(Users::getPassword,password);
        return usersMapper.selectCount(queryWrapper) > 0;
    }
}
