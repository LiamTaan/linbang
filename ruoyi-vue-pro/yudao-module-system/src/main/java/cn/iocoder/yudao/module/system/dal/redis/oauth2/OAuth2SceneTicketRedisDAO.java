package cn.iocoder.yudao.module.system.dal.redis.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants.OAUTH2_SCENE_TICKET;

@Repository
public class OAuth2SceneTicketRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public SceneTicketValue get(String ticket) {
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(formatKey(ticket)), SceneTicketValue.class);
    }

    public void set(String ticket, SceneTicketValue value) {
        long ttl = LocalDateTimeUtil.between(LocalDateTime.now(), value.getExpireAt(), ChronoUnit.SECONDS);
        if (ttl > 0) {
            stringRedisTemplate.opsForValue().set(formatKey(ticket), JsonUtils.toJsonString(value), ttl, TimeUnit.SECONDS);
        }
    }

    public void delete(String ticket) {
        stringRedisTemplate.delete(formatKey(ticket));
    }

    public SceneTicketValue consume(String ticket) {
        String key = formatKey(ticket);
        String raw = stringRedisTemplate.opsForValue().get(key);
        if (raw == null) {
            return null;
        }
        Boolean deleted = stringRedisTemplate.delete(key);
        return Boolean.TRUE.equals(deleted) ? JsonUtils.parseObject(raw, SceneTicketValue.class) : null;
    }

    private static String formatKey(String ticket) {
        return String.format(OAUTH2_SCENE_TICKET, ticket);
    }

    @Data
    public static class SceneTicketValue {

        private String scene;
        private Long userId;
        private Integer userType;
        private Long tenantId;
        private LocalDateTime expireAt;

    }

}
