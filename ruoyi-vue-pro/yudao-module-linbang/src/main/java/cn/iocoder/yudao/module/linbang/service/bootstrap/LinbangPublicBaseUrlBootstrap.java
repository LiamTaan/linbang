package cn.iocoder.yudao.module.linbang.service.bootstrap;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileConfigDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileConfigMapper;
import cn.iocoder.yudao.module.infra.framework.file.core.client.db.DBFileClientConfig;
import cn.iocoder.yudao.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class LinbangPublicBaseUrlBootstrap implements ApplicationRunner {

    @Value("${linbang.public-base-url:}")
    private String publicBaseUrl;

    @Resource
    private FileConfigMapper fileConfigMapper;
    @Resource
    private FileConfigService fileConfigService;

    @Override
    public void run(ApplicationArguments args) {
        String normalizedBaseUrl = normalizeUrl(publicBaseUrl);
        if (StrUtil.isBlank(normalizedBaseUrl)) {
            return;
        }
        FileConfigDO masterConfig = fileConfigMapper.selectByMaster();
        if (masterConfig == null || masterConfig.getConfig() == null) {
            return;
        }
        Map<String, Object> updatedConfig = buildUpdatedConfig(masterConfig, normalizedBaseUrl);
        if (updatedConfig == null) {
            return;
        }
        FileConfigSaveReqVO reqVO = new FileConfigSaveReqVO();
        reqVO.setId(masterConfig.getId());
        reqVO.setName(masterConfig.getName());
        reqVO.setStorage(masterConfig.getStorage());
        reqVO.setRemark(masterConfig.getRemark());
        reqVO.setConfig(updatedConfig);
        fileConfigService.updateFileConfig(reqVO);
        log.info("[linbang] 已同步主文件配置域名为 {}", normalizedBaseUrl);
    }

    private Map<String, Object> buildUpdatedConfig(FileConfigDO masterConfig, String normalizedBaseUrl) {
        Object fileClientConfig = masterConfig.getConfig();
        if (fileClientConfig instanceof LocalFileClientConfig) {
            LocalFileClientConfig localConfig = (LocalFileClientConfig) fileClientConfig;
            String currentDomain = normalizeUrl(localConfig.getDomain());
            if (StrUtil.equals(currentDomain, normalizedBaseUrl)) {
                return null;
            }
            localConfig.setDomain(normalizedBaseUrl);
            return toConfigMap(localConfig);
        }
        if (fileClientConfig instanceof DBFileClientConfig) {
            DBFileClientConfig dbConfig = (DBFileClientConfig) fileClientConfig;
            String currentDomain = normalizeUrl(dbConfig.getDomain());
            if (StrUtil.equals(currentDomain, normalizedBaseUrl)) {
                return null;
            }
            dbConfig.setDomain(normalizedBaseUrl);
            return toConfigMap(dbConfig);
        }
        return null;
    }

    private Map<String, Object> toConfigMap(Object config) {
        return JsonUtils.parseObject(JsonUtils.toJsonString(config), new TypeReference<Map<String, Object>>() {
        });
    }

    private String normalizeUrl(String url) {
        return StrUtil.removeSuffix(StrUtil.trimToEmpty(url), "/");
    }
}
