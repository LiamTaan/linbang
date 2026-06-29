package cn.iocoder.yudao.module.linbang.service.userrestrictrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordRespVO;

public interface UserRestrictRecordService {

    PageResult<UserRestrictRecordRespVO> getPage(UserRestrictRecordPageReqVO reqVO);

    UserRestrictRecordDetailRespVO getDetail(Long id);
}
