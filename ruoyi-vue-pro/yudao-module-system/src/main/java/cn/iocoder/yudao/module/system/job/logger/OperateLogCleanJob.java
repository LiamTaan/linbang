package cn.iocoder.yudao.module.system.job.logger;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.service.logger.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
@Slf4j
public class OperateLogCleanJob implements JobHandler {

    private static final int RETAIN_DAYS = 365;

    @Resource
    private OperateLogService operateLogService;

    @Override
    @TenantIgnore
    public String execute(String param) {
        int cleanCount = operateLogService.cleanOperateLogsBefore(LocalDateTime.now().minusDays(RETAIN_DAYS));
        log.info("[execute][定时清理操作日志 {} 条，保留最近 {} 天]", cleanCount, RETAIN_DAYS);
        return String.format("定时清理操作日志 %s 条，保留最近 %s 天", cleanCount, RETAIN_DAYS);
    }
}
