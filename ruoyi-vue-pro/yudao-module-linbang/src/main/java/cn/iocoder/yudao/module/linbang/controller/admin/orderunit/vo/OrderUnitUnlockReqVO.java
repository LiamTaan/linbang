package cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 人工解锁单元 Request VO")
@Data
public class OrderUnitUnlockReqVO {

    @Schema(description = "单元编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "单元编号不能为空")
    private Long unitId;

    @Schema(description = "解锁备注", example = "人工复核后解锁")
    private String unlockRemark;

}
