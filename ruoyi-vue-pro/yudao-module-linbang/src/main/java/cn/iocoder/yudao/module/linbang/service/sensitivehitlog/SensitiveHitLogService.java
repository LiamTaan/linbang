package cn.iocoder.yudao.module.linbang.service.sensitivehitlog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogRespVO;

public interface SensitiveHitLogService {

    PageResult<SensitiveHitLogRespVO> getPage(SensitiveHitLogPageReqVO reqVO);

    SensitiveHitLogDetailRespVO getDetail(Long id);
}
