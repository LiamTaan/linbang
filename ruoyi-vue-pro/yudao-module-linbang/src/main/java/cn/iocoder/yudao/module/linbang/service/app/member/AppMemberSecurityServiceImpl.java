package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberLoginLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberLoginLogRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberSecurityUpdatePasswordReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.usersensitivecustomword.UserSensitiveCustomWordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.usersensitivecustomword.UserSensitiveCustomWordMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.system.dal.dataobject.logger.LoginLogDO;
import cn.iocoder.yudao.module.system.dal.mysql.logger.LoginLogMapper;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

@Service
@Validated
public class AppMemberSecurityServiceImpl implements AppMemberSecurityService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private LoginLogMapper loginLogMapper;
    @Resource
    private UserSensitiveCustomWordMapper userSensitiveCustomWordMapper;

    @Override
    public void updatePassword(Long authUserId, AppMemberSecurityUpdatePasswordReqVO reqVO) {
        memberUserService.updateMemberUserPassword(authUserId, reqVO.getPassword(), reqVO.getCode());
    }

    @Override
    public PageResult<AppMemberLoginLogRespVO> getLoginLogPage(Long authUserId, AppMemberLoginLogPageReqVO reqVO) {
        PageResult<LoginLogDO> pageResult = loginLogMapper.selectPage(reqVO, new LambdaQueryWrapperX<LoginLogDO>()
                .eq(LoginLogDO::getUserId, authUserId)
                .eq(LoginLogDO::getUserType, UserTypeEnum.MEMBER.getValue())
                .eqIfPresent(LoginLogDO::getResult, Boolean.TRUE.equals(reqVO.getSuccess()) ? LoginResultEnum.SUCCESS.getResult() : null)
                .gtIfPresent(LoginLogDO::getResult, Boolean.FALSE.equals(reqVO.getSuccess()) ? LoginResultEnum.SUCCESS.getResult() : null)
                .orderByDesc(LoginLogDO::getId));
        return BeanUtils.toBean(pageResult, AppMemberLoginLogRespVO.class);
    }

    @Override
    public PageResult<AppUserSensitiveCustomWordRespVO> getSensitiveCustomWordPage(Long authUserId,
                                                                                    AppUserSensitiveCustomWordPageReqVO reqVO) {
        return BeanUtils.toBean(userSensitiveCustomWordMapper.selectPage(authUserId, reqVO),
                AppUserSensitiveCustomWordRespVO.class);
    }

    @Override
    public Long createSensitiveCustomWord(Long authUserId, AppUserSensitiveCustomWordCreateReqVO reqVO) {
        UserSensitiveCustomWordDO customWord = UserSensitiveCustomWordDO.builder()
                .userId(authUserId)
                .word(reqVO.getWord())
                .sceneType(reqVO.getSceneType())
                .status("ENABLE")
                .remark(reqVO.getRemark())
                .build();
        userSensitiveCustomWordMapper.insert(customWord);
        return customWord.getId();
    }

    @Override
    public void deleteSensitiveCustomWord(Long authUserId, Long id) {
        UserSensitiveCustomWordDO customWord = validateCustomWord(authUserId, id);
        userSensitiveCustomWordMapper.deleteById(customWord.getId());
    }

    @Override
    public void updateSensitiveCustomWordStatus(Long authUserId, AppUserSensitiveCustomWordStatusUpdateReqVO reqVO) {
        UserSensitiveCustomWordDO customWord = validateCustomWord(authUserId, reqVO.getId());
        userSensitiveCustomWordMapper.updateById(UserSensitiveCustomWordDO.builder()
                .id(customWord.getId())
                .status(reqVO.getStatus())
                .build());
    }

    private UserSensitiveCustomWordDO validateCustomWord(Long authUserId, Long id) {
        UserSensitiveCustomWordDO customWord = userSensitiveCustomWordMapper.selectById(id);
        if (customWord == null || !authUserId.equals(customWord.getUserId())) {
            throw cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil
                    .exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_SENSITIVE_CUSTOM_WORD_NOT_EXISTS);
        }
        return customWord;
    }
}
