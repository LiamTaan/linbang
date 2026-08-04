package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppMemberQualificationServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AppMemberQualificationServiceImpl service;

    @Mock
    private MemberUserService memberUserService;
    @Mock
    private MessageRecordMapper messageRecordMapper;

    @Test
    void getReminderPage_usesDatabasePagingAndConvertsRecords() {
        AppMemberQualificationReminderPageReqVO reqVO = new AppMemberQualificationReminderPageReqVO();
        reqVO.setPageNo(2);
        reqVO.setPageSize(10);
        reqVO.setReadStatus("UNREAD");
        MessageRecordDO record = MessageRecordDO.builder()
                .id(100L)
                .title("title")
                .contentSnapshot("content")
                .readStatus("UNREAD")
                .sendTime(LocalDateTime.of(2026, 8, 4, 10, 0))
                .bizId(200L)
                .build();
        when(memberUserService.getOrCreateMemberUser(1L)).thenReturn(MemberUserDO.builder().id(2L).build());
        when(messageRecordMapper.selectQualificationReminderPage(eq(2L), eq("UNREAD"), same(reqVO),
                eq(MessageCenterConstants.BIZ_TYPE_QUALIFICATION_EXPIRY)))
                .thenReturn(new PageResult<>(Collections.singletonList(record), 11L));

        PageResult<AppMemberQualificationReminderRespVO> result = service.getReminderPage(1L, reqVO);

        assertEquals(11L, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals(100L, result.getList().get(0).getId());
        assertEquals(200L, result.getList().get(0).getBizId());
        verify(messageRecordMapper).selectQualificationReminderPage(eq(2L), eq("UNREAD"), same(reqVO),
                eq(MessageCenterConstants.BIZ_TYPE_QUALIFICATION_EXPIRY));
    }
}
