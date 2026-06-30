package cn.iocoder.yudao.module.linbang.service.app.promote;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppInviteCodeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteCenterRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTemplatePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTemplateRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTeamStatsRespVO;
import cn.iocoder.yudao.module.linbang.constants.PromoterLevelConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation.PromoterRelationMapper;
import cn.iocoder.yudao.module.linbang.service.commissionorder.CommissionOrderService;
import cn.iocoder.yudao.module.linbang.service.promoter.PromoterService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_NOT_EXISTS;

@Service
@Validated
public class AppPromoteServiceImpl implements AppPromoteService {

    @Resource
    private PromoterService promoterService;
    @Resource
    private CommissionOrderService commissionOrderService;
    @Resource
    private PromoterRelationMapper promoterRelationMapper;
    @Resource
    private MessageTemplateMapper messageTemplateMapper;

    @Override
    public AppPromoteCenterRespVO getPromoteCenter(Long userId) {
        PromoterDO promoter = getRequiredPromoter(userId);
        PageResult<CommissionOrderDO> recentPage = commissionOrderService.getAppCommissionOrderPage(promoter.getId(),
                new AppCommissionPageReqVO());
        List<CommissionOrderDO> commissionOrders = recentPage.getList();
        AppPromoteCenterRespVO respVO = BeanUtils.toBean(promoter, AppPromoteCenterRespVO.class);
        respVO.setPromoterId(promoter.getId());
        respVO.setLevelCode(resolvePromoterLevelCode(promoter));
        respVO.setLevelName(resolvePromoterLevelName(promoter));
        respVO.setUpgradeConditionDesc(resolveUpgradeConditionDesc(promoter));
        respVO.setNextLevelNeedMetric(resolveNextLevelNeedMetric(promoter));
        respVO.setPendingCommissionCount(countByStatus(commissionOrders, "PENDING"));
        respVO.setSettledCommissionCount(countByStatus(commissionOrders, "SETTLED"));
        respVO.setInvalidCommissionCount(countByStatus(commissionOrders, "INVALID"));
        respVO.setPendingCommissionAmount(sumAmountByStatus(commissionOrders, "PENDING"));
        respVO.setSettledCommissionAmount(sumAmountByStatus(commissionOrders, "SETTLED"));
        respVO.setPendingSettleCommissionAmount(respVO.getPendingCommissionAmount());
        respVO.setInviteShortLink(promoter.getInviteUrl());
        respVO.setInvitePosterUrl("/app/promote/poster?code=" + promoter.getInviteCode());
        respVO.setRecentCommissionOrders(commissionOrders.stream()
                .limit(5)
                .map(item -> BeanUtils.toBean(item, AppPromoteCenterRespVO.RecentCommissionRespVO.class))
                .collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<CommissionOrderDO> getCommissionPage(Long userId, AppCommissionPageReqVO reqVO) {
        PromoterDO promoter = getRequiredPromoter(userId);
        return commissionOrderService.getAppCommissionOrderPage(promoter.getId(), reqVO);
    }

    @Override
    public AppInviteCodeRespVO getInviteCode(Long userId) {
        PromoterDO promoter = getRequiredPromoter(userId);
        return new AppInviteCodeRespVO(promoter.getInviteCode(), promoter.getInviteUrl(),
                promoter.getInviteUrl(), "/app/promote/poster?code=" + promoter.getInviteCode());
    }

    @Override
    public void bindInviteCode(Long userId, AppPromoteInviteCodeBindReqVO reqVO) {
        promoterService.bindInviteCode(userId, reqVO);
    }

    @Override
    public AppPromoteTeamStatsRespVO getTeamStats(Long userId) {
        PromoterDO promoter = getRequiredPromoter(userId);
        List<PromoterRelationDO> firstLevelRelations = promoterRelationMapper.selectList(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, promoter.getId())
                .orderByDesc(PromoterRelationDO::getBindTime, PromoterRelationDO::getId));
        List<Long> firstLevelUserIds = firstLevelRelations.stream()
                .map(PromoterRelationDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<PromoterDO> firstLevelPromoters = firstLevelUserIds.isEmpty() ? java.util.Collections.emptyList()
                : firstLevelUserIds.stream()
                .map(promoterService::getPromoterByUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Long> firstLevelPromoterIds = firstLevelPromoters.stream()
                .map(PromoterDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<PromoterRelationDO> secondLevelRelations = firstLevelPromoterIds.isEmpty() ? java.util.Collections.emptyList()
                : promoterRelationMapper.selectList(new LambdaQueryWrapperX<PromoterRelationDO>()
                .in(PromoterRelationDO::getPromoterId, firstLevelPromoterIds)
                .orderByDesc(PromoterRelationDO::getBindTime, PromoterRelationDO::getId));

        AppPromoteTeamStatsRespVO respVO = new AppPromoteTeamStatsRespVO();
        respVO.setFirstLevelUserCount(firstLevelRelations.size());
        respVO.setFirstLevelConvertCount((int) firstLevelRelations.stream()
                .filter(item -> "CONVERTED".equalsIgnoreCase(item.getConvertStatus()))
                .count());
        respVO.setFirstLevelCommissionAmount(sumCommissionAmount(promoter.getId(), firstLevelUserIds));
        respVO.setSecondLevelUserCount(secondLevelRelations.size());
        respVO.setSecondLevelConvertCount((int) secondLevelRelations.stream()
                .filter(item -> "CONVERTED".equalsIgnoreCase(item.getConvertStatus()))
                .count());
        respVO.setSecondLevelCommissionAmount(sumCommissionAmount(firstLevelPromoterIds));

        List<AppPromoteTeamStatsRespVO.RecentConvertRespVO> recentConverts = new ArrayList<>();
        firstLevelRelations.stream().limit(5).forEach(item -> recentConverts.add(convertRecent(item, "FIRST")));
        if (recentConverts.size() < 5) {
            secondLevelRelations.stream().limit(5 - recentConverts.size())
                    .forEach(item -> recentConverts.add(convertRecent(item, "SECOND")));
        }
        respVO.setRecentConverts(recentConverts);
        return respVO;
    }

    @Override
    public PageResult<AppPromoteTemplateRespVO> getTemplatePage(Long userId, AppPromoteTemplatePageReqVO reqVO) {
        getRequiredPromoter(userId);
        PageResult<MessageTemplateDO> pageResult = messageTemplateMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<MessageTemplateDO>()
                        .eq(MessageTemplateDO::getStatus, "ENABLE")
                        .likeIfPresent(MessageTemplateDO::getMessageCategory, reqVO.getMessageCategory())
                        .in(MessageTemplateDO::getMessageCategory, "MARKETING", "SYSTEM")
                        .orderByAsc(MessageTemplateDO::getSort)
                        .orderByDesc(MessageTemplateDO::getId));
        return new PageResult<>(pageResult.getList().stream().map(this::buildTemplateResp).collect(Collectors.toList()),
                pageResult.getTotal());
    }

    @Override
    public AppPromoteTemplateRespVO getTemplate(Long userId, Long id) {
        getRequiredPromoter(userId);
        MessageTemplateDO template = messageTemplateMapper.selectById(id);
        if (template == null || !"ENABLE".equalsIgnoreCase(template.getStatus())) {
            return null;
        }
        return buildTemplateResp(template);
    }

    private PromoterDO getRequiredPromoter(Long userId) {
        PromoterDO promoter = promoterService.getPromoterByUserId(userId);
        if (promoter == null || "DISABLE".equalsIgnoreCase(promoter.getStatus())) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        return promoter;
    }

    private Integer countByStatus(List<CommissionOrderDO> commissionOrders, String status) {
        return (int) commissionOrders.stream()
                .filter(item -> Objects.equals(item.getStatus(), status))
                .count();
    }

    private BigDecimal sumAmountByStatus(List<CommissionOrderDO> commissionOrders, String status) {
        return commissionOrders.stream()
                .filter(item -> Objects.equals(item.getStatus(), status))
                .map(CommissionOrderDO::getCommissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumCommissionAmount(Long promoterId, List<Long> userIds) {
        if (promoterId == null || userIds == null || userIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return commissionOrderService.getAppCommissionOrderPage(promoterId, new AppCommissionPageReqVO()).getList().stream()
                .filter(item -> userIds.contains(item.getUserId()))
                .map(CommissionOrderDO::getCommissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumCommissionAmount(List<Long> promoterIds) {
        if (promoterIds == null || promoterIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Long promoterId : promoterIds) {
            total = total.add(commissionOrderService.getAppCommissionOrderPage(promoterId, new AppCommissionPageReqVO()).getList().stream()
                    .map(CommissionOrderDO::getCommissionAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return total;
    }

    private AppPromoteTeamStatsRespVO.RecentConvertRespVO convertRecent(PromoterRelationDO relation, String level) {
        AppPromoteTeamStatsRespVO.RecentConvertRespVO respVO = new AppPromoteTeamStatsRespVO.RecentConvertRespVO();
        respVO.setUserId(relation.getUserId());
        respVO.setLevel(level);
        respVO.setBindTime(relation.getBindTime());
        respVO.setFirstOrderId(relation.getFirstOrderId());
        respVO.setConvertStatus(relation.getConvertStatus());
        return respVO;
    }

    private AppPromoteTemplateRespVO buildTemplateResp(MessageTemplateDO template) {
        AppPromoteTemplateRespVO respVO = new AppPromoteTemplateRespVO();
        respVO.setId(template.getId());
        respVO.setTemplateCode(template.getTemplateCode());
        respVO.setTitle(template.getTitleTemplate());
        respVO.setContent(template.getContentTemplate());
        respVO.setTemplateType(template.getTemplateType());
        respVO.setChannelType(template.getChannelType());
        respVO.setMessageCategory(template.getMessageCategory());
        respVO.setRouteType(template.getRouteType());
        respVO.setRouteValue(template.getRouteValue());
        respVO.setStatus(template.getStatus());
        respVO.setUpdateTime(template.getUpdateTime());
        return respVO;
    }

    private String resolvePromoterLevelCode(PromoterDO promoter) {
        int bindUserCount = defaultInt(promoter.getBindUserCount());
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_SENIOR_THRESHOLD) {
            return PromoterLevelConstants.LEVEL_CODE_SENIOR;
        }
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_MIDDLE_THRESHOLD) {
            return PromoterLevelConstants.LEVEL_CODE_MIDDLE;
        }
        return PromoterLevelConstants.LEVEL_CODE_PRIMARY;
    }

    private String resolvePromoterLevelName(PromoterDO promoter) {
        int bindUserCount = defaultInt(promoter.getBindUserCount());
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_SENIOR_THRESHOLD) {
            return "高级推广员";
        }
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_MIDDLE_THRESHOLD) {
            return "中级推广员";
        }
        return "初级推广员";
    }

    private String resolveUpgradeConditionDesc(PromoterDO promoter) {
        int bindUserCount = defaultInt(promoter.getBindUserCount());
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_SENIOR_THRESHOLD) {
            return "当前已是最高等级，继续保持活跃转化和稳定服务。";
        }
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_MIDDLE_THRESHOLD) {
            return "累计推广 50 人可升级高级推广员。";
        }
        return "累计推广 10 人可升级中级推广员。";
    }

    private String resolveNextLevelNeedMetric(PromoterDO promoter) {
        int bindUserCount = defaultInt(promoter.getBindUserCount());
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_SENIOR_THRESHOLD) {
            return "已推广 " + bindUserCount + " 人，已达到最高等级";
        }
        if (bindUserCount >= PromoterLevelConstants.PROMOTER_MIDDLE_THRESHOLD) {
            return "已推广 " + bindUserCount + "/" + PromoterLevelConstants.PROMOTER_SENIOR_THRESHOLD + " 人，还差 "
                    + Math.max(0, PromoterLevelConstants.PROMOTER_SENIOR_THRESHOLD - bindUserCount)
                    + " 人可升级高级推广员";
        }
        return "已推广 " + bindUserCount + "/" + PromoterLevelConstants.PROMOTER_MIDDLE_THRESHOLD + " 人，还差 "
                + Math.max(0, PromoterLevelConstants.PROMOTER_MIDDLE_THRESHOLD - bindUserCount)
                + " 人可升级中级推广员";
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }
}
