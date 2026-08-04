package cn.iocoder.yudao.module.linbang.service.app.promote;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppInviteCodeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromotePosterRespVO;
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
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation.PromoterRelationMapper;
import cn.iocoder.yudao.module.linbang.service.commissionorder.CommissionOrderService;
import cn.iocoder.yudao.module.linbang.service.promoter.PromoterService;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.system.api.social.SocialClientApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialWxQrcodeReqDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    @Resource
    private CommissionOrderMapper commissionOrderMapper;
    @Resource
    private SocialClientApi socialClientApi;
    @Resource
    private FileService fileService;

    @Override
    public AppPromoteCenterRespVO getPromoteCenter(Long userId) {
        PromoterDO promoter = promoterService.syncPromoterMetrics(getRequiredPromoter(userId).getId());
        List<CommissionOrderDO> recentCommissionOrders = commissionOrderMapper.selectList(
                new LambdaQueryWrapperX<CommissionOrderDO>()
                        .eq(CommissionOrderDO::getPromoterId, promoter.getId())
                        .orderByDesc(CommissionOrderDO::getId)
                        .last("LIMIT 5"));
        AppPromoteCenterRespVO respVO = BeanUtils.toBean(promoter, AppPromoteCenterRespVO.class);
        respVO.setPromoterId(promoter.getId());
        respVO.setLevelCode(resolvePromoterLevelCode(promoter));
        respVO.setLevelName(resolvePromoterLevelName(promoter));
        respVO.setUpgradeConditionDesc(resolveUpgradeConditionDesc(promoter));
        respVO.setNextLevelNeedMetric(resolveNextLevelNeedMetric(promoter));
        respVO.setPendingConvertCount(Math.max(0,
                defaultInt(promoter.getBindUserCount()) - defaultInt(promoter.getConvertCount())));
        respVO.setPendingCommissionCount(countByStatus(promoter.getId(), "PENDING"));
        respVO.setSettledCommissionCount(countByStatus(promoter.getId(), "SETTLED"));
        respVO.setInvalidCommissionCount(countByStatus(promoter.getId(), "REFUNDED"));
        respVO.setPendingCommissionAmount(sumAmountByStatus(promoter.getId(), "PENDING"));
        respVO.setSettledCommissionAmount(sumAmountByStatus(promoter.getId(), "SETTLED"));
        respVO.setPendingSettleCommissionAmount(respVO.getPendingCommissionAmount());
        respVO.setInviteShortLink(promoter.getInviteUrl());
        respVO.setInvitePosterUrl(null);
        respVO.setRecentCommissionOrders(recentCommissionOrders.stream()
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
                promoter.getInviteUrl(), null);
    }

    @Override
    public void bindInviteCode(Long userId, AppPromoteInviteCodeBindReqVO reqVO) {
        promoterService.bindInviteCode(userId, reqVO);
    }

    @Override
    public AppPromotePosterRespVO generatePoster(Long userId) {
        PromoterDO promoter = getRequiredPromoter(userId);
        try {
            SocialWxQrcodeReqDTO reqDTO = new SocialWxQrcodeReqDTO();
            reqDTO.setScene("p=" + promoter.getInviteCode());
            reqDTO.setPath("pages/index/index");
            reqDTO.setWidth(430);
            reqDTO.setCheckPath(Boolean.TRUE);
            reqDTO.setHyaline(Boolean.FALSE);
            byte[] qrcode = socialClientApi.getWxaQrcode(reqDTO);
            String qrcodeUrl = fileService.createFile(qrcode, "promote-qrcode-" + promoter.getInviteCode() + ".png",
                    "linbang/promote/qrcode", "image/png");
            byte[] poster = buildPoster(qrcode, promoter.getInviteCode());
            String posterUrl = fileService.createFile(poster, "promote-poster-" + promoter.getInviteCode() + ".png",
                    "linbang/promote/poster", "image/png");
            return new AppPromotePosterRespVO(qrcodeUrl, posterUrl);
        } catch (Exception ex) {
            throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_QRCODE_GENERATE_FAILED);
        }
    }

    @Override
    public AppPromoteTeamStatsRespVO getTeamStats(Long userId) {
        PromoterDO promoter = getRequiredPromoter(userId);
        List<PromoterRelationDO> firstLevelRelations = promoterRelationMapper.selectList(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, promoter.getId())
                .orderByDesc(PromoterRelationDO::getBindTime, PromoterRelationDO::getId)
                .last("LIMIT 500"));
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
                        .orderByDesc(PromoterRelationDO::getBindTime, PromoterRelationDO::getId)
                        .last("LIMIT 500"));

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
        // 二级团队仅做统计展示，不参与当前推广员的佣金结算。
        respVO.setSecondLevelCommissionAmount(BigDecimal.ZERO);

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

    private Integer countByStatus(Long promoterId, String status) {
        return Math.toIntExact(commissionOrderMapper.selectCount(new LambdaQueryWrapperX<CommissionOrderDO>()
                .eq(CommissionOrderDO::getPromoterId, promoterId)
                .eq(CommissionOrderDO::getStatus, status)));
    }

    private BigDecimal sumAmountByStatus(Long promoterId, String status) {
        return defaultAmount(commissionOrderMapper.selectAmountSumByPromoterIdAndStatus(promoterId, status));
    }

    private BigDecimal sumCommissionAmount(Long promoterId, List<Long> userIds) {
        if (promoterId == null || userIds == null || userIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return defaultAmount(commissionOrderMapper.selectAmountSumByPromoterIdAndUserIds(promoterId, userIds));
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
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

    private byte[] buildPoster(byte[] qrcodeBytes, String inviteCode) throws Exception {
        BufferedImage qrcode = ImageIO.read(new ByteArrayInputStream(qrcodeBytes));
        if (qrcode == null) {
            throw new IllegalArgumentException("invalid qrcode image");
        }
        BufferedImage poster = new BufferedImage(750, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = poster.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(240, 247, 255));
            graphics.fillRect(0, 0, poster.getWidth(), poster.getHeight());
            graphics.setColor(new Color(46, 131, 240));
            graphics.fillRect(0, 0, poster.getWidth(), 210);
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("SansSerif", Font.BOLD, 48));
            graphics.drawString("邻里互助", 270, 90);
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 30));
            graphics.drawString("扫码进入小程序，发现身边互助服务", 130, 155);
            graphics.setColor(Color.WHITE);
            graphics.fillRoundRect(110, 260, 530, 590, 24, 24);
            graphics.drawImage(qrcode, 185, 315, 380, 380, null);
            graphics.setColor(new Color(32, 46, 64));
            graphics.setFont(new Font("SansSerif", Font.BOLD, 32));
            graphics.drawString("邀请码：" + inviteCode, 245, 760);
            graphics.setColor(new Color(100, 116, 139));
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 24));
            graphics.drawString("首次登录后将自动锁定邀请关系", 195, 810);
        } finally {
            graphics.dispose();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(poster, "png", output);
        return output.toByteArray();
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
