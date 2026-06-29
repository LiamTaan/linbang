package cn.iocoder.yudao.module.linbang.service.promoteappeal;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoteappeal.PromoteAppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontent.PromoteContentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoteappeal.PromoteAppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promotecontent.PromoteContentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
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
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTE_APPEAL_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTE_APPEAL_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTE_CONTENT_NOT_EXISTS;

@Service
public class PromoteAppealServiceImpl implements PromoteAppealService {

    @Resource
    private PromoteAppealMapper promoteAppealMapper;
    @Resource
    private PromoteContentMapper promoteContentMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private PromoterMapper promoterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAppAppeal(Long userId, AppPromoteAppealCreateReqVO reqVO) {
        PromoteContentDO content = promoteContentMapper.selectById(reqVO.getContentId());
        if (content == null || !Objects.equals(content.getUserId(), userId)) {
            throw exception(PROMOTE_CONTENT_NOT_EXISTS);
        }
        PromoteAppealDO appeal = PromoteAppealDO.builder()
                .contentId(content.getId())
                .promoterId(content.getPromoterId())
                .userId(userId)
                .appealReason(reqVO.getAppealReason())
                .status(LinbangRiskConstants.PROMOTE_APPEAL_STATUS_PENDING)
                .build();
        promoteAppealMapper.insert(appeal);
        return appeal.getId();
    }

    @Override
    public PageResult<AppPromoteAppealRespVO> getAppPage(Long userId, AppPromoteAppealPageReqVO reqVO) {
        PromoteAppealPageReqVO adminReq = new PromoteAppealPageReqVO();
        adminReq.setPageNo(reqVO.getPageNo());
        adminReq.setPageSize(reqVO.getPageSize());
        adminReq.setUserId(userId);
        adminReq.setStatus(reqVO.getStatus());
        PageResult<PromoteAppealDO> page = promoteAppealMapper.selectPageByPromoterIds(adminReq, Collections.<Long>emptySet());
        List<AppPromoteAppealRespVO> list = BeanUtils.toBean(page.getList(), AppPromoteAppealRespVO.class);
        fillContentTitle(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PageResult<PromoteAppealRespVO> getAdminPage(PromoteAppealPageReqVO reqVO) {
        PageResult<PromoteAppealDO> page = promoteAppealMapper.selectPageByPromoterIds(reqVO, Collections.<Long>emptySet());
        List<PromoteAppealRespVO> list = BeanUtils.toBean(page.getList(), PromoteAppealRespVO.class);
        fillAdminDisplay(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PromoteAppealRespVO getAdminAppeal(Long id) {
        PromoteAppealDO appeal = validateAppeal(id);
        PromoteAppealRespVO respVO = BeanUtils.toBean(appeal, PromoteAppealRespVO.class);
        fillAdminDisplay(Collections.singletonList(respVO));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditAppeal(Long operatorId, PromoteAppealAuditReqVO reqVO) {
        PromoteAppealDO appeal = validateAppeal(reqVO.getId());
        if (!Objects.equals(appeal.getStatus(), LinbangRiskConstants.PROMOTE_APPEAL_STATUS_PENDING)) {
            throw exception(PROMOTE_APPEAL_STATUS_INVALID);
        }
        promoteAppealMapper.updateById(PromoteAppealDO.builder()
                .id(appeal.getId())
                .status(reqVO.getAuditResult())
                .auditRemark(reqVO.getAuditRemark())
                .rejectReason(reqVO.getRejectReason())
                .auditBy(operatorId)
                .auditTime(java.time.LocalDateTime.now())
                .build());
        if (Objects.equals(reqVO.getAuditResult(), LinbangRiskConstants.PROMOTE_APPEAL_STATUS_APPROVED)) {
            PromoteContentDO content = promoteContentMapper.selectById(appeal.getContentId());
            if (content != null) {
                promoteContentMapper.updateById(PromoteContentDO.builder()
                        .id(content.getId())
                        .status(LinbangRiskConstants.PROMOTE_STATUS_APPROVED)
                        .offlineReason(null)
                        .rejectReason(null)
                        .build());
                PromoterDO promoter = promoterMapper.selectById(content.getPromoterId());
                if (promoter != null && Objects.equals(promoter.getLevelCode(), LinbangRiskConstants.PENALTY_ACTION_RESTRICT_PROMOTE)) {
                    promoterMapper.updateById(PromoterDO.builder().id(promoter.getId()).levelCode("NORMAL").build());
                }
            }
        }
    }

    private PromoteAppealDO validateAppeal(Long id) {
        PromoteAppealDO appeal = promoteAppealMapper.selectById(id);
        if (appeal == null) {
            throw exception(PROMOTE_APPEAL_NOT_EXISTS);
        }
        return appeal;
    }

    private void fillContentTitle(List<? extends AppPromoteAppealRespVO> list) {
        Map<Long, PromoteContentDO> contentMap = convertSet(list, AppPromoteAppealRespVO::getContentId, item -> item.getContentId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(promoteContentMapper.selectBatchIds(convertSet(list, AppPromoteAppealRespVO::getContentId, item -> item.getContentId() != null)),
                PromoteContentDO::getId);
        list.forEach(item -> {
            PromoteContentDO content = contentMap.get(item.getContentId());
            if (content != null) {
                item.setContentTitle(content.getTitle());
            }
        });
    }

    private void fillAdminDisplay(List<PromoteAppealRespVO> list) {
        Map<Long, PromoteContentDO> contentMap = convertSet(list, PromoteAppealRespVO::getContentId, item -> item.getContentId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(promoteContentMapper.selectBatchIds(convertSet(list, PromoteAppealRespVO::getContentId, item -> item.getContentId() != null)),
                PromoteContentDO::getId);
        Map<Long, MemberUserDO> userMap = convertSet(list, PromoteAppealRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, PromoteAppealRespVO::getUserId, item -> item.getUserId() != null)),
                MemberUserDO::getId);
        list.forEach(item -> {
            PromoteContentDO content = contentMap.get(item.getContentId());
            if (content != null) {
                item.setContentTitle(content.getTitle());
            }
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
        });
    }
}
