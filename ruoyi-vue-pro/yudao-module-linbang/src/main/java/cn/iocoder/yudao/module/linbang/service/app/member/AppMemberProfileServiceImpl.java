package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

@Service
@Validated
public class AppMemberProfileServiceImpl implements AppMemberProfileService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;

    @Override
    public AppMemberProfileRespVO getProfile(Long authUserId) {
        MemberUserDO user = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(user.getId());
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(MerchantInfoDO::getUserId, authUserId);

        AppMemberProfileRespVO respVO = new AppMemberProfileRespVO();
        respVO.setId(user.getId());
        respVO.setUserNo(user.getUserNo());
        respVO.setMobile(user.getMobile());
        respVO.setNickname(user.getNickname());
        respVO.setAvatar(user.getAvatar());
        respVO.setCurrentRoleCode(user.getCurrentRoleCode());
        respVO.setRealNameStatus(realName != null ? realName.getAuditStatus() : null);
        respVO.setCreditScore(merchantInfo != null ? merchantInfo.getCreditScore() : 100);
        respVO.setCreditLevel(merchantInfo != null ? merchantInfo.getCreditLevel() : "NORMAL");
        return respVO;
    }

    @Override
    public void updateProfile(Long authUserId, AppMemberProfileUpdateReqVO reqVO) {
        memberUserService.updateMemberUserProfile(authUserId, reqVO.getNickname(), reqVO.getAvatar(),
                reqVO.getGender(), reqVO.getBirthday());
    }
}
