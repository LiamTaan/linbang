package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameCreateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppMemberRealNameServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AppMemberRealNameServiceImpl service;

    @Mock
    private MemberUserService memberUserService;
    @Mock
    private MemberUserMapper memberUserMapper;
    @Mock
    private MemberUserRealNameMapper memberUserRealNameMapper;

    @Test
    void startVerify_rejectsMissingProfileAfterLockingUser() {
        MemberUserDO user = MemberUserDO.builder().id(1L).build();
        when(memberUserService.getOrCreateMemberUser(10L)).thenReturn(user);
        when(memberUserMapper.selectByIdForUpdate(1L)).thenReturn(user);
        when(memberUserRealNameMapper.selectByUserIdForUpdate(1L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> service.startVerify(10L));

        verify(memberUserMapper).selectByIdForUpdate(1L);
        verify(memberUserRealNameMapper, never()).insert(any(MemberUserRealNameDO.class));
    }

    @Test
    void startVerify_rejectsApprovedProfile() {
        MemberUserDO user = MemberUserDO.builder().id(1L).build();
        MemberUserRealNameDO approved = MemberUserRealNameDO.builder()
                .id(2L).userId(1L).auditStatus("APPROVED").build();
        when(memberUserService.getOrCreateMemberUser(10L)).thenReturn(user);
        when(memberUserMapper.selectByIdForUpdate(1L)).thenReturn(user);
        when(memberUserRealNameMapper.selectByUserIdForUpdate(1L)).thenReturn(approved);

        assertThrows(ServiceException.class, () -> service.startVerify(10L));

        verify(memberUserRealNameMapper, never()).updateById(any(MemberUserRealNameDO.class));
    }

    @Test
    void createOrUpdateRealName_rejectsApprovedProfile() {
        MemberUserDO user = MemberUserDO.builder().id(1L).build();
        MemberUserRealNameDO approved = MemberUserRealNameDO.builder()
                .id(2L).userId(1L).auditStatus("APPROVED").build();
        when(memberUserService.getOrCreateMemberUser(10L)).thenReturn(user);
        when(memberUserMapper.selectByIdForUpdate(1L)).thenReturn(user);
        when(memberUserRealNameMapper.selectByUserIdForUpdate(1L)).thenReturn(approved);

        assertThrows(ServiceException.class,
                () -> service.createOrUpdateRealName(10L, new AppMemberRealNameCreateReqVO()));

        verify(memberUserRealNameMapper, never()).updateById(any(MemberUserRealNameDO.class));
    }
}
