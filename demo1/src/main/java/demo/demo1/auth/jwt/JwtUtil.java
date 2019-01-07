package demo.demo1.auth.jwt;

import demo.demo1.User.model.User;
import demo.demo1.User.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    /**
    token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 设置http head中Authorization字段为token
     */
    public static final String HEADER_STRING = "Authorization";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    private UserService userService;

    /**
     * 依据登录的账号生成token
     */
    public String generateToken(User user) throws Exception {

        if (user == null || user.getId() == null) {
            throw new Exception(String.format("user %s not valid", user));
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getId());
        claims.put("aud", "web");
        claims.put("iss", "demo");
        claims.put("iat", new Date());

        return JwtUtil.TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }

    /**
     * 解析token
     */
    public Claims parseTokenClaims(String token) throws Exception {

        try {
            String pure = token.replace(JwtUtil.TOKEN_PREFIX, "");
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(pure).getBody();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * 验证token
     */
    public Boolean validateToken(String token) {

        try {
            String pure = token.replace(JwtUtil.TOKEN_PREFIX, "");
            Claims claims = parseTokenClaims(pure);
            String subject = claims.getSubject();
            User user = userService.getUserById(UUID.fromString(subject));
            if (user == null) {
                return false;
            } else if (claims.getExpiration().after(new Date())) {
                return true;
            }
            return false;
        } catch (Exception e) {

        }
        return false;
    }

}
