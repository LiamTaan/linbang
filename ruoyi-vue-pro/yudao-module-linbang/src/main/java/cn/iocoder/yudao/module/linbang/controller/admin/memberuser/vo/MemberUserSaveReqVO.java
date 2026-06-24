package cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Schema(description = "管理后台 - 用户主表新增/修改 Request VO")
@Data
public class MemberUserSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13556")
    private Long id;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @NotEmpty(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "注册来源")
    private String registerSource;

    @Schema(description = "当前角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "当前角色编码不能为空")
    private String currentRoleCode;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "备注", example = "??")
    private String remark;

}
