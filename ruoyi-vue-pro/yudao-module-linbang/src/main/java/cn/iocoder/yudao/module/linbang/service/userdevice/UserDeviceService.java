package cn.iocoder.yudao.module.linbang.service.userdevice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo.UserDevicePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo.UserDeviceRespVO;

public interface UserDeviceService {

    PageResult<UserDeviceRespVO> getPage(UserDevicePageReqVO reqVO);
}
