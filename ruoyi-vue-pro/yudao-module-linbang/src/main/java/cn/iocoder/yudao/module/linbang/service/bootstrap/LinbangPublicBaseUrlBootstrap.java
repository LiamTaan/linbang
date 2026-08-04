package cn.iocoder.yudao.module.linbang.service.bootstrap;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileConfigDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileConfigMapper;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class LinbangPublicBaseUrlBootstrap implements ApplicationRunner {

    private static final int FILE_URL_SYNC_BATCH_SIZE = 500;

    @Value("${linbang.public-base-url:}")
    private String publicBaseUrl;
    @Value("${server.port:48080}")
    private Integer serverPort;

    @Resource
    private FileConfigMapper fileConfigMapper;
    @Resource
    private FileConfigService fileConfigService;
    @Resource
    private FileMapper fileMapper;

    @Override
    public void run(ApplicationArguments args) {
        String normalizedBaseUrl = resolveEffectiveBaseUrl();
        if (StrUtil.isBlank(normalizedBaseUrl)) {
            return;
        }
        FileConfigDO masterConfig = fileConfigMapper.selectByMaster();
        if (masterConfig == null || masterConfig.getConfig() == null) {
            return;
        }
        if (!isSupportedFileClientConfig(masterConfig.getConfig())) {
            return;
        }
        Map<String, Object> updatedConfig = buildUpdatedConfig(masterConfig, normalizedBaseUrl);
        if (updatedConfig != null) {
            FileConfigSaveReqVO reqVO = new FileConfigSaveReqVO();
            reqVO.setId(masterConfig.getId());
            reqVO.setName(masterConfig.getName());
            reqVO.setStorage(masterConfig.getStorage());
            reqVO.setRemark(masterConfig.getRemark());
            reqVO.setConfig(updatedConfig);
            fileConfigService.updateFileConfig(reqVO);
        }
        int updatedFileCount = syncHistoricalFileUrls(masterConfig.getId(), normalizedBaseUrl);
        log.info("[linbang] 已同步主文件配置域名为 {}，校正历史文件 URL {} 条", normalizedBaseUrl, updatedFileCount);
    }

    int syncHistoricalFileUrls(Long configId, String normalizedBaseUrl) {
        Long afterId = null;
        int updatedCount = 0;
        while (true) {
            List<FileDO> files = fileMapper.selectListByConfigIdAfterId(
                    configId, afterId, FILE_URL_SYNC_BATCH_SIZE);
            if (files.isEmpty()) {
                break;
            }
            List<FileDO> updates = new ArrayList<>();
            for (FileDO file : files) {
                String expectedUrl = buildPublicFileUrl(normalizedBaseUrl, configId, file.getPath());
                if (StrUtil.equals(normalizeUrl(file.getUrl()), expectedUrl)) {
                    continue;
                }
                updates.add(new FileDO().setId(file.getId()).setUrl(expectedUrl));
            }
            if (!updates.isEmpty()) {
                if (!Boolean.TRUE.equals(fileMapper.updateBatch(updates, FILE_URL_SYNC_BATCH_SIZE))) {
                    throw new IllegalStateException("Historical file URL batch update failed");
                }
                updatedCount += updates.size();
            }
            afterId = files.get(files.size() - 1).getId();
            if (files.size() < FILE_URL_SYNC_BATCH_SIZE) {
                break;
            }
        }
        return updatedCount;
    }

    private boolean isSupportedFileClientConfig(Object fileClientConfig) {
        return fileClientConfig instanceof LocalFileClientConfig || fileClientConfig instanceof DBFileClientConfig;
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

    private String resolveEffectiveBaseUrl() {
        String configuredUrl = normalizeUrl(publicBaseUrl);
        if (StrUtil.isBlank(configuredUrl)) {
            return resolveLocalBaseUrl();
        }
        String configuredHost = extractHost(configuredUrl);
        if (StrUtil.isBlank(configuredHost)) {
            return configuredUrl;
        }
        if (!isLocalOrPrivateIp(configuredHost)) {
            return configuredUrl;
        }
        Set<String> localIpv4Hosts = resolveLocalIpv4Hosts();
        if (localIpv4Hosts.contains(configuredHost)) {
            return configuredUrl;
        }
        String localBaseUrl = resolveLocalBaseUrl();
        if (StrUtil.isNotBlank(localBaseUrl)) {
            log.warn("[linbang] 检测到 public-base-url 使用了当前机器不存在的本地地址 {}，自动切换为 {}", configuredUrl, localBaseUrl);
            return localBaseUrl;
        }
        return configuredUrl;
    }

    private String resolveLocalBaseUrl() {
        Set<String> localIpv4Hosts = resolveLocalIpv4Hosts();
        for (String host : localIpv4Hosts) {
            if (!host.startsWith("127.")) {
                return "http://" + host + ":" + serverPort;
            }
        }
        return "http://127.0.0.1:" + serverPort;
    }

    private Set<String> resolveLocalIpv4Hosts() {
        Set<String> hosts = new HashSet<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces != null && interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        hosts.add(address.getHostAddress());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("[linbang] 解析本机 IPv4 地址失败", ex);
        }
        hosts.add("127.0.0.1");
        return hosts;
    }

    private String extractHost(String url) {
        try {
            return URI.create(url).getHost();
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isLocalOrPrivateIp(String host) {
        if ("localhost".equalsIgnoreCase(host) || host.startsWith("127.")) {
            return true;
        }
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isSiteLocalAddress();
        } catch (Exception ex) {
            return false;
        }
    }

    private String buildPublicFileUrl(String normalizedBaseUrl, Long configId, String path) {
        return StrUtil.format("{}/admin-api/infra/file/{}/get/{}",
                normalizedBaseUrl, configId, HttpUtils.encodeUrlPath(path));
    }
}
