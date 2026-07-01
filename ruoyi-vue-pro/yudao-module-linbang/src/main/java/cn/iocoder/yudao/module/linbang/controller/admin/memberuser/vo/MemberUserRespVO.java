package cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户主表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MemberUserRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13556")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("用户编号")
    private String userNo;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @ExcelProperty("昵称")
    private String nickname;

    @Schema(description = "头像")
    @ExcelProperty("头像")
    private String avatar;

    @Schema(description = "性别")
    @ExcelProperty("性别")
    private Integer gender;

    @Schema(description = "生日")
    @ExcelProperty("生日")
    private LocalDate birthday;

    @Schema(description = "注册来源")
    @ExcelProperty("注册来源")
    private String registerSource;

    @Schema(description = "当前角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "当前角色编码", converter = DictConvert.class)
    @DictFormat("lb_role_code") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String currentRoleCode;

    @Schema(description = "已开通角色编码列表")
    private List<String> enabledRoleCodes;

    @Schema(description = OpenApiSchemaConstants.USER_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "最后登录时间")
    @ExcelProperty("最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    @ExcelProperty("最后登录IP")
    private String lastLoginIp;

    @Schema(description = "备注", example = "??")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
