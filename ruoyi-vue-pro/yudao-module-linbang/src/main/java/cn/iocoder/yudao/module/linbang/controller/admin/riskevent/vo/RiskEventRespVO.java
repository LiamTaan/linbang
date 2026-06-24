package cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 风险事件 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RiskEventRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("业务类型")
    private String bizType;

    @ExcelProperty("业务ID")
    private Long bizId;

    @ExcelProperty("业务对象")
    private String bizDisplay;

    @ExcelProperty("风险类型")
    private String riskType;

    @ExcelProperty("风险等级")
    private String riskLevel;

    @ExcelProperty("命中规则")
    private String hitRuleCode;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("处理人")
    private Long handleBy;

    @ExcelProperty("处理时间")
    private LocalDateTime handleTime;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
