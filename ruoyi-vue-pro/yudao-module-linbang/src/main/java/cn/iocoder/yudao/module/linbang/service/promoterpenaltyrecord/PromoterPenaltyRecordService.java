package cn.iocoder.yudao.module.linbang.service.promoterpenaltyrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordRespVO;

public interface PromoterPenaltyRecordService {

    PageResult<PromoterPenaltyRecordRespVO> getAdminPage(PromoterPenaltyRecordPageReqVO reqVO);

    PromoterPenaltyRecordDetailRespVO getDetail(Long id);
}
