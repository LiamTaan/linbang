package cn.iocoder.yudao.module.linbang.service.matchpushbatch;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchRespVO;

public interface MatchPushBatchService {

    PageResult<MatchPushBatchRespVO> getMatchPushBatchPage(MatchPushBatchPageReqVO reqVO);

    MatchPushBatchRespVO getMatchPushBatch(Long id);
}
