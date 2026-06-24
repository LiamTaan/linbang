package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameRespVO;

import javax.validation.Valid;

public interface AppMemberRealNameService {

    Long createOrUpdateRealName(Long authUserId, @Valid AppMemberRealNameCreateReqVO reqVO);

    AppMemberRealNameRespVO getRealName(Long authUserId);
}
