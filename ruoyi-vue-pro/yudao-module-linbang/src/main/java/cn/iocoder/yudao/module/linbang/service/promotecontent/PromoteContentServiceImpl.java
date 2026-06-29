package cn.iocoder.yudao.module.linbang.service.promotecontent;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentOfflineReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontent.PromoteContentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontentauditlog.PromoteContentAuditLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterpenaltyrecord.PromoterPenaltyRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promotecontent.PromoteContentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promotecontentauditlog.PromoteContentAuditLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterpenaltyrecord.PromoterPenaltyRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.service.promoter.PromoterService;
import cn.iocoder.yudao.module.linbang.service.punishlog.PunishLogWriteService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveContentDetectService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveDetectResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTE_CONTENT_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTE_CONTENT_STATUS_INVALID;

@Service
public class PromoteContentServiceImpl implements PromoteContentService {

    @Resource
    private PromoteContentMapper promoteContentMapper;
    @Resource
    private PromoteContentAuditLogMapper promoteContentAuditLogMapper;
    @Resource
    private PromoterPenaltyRecordMapper promoterPenaltyRecordMapper;
    @Resource
    private PromoterMapper promoterMapper;
    @Resource
    private PromoterService promoterService;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private SensitiveContentDetectService sensitiveContentDetectService;
    @Resource
    private PunishLogWriteService punishLogWriteService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAppContent(Long userId, AppPromoteContentCreateReqVO reqVO) {
        PromoterDO promoter = promoterService.getOrCreatePromoter(userId);
        if (!Objects.equals(promoter.getStatus(), LinbangRiskConstants.STATUS_ENABLE)
                || Objects.equals(promoter.getLevelCode(), LinbangRiskConstants.PENALTY_ACTION_RESTRICT_PROMOTE)) {
            throw exception(PROMOTE_CONTENT_STATUS_INVALID);
        }
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_PROMOTE, userId, "PROMOTE_CONTENT", null,
                reqVO.getTitle() + "\n" + reqVO.getContent());
        PromoteContentDO content = PromoteContentDO.builder()
                .promoterId(promoter.getId())
                .userId(userId)
                .title(reqVO.getTitle())
                .content(resolveProcessedContent(reqVO.getContent(), detectResult))
                .imageUrls(reqVO.getImageUrls())
                .status(resolveInitialStatus(detectResult))
                .systemAuditResult(detectResult.isHit() ? "REJECT" : "PASS")
                .systemAuditRemark(buildSystemAuditRemark(detectResult))
                .systemAuditTime(java.time.LocalDateTime.now())
                .build();
        promoteContentMapper.insert(content);
        saveAuditLog(content.getId(), LinbangRiskConstants.PROMOTE_AUDIT_TYPE_SYSTEM, content.getSystemAuditResult(),
                content.getSystemAuditRemark(), null, 0L);
        return content.getId();
    }

    @Override
    public PageResult<AppPromoteContentRespVO> getAppContentPage(Long userId, AppPromoteContentPageReqVO reqVO) {
        PromoterDO promoter = promoterService.getOrCreatePromoter(userId);
        PromoteContentPageReqVO adminReq = new PromoteContentPageReqVO();
        adminReq.setPageNo(reqVO.getPageNo());
        adminReq.setPageSize(reqVO.getPageSize());
        adminReq.setPromoterId(promoter.getId());
        adminReq.setUserId(userId);
        adminReq.setStatus(reqVO.getStatus());
        PageResult<PromoteContentDO> page = promoteContentMapper.selectPageByPromoterIds(adminReq, Collections.singleton(promoter.getId()));
        return new PageResult<>(BeanUtils.toBean(page.getList(), AppPromoteContentRespVO.class), page.getTotal());
    }

    @Override
    public AppPromoteContentRespVO getAppContent(Long userId, Long id) {
        PromoteContentDO content = validateContent(id);
        if (!Objects.equals(content.getUserId(), userId)) {
            throw exception(PROMOTE_CONTENT_NOT_EXISTS);
        }
        return BeanUtils.toBean(content, AppPromoteContentRespVO.class);
    }

    @Override
    public PageResult<PromoteContentRespVO> getAdminPage(PromoteContentPageReqVO reqVO) {
        PageResult<PromoteContentDO> page = promoteContentMapper.selectPageByPromoterIds(reqVO, Collections.<Long>emptySet());
        List<PromoteContentRespVO> list = BeanUtils.toBean(page.getList(), PromoteContentRespVO.class);
        fillUserDisplay(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PromoteContentRespVO getAdminContent(Long id) {
        PromoteContentDO content = validateContent(id);
        PromoteContentRespVO respVO = BeanUtils.toBean(content, PromoteContentRespVO.class);
        fillUserDisplay(Collections.singletonList(respVO));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditContent(Long operatorId, PromoteContentAuditReqVO reqVO) {
        PromoteContentDO content = validateContent(reqVO.getId());
        if (!Objects.equals(content.getStatus(), LinbangRiskConstants.PROMOTE_STATUS_PENDING_MANUAL_AUDIT)) {
            throw exception(PROMOTE_CONTENT_STATUS_INVALID);
        }
        PromoteContentDO update = PromoteContentDO.builder()
                .id(content.getId())
                .manualAuditResult(reqVO.getAuditResult())
                .manualAuditRemark(reqVO.getAuditRemark())
                .manualAuditBy(operatorId)
                .manualAuditTime(java.time.LocalDateTime.now())
                .rejectReason(reqVO.getRejectReason())
                .status(Objects.equals(reqVO.getAuditResult(), "APPROVED")
                        ? LinbangRiskConstants.PROMOTE_STATUS_APPROVED
                        : LinbangRiskConstants.PROMOTE_STATUS_REJECTED)
                .build();
        promoteContentMapper.updateById(update);
        saveAuditLog(content.getId(), LinbangRiskConstants.PROMOTE_AUDIT_TYPE_MANUAL, reqVO.getAuditResult(),
                reqVO.getAuditRemark(), reqVO.getRejectReason(), operatorId);
        if (!Objects.equals(reqVO.getAuditResult(), "APPROVED")) {
            applyPenalty(content, reqVO.getPenaltyAction(), reqVO.getScoreChange(), reqVO.getPenaltyReason());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineContent(Long operatorId, PromoteContentOfflineReqVO reqVO) {
        PromoteContentDO content = validateContent(reqVO.getId());
        if (!Objects.equals(content.getStatus(), LinbangRiskConstants.PROMOTE_STATUS_APPROVED)) {
            throw exception(PROMOTE_CONTENT_STATUS_INVALID);
        }
        promoteContentMapper.updateById(PromoteContentDO.builder()
                .id(content.getId())
                .status(LinbangRiskConstants.PROMOTE_STATUS_OFFLINE)
                .offlineReason(reqVO.getOfflineReason())
                .manualAuditBy(operatorId)
                .manualAuditTime(java.time.LocalDateTime.now())
                .build());
        saveAuditLog(content.getId(), "OFFLINE", "OFFLINE", reqVO.getOfflineReason(), null, operatorId);
        applyPenalty(content, reqVO.getPenaltyAction(), reqVO.getScoreChange(), reqVO.getPenaltyReason());
    }

    private PromoteContentDO validateContent(Long id) {
        PromoteContentDO content = promoteContentMapper.selectById(id);
        if (content == null) {
            throw exception(PROMOTE_CONTENT_NOT_EXISTS);
        }
        return content;
    }

    private void fillUserDisplay(List<PromoteContentRespVO> list) {
        Map<Long, MemberUserDO> userMap = convertSet(list, PromoteContentRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, PromoteContentRespVO::getUserId, item -> item.getUserId() != null)),
                MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
        });
    }

    private void saveAuditLog(Long contentId, String auditType, String auditResult, String auditRemark,
                              String rejectReason, Long auditBy) {
        promoteContentAuditLogMapper.insert(PromoteContentAuditLogDO.builder()
                .contentId(contentId)
                .auditType(auditType)
                .auditResult(auditResult)
                .auditRemark(auditRemark)
                .rejectReason(rejectReason)
                .auditBy(auditBy)
                .auditTime(java.time.LocalDateTime.now())
                .build());
    }

    private void applyPenalty(PromoteContentDO content, String penaltyAction, Integer scoreChange, String reason) {
        if (StrUtil.isBlank(penaltyAction)) {
            return;
        }
        PromoterPenaltyRecordDO record = PromoterPenaltyRecordDO.builder()
                .promoterId(content.getPromoterId())
                .userId(content.getUserId())
                .contentId(content.getId())
                .penaltyAction(penaltyAction)
                .scoreChange(scoreChange)
                .reason(StrUtil.blankToDefault(reason, "推广内容违规处罚"))
                .status("ACTIVE")
                .build();
        promoterPenaltyRecordMapper.insert(record);
        punishLogWriteService.createPunishLog(content.getUserId(), penaltyAction, record.getStatus(), record.getReason(),
                "PROMOTE_CONTENT", content.getId(), "PROMOTER_PENALTY_RECORD", record.getId(),
                null, record.getCreateTime(), record.getCreateTime(), null, null);
        if (Objects.equals(penaltyAction, LinbangRiskConstants.PENALTY_ACTION_DEMOTE)) {
            promoterMapper.updateById(PromoterDO.builder().id(content.getPromoterId()).levelCode("NORMAL").build());
        } else if (Objects.equals(penaltyAction, LinbangRiskConstants.PENALTY_ACTION_RESTRICT_PROMOTE)) {
            promoterMapper.updateById(PromoterDO.builder()
                    .id(content.getPromoterId())
                    .levelCode(LinbangRiskConstants.PENALTY_ACTION_RESTRICT_PROMOTE)
                    .build());
        } else if (Objects.equals(penaltyAction, LinbangRiskConstants.PENALTY_ACTION_DISABLE_PROMOTER)) {
            promoterMapper.updateById(PromoterDO.builder().id(content.getPromoterId()).status("DISABLE").build());
        }
    }

    private String resolveInitialStatus(SensitiveDetectResult detectResult) {
        if (!detectResult.isHit()) {
            return LinbangRiskConstants.PROMOTE_STATUS_PENDING_MANUAL_AUDIT;
        }
        if (Objects.equals(detectResult.getStrategy(), LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK)) {
            return LinbangRiskConstants.PROMOTE_STATUS_REJECTED;
        }
        return LinbangRiskConstants.PROMOTE_STATUS_PENDING_MANUAL_AUDIT;
    }

    private String resolveProcessedContent(String source, SensitiveDetectResult detectResult) {
        return Objects.equals(detectResult.getStrategy(), LinbangRiskConstants.SENSITIVE_STRATEGY_REPLACE)
                ? detectResult.getProcessedContent()
                : source;
    }

    private String buildSystemAuditRemark(SensitiveDetectResult detectResult) {
        if (!detectResult.isHit()) {
            return "系统审核通过";
        }
        return "系统审核命中 " + detectResult.getHitCount() + " 个敏感词，处理策略：" + detectResult.getStrategy();
    }
}
