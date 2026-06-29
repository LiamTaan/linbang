package cn.iocoder.yudao.module.linbang.service.userriskrelation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo.UserRiskRelationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo.UserRiskRelationRespVO;

public interface UserRiskRelationService {

    PageResult<UserRiskRelationRespVO> getPage(UserRiskRelationPageReqVO reqVO);
}
