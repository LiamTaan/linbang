package cn.iocoder.yudao.module.linbang.service.platformconfig;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.dal.mysql.config.ConfigMapper;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppAgreementRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppPlatformSettingsRespVO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PLATFORM_CONFIG_CATEGORY_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PLATFORM_CONFIG_NOT_EXISTS;

@Service
@Validated
public class PlatformConfigServiceImpl implements PlatformConfigService {

    @Resource
    private ConfigService configService;
    @Resource
    private ConfigMapper configMapper;

    @Override
    public AppPlatformSettingsRespVO getAppSettings() {
        AppPlatformSettingsRespVO respVO = new AppPlatformSettingsRespVO();
        respVO.setServiceHotline(getValue(PlatformConfigKeyConstants.APP_SERVICE_HOTLINE));
        respVO.setServiceWechat(getValue(PlatformConfigKeyConstants.APP_SERVICE_WECHAT));
        respVO.setAboutUs(getValue(PlatformConfigKeyConstants.APP_ABOUT_US));
        respVO.setIosDownloadUrl(getValue(PlatformConfigKeyConstants.APP_IOS_DOWNLOAD_URL));
        respVO.setAndroidDownloadUrl(getValue(PlatformConfigKeyConstants.APP_ANDROID_DOWNLOAD_URL));
        respVO.setMessageNotice(getValue(PlatformConfigKeyConstants.APP_MESSAGE_NOTICE));
        respVO.setFeedbackTypes(splitValues(getValue(PlatformConfigKeyConstants.APP_FEEDBACK_TYPES)));
        return respVO;
    }

    @Override
    public AppAgreementRespVO getAgreement() {
        AppAgreementRespVO respVO = new AppAgreementRespVO();
        respVO.setServiceAgreement(getValue(PlatformConfigKeyConstants.AGREEMENT_SERVICE));
        respVO.setPrivacyAgreement(getValue(PlatformConfigKeyConstants.AGREEMENT_PRIVACY));
        return respVO;
    }

    @Override
    public PageResult<PlatformConfigRespVO> getPlatformConfigPage(PlatformConfigPageReqVO reqVO) {
        PageResult<ConfigDO> page = configMapper.selectPage(reqVO, new LambdaQueryWrapperX<ConfigDO>()
                .inIfPresent(ConfigDO::getCategory, PlatformConfigKeyConstants.SUPPORTED_CATEGORIES)
                .eqIfPresent(ConfigDO::getCategory, reqVO.getCategory())
                .likeIfPresent(ConfigDO::getName, reqVO.getName())
                .likeIfPresent(ConfigDO::getConfigKey, reqVO.getKey())
                .betweenIfPresent(ConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ConfigDO::getId));
        List<PlatformConfigRespVO> list = page.getList().stream().map(this::toRespVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PlatformConfigRespVO getPlatformConfig(Long id) {
        return toRespVO(getValidatedConfig(id));
    }

    @Override
    public PlatformConfigDetailRespVO getPlatformConfigDetail(Long id) {
        ConfigDO config = getValidatedConfig(id);
        List<ConfigDO> sameCategoryConfigs = configMapper.selectList(new LambdaQueryWrapperX<ConfigDO>()
                .eq(ConfigDO::getCategory, config.getCategory())
                .orderByDesc(ConfigDO::getId));
        return PlatformConfigDetailAssembler.build(config, sameCategoryConfigs);
    }

    @Override
    public Long createPlatformConfig(PlatformConfigSaveReqVO reqVO) {
        validateSupportedCategory(reqVO.getCategory());
        ConfigSaveReqVO saveReqVO = toSaveReqVO(reqVO);
        return configService.createConfig(saveReqVO);
    }

    @Override
    public void updatePlatformConfig(PlatformConfigSaveReqVO reqVO) {
        validateSupportedCategory(reqVO.getCategory());
        getValidatedConfig(reqVO.getId());
        ConfigSaveReqVO saveReqVO = toSaveReqVO(reqVO);
        saveReqVO.setId(reqVO.getId());
        configService.updateConfig(saveReqVO);
    }

    private PlatformConfigRespVO toRespVO(ConfigDO configDO) {
        PlatformConfigRespVO respVO = new PlatformConfigRespVO();
        respVO.setId(configDO.getId());
        respVO.setCategory(configDO.getCategory());
        respVO.setName(configDO.getName());
        respVO.setKey(configDO.getConfigKey());
        respVO.setValue(configDO.getValue());
        respVO.setVisible(configDO.getVisible());
        respVO.setRemark(configDO.getRemark());
        respVO.setCreateTime(configDO.getCreateTime());
        return respVO;
    }

    private ConfigSaveReqVO toSaveReqVO(PlatformConfigSaveReqVO reqVO) {
        ConfigSaveReqVO saveReqVO = new ConfigSaveReqVO();
        saveReqVO.setCategory(reqVO.getCategory());
        saveReqVO.setName(reqVO.getName());
        saveReqVO.setKey(reqVO.getKey());
        saveReqVO.setValue(reqVO.getValue());
        saveReqVO.setVisible(reqVO.getVisible());
        saveReqVO.setRemark(reqVO.getRemark());
        return saveReqVO;
    }

    private String getValue(String key) {
        ConfigDO config = configService.getConfigByKey(key);
        return config == null || config.getValue() == null ? "" : config.getValue();
    }

    private List<String> splitValues(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private void validateSupportedCategory(String category) {
        if (!PlatformConfigKeyConstants.SUPPORTED_CATEGORIES.contains(category)) {
            throw exception(PLATFORM_CONFIG_CATEGORY_INVALID);
        }
    }

    private ConfigDO getValidatedConfig(Long id) {
        ConfigDO config = configMapper.selectById(id);
        if (config == null || !PlatformConfigKeyConstants.SUPPORTED_CATEGORIES.contains(config.getCategory())) {
            throw exception(PLATFORM_CONFIG_NOT_EXISTS);
        }
        return config;
    }
}
