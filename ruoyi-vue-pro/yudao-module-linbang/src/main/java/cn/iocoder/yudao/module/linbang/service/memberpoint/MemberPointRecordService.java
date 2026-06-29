package cn.iocoder.yudao.module.linbang.service.memberpoint;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberpoint.MemberPointRecordDO;

public interface MemberPointRecordService {

    Integer getUserPointBalance(Long userId);

    PageResult<MemberPointRecordDO> getPointRecordPage(Long userId, PageParam pageParam);
}
