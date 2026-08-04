package cn.iocoder.yudao.module.linbang.service.concurrency;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo.AppMemberRoleContextRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantEntryCreateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply.MemberRoleApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberRoleApplyServiceImpl;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberRoleContextService;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantEntryServiceImpl;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationSubmissionConcurrencyTest extends BaseMockitoUnitTest {

    @Mock
    private MemberUserService memberUserService;
    @Mock
    private MemberUserMapper memberUserMapper;
    @Mock
    private MemberRoleApplyMapper memberRoleApplyMapper;
    @Mock
    private AppMemberRoleContextService memberRoleContextService;
    @Mock
    private MerchantEntryMapper merchantEntryMapper;

    @Test
    void createRoleApply_rejectsExistingActiveApplicationAfterUserLock() {
        AppMemberRoleApplyServiceImpl service = new AppMemberRoleApplyServiceImpl();
        ReflectionTestUtils.setField(service, "memberUserService", memberUserService);
        ReflectionTestUtils.setField(service, "memberUserMapper", memberUserMapper);
        ReflectionTestUtils.setField(service, "memberRoleApplyMapper", memberRoleApplyMapper);
        ReflectionTestUtils.setField(service, "appMemberRoleContextService", memberRoleContextService);
        mockLockedUser();
        AppMemberRoleContextRespVO context = new AppMemberRoleContextRespVO();
        context.setEnabledRoleCodes(Collections.<String>emptyList());
        when(memberRoleContextService.getRoleContext(100L)).thenReturn(context);
        when(memberRoleApplyMapper.selectActiveByUserIdAndRoleCodeForUpdate(1L, "PROMOTER"))
                .thenReturn(MemberRoleApplyDO.builder().id(10L).build());
        AppMemberRoleApplyCreateReqVO reqVO = new AppMemberRoleApplyCreateReqVO();
        reqVO.setApplyRoleCode("PROMOTER");

        assertThrows(ServiceException.class, () -> service.createRoleApply(100L, reqVO));

        verify(memberUserMapper).selectByIdForUpdate(1L);
        verify(memberRoleApplyMapper, never()).insert(any(MemberRoleApplyDO.class));
    }

    @Test
    void createMerchantEntry_rejectsExistingActiveApplicationAfterUserLock() {
        AppMerchantEntryServiceImpl service = new AppMerchantEntryServiceImpl();
        ReflectionTestUtils.setField(service, "memberUserService", memberUserService);
        ReflectionTestUtils.setField(service, "memberUserMapper", memberUserMapper);
        ReflectionTestUtils.setField(service, "merchantEntryMapper", merchantEntryMapper);
        mockLockedUser();
        when(merchantEntryMapper.selectLatestByUserIdForUpdate(1L))
                .thenReturn(MerchantEntryDO.builder().id(10L).status("PENDING").build());

        assertThrows(ServiceException.class,
                () -> service.createEntry(100L, new AppMerchantEntryCreateReqVO()));

        verify(memberUserMapper).selectByIdForUpdate(1L);
        verify(merchantEntryMapper, never()).insert(any(MerchantEntryDO.class));
    }

    private void mockLockedUser() {
        MemberUserDO user = MemberUserDO.builder().id(1L).build();
        when(memberUserService.getOrCreateMemberUser(100L)).thenReturn(user);
        when(memberUserMapper.selectByIdForUpdate(1L)).thenReturn(user);
    }
}
