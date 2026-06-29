package cn.iocoder.yudao.module.linbang.job.match;

import cn.iocoder.yudao.module.linbang.service.match.MatchDispatchService;
import cn.iocoder.yudao.module.linbang.service.match.PriorityPoolService;
import cn.iocoder.yudao.module.linbang.service.match.ShowcaseRewardService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LinbangMatchDispatchJob {

    @Resource
    private MatchDispatchService matchDispatchService;
    @Resource
    private PriorityPoolService priorityPoolService;
    @Resource
    private ShowcaseRewardService showcaseRewardService;

    @Scheduled(cron = "0 * * * * ?")
    public void processDispatchTick() {
        matchDispatchService.processDispatchTick();
    }

    @Scheduled(cron = "15 * * * * ?")
    public void processRewardExpiry() {
        showcaseRewardService.disableExpiredRewards();
    }

    @Scheduled(cron = "0 10 2 * * ?")
    public void recomputePriorityPool() {
        priorityPoolService.recomputeAllPriorityPool();
    }
}
