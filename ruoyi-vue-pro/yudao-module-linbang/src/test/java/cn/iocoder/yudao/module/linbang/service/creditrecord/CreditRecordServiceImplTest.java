package cn.iocoder.yudao.module.linbang.service.creditrecord;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrule.CreditRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreditRecordServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private CreditRecordServiceImpl service;

    @Mock
    private CreditRecordMapper creditRecordMapper;
    @Mock
    private CreditRuleMapper creditRuleMapper;
    @Mock
    private MemberUserMapper memberUserMapper;
    @Mock
    private MerchantInfoMapper merchantInfoMapper;

    @Test
    void applyCreditRule_returnsExistingBusinessRecordWithoutChangingScore() {
        mockUserAndRule();
        when(creditRecordMapper.selectByBizKeyForUpdate(1L, "RULE", "ORDER", 9L))
                .thenReturn(CreditRecordDO.builder().id(20L).build());

        Long id = service.applyCreditRule(1L, 2L, "RULE", "ORDER", 9L, "remark");

        assertEquals(20L, id);
        verify(creditRecordMapper, never()).insert(any(CreditRecordDO.class));
        verify(merchantInfoMapper, never()).updateById(any(MerchantInfoDO.class));
    }

    @Test
    void applyCreditRule_insertsLedgerBeforeUpdatingMerchantScore() {
        mockUserAndRule();
        MerchantInfoDO merchant = MerchantInfoDO.builder()
                .id(2L).userId(1L).creditScore(100).build();
        when(creditRecordMapper.selectByBizKeyForUpdate(1L, "RULE", "ORDER", 9L)).thenReturn(null);
        when(merchantInfoMapper.selectByIdForUpdate(2L)).thenReturn(merchant);

        service.applyCreditRule(1L, 2L, "RULE", "ORDER", 9L, "remark");

        InOrder inOrder = inOrder(creditRecordMapper, merchantInfoMapper);
        inOrder.verify(creditRecordMapper).insert(any(CreditRecordDO.class));
        inOrder.verify(merchantInfoMapper).updateById(any(MerchantInfoDO.class));
    }

    @Test
    void applyCreditRule_recoversFromConcurrentBusinessRecordInsert() {
        mockUserAndRule();
        MerchantInfoDO merchant = MerchantInfoDO.builder()
                .id(2L).userId(1L).creditScore(100).build();
        CreditRecordDO concurrent = CreditRecordDO.builder().id(30L).build();
        when(creditRecordMapper.selectByBizKeyForUpdate(1L, "RULE", "ORDER", 9L))
                .thenReturn(null, concurrent);
        when(merchantInfoMapper.selectByIdForUpdate(2L)).thenReturn(merchant);
        doThrow(new DuplicateKeyException("duplicate"))
                .when(creditRecordMapper).insert(any(CreditRecordDO.class));

        Long id = service.applyCreditRule(1L, 2L, "RULE", "ORDER", 9L, "remark");

        assertEquals(30L, id);
        verify(merchantInfoMapper, never()).updateById(any(MerchantInfoDO.class));
    }

    @SuppressWarnings("unchecked")
    private void mockUserAndRule() {
        when(memberUserMapper.selectByIdForUpdate(1L)).thenReturn(MemberUserDO.builder().id(1L).build());
        when(creditRuleMapper.selectOne(any(LambdaQueryWrapperX.class))).thenReturn(CreditRuleDO.builder()
                .id(3L)
                .ruleCode("RULE")
                .ruleName("Rule")
                .scoreChange(5)
                .triggerType("SYSTEM")
                .status("ENABLE")
                .build());
    }
}
