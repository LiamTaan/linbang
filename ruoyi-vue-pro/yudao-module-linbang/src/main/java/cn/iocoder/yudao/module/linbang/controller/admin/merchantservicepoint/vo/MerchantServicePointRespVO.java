package cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 服务点 Response VO")
public class MerchantServicePointRespVO {

    @ExcelProperty("ID")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("服务商ID")
    @Schema(description = "服务商 ID，关联服务商档案")
    private Long merchantId;

    @ExcelProperty("服务商名称")
    @Schema(description = "服务商名称")
    private String merchantName;

    @ExcelProperty("联系人")
    @Schema(description = "联系人姓名")
    private String contactName;

    @ExcelProperty("联系手机")
    @Schema(description = "联系人手机号")
    private String contactMobile;

    @ExcelProperty("服务点名称")
    @Schema(description = "服务点名称")
    private String pointName;

    @ExcelProperty("省")
    @Schema(description = "省名称")
    private String province;

    @ExcelProperty("市")
    @Schema(description = "市名称")
    private String city;

    @ExcelProperty("区")
    @Schema(description = "区或县名称")
    private String district;

    @ExcelProperty("街道")
    @Schema(description = "街道或乡镇名称")
    private String street;

    @ExcelProperty("详细地址")
    @Schema(description = "详细地址")
    private String detailAddress;

    @ExcelProperty("经度")
    @Schema(description = "经度，GCJ-02 坐标系")
    private BigDecimal longitude;

    @ExcelProperty("纬度")
    @Schema(description = "纬度，GCJ-02 坐标系")
    private BigDecimal latitude;

    @ExcelProperty("服务半径(公里)")
    @Schema(description = "服务点服务半径，单位：公里")
    private BigDecimal serviceRadiusKm;

    @ExcelProperty("状态")
    @Schema(description = "服务点状态：ENABLE 启用、DISABLE 停用")
    private String status;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
