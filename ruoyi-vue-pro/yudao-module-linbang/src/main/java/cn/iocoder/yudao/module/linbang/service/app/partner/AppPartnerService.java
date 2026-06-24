package cn.iocoder.yudao.module.linbang.service.app.partner;

import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerWorkbenchRespVO;

public interface AppPartnerService {

    AppPartnerWorkbenchRespVO getWorkbench(Long userId);
}
