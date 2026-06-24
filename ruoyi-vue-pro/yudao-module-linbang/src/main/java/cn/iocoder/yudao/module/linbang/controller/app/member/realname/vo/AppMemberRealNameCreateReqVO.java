package cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 实名认证提交 Request VO")
@Data
public class AppMemberRealNameCreateReqVO {

    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice Zhang")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED, example = "110101199001011234")
    @NotBlank(message = "身份证号不能为空")
    private String idCardNo;

    @Schema(description = "身份证正面文件 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "身份证正面文件不能为空")
    private Long idCardFrontFileId;

    @Schema(description = "身份证反面文件 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "身份证反面文件不能为空")
    private Long idCardBackFileId;

    @Schema(description = "手持证件文件 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "手持证件文件不能为空")
    private Long holdCardFileId;
}
