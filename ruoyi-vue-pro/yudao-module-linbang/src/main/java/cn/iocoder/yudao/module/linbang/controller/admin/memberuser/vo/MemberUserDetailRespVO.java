package cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.desensitize.core.slider.annotation.IdCardDesensitize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 用户详情 Response VO")
@Data
public class MemberUserDetailRespVO {

    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "平台用户业务编号")
    private String userNo;
    @Schema(description = "用户手机号")
    private String mobile;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户头像地址")
    private String avatar;
    @Schema(description = "用户性别：0 未知、1 男、2 女")
    private Integer gender;
    @Schema(description = "用户生日，格式为 yyyy-MM-dd")
    private LocalDate birthday;
    @Schema(description = "用户注册来源渠道编码")
    private String registerSource;
    @Schema(description = "当前生效角色编码；App 角色专属动作以该角色为准")
    private String currentRoleCode;
    @Schema(description = "用户已开通并允许切换的角色编码列表")
    private List<String> enabledRoleCodes;
    @Schema(description = "用户状态：ENABLE 启用、DISABLE 停用")
    private String status;
    @Schema(description = "最近一次登录时间")
    private LocalDateTime lastLoginTime;
    @Schema(description = "最近一次登录 IP 地址")
    private String lastLoginIp;
    @Schema(description = "业务备注")
    private String remark;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
    @Schema(description = "记录最后更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "实名姓名")
    private RealNameRespVO realName;
    @Schema(description = "关联服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "用户最近一笔服务商入驻申请摘要")
    private LatestEntryRespVO latestEntry;
    @Schema(description = "业务统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "用户资质列表")
    private List<QualificationRespVO> qualifications;
    @Schema(description = "用户收货地址列表")
    private List<AddressRespVO> addresses;
    @Schema(description = "最近信用分变更记录列表")
    private List<CreditRecordRespVO> creditRecords;

    @Data
    @Schema(description = "管理后台 - 实名认证摘要 Response VO")
    public static class RealNameRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "实名姓名")
        private String realName;
        @IdCardDesensitize
        @Schema(description = "实名身份证号；接口输出时按脱敏规则展示")
        private String idCardNo;
        @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "审核人后台用户 ID")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "审核驳回原因")
        private String rejectReason;
    }

    @Data
    @Schema(description = "管理后台 - 服务商摘要 Response VO")
    public static class MerchantRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "平台用户 ID，关联用户档案")
        private Long userId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人姓名")
        private String contactName;
        @Schema(description = "联系人手机号")
        private String contactMobile;
        @Schema(description = "服务商状态：ENABLE 启用、DISABLE 停用")
        private String status;
        @Schema(description = "服务商接单状态：ENABLE 可接单、DISABLE 暂停接单")
        private String acceptStatus;
        @Schema(description = "当前信用分")
        private Integer creditScore;
        @Schema(description = "信用等级编码，按平台信用等级规则展示")
        private String creditLevel;
        @Schema(description = "服务商服务范围说明")
        private String serviceScopeDesc;
    }

    @Data
    @Schema(description = "管理后台 - 最近入驻申请摘要 Response VO")
    public static class LatestEntryRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "服务商 ID，关联服务商档案")
        private Long merchantId;
        @Schema(description = "服务商入驻申请业务编号")
        private String entryNo;
        @Schema(description = "业务所属高德行政区划编码")
        private String regionCode;
        @Schema(description = "入驻初审状态：PENDING 待初审、APPROVED 已通过、REJECTED 已驳回")
        private String firstAuditStatus;
        @Schema(description = "入驻终审状态：PENDING 待终审、APPROVED 已通过、REJECTED 已驳回")
        private String finalAuditStatus;
        @Schema(description = "入驻状态：PENDING 待审核、FIRST_APPROVED 初审通过、APPROVED 终审通过、REJECTED 已驳回")
        private String status;
        @Schema(description = "业务备注")
        private String remark;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 统计摘要 Response VO")
    public static class SummaryRespVO {
        @Schema(description = "用户资质数量，单位：个")
        private Integer qualificationCount;
        @Schema(description = "审核通过的资质数量，单位：个")
        private Integer approvedQualificationCount;
        @Schema(description = "审核驳回的资质数量，单位：个")
        private Integer rejectedQualificationCount;
        @Schema(description = "用户收货地址数量，单位：个")
        private Integer addressCount;
        @Schema(description = "默认地址数量，正常情况下为 0 或 1")
        private Integer defaultAddressCount;
        @Schema(description = "用户信用分变更记录数量，单位：条")
        private Integer creditRecordCount;
        @Schema(description = "最近计算得到的信用分")
        private Integer latestCreditScore;
        @Schema(description = "最近计算得到的信用等级编码")
        private String latestCreditLevel;
        @Schema(description = "实名认证是否已审核通过")
        private Boolean realNameApproved;
        @Schema(description = "用户是否已绑定服务商档案")
        private Boolean merchantBound;
        @Schema(description = "最近一笔服务商入驻申请是否已终审通过")
        private Boolean latestEntryApproved;
    }

    @Data
    @Schema(description = "管理后台 - 资质摘要 Response VO")
    public static class QualificationRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "资质类型，按 lb_qualification_type 字典展示，例如 BUSINESS_LICENSE 营业执照、ELECTRICIAN 电工证、WELDER 焊工证")
        private String qualificationType;
        @Schema(description = "资质名称")
        private String qualificationName;
        @Schema(description = "资质证书编号")
        private String qualificationNo;
        @Schema(description = "文件 ID，关联文件中心文件")
        private Long fileId;
        @Schema(description = "资质有效期开始日期，格式为 yyyy-MM-dd")
        private LocalDate validStartDate;
        @Schema(description = "资质有效期结束日期，格式为 yyyy-MM-dd")
        private LocalDate validEndDate;
        @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "审核人后台用户 ID")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "审核驳回原因")
        private String rejectReason;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 地址摘要 Response VO")
    public static class AddressRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "收货联系人姓名")
        private String receiverName;
        @Schema(description = "收货联系人手机号")
        private String receiverMobile;
        @Schema(description = "省名称")
        private String province;
        @Schema(description = "市名称")
        private String city;
        @Schema(description = "区或县名称")
        private String district;
        @Schema(description = "街道或乡镇名称")
        private String street;
        @Schema(description = "详细地址")
        private String detailAddress;
        @Schema(description = "经度，GCJ-02 坐标系")
        private BigDecimal longitude;
        @Schema(description = "纬度，GCJ-02 坐标系")
        private BigDecimal latitude;
        @Schema(description = "高德行政区划编码")
        private String adcode;
        @Schema(description = "是否默认地址：true 是、false 否")
        private Boolean isDefault;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 信用记录摘要 Response VO")
    public static class CreditRecordRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "规则唯一编码")
        private String ruleCode;
        @Schema(description = "规则名称")
        private String ruleName;
        @Schema(description = "信用分变动值；正数加分、负数扣分")
        private Integer scoreChange;
        @Schema(description = "变动前的信用分")
        private Integer beforeScore;
        @Schema(description = "变动后的信用分")
        private Integer afterScore;
        @Schema(description = "信用规则触发类型：AUTO 系统自动触发、MANUAL 人工调整")
        private String triggerType;
        @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
        private String bizType;
        @Schema(description = "关联业务对象 ID；由 bizType 指明对象类型")
        private Long bizId;
        @Schema(description = "业务备注")
        private String remark;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }
}
