package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberLoginLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberLoginLogRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberSecurityUpdatePasswordReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordStatusUpdateReqVO;

import javax.validation.Valid;

public interface AppMemberSecurityService {

    void updatePassword(Long authUserId, @Valid AppMemberSecurityUpdatePasswordReqVO reqVO);

    PageResult<AppMemberLoginLogRespVO> getLoginLogPage(Long authUserId, AppMemberLoginLogPageReqVO reqVO);

    PageResult<AppUserSensitiveCustomWordRespVO> getSensitiveCustomWordPage(Long authUserId,
                                                                            AppUserSensitiveCustomWordPageReqVO reqVO);

    Long createSensitiveCustomWord(Long authUserId, @Valid AppUserSensitiveCustomWordCreateReqVO reqVO);

    void deleteSensitiveCustomWord(Long authUserId, Long id);

    void updateSensitiveCustomWordStatus(Long authUserId, @Valid AppUserSensitiveCustomWordStatusUpdateReqVO reqVO);
}
