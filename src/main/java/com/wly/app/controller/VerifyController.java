package com.wly.app.controller;

import com.wly.app.config.VerifyCodeConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class VerifyController {


    @GetMapping("/vercode")
    public void code(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        VerifyCodeConfig verifyCode = new VerifyCodeConfig();
        BufferedImage image = verifyCode.getImage();
        String text = verifyCode.getText();
        HttpSession session = req.getSession();
        session.setAttribute("verify_code", text);
        verifyCode.output(image, resp.getOutputStream());
    }
}
