package cn.iocoder.yudao.module.linbang.controller.admin.memberaddress.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 用户地址表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MemberUserAddressRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21642")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17886")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "用户编号")
    @ExcelProperty("用户编号")
    private String userNo;

    @Schema(description = "用户昵称")
    @ExcelProperty("用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号")
    @ExcelProperty("用户手机号")
    private String userMobile;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @ExcelProperty("联系人")
    private String receiverName;

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("联系电话")
    private String receiverMobile;

    @Schema(description = "省")
    @ExcelProperty("省")
    private String province;

    @Schema(description = "市")
    @ExcelProperty("市")
    private String city;

    @Schema(description = "区")
    @ExcelProperty("区")
    private String district;

    @Schema(description = "街道")
    @ExcelProperty("街道")
    private String street;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("详细地址")
    private String detailAddress;

    @Schema(description = "经度")
    @ExcelProperty("经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    @ExcelProperty("纬度")
    private BigDecimal latitude;

    @Schema(description = "区域编码")
    @ExcelProperty("区域编码")
    private String adcode;

    @Schema(description = "是否默认", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否默认")
    private Boolean isDefault;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
