package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo.AppMemberRoleContextRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply.MemberRoleApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerinfo.PartnerInfoMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_SWITCH_NOT_ALLOWED;

@Service
public class AppMemberRoleContextServiceImpl implements AppMemberRoleContextService {

    private static final List<String> SUPPORTED_ROLE_CODES = Arrays.asList(
            "USER", "MERCHANT", "PROMOTER", "PARTNER");

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private PartnerInfoMapper partnerInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MemberRoleApplyMapper memberRoleApplyMapper;

    @Override
    public AppMemberRoleContextRespVO getRoleContext(Long authUserId) {
        MemberUserDO user = memberUserService.getOrCreateMemberUser(authUserId);
        PartnerInfoDO partnerInfo = partnerInfoMapper.selectOne(PartnerInfoDO::getUserId, authUserId);
        MerchantEntryDO merchantEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, authUserId)
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        List<MemberRoleApplyDO> applies = memberRoleApplyMapper.selectList(new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getUserId, authUserId)
                .orderByDesc(MemberRoleApplyDO::getId));

        Set<String> enabledRoleCodes = new LinkedHashSet<>();
        enabledRoleCodes.add("USER");
        if (merchantEntry != null && "APPROVED".equalsIgnoreCase(merchantEntry.getFinalAuditStatus())) {
            enabledRoleCodes.add("MERCHANT");
        }
        if (partnerInfo != null && "ENABLE".equalsIgnoreCase(partnerInfo.getStatus())) {
            enabledRoleCodes.add("PARTNER");
        }
        applies.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(item.getAuditStatus()))
                .map(MemberRoleApplyDO::getApplyRoleCode)
                .filter(StrUtil::isNotBlank)
                .forEach(enabledRoleCodes::add);

        List<AppMemberRoleContextRespVO.RoleSummaryItem> roleSummaries = new ArrayList<>();
        for (String roleCode : SUPPORTED_ROLE_CODES) {
            AppMemberRoleContextRespVO.RoleSummaryItem item = new AppMemberRoleContextRespVO.RoleSummaryItem();
            item.setRoleCode(roleCode);
            item.setRoleName(resolveRoleName(roleCode));
            item.setCurrent(Objects.equals(roleCode, user.getCurrentRoleCode()));
            item.setSwitchable(enabledRoleCodes.contains(roleCode));
            item.setRoleStatus(resolveRoleStatus(roleCode, enabledRoleCodes, applies, merchantEntry));
            item.setPermissionDesc(resolveRolePermissionDesc(roleCode));
            item.setEntryModeDesc(resolveRoleEntryModeDesc(roleCode));
            item.setMainPermissions(resolveRoleMainPermissions(roleCode));
            item.setSwitchRequiredForActions(!"USER".equals(roleCode));
            roleSummaries.add(item);
        }

        AppMemberRoleContextRespVO respVO = new AppMemberRoleContextRespVO();
        respVO.setCurrentRoleCode(user.getCurrentRoleCode());
        respVO.setCurrentRoleName(resolveRoleName(user.getCurrentRoleCode()));
        respVO.setEnabledRoleCodes(new ArrayList<>(enabledRoleCodes));
        respVO.setSwitchableRoleCodes(roleSummaries.stream()
                .filter(AppMemberRoleContextRespVO.RoleSummaryItem::getSwitchable)
                .map(AppMemberRoleContextRespVO.RoleSummaryItem::getRoleCode)
                .collect(Collectors.toList()));
        respVO.setRoleSummaries(roleSummaries);
        return respVO;
    }

    @Override
    public void switchRole(Long authUserId, String targetRoleCode) {
        AppMemberRoleContextRespVO roleContext = getRoleContext(authUserId);
        if (roleContext.getSwitchableRoleCodes() == null
                || !roleContext.getSwitchableRoleCodes().contains(targetRoleCode)) {
            throw exception(MEMBER_ROLE_SWITCH_NOT_ALLOWED);
        }
        memberUserService.updateMemberUserRole(authUserId, targetRoleCode);
    }

    private String resolveRoleStatus(String roleCode, Set<String> enabledRoleCodes, List<MemberRoleApplyDO> applies,
                                     MerchantEntryDO merchantEntry) {
        if (enabledRoleCodes.contains(roleCode)) {
            return "ENABLED";
        }
        if ("MERCHANT".equals(roleCode)) {
            if (merchantEntry == null) {
                return "AVAILABLE";
            }
            if ("REJECTED".equalsIgnoreCase(merchantEntry.getStatus())) {
                return "REJECTED";
            }
            return "PENDING";
        }
        MemberRoleApplyDO latest = applies == null ? null : applies.stream()
                .filter(item -> Objects.equals(roleCode, item.getApplyRoleCode()))
                .findFirst()
                .orElse(null);
        if (latest == null) {
            return "AVAILABLE";
        }
        if ("REJECTED".equalsIgnoreCase(latest.getAuditStatus())) {
            return "REJECTED";
        }
        if ("APPROVED".equalsIgnoreCase(latest.getAuditStatus())) {
            return "ENABLED";
        }
        return "PENDING";
    }

    private String resolveRoleName(String roleCode) {
        if ("MERCHANT".equals(roleCode)) {
            return "服务商";
        }
        if ("PROMOTER".equals(roleCode)) {
            return "推广员";
        }
        if ("PARTNER".equals(roleCode)) {
            return "区域合作商";
        }
        if ("PLATFORM_OPERATOR".equals(roleCode)) {
            return "平台管理员（请使用管理后台）";
        }
        return "普通用户";
    }

    private String resolveRolePermissionDesc(String roleCode) {
        if ("MERCHANT".equals(roleCode)) {
            return "可抢单接单、履约服务、管理服务点与经营报价。";
        }
        if ("PROMOTER".equals(roleCode)) {
            return "可查看推广模板、邀请码、团队转化与佣金结算。";
        }
        if ("PARTNER".equals(roleCode)) {
            return "可查看辖区、处理入驻初审、协调纠纷、提交价格建议。";
        }
        if ("PLATFORM_OPERATOR".equals(roleCode)) {
            return "可在管理端执行全局数据查看、权限配置、规则调整、终审、账号管理、动态密钥验证、操作日志和数据导入导出。";
        }
        return "可下单、查看订单、管理个人资料与钱包。";
    }

    private String resolveRoleEntryModeDesc(String roleCode) {
        if ("MERCHANT".equals(roleCode)) {
            return "切换后进入服务商视角，可使用抢单大厅、履约与经营管理能力。";
        }
        if ("PROMOTER".equals(roleCode)) {
            return "切换后进入推广员视角，可查看推广、团队和佣金数据。";
        }
        if ("PARTNER".equals(roleCode)) {
            return "切换后进入区域合作商视角，可处理辖区协同事务。";
        }
        return "默认进入普通用户视角，可发单、支付和查看自己的订单。";
    }

    private List<String> resolveRoleMainPermissions(String roleCode) {
        if ("MERCHANT".equals(roleCode)) {
            return Arrays.asList("抢单/接单", "履约服务", "服务点管理", "经营报价");
        }
        if ("PROMOTER".equals(roleCode)) {
            return Arrays.asList("推广内容", "团队数据", "佣金结算", "悬赏参与");
        }
        if ("PARTNER".equals(roleCode)) {
            return Arrays.asList("辖区查看", "入驻初审", "纠纷协调", "价格建议", "辖区推广数据", "会议通知");
        }
        return Arrays.asList("发单", "支付", "退款", "评价", "投诉/申诉", "查看我的订单");
    }
}
