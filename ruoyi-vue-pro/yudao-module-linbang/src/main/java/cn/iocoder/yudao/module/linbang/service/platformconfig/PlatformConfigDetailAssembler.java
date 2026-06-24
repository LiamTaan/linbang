package cn.iocoder.yudao.module.linbang.service.platformconfig;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigDetailRespVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class PlatformConfigDetailAssembler {

    private PlatformConfigDetailAssembler() {
    }

    static PlatformConfigDetailRespVO build(ConfigDO config, List<ConfigDO> sameCategoryConfigs) {
        PlatformConfigDetailRespVO respVO = BeanUtils.toBean(config, PlatformConfigDetailRespVO.class);
        respVO.setSummary(buildSummary(config.getCategory(), sameCategoryConfigs));
        respVO.setRelatedConfigs(buildRelatedConfigs(sameCategoryConfigs, config.getId()));
        return respVO;
    }

    private static PlatformConfigDetailRespVO.SummaryRespVO buildSummary(String category, List<ConfigDO> sameCategoryConfigs) {
        PlatformConfigDetailRespVO.SummaryRespVO summary = new PlatformConfigDetailRespVO.SummaryRespVO();
        List<ConfigDO> source = sameCategoryConfigs == null ? Collections.emptyList() : sameCategoryConfigs;
        summary.setSameCategoryCount(source.size());
        summary.setVisibleCount((int) source.stream().filter(item -> Boolean.TRUE.equals(item.getVisible())).count());
        summary.setHiddenCount((int) source.stream().filter(item -> Boolean.FALSE.equals(item.getVisible())).count());
        summary.setAgreementCategory(PlatformConfigKeyConstants.CATEGORY_AGREEMENT.equals(category));
        summary.setPlatformCategory(PlatformConfigKeyConstants.CATEGORY_PLATFORM.equals(category));
        return summary;
    }

    private static List<PlatformConfigDetailRespVO.RelatedConfigRespVO> buildRelatedConfigs(List<ConfigDO> sameCategoryConfigs,
                                                                                             Long currentId) {
        if (sameCategoryConfigs == null || sameCategoryConfigs.isEmpty()) {
            return Collections.emptyList();
        }
        return sameCategoryConfigs.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, PlatformConfigDetailRespVO.RelatedConfigRespVO.class))
                .collect(Collectors.toList());
    }
}
