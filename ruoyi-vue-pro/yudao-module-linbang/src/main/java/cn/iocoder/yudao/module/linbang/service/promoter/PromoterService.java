package cn.iocoder.yudao.module.linbang.service.promoter;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;

public interface PromoterService {

    PromoterDO getOrCreatePromoter(Long userId);

    PageResult<PromoterRespVO> getPromoterPage(PromoterPageReqVO reqVO);

    PromoterDetailRespVO getPromoterDetail(Long id);
}
