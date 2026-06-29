package cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 实名认证详情 Response VO")
@Data
public class MemberUserRealNameDetailRespVO {

    @Schema(description = "实名认证 ID", example = "1")
    private Long id;
    @Schema(description = "用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    @Schema(description = "身份证号")
    private String idCardNo;
    @Schema(description = "身份证正面文件 ID", example = "5001")
    private Long idCardFrontFileId;
    @Schema(description = "身份证反面文件 ID", example = "5002")
    private Long idCardBackFileId;
    @Schema(description = "手持证件文件 ID", example = "5003")
    private Long holdCardFileId;
    @Schema(description = "手持身份证视频文件 ID", example = "5004")
    private Long holdCardVideoFileId;
    @Schema(description = "身份证有效期开始")
    private LocalDate idCardValidFrom;
    @Schema(description = "身份证有效期结束")
    private LocalDate idCardValidEnd;
    @Schema(description = "活体核验结果")
    private String livenessResult;
    @Schema(description = "人脸核验结果")
    private String faceVerifyResult;
    @Schema(description = "微信实名关联状态")
    private String wechatRealNameStatus;
    @Schema(description = "支付宝实名关联状态")
    private String alipayRealNameStatus;
    @Schema(description = "核验供应商")
    private String verifyProvider;
    @Schema(description = "核验流水号")
    private String verifyFlowNo;
    @Schema(description = "核验发起时间")
    private LocalDateTime verifyStartedTime;
    @Schema(description = "核验完成时间")
    private LocalDateTime verifyCompletedTime;
    @Schema(description = "核验失败原因")
    private String verifyFailReason;
    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;
    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "审核人", example = "1")
    private Long auditBy;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    @Schema(description = "驳回原因")
    private String rejectReason;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "用户摘要")
    private UserRespVO user;
    @Schema(description = "关联服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "最近一次入驻申请摘要")
    private LatestEntryRespVO latestEntry;
    @Schema(description = "统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "资质列表")
    private List<QualificationRespVO> qualifications;
    @Schema(description = "信用记录列表")
    private List<CreditRecordRespVO> creditRecords;

    @Schema(description = "用户摘要")
    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "1001")
        private Long id;
        @Schema(description = "用户编号")
        private String userNo;
        @Schema(description = "手机号")
        private String mobile;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
        @Schema(description = "最近登录 IP")
        private String lastLoginIp;
    }

    @Schema(description = "服务商摘要")
    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "2001")
        private Long id;
        @Schema(description = "关联用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人")
        private String contactName;
        @Schema(description = "联系电话")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分")
        private Integer creditScore;
        @Schema(description = "信用等级")
        private String creditLevel;
        @Schema(description = "服务范围说明")
        private String serviceScopeDesc;
    }

    @Schema(description = "最近一次入驻申请摘要")
    @Data
    public static class LatestEntryRespVO {
        @Schema(description = "入驻申请 ID", example = "1")
        private Long id;
        @Schema(description = "入驻申请单号")
        private String entryNo;
        @Schema(description = "区域编码")
        private String regionCode;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String firstAuditStatus;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String finalAuditStatus;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "APPROVED")
        private String status;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "统计摘要")
    @Data
    public static class SummaryRespVO {
        @Schema(description = "资质总数")
        private Integer qualificationCount;
        @Schema(description = "通过资质数")
        private Integer approvedQualificationCount;
        @Schema(description = "驳回资质数")
        private Integer rejectedQualificationCount;
        @Schema(description = "信用记录数")
        private Integer creditRecordCount;
        @Schema(description = "最新信用分")
        private Integer latestCreditScore;
        @Schema(description = "最新信用等级")
        private String latestCreditLevel;
        @Schema(description = "是否已绑定服务商")
        private Boolean merchantBound;
        @Schema(description = "最近入驻申请是否已通过")
        private Boolean latestEntryApproved;
        @Schema(description = "是否已上传手持视频")
        private Boolean holdCardVideoUploaded;
        @Schema(description = "微信实名是否已匹配")
        private Boolean wechatMatched;
        @Schema(description = "支付宝实名是否已匹配")
        private Boolean alipayMatched;
    }

    @Schema(description = "资质摘要")
    @Data
    public static class QualificationRespVO {
        @Schema(description = "资质 ID", example = "1")
        private Long id;
        @Schema(description = "资质类型", example = "BUSINESS_LICENSE")
        private String qualificationType;
        @Schema(description = "资质名称", example = "营业执照")
        private String qualificationName;
        @Schema(description = "资质编号")
        private String qualificationNo;
        @Schema(description = "文件 ID", example = "6001")
        private Long fileId;
        @Schema(description = "有效开始日期")
        private LocalDate validStartDate;
        @Schema(description = "有效结束日期")
        private LocalDate validEndDate;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "审核人", example = "1")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "驳回原因")
        private String rejectReason;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "信用记录摘要")
    @Data
    public static class CreditRecordRespVO {
        @Schema(description = "记录 ID", example = "1")
        private Long id;
        @Schema(description = "规则编码")
        private String ruleCode;
        @Schema(description = "规则名称")
        private String ruleName;
        @Schema(description = "分值变动")
        private Integer scoreChange;
        @Schema(description = "变动前分值")
        private Integer beforeScore;
        @Schema(description = "变动后分值")
        private Integer afterScore;
        @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
        private String triggerType;
        @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "ORDER")
        private String bizType;
        @Schema(description = "业务 ID，关联订单、单元、投诉、申诉或提现等业务主键", example = "1001")
        private Long bizId;
        @Schema(description = "备注，记录加减分原因说明")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
