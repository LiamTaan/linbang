package cn.iocoder.yudao.module.linbang.job.message;

import cn.iocoder.yudao.module.linbang.service.messagecampaign.MessageCampaignService;
import cn.iocoder.yudao.module.linbang.service.messageoptimization.MessageOptimizationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageCampaignScheduleJob {

    @Resource
    private MessageCampaignService messageCampaignService;
    @Resource
    private MessageOptimizationService messageOptimizationService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void executePendingCampaigns() {
        messageCampaignService.executeScheduledCampaigns();
    }

    @Scheduled(cron = "0 15 2 * * ?")
    public void refreshOptimizationCandidates() {
        messageOptimizationService.refreshCandidates();
    }
}
