package cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 区域合作商 Response VO")
public class PartnerInfoRespVO {

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

    @ExcelProperty("合作商名称")
    @Schema(description = "区域合作商名称")
    private String partnerName;

    @ExcelProperty("联系人")
    @Schema(description = "联系人姓名")
    private String contactName;

    @ExcelProperty("联系人手机号")
    @Schema(description = "联系人手机号")
    private String contactMobile;

    @ExcelProperty("状态")
    @Schema(description = "区域合作商状态：ENABLE 启用、DISABLE 停用")
    private String status;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;

    @Schema(description = "合作商负责的高德行政区划编码列表")
    private List<String> regionAdcodes;
}
