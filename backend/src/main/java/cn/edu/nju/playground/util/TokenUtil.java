package cn.edu.nju.playground.util;

import cn.edu.nju.playground.model.po.User;
import cn.edu.nju.playground.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenUtil {
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7天过期

    private final UserRepository userRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 生成JWT token
     * @param user 用户信息
     * @return token字符串
     */
    public String generateToken(User user) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);

        return JWT.create()
                .withAudience(String.valueOf(user.getId()))
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(user.getPassword()));
    }

    /**
     * 验证token是否有效
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Long userId = getUserId(token);
            if (userId == null) {
                return false;
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            jwtVerifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从HTTP请求中获取token
     * @param request HTTP请求
     * @return token字符串，如果没有则返回null
     */
    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader(TOKEN_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        try {
            return Long.parseLong(JWT.decode(token).getAudience().get(0));
        } catch (JWTDecodeException | NumberFormatException e) {
            log.debug("解析token中的用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前请求的用户ID
     * @param token JWT token
     * @return 当前用户ID，如果未登录则返回null
     */
    public User getUser(String token) {
        Long userId = getUserId(token);
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * 获取当前请求的用户ID
     * @return 当前用户ID，如果未登录则返回null
     */
    public Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String token = getToken(request);

        if (token == null || !validateToken(token)) {
            return null;
        }

        return getUserId(token);
    }

    /**
     * 获取当前请求的用户
     * @return 当前用户，如果未登录则返回null
     */
    public User getCurrentUser() {
        User currentUser = (User) httpServletRequest.getSession().getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser;
        }

        Long userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }
}