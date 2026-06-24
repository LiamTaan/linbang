package cn.iocoder.yudao.module.linbang.service.riskevent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventRespVO;

public interface RiskEventService {

    RiskEventDetailRespVO getRiskEventDetail(Long id);

    PageResult<RiskEventRespVO> getRiskEventPage(RiskEventPageReqVO reqVO);
}
