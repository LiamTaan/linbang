package cn.iocoder.yudao.module.linbang.service.messageoptimization;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationSaveReqVO;

public interface MessageOptimizationService {

    PageResult<MessageOptimizationRespVO> getPage(MessageOptimizationPageReqVO reqVO);

    MessageOptimizationRespVO get(Long id);

    void save(MessageOptimizationSaveReqVO reqVO);

    int refreshCandidates();
}
