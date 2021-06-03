package com.wly.app.config;

import com.wly.app.entity.UserDO;
import com.wly.app.model.JsonData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    public static final String SUBJECT = "njuptwly";  //发行者

    public static final long EXPIRE = 1000 * 60 * 60 * 24 * 7; //过期时间一周

    public static final String APPSECRET = "wly666";  //密钥

    /**
     * 生成jwt
     * @param user 传入用户
     * @return token令牌
     */
    public static String geneJsonWebToken(UserDO user) {

        //对参数进行校验
        if (user == null || user.getUserId() == null || user.getUsername() == null) {
            return null;
        }
        //构建jwt及其内容，claim后相当于key-value形式
        try {
            String token = Jwts.builder().setSubject(SUBJECT)
                    .claim("id", user.getUserId())        //存入信息
                    .claim("name", user.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))  //当前时间戳+过期时间
                    .signWith(SignatureAlgorithm.HS256, APPSECRET)   //设置签名方式 hs256
                    .compact();    //把生成的长串进行压缩，返回String
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonData.buildError("生成token错误").toString();
        }

    }

    /**
     * 校验JWT
     * @param token 令牌
     * @return 用户信息
     */
    public static Claims checkJWT(String token) {
        try {
            //parse生成解析类，加入密钥，解析token，取出claim
            final Claims claims = Jwts.parser().setSigningKey(APPSECRET).parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}