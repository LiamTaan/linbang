package cn.iocoder.yudao.module.linbang.controller.app.member.address.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 更新地址 Request VO")
@Data
public class AppMemberAddressUpdateReqVO extends AppMemberAddressCreateReqVO {

    @Schema(description = "地址 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "地址 ID 不能为空")
    private Long id;
}
