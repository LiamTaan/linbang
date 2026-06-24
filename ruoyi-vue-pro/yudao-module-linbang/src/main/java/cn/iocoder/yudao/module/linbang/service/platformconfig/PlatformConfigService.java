package cn.iocoder.yudao.module.linbang.service.platformconfig;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppAgreementRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppPlatformSettingsRespVO;

public interface PlatformConfigService {

    AppPlatformSettingsRespVO getAppSettings();

    AppAgreementRespVO getAgreement();

    PageResult<PlatformConfigRespVO> getPlatformConfigPage(PlatformConfigPageReqVO reqVO);

    PlatformConfigRespVO getPlatformConfig(Long id);

    PlatformConfigDetailRespVO getPlatformConfigDetail(Long id);

    Long createPlatformConfig(PlatformConfigSaveReqVO reqVO);

    void updatePlatformConfig(PlatformConfigSaveReqVO reqVO);
}
