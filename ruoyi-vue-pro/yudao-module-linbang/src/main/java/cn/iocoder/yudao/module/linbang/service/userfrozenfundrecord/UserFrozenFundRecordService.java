package cn.iocoder.yudao.module.linbang.service.userfrozenfundrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordRespVO;

public interface UserFrozenFundRecordService {

    PageResult<UserFrozenFundRecordRespVO> getPage(UserFrozenFundRecordPageReqVO reqVO);

    UserFrozenFundRecordDetailRespVO getDetail(Long id);
}
