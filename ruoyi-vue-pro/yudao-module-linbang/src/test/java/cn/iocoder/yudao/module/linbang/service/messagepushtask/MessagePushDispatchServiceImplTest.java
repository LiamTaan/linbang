package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagecampaign.MessageCampaignMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.system.api.social.SocialClientApi;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

class MessagePushDispatchServiceImplTest extends BaseMockitoUnitTest {

    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                MessageRecordDO.class);
    }

    @InjectMocks
    private MessagePushDispatchServiceImpl service;

    @Mock
    private MessageCampaignMapper messageCampaignMapper;
    @Mock
    private MessageRecordMapper messageRecordMapper;
    @Mock
    private SmsSendApi smsSendApi;
    @Mock
    private SocialClientApi socialClientApi;

    @Test
    void dispatchSingle_doesNotSendBeforeCommitOrAfterRollback() {
        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.initSynchronization();
        try {
            service.dispatchSingle("template", "task", "ORDER", 1L, 2L, "test");

            verifyNoInteractions(messageCampaignMapper, smsSendApi, socialClientApi);
            List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
            assertEquals(1, synchronizations.size());
            synchronizations.get(0).afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
            verifyNoInteractions(messageCampaignMapper, smsSendApi, socialClientApi);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

    @Test
    void buildChannelDedupeKey_isolatedByChannelAndTemplate() {
        MessageTemplateDO sms = MessageTemplateDO.builder().id(10L).channelType("SMS").build();
        MessageTemplateDO wechat = MessageTemplateDO.builder().id(11L).channelType("WECHAT_MP_TEMPLATE").build();

        String smsKey = ReflectionTestUtils.invokeMethod(service, "buildChannelDedupeKey", "logical-key", sms);
        String wechatKey = ReflectionTestUtils.invokeMethod(service, "buildChannelDedupeKey", "logical-key", wechat);

        assertEquals("logical-key:SMS:10", smsKey);
        assertEquals("logical-key:WECHAT_MP_TEMPLATE:11", wechatKey);
        assertNotEquals(smsKey, wechatKey);
    }

    @Test
    void filterAlreadyDispatchedTargets_skipsExistingDedupeKeys() {
        MessageTemplateDO template = MessageTemplateDO.builder().id(10L).channelType("SMS").build();
        MessagePushDispatchTarget existing = new MessagePushDispatchTarget(1L, null, "campaign:1:user:1");
        MessagePushDispatchTarget pending = new MessagePushDispatchTarget(2L, null, "campaign:1:user:2");
        when(messageRecordMapper.selectList(any())).thenReturn(Collections.singletonList(
                MessageRecordDO.builder().dedupeKey("campaign:1:user:1:SMS:10").build()));

        List<MessagePushDispatchTarget> result = ReflectionTestUtils.invokeMethod(service,
                "filterAlreadyDispatchedTargets", template, Arrays.asList(existing, pending));

        assertEquals(Collections.singletonList(pending), result);
    }
}
