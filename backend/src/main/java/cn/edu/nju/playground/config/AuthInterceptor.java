package cn.edu.nju.playground.config;

import cn.edu.nju.playground.exception.PlaygroundException;
import cn.edu.nju.playground.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenUtil tokenUtil;

    private static final Map<String, String> WHITELIST = Map.of(
        "/api/auth/login", "POST",
        "/api/auth/register", "POST",
        "/api/activities", "GET",
        "/api/activities/\\d+", "GET",
        "/api/activities/\\d+/comments", "GET",
        "/api/activities/\\d+/comments/**", "GET",
        "/api/user/\\d+/info", "GET",
        "/doc.html", "GET",
        "/swagger-ui/**", "GET",
        "/v3/api-docs/**", "GET"
    );

    /**
     * 检查路径和方法是否在白名单中
     *
     * @param path   请求路径
     * @param method HTTP方法
     * @return 是否在白名单中
     */
    private boolean isWhitelisted(String path, String method) {
        // 移除查询参数
        String cleanPath = path.split("\\?")[0];

        // 遍历白名单进行匹配
        return WHITELIST.entrySet().stream().anyMatch(entry -> {
            String whitelistPath = entry.getKey();
            String whitelistMethod = entry.getValue();

            // 方法必须匹配（忽略大小写）
            if (!whitelistMethod.equalsIgnoreCase(method)) {
                return false;
            }

            // 路径必须匹配
            return cleanPath.equals(whitelistPath) || cleanPath.matches(whitelistPath.replace("*", ".*"));
        });
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if (isWhitelisted(request.getRequestURI(), request.getMethod())) {
            return true; // 如果是白名单中的请求，直接放行
        }

        if (request.getRequestURI().startsWith("/error")) {
            throw PlaygroundException.badRequest();
        }

        String token = tokenUtil.getToken(request);
        if (tokenUtil.validateToken(token)) {
            request.getSession().setAttribute("currentUser", tokenUtil.getUser(token));
            return true;
        }

        throw PlaygroundException.notLoggedIn();
    }
}
