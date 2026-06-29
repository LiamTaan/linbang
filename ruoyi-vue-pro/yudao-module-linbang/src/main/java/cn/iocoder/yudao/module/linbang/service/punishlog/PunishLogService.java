package cn.iocoder.yudao.module.linbang.service.punishlog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogRespVO;

public interface PunishLogService {

    PageResult<PunishLogRespVO> getPage(PunishLogPageReqVO reqVO);

    PunishLogDetailRespVO getDetail(Long id);
}
