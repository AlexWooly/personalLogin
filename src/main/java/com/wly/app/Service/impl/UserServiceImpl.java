package com.wly.app.Service.impl;

import com.wly.app.Service.UserService;
import com.wly.app.config.JwtUtils;
import com.wly.app.entity.UserDO;
import com.wly.app.entity.UserRegisterParam;
import com.wly.app.mapper.UserMapper;
import com.wly.app.model.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EmailCodeService emailCodeService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public int save(UserDO userDO) {
        return userMapper.insert(userDO);
    }

    @Override
    public UserDO findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public JsonData register(UserRegisterParam param) {
        //先去redis校验是否已经注册
        String pas=(String) redisTemplate.opsForValue().get(param.getUsername()+"registered");
        //如果redis没有则去数据库校验
        if(pas==null) {
            //根据邮箱去数据库查询
            UserDO userDO = userMapper.selectByUsername(param.getUsername());
            //判断有没有该用户
            if (userDO != null) {
                return JsonData.buildError("用户已经被注册");
            }
        }else {
            return JsonData.buildError("用户已经被注册");
        }
        JsonData data =  emailCodeService.verificateCode(param.getUsername(), param.getCode());
        if (data.getCode() == -1){
            return data;
        }

        try {
            // 密码加密存储
            UserDO userDO = param.convert();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String password = bCryptPasswordEncoder.encode(userDO.getPassword());
            userDO.setPassword(password);
            // 写入数据库
            userMapper.insert(userDO);
            //redis缓存，避免缓存穿透
            redisTemplate.opsForValue().set(userDO.getUsername()+"registered","registered",60*60*24, TimeUnit.SECONDS);
            return JsonData.buildSuccess("注册成功");
        } catch (Exception e) {
            // 注册错误
            return JsonData.buildError("注册错误");
        }
    }
}
