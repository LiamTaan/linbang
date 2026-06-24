package cn.iocoder.yudao.module.linbang.service.partnerinfo;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;

import java.util.List;

public interface PartnerInfoService {

    PageResult<PartnerInfoRespVO> getPartnerInfoPage(PartnerInfoPageReqVO reqVO);

    PartnerInfoDO getPartnerInfo(Long id);

    PartnerInfoDetailRespVO getPartnerInfoDetail(Long id);

    PartnerInfoDO getPartnerInfoByUserId(Long userId);

    PartnerInfoDO getOrCreatePartner(Long userId);

    List<String> getPartnerRegionAdcodes(Long partnerId);
}
