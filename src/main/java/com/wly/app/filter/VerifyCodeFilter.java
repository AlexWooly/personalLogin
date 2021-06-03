package com.wly.app.filter;
import com.wly.app.model.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class  VerifyCodeFilter extends OncePerRequestFilter {
    final String defaultFilterProcessUrl = "/doLogin";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if ("POST".equalsIgnoreCase(request.getMethod()) && defaultFilterProcessUrl.equals(request.getServletPath())) {
            // 验证码验证
            String requestCaptcha = request.getParameter("code");
            String genCaptcha = (String) request.getSession().getAttribute("verify_code");
            if (!StringUtils.isEmpty(genCaptcha)) {
                // 随手清除验证码，无论是失败，还是成功。客户端应在登录失败时刷新验证码
                request.getSession().removeAttribute("captcha");
            }
            if (StringUtils.isEmpty(requestCaptcha)||StringUtils.isEmpty(genCaptcha)) {
                OutputStream outputStream = response.getOutputStream();
                String json = new ObjectMapper().writeValueAsString(JsonData.buildError("验证码为空"));
                outputStream.write(json.getBytes());
                outputStream.flush();
                outputStream.close();
            } else if (!genCaptcha.toLowerCase().equals(requestCaptcha.toLowerCase())) {
                OutputStream outputStream = response.getOutputStream();
                String json = new ObjectMapper().writeValueAsString(JsonData.buildError("验证码错误"));
                outputStream.write(json.getBytes());
                outputStream.flush();
                outputStream.close();
            }else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

}