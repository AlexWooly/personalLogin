package com.wly.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HelloController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

    @GetMapping("/origin")
    @ResponseBody
    public String origin(){
        return "这是首页，无需登陆";
    }

    @PostMapping("/hello")
    public String hello2(){
        return "/hello";
    }

}
