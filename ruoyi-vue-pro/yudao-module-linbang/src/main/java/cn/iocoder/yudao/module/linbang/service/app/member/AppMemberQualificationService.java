package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppCertExemptionCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppCertExemptionRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationSummaryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationUpdateReqVO;

import javax.validation.Valid;

public interface AppMemberQualificationService {

    Long createQualification(Long authUserId, @Valid AppMemberQualificationCreateReqVO reqVO);

    void updateQualification(Long authUserId, @Valid AppMemberQualificationUpdateReqVO reqVO);

    PageResult<AppMemberQualificationRespVO> getQualificationPage(Long authUserId);

    AppMemberQualificationRespVO getQualification(Long authUserId, Long id);

    AppMemberQualificationSummaryRespVO getQualificationSummary(Long authUserId);

    PageResult<AppMemberQualificationReminderRespVO> getReminderPage(Long authUserId, AppMemberQualificationReminderPageReqVO reqVO);

    Long createCertExemption(Long authUserId, @Valid AppCertExemptionCreateReqVO reqVO);

    PageResult<AppCertExemptionRespVO> getCertExemptionPage(Long authUserId);

}
