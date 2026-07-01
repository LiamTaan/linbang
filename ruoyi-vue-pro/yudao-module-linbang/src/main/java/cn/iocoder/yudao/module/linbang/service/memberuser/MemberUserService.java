package cn.iocoder.yudao.module.linbang.service.memberuser;

import cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户主表 Service 接口
 *
 * @author dawn
 */
public interface MemberUserService {

    /**
     * 创建用户主表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMemberUser(@Valid MemberUserSaveReqVO createReqVO);

    /**
     * 更新用户主表
     *
     * @param updateReqVO 更新信息
     */
    void updateMemberUser(@Valid MemberUserSaveReqVO updateReqVO);

    /**
     * 删除用户主表
     *
     * @param id 编号
     */
    void deleteMemberUser(Long id);

    /**
    * 批量删除用户主表
    *
    * @param ids 编号
    */
    void deleteMemberUserListByIds(List<Long> ids);

    /**
     * 获得用户主表
     *
     * @param id 编号
     * @return 用户主表
     */
    MemberUserDO getMemberUser(Long id);

    /**
     * 获得用户详情
     *
     * @param id 编号
     * @return 用户详情
     */
    MemberUserDetailRespVO getMemberUserDetail(Long id);

    /**
     * 基于登录用户编号，获得邻里用户档案。
     *
     * @param authUserId 登录用户编号
     * @return 邻里用户档案
     */
    MemberUserDO getOrCreateMemberUser(Long authUserId);

    /**
     * 按手机号获取邻里用户。
     *
     * @param mobile 手机号
     * @return 邻里用户
     */
    MemberUserDO getMemberUserByMobile(String mobile);

    MemberUserDO getMemberUserByUsername(String username);

    /**
     * 手机验证码登录时，按手机号获取或创建邻里用户。
     *
     * @param mobile 手机号
     * @param registerSource 注册来源
     * @return 邻里用户
     */
    MemberUserDO createMemberUserIfAbsent(String mobile, String registerSource);

    MemberUserDO registerMemberUser(String username, String mobile, String encodedPassword, String accountType,
                                    String registerSource, String registerSourceDetail,
                                    String agreementVersion, LocalDateTime agreementConfirmedTime);

    void updateRegisterAgreement(Long userId, String agreementVersion, LocalDateTime confirmedTime);

    /**
     * 更新用户最后登录信息。
     *
     * @param userId 用户编号
     * @param loginIp 登录 IP
     */
    void updateMemberUserLogin(Long userId, String loginIp);

    /**
     * 更新用户资料。
     *
     * @param userId 用户编号
     * @param nickname 昵称
     * @param avatar 头像
     * @param gender 性别
     * @param birthday 生日
     */
    void updateMemberUserProfile(Long userId, String nickname, String avatar, Integer gender, LocalDate birthday);

    /**
     * 使用短信验证码更新登录密码。
     *
     * @param userId 用户编号
     * @param password 新密码明文
     * @param code 短信验证码
     */
    void updateMemberUserPassword(Long userId, String password, String code);

    /**
     * 更新用户当前角色。
     *
     * @param userId 用户编号
     * @param currentRoleCode 当前角色编码
     */
    void updateMemberUserRole(Long userId, String currentRoleCode);

    void updateMemberUserStatus(Long userId, String status, String remark);

    /**
     * 限制/封禁/拉黑用户
     *
     * @param reqVO 请求
     */
    void restrictMemberUser(@Valid MemberUserRestrictReqVO reqVO);

    /**
     * 解除限制/封禁
     *
     * @param reqVO 请求
     */
    void releaseMemberUserRestrict(@Valid MemberUserReleaseRestrictReqVO reqVO);

    /**
     * 获得用户主表分页
     *
     * @param pageReqVO 分页查询
     * @return 用户主表分页
     */
    PageResult<MemberUserDO> getMemberUserPage(MemberUserPageReqVO pageReqVO);

    List<String> getEnabledRoleCodes(Long userId);

}
