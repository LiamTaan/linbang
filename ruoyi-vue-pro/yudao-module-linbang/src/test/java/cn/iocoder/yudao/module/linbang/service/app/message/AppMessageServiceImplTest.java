package cn.iocoder.yudao.module.linbang.service.app.message;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSettingRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appmessagesetting.AppMessageSettingDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appmessagesetting.AppMessageSettingMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import cn.iocoder.yudao.module.linbang.service.messagecampaign.MessageCampaignService;
import cn.iocoder.yudao.module.linbang.service.messagefeedback.MessageFeedbackStatService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveContentDetectService;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppMessageServiceImplTest extends BaseMockitoUnitTest {

    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                MessageRecordDO.class);
    }

    @InjectMocks
    private AppMessageServiceImpl appMessageService;
    @Mock private MessageRecordMapper messageRecordMapper;
    @Mock private MessageTemplateMapper messageTemplateMapper;
    @Mock private SensitiveContentDetectService sensitiveContentDetectService;
    @Mock private AppMessageSettingMapper appMessageSettingMapper;
    @Mock private MessageCampaignService messageCampaignService;
    @Mock private MessageFeedbackStatService messageFeedbackStatService;

    @Test
    void recordExternalClick_refreshesStatisticsOnlyForFirstSuccessfulUpdate() {
        MessageRecordDO firstRead = MessageRecordDO.builder().id(1L)
                .externalClickToken("click-token").routeValue("/pages/order/detail").build();
        MessageRecordDO staleSecondRead = MessageRecordDO.builder().id(1L)
                .externalClickToken("click-token").routeValue("/pages/order/detail").build();
        when(messageRecordMapper.selectOne(any(LambdaQueryWrapperX.class)))
                .thenReturn(firstRead, staleSecondRead);
        when(messageRecordMapper.update(isNull(), any())).thenReturn(1, 0);

        assertEquals("/pages/order/detail",
                appMessageService.recordExternalClickAndResolveTarget(1L, "click-token"));
        assertEquals("/pages/order/detail",
                appMessageService.recordExternalClickAndResolveTarget(1L, "click-token"));

        verify(messageRecordMapper, times(2)).update(isNull(), any());
        verify(messageFeedbackStatService, times(1)).refreshByRecord(any(MessageRecordDO.class));
    }

    @Test
    void getMessageSetting_returnsConcurrentInsertAfterDuplicateKey() {
        AppMessageSettingDO concurrent = AppMessageSettingDO.builder()
                .id(9L).userId(10L)
                .voiceReadEnabled(Boolean.TRUE)
                .popupEnabled(Boolean.FALSE)
                .marketingEnabled(Boolean.TRUE)
                .build();
        when(appMessageSettingMapper.selectByUserId(10L)).thenReturn(null);
        when(appMessageSettingMapper.insert(any(AppMessageSettingDO.class)))
                .thenThrow(new DuplicateKeyException("duplicate user setting"));
        when(appMessageSettingMapper.selectByUserIdForUpdate(10L)).thenReturn(concurrent);

        AppMessageSettingRespVO result = appMessageService.getMessageSetting(10L);

        assertTrue(result.getVoiceReadEnabled());
        assertFalse(result.getPopupEnabled());
        assertTrue(result.getMarketingEnabled());
    }
}
