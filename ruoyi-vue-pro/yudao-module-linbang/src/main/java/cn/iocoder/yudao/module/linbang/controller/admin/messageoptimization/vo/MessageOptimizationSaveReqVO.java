package cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 消息优化 新增或修改 Request VO")
public class MessageOptimizationSaveReqVO {

    @NotNull(message = "优化记录 ID 不能为空")
    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "消息优化建议说明")
    private String optimizationNote;

    @Schema(description = "建议的下一步处理动作")
    private String nextAction;

    @Schema(description = "配置归属方或维护责任方")
    private String owner;

    @Schema(description = "建议处理截止时间")
    private LocalDateTime deadline;
}
