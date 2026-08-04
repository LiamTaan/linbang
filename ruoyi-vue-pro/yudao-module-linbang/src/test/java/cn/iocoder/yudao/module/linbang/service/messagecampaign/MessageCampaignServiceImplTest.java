package cn.iocoder.yudao.module.linbang.service.messagecampaign;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagecampaign.MessageCampaignMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageCampaignServiceImplTest extends BaseMockitoUnitTest {

    @BeforeAll
    static void initTableInfo() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), MessageCampaignDO.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), MemberUserDO.class);
    }

    @InjectMocks
    private MessageCampaignServiceImpl service;
    @Mock
    private MessageCampaignMapper messageCampaignMapper;
    @Mock
    private MessageRecordMapper messageRecordMapper;
    @Mock
    private MemberUserMapper memberUserMapper;
    @Mock
    private MessagePushDispatchService messagePushDispatchService;
    @Mock
    private TransactionTemplate transactionTemplate;

    @Test
    void dispatchTargetUserBatches_usesBoundedKeysetBatchesAndStableDedupeKeys() {
        MessageCampaignDO campaign = buildCampaign();
        List<MemberUserDO> firstBatch = buildUsers(1L, 500);
        List<MemberUserDO> secondBatch = buildUsers(501L, 1);
        when(memberUserMapper.selectList(any())).thenReturn(firstBatch, secondBatch);

        Integer planned = ReflectionTestUtils.invokeMethod(service, "dispatchTargetUserBatches", campaign);

        assertEquals(501, planned);
        ArgumentCaptor<List<MessagePushDispatchTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        verify(messagePushDispatchService, times(2))
                .dispatchCampaign(eq(campaign), eq("消息投放活动执行"), targetCaptor.capture());
        assertEquals(500, targetCaptor.getAllValues().get(0).size());
        assertEquals(1, targetCaptor.getAllValues().get(1).size());
        assertEquals("campaign:10:user:1", targetCaptor.getAllValues().get(0).get(0).getDedupeKey());
        assertEquals("campaign:10:user:501", targetCaptor.getAllValues().get(1).get(0).getDedupeKey());
    }

    @Test
    void executeNow_marksCampaignFailedWhenDispatchFails() {
        MessageCampaignDO campaign = buildCampaign();
        when(messageCampaignMapper.selectById(campaign.getId())).thenReturn(campaign);
        when(messageCampaignMapper.update(any(), any())).thenReturn(1);
        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        }).when(transactionTemplate).execute(any());
        when(memberUserMapper.selectList(any())).thenReturn(Collections.singletonList(
                MemberUserDO.builder().id(1L).build()));
        doThrow(new IllegalStateException("dispatch unavailable")).when(messagePushDispatchService)
                .dispatchCampaign(eq(campaign), any(), any());

        assertThrows(IllegalStateException.class, () -> service.executeNow(campaign.getId()));

        ArgumentCaptor<LambdaUpdateWrapper<MessageCampaignDO>> wrapperCaptor = ArgumentCaptor.forClass(LambdaUpdateWrapper.class);
        verify(messageCampaignMapper, times(2)).update(eq(null), wrapperCaptor.capture());
        assertTrue(wrapperCaptor.getAllValues().stream()
                .anyMatch(wrapper -> wrapper.getParamNameValuePairs().containsValue(
                        MessageCenterConstants.EXECUTE_STATUS_FAILED)));
    }

    private MessageCampaignDO buildCampaign() {
        return MessageCampaignDO.builder()
                .id(10L)
                .campaignName("campaign")
                .auditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_PENDING)
                .targetMode(MessageCenterConstants.TARGET_MODE_FULL_PLATFORM)
                .build();
    }

    private List<MemberUserDO> buildUsers(long firstId, int count) {
        List<MemberUserDO> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            users.add(MemberUserDO.builder().id(firstId + i).build());
        }
        return users;
    }

}
