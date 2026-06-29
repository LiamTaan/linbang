package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 更新资质认证 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMemberQualificationUpdateReqVO extends AppMemberQualificationCreateReqVO {

    @Schema(description = "资质 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "资质 ID 不能为空")
    private Long id;
}
