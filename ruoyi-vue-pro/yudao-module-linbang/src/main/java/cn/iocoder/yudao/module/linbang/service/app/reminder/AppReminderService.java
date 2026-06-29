package cn.iocoder.yudao.module.linbang.service.app.reminder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderHistoryPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderHistoryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderUpdateReqVO;

import javax.validation.Valid;

public interface AppReminderService {

    Long createReminder(Long authUserId, @Valid AppReminderCreateReqVO reqVO);

    void updateReminder(Long authUserId, @Valid AppReminderUpdateReqVO reqVO);

    void deleteReminder(Long authUserId, Long id);

    AppReminderRespVO getReminder(Long authUserId, Long id);

    PageResult<AppReminderRespVO> getReminderPage(Long authUserId, AppReminderPageReqVO reqVO);

    PageResult<AppReminderHistoryRespVO> getReminderHistoryPage(Long authUserId, AppReminderHistoryPageReqVO reqVO);

    void triggerDueReminders();
}
