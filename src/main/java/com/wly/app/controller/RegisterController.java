package com.wly.app.controller;

import com.wly.app.Service.UserService;
import com.wly.app.Service.impl.EmailCodeService;
import com.wly.app.entity.UserDO;
import com.wly.app.entity.UserRegisterParam;
import com.wly.app.mapper.UserMapper;
import com.wly.app.model.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
public class RegisterController {

    @Autowired
    EmailCodeService emailCodeService;

    @Resource
    private UserMapper userMapper;

    @Autowired
    UserService userService;

//    @RequestMapping("/register")
//    public String register() {
//        return "register";
//    }
//
//    @RequestMapping("/register-error")
//    public String registerError(Model model) {
//        // Model 的作用是往 Web 页面穿数据
//        // model 添加一个参数 error 其作用是如果此参数为 true，就显示下面一行 HTML 代码
//        // <p th:if="${error}" class="error">注册错误</p>
//        model.addAttribute("error", true);
//        return "register";
//    }

    @PostMapping("/emailcode")
    @ResponseBody
    public JsonData getCode(@RequestParam("email")String email){
            try {
                return emailCodeService.sendMail(email);
            }catch (Exception e){
                return JsonData.buildError("验证码发送失败");
            }

    }

    @PostMapping("/register-save")
    @ResponseBody
    public JsonData registerSave(@RequestBody UserRegisterParam param) {
        // 判断 username password 不能为空
        if (param.getUsername() == null || param.getPassword() == null || param.getUserRole() == null) {
            return JsonData.buildError("username或password 为空");
        }
        if (param.getCode() == null){
            return JsonData.buildError("验证码为空");
        }
        try {
            return userService.register(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonData.buildError("注册出错了");
    }


}
