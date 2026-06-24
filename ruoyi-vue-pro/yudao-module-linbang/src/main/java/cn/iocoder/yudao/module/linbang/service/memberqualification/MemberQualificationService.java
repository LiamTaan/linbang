package cn.iocoder.yudao.module.linbang.service.memberqualification;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationRespVO;

import javax.validation.Valid;

public interface MemberQualificationService {

    PageResult<MemberQualificationRespVO> getQualificationPage(MemberQualificationPageReqVO pageReqVO);

    MemberQualificationDetailRespVO getQualificationDetail(Long id);

    void auditQualification(@Valid MemberQualificationAuditReqVO reqVO);

}
