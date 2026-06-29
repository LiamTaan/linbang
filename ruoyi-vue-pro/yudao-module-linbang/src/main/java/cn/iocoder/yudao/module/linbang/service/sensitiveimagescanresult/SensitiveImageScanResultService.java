package cn.iocoder.yudao.module.linbang.service.sensitiveimagescanresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo.SensitiveImageScanResultPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo.SensitiveImageScanResultRespVO;

public interface SensitiveImageScanResultService {

    PageResult<SensitiveImageScanResultRespVO> getPage(SensitiveImageScanResultPageReqVO reqVO);
}
