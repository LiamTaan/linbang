package cn.iocoder.yudao.module.system.service.oauth2;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2SceneTicketCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2SceneTicketRespDTO;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.module.system.dal.redis.oauth2.OAuth2SceneTicketRedisDAO;
import cn.iocoder.yudao.module.system.dal.redis.oauth2.OAuth2SceneTicketRedisDAO.SceneTicketValue;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;

@Service
@Validated
public class OAuth2SceneTicketServiceImpl implements OAuth2SceneTicketService {

    public static final String SCENE_JMREPORT = "JMREPORT";
    public static final String SCENE_GOVIEW = "GOVIEW";
    public static final String SCENE_WEBSOCKET = "WEBSOCKET";

    private static final Map<String, Integer> EXPIRES_SECONDS;

    static {
        Map<String, Integer> expires = new HashMap<>();
        expires.put(SCENE_JMREPORT, 600);
        expires.put(SCENE_GOVIEW, 600);
        expires.put(SCENE_WEBSOCKET, 60);
        EXPIRES_SECONDS = Collections.unmodifiableMap(expires);
    }

    @Resource
    private OAuth2SceneTicketRedisDAO oauth2SceneTicketRedisDAO;

    @Override
    public OAuth2SceneTicketRespDTO createSceneTicket(OAuth2SceneTicketCreateReqDTO reqDTO) {
        String scene = normalizeAndValidateScene(reqDTO.getScene());
        Integer expiresInSeconds = EXPIRES_SECONDS.get(scene);
        String token = IdUtil.fastSimpleUUID();
        SceneTicketValue value = new SceneTicketValue();
        value.setScene(scene);
        value.setUserId(reqDTO.getUserId());
        value.setUserType(reqDTO.getUserType());
        value.setTenantId(reqDTO.getTenantId());
        value.setExpireAt(LocalDateTime.now().plusSeconds(expiresInSeconds));
        oauth2SceneTicketRedisDAO.set(token, value);
        OAuth2SceneTicketRespDTO respDTO = new OAuth2SceneTicketRespDTO();
        respDTO.setToken(token);
        respDTO.setExpiresInSeconds(expiresInSeconds);
        return respDTO;
    }

    @Override
    public OAuth2AccessTokenCheckRespDTO checkSceneTicket(String ticket, String scene) {
        return validateTicket(ticket, scene, false);
    }

    @Override
    public OAuth2AccessTokenCheckRespDTO consumeSceneTicket(String ticket, String scene) {
        return validateTicket(ticket, scene, true);
    }

    private OAuth2AccessTokenCheckRespDTO validateTicket(String ticket, String scene, boolean consume) {
        if (StrUtil.isBlank(ticket)) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "场景票据不存在");
        }
        String normalizedScene = normalizeAndValidateScene(scene);
        SceneTicketValue value = consume ? oauth2SceneTicketRedisDAO.consume(ticket) : oauth2SceneTicketRedisDAO.get(ticket);
        if (value == null) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "场景票据不存在");
        }
        if (ObjectUtil.notEqual(normalizedScene, value.getScene())) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "场景票据场景不匹配");
        }
        if (DateUtils.isExpired(value.getExpireAt())) {
            oauth2SceneTicketRedisDAO.delete(ticket);
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "场景票据已过期");
        }
        OAuth2AccessTokenCheckRespDTO respDTO = new OAuth2AccessTokenCheckRespDTO();
        respDTO.setUserId(value.getUserId());
        respDTO.setUserType(value.getUserType());
        respDTO.setTenantId(value.getTenantId());
        respDTO.setExpiresTime(value.getExpireAt());
        respDTO.setUserInfo(Collections.emptyMap());
        respDTO.setScopes(Collections.emptyList());
        return respDTO;
    }

    private String normalizeAndValidateScene(String scene) {
        String normalizedScene = StrUtil.trimToEmpty(scene).toUpperCase();
        if (!EXPIRES_SECONDS.containsKey(normalizedScene)) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "不支持的场景票据");
        }
        return normalizedScene;
    }

}
