package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord.PriorityPoolRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.prioritypoolrecord.PriorityPoolRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PriorityPoolServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PriorityPoolServiceImpl service;

    @Mock
    private PriorityPoolRecordMapper priorityPoolRecordMapper;
    @Mock
    private ReviewCommentMapper reviewCommentMapper;
    @Mock
    private MerchantInfoMapper merchantInfoMapper;
    @Mock
    private MessagePushDispatchService messagePushDispatchService;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private TransactionStatus transactionStatus;

    @BeforeEach
    void setUpTenantContext() {
        TenantContextHolder.setTenantId(1L);
    }

    @AfterEach
    void clearTenantContext() {
        TenantContextHolder.clear();
    }

    @Test
    void recomputeMerchantPriorityPool_preservesManualFreeze() {
        MerchantInfoDO merchant = MerchantInfoDO.builder().id(1L).userId(10L).status("ENABLE").build();
        PriorityPoolRecordDO frozen = PriorityPoolRecordDO.builder()
                .id(20L).merchantId(1L).status("FROZEN").currentFlag(true).build();
        when(merchantInfoMapper.selectByIdForUpdate(1L)).thenReturn(merchant);
        when(priorityPoolRecordMapper.selectCurrentByMerchantId(1L)).thenReturn(frozen);

        service.recomputeMerchantPriorityPool(1L);

        verify(reviewCommentMapper, never()).selectList(any());
        verify(priorityPoolRecordMapper, never()).insert(any(PriorityPoolRecordDO.class));
        verify(messagePushDispatchService, never()).dispatchSingleIdempotent(
                any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void recomputeMerchantPriorityPool_usesTransitionIdForEntryNotification() {
        MerchantInfoDO merchant = MerchantInfoDO.builder().id(1L).userId(10L).status("ENABLE").build();
        List<ReviewCommentDO> reviews = IntStream.range(0, 15)
                .mapToObj(index -> ReviewCommentDO.builder().starLevel(5).build())
                .collect(Collectors.toList());
        when(merchantInfoMapper.selectByIdForUpdate(1L)).thenReturn(merchant);
        when(priorityPoolRecordMapper.selectCurrentByMerchantId(1L)).thenReturn(null);
        when(reviewCommentMapper.selectList(any())).thenReturn(reviews);
        doAnswer(invocation -> {
            PriorityPoolRecordDO record = invocation.getArgument(0);
            record.setId(99L);
            return 1;
        }).when(priorityPoolRecordMapper).insert(any(PriorityPoolRecordDO.class));

        service.recomputeMerchantPriorityPool(1L);

        verify(messagePushDispatchService).dispatchSingleIdempotent(
                eq("lb_priority_pool_entered"), eq("优先池入池通知"), eq("PRIORITY_POOL"), eq(1L), eq(10L),
                eq("优先池重算入池通知"), eq("lb_priority_pool_entered:1:99"));
    }
    @Test
    void recomputeAllPriorityPool_usesBoundedTransactionBatches() {
        List<MerchantInfoDO> merchants = IntStream.rangeClosed(1, 26)
                .mapToObj(id -> MerchantInfoDO.builder().id((long) id).userId((long) (100 + id)).status("ENABLE").build())
                .collect(Collectors.toList());
        when(merchantInfoMapper.selectList(any())).thenReturn(merchants, Collections.emptyList());
        when(reviewCommentMapper.selectPriorityEligibleUserIds(eq(1L), any())).thenReturn(Collections.emptyList());
        merchants.forEach(merchant -> when(merchantInfoMapper.selectByIdForUpdate(merchant.getId())).thenReturn(merchant));
        when(priorityPoolRecordMapper.selectCurrentByMerchantId(any())).thenReturn(null);
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        service.recomputeAllPriorityPool();

        verify(transactionManager, times(2)).getTransaction(any());
        verify(transactionManager, times(2)).commit(transactionStatus);
    }
}
