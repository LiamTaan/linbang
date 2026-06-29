package cn.iocoder.yudao.framework.websocket.core.security;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.filter.TokenAuthenticationFilter;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.websocket.core.util.WebSocketFrameworkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 登录用户的 {@link HandshakeInterceptor} 实现类
 *
 * 流程如下：
 * 1. 前端连接 websocket 时，会通过拼接 ?token={token} 到 ws:// 连接后，这样它可以被 {@link TokenAuthenticationFilter} 所认证通过
 * 2. {@link LoginUserHandshakeInterceptor} 负责把 {@link LoginUser} 添加到 {@link WebSocketSession} 中
 *
 * @author 芋道源码
 */
@RequiredArgsConstructor
public class LoginUserHandshakeInterceptor implements HandshakeInterceptor {

    private static final String SCENE_WEBSOCKET = "WEBSOCKET";

    private final OAuth2TokenCommonApi oauth2TokenApi;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (request instanceof ServletServerHttpRequest) {
            String token = ((ServletServerHttpRequest) request).getServletRequest().getParameter("token");
            if (StrUtil.isNotBlank(token)) {
                OAuth2AccessTokenCheckRespDTO ticket = oauth2TokenApi.consumeSceneTicket(token, SCENE_WEBSOCKET);
                loginUser = new LoginUser().setId(ticket.getUserId()).setUserType(ticket.getUserType())
                        .setInfo(ticket.getUserInfo()).setTenantId(ticket.getTenantId())
                        .setScopes(ticket.getScopes()).setExpiresTime(ticket.getExpiresTime());
            }
        }
        if (loginUser == null) {
            return false;
        }
        WebSocketFrameworkUtils.setLoginUser(loginUser, attributes);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // do nothing
    }

}
