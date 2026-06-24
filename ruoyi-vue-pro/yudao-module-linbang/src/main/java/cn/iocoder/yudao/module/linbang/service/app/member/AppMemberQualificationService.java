package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationRespVO;

import javax.validation.Valid;

public interface AppMemberQualificationService {

    Long createQualification(Long authUserId, @Valid AppMemberQualificationCreateReqVO reqVO);

    PageResult<AppMemberQualificationRespVO> getQualificationPage(Long authUserId);

}
