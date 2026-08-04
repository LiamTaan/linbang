package cn.iocoder.yudao.module.linbang.service.app.reminder;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userreminder.UserReminderDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userreminder.UserReminderMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppReminderServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AppReminderServiceImpl service;

    @Mock
    private UserReminderMapper userReminderMapper;
    @Mock
    private MessageRecordMapper messageRecordMapper;

    @Test
    void triggerDueReminders_recoversFromConcurrentMessageInsert() {
        LocalDateTime remindTime = LocalDateTime.of(2026, 8, 3, 9, 0);
        UserReminderDO reminder = UserReminderDO.builder()
                .id(1L)
                .userId(2L)
                .title("title")
                .content("content")
                .nextRemindTime(remindTime)
                .repeatType("NONE")
                .status("ACTIVE")
                .build();
        when(userReminderMapper.selectDueListForUpdate(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(reminder));
        when(messageRecordMapper.selectByDedupeKey(anyString())).thenReturn(null);
        doThrow(new DuplicateKeyException("duplicate"))
                .when(messageRecordMapper).insert(any(MessageRecordDO.class));
        when(messageRecordMapper.selectByDedupeKeyForUpdate(anyString()))
                .thenReturn(MessageRecordDO.builder().id(3L).build());

        service.triggerDueReminders();

        ArgumentCaptor<UserReminderDO> updateCaptor = ArgumentCaptor.forClass(UserReminderDO.class);
        verify(userReminderMapper).selectDueListForUpdate(any(LocalDateTime.class));
        verify(userReminderMapper).updateById(updateCaptor.capture());
        assertEquals("TRIGGERED", updateCaptor.getValue().getStatus());
    }
}
