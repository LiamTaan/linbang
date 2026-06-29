package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameProgressRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameStartVerifyRespVO;

import javax.validation.Valid;

public interface AppMemberRealNameService {

    AppMemberRealNameStartVerifyRespVO startVerify(Long authUserId);

    Long createOrUpdateRealName(Long authUserId, @Valid AppMemberRealNameCreateReqVO reqVO);

    AppMemberRealNameRespVO getRealName(Long authUserId);

    AppMemberRealNameProgressRespVO getProgress(Long authUserId);
}
