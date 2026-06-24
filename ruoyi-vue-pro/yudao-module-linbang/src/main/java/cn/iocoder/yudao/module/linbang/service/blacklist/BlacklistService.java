package cn.iocoder.yudao.module.linbang.service.blacklist;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistRespVO;

public interface BlacklistService {

    BlacklistDetailRespVO getBlacklistDetail(Long id);

    PageResult<BlacklistRespVO> getBlacklistPage(BlacklistPageReqVO reqVO);
}
