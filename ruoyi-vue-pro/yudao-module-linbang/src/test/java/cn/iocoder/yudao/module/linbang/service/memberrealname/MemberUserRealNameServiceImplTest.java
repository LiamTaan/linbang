package cn.iocoder.yudao.module.linbang.service.memberrealname;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo.MemberUserRealNameAuditReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberUserRealNameServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private MemberUserRealNameServiceImpl service;

    @Mock
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Mock
    private MemberUserMapper memberUserMapper;
    @Mock
    private CreditRecordService creditRecordService;

    @Test
    void auditMemberUserRealName_rejectsApprovalBeforeIdentityVerificationPasses() {
        MemberUserRealNameDO record = MemberUserRealNameDO.builder()
                .id(10L).userId(1L).auditStatus("PENDING")
                .livenessResult("FAIL").faceVerifyResult("PASS").build();
        when(memberUserRealNameMapper.selectById(10L)).thenReturn(record);
        when(memberUserMapper.selectByIdForUpdate(1L)).thenReturn(MemberUserDO.builder().id(1L).build());
        when(memberUserRealNameMapper.selectByIdForUpdate(10L)).thenReturn(record);
        MemberUserRealNameAuditReqVO reqVO = new MemberUserRealNameAuditReqVO();
        reqVO.setId(10L);
        reqVO.setAuditStatus("APPROVED");

        assertThrows(ServiceException.class, () -> service.auditMemberUserRealName(reqVO));

        verify(memberUserRealNameMapper, never()).updateById(any(MemberUserRealNameDO.class));
        verify(creditRecordService, never()).applyCreditRule(any(), any(), any(), any(), any(), any());
    }
}
