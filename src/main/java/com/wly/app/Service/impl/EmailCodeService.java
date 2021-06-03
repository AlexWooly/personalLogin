package com.wly.app.Service.impl;

import com.wly.app.entity.UserDO;
import com.wly.app.mapper.UserMapper;
import com.wly.app.model.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * author wly
 * data  4.5
 */

@Service
public class EmailCodeService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${sendEmailAddress}")
    private String sendEmailAddress;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static String EMAIL_EMPTY = "100001";

    private static String EMAIL_CODE_EMPTY = "100002";

    private static String VERIFICATE_FAIL = "100003";

    public JsonData sendMail(String email) {
        String format = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        if (!email.matches(format)){
            return JsonData.buildError("邮箱格式错误");
        }
        //先去redis校验是否已经注册
        String pas=(String) redisTemplate.opsForValue().get(email+"registered");
        //如果redis没有则去数据库校验
        if(pas==null) {
            //根据邮箱去数据库查询
            UserDO userDO = userMapper.selectByUsername(email);
            //判断有没有该用户
            if (userDO != null) {
                return JsonData.buildError("用户已经被注册");
            }
        }else {
            return JsonData.buildError("用户已经被注册");
        }
        String precode = (String)redisTemplate.opsForValue().get(email+"code");
        if (precode!=null){
            return JsonData.buildError("请勿频繁发送验证码");
        }
        //随机的验证码
        String code = String.valueOf((int)((Math.random()*9+1)*1000));

        if (StringUtils.isEmpty(email)) {
            return JsonData.buildError("邮箱不能为空");
        }

        if (StringUtils.isEmpty(sendEmailAddress)) {
            return JsonData.buildError("发件人邮箱为空");
        }


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendEmailAddress);
        message.setTo(email);
        message.setSubject("邮箱验证码");
        message.setText(code);
        mailSender.send(message);
        message.setText("");
        //redis缓存
        redisTemplate.opsForValue().set(email+"code", code,60*5, TimeUnit.SECONDS);
        return JsonData.buildSuccess("发送成功");
    }

    public JsonData verificateCode(String email,String code) {

        if (StringUtils.isEmpty(email)) {
            return JsonData.buildError("邮箱不为空");
        }


        String existCode = redisTemplate.opsForValue().get(email);
        if (StringUtils.isEmpty(code)) {
            return JsonData.buildError("验证码为空");
        }

        assert existCode != null;
        if (!existCode.equals(code)) {
            return JsonData.buildError("验证码错误或失效");
        }
        return JsonData.buildSuccess("success");
    }
}
