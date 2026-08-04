package cn.iocoder.yudao.module.linbang.service.concurrency;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoteappeal.PromoteAppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontent.PromoteContentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.rewardorderparticipation.RewardOrderParticipationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoteappeal.PromoteAppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promotecontent.PromoteContentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.rewardorderparticipation.RewardOrderParticipationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.showcasereward.ShowcaseRewardMapper;
import cn.iocoder.yudao.module.linbang.service.app.rewardorder.AppRewardOrderServiceImpl;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.promoteappeal.PromoteAppealServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DuplicateSubmissionRecoveryTest extends BaseMockitoUnitTest {

    @Mock
    private MemberUserService memberUserService;
    @Mock
    private ShowcaseRewardMapper showcaseRewardMapper;
    @Mock
    private RewardOrderParticipationMapper participationMapper;
    @Mock
    private PromoteContentMapper promoteContentMapper;
    @Mock
    private PromoteAppealMapper promoteAppealMapper;

    @Test
    @SuppressWarnings("unchecked")
    void participateRewardOrder_returnsExistingParticipation() {
        AppRewardOrderServiceImpl service = new AppRewardOrderServiceImpl();
        ReflectionTestUtils.setField(service, "memberUserService", memberUserService);
        ReflectionTestUtils.setField(service, "showcaseRewardMapper", showcaseRewardMapper);
        ReflectionTestUtils.setField(service, "rewardOrderParticipationMapper", participationMapper);
        when(memberUserService.getOrCreateMemberUser(100L))
                .thenReturn(MemberUserDO.builder().id(1L).build());
        ShowcaseRewardDO reward = ShowcaseRewardDO.builder()
                .id(10L).userId(2L).auditStatus("APPROVED").build();
        doReturn(reward).when(showcaseRewardMapper)
                .selectOneForUpdate(any(SFunction.class), eq(10L));
        when(participationMapper.selectByRewardAndParticipantForUpdate(10L, 1L))
                .thenReturn(RewardOrderParticipationDO.builder().id(20L).build());
        AppRewardOrderParticipateReqVO reqVO = new AppRewardOrderParticipateReqVO();
        reqVO.setRewardOrderId(10L);

        Long id = service.participateRewardOrder(100L, reqVO);

        assertEquals(20L, id);
        verify(participationMapper, never()).insert(any(RewardOrderParticipationDO.class));
    }

    @Test
    void createPromoteAppeal_returnsExistingPendingAppeal() {
        PromoteAppealServiceImpl service = new PromoteAppealServiceImpl();
        ReflectionTestUtils.setField(service, "promoteContentMapper", promoteContentMapper);
        ReflectionTestUtils.setField(service, "promoteAppealMapper", promoteAppealMapper);
        PromoteContentDO content = PromoteContentDO.builder().id(10L).userId(1L).promoterId(2L).build();
        when(promoteContentMapper.selectByIdForUpdate(10L)).thenReturn(content);
        when(promoteAppealMapper.selectPendingForUpdate(10L, 2L))
                .thenReturn(PromoteAppealDO.builder().id(20L).build());
        AppPromoteAppealCreateReqVO reqVO = new AppPromoteAppealCreateReqVO();
        reqVO.setContentId(10L);
        reqVO.setAppealReason("reason");

        Long id = service.createAppAppeal(1L, reqVO);

        assertEquals(20L, id);
        verify(promoteAppealMapper, never()).insert(any(PromoteAppealDO.class));
    }

    @Test
    void auditPromoteAppeal_rejectsPreviouslyAuditedAppeal() {
        PromoteAppealServiceImpl service = new PromoteAppealServiceImpl();
        ReflectionTestUtils.setField(service, "promoteContentMapper", promoteContentMapper);
        ReflectionTestUtils.setField(service, "promoteAppealMapper", promoteAppealMapper);
        PromoteAppealDO approved = PromoteAppealDO.builder()
                .id(20L).contentId(10L).status("APPROVED").build();
        when(promoteAppealMapper.selectById(20L)).thenReturn(approved);
        when(promoteContentMapper.selectByIdForUpdate(10L))
                .thenReturn(PromoteContentDO.builder().id(10L).build());
        when(promoteAppealMapper.selectByIdForUpdate(20L)).thenReturn(approved);
        PromoteAppealAuditReqVO reqVO = new PromoteAppealAuditReqVO();
        reqVO.setId(20L);
        reqVO.setAuditResult("APPROVED");

        assertThrows(ServiceException.class, () -> service.auditAppeal(1L, reqVO));

        verify(promoteAppealMapper, never()).updateById(any(PromoteAppealDO.class));
    }
}
