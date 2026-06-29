package cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MessageOptimizationSaveReqVO {

    @NotNull(message = "优化记录 ID 不能为空")
    private Long id;

    private String optimizationNote;

    private String nextAction;

    private String owner;

    private LocalDateTime deadline;
}
