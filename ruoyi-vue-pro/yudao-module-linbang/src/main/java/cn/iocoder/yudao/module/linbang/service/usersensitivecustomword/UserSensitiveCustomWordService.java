package cn.iocoder.yudao.module.linbang.service.usersensitivecustomword;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo.UserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo.UserSensitiveCustomWordRespVO;

public interface UserSensitiveCustomWordService {

    PageResult<UserSensitiveCustomWordRespVO> getPage(UserSensitiveCustomWordPageReqVO reqVO);
}
