package cn.iocoder.yudao.module.linbang.map.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "yudao.map.amap")
@Data
public class AmapProperties {

    /**
     * 是否启用高德地图能力
     */
    private boolean enabled = true;

    /**
     * 高德 Web Service API 地址
     */
    private String baseUrl = "https://restapi.amap.com";

    /**
     * 高德 Web Service Key
     */
    private String key;

    public boolean hasKey() {
        return key != null && !key.trim().isEmpty();
    }

}
