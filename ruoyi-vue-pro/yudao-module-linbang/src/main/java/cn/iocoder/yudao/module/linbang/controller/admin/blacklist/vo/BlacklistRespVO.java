package cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 黑名单 Response VO")
public class BlacklistRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("用户ID")
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;

    @ExcelProperty("用户编号")
    @Schema(description = "平台用户业务编号")
    private String userNo;

    @ExcelProperty("用户昵称")
    @Schema(description = "用户昵称")
    private String userNickname;

    @ExcelProperty("用户手机号")
    @Schema(description = "用户手机号")
    private String userMobile;

    @ExcelProperty("黑名单类型")
    @Schema(description = "黑名单类型，按平台黑名单字典展示，常见值 RISK 表示风控拉黑")
    private String blackType;

    @ExcelProperty("原因")
    @Schema(description = "业务原因说明")
    private String reason;

    @ExcelProperty("开始时间")
    @Schema(description = "业务生效开始时间")
    private LocalDateTime startTime;

    @ExcelProperty("结束时间")
    @Schema(description = "业务结束时间；为空表示尚未结束或长期有效")
    private LocalDateTime endTime;

    @ExcelProperty("状态")
    @Schema(description = "黑名单状态：ENABLE 生效、DISABLE 已停用")
    private String status;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
