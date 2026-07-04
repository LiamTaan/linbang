package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 推送批次 Response VO")
@Data
public class MatchPushBatchRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "订单号，指向主订单业务编号", example = "LBO202607040001")
    private String orderNo;

    @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "PENDING_ACCEPT")
    private String orderStatus;

    @Schema(description = "下单用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "下单用户编号", example = "LBU000001")
    private String userNo;

    @Schema(description = "下单用户昵称", example = "王阿姨")
    private String userNickname;

    @Schema(description = "下单用户手机号", example = "13800138000")
    private String userMobile;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "单元号，指向拆分单元业务编号", example = "LBU202607040001-1")
    private String unitNo;

    @Schema(description = "单元序号", example = "1")
    private Integer unitSeq;

    @Schema(description = "单元标题，表示当前推送监控对应的服务内容", example = "厨房深度保洁")
    private String unitTitle;

    @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "PENDING_ACCEPT")
    private String unitStatus;

    @Schema(description = "阶段号")
    private Integer stageNo;

    @Schema(description = "批次号")
    private Integer pushBatchNo;

    @Schema(description = "起始半径公里")
    private BigDecimal radiusStartKm;

    @Schema(description = "结束半径公里")
    private BigDecimal radiusEndKm;

    @Schema(description = "计划推送时间")
    private LocalDateTime plannedAt;

    @Schema(description = "过期时间")
    private LocalDateTime expiredAt;

    @Schema(description = OpenApiSchemaConstants.MATCH_PUSH_BATCH_STATUS, example = "PUSHING")
    private String status;

    @Schema(description = OpenApiSchemaConstants.MATCH_PUSH_BATCH_TRIGGER_TYPE, example = "ORDER_PAID")
    private String triggerType;

    @Schema(description = "当前已承接服务商 ID；未接单时为空", example = "3001")
    private Long acceptedMerchantId;

    @Schema(description = "当前已承接服务商名称；未接单时为空", example = "安心家政")
    private String acceptedMerchantName;

    @Schema(description = "当前已承接服务商联系人", example = "李师傅")
    private String acceptedMerchantContactName;

    @Schema(description = "当前已承接服务商联系手机", example = "13800138001")
    private String acceptedMerchantContactMobile;

    @Schema(description = "本批次已推送服务商数", example = "3")
    private Integer pushedMerchantCount;

    @Schema(description = "本批次已接单服务商数", example = "1")
    private Integer acceptedMatchCount;

    @Schema(description = "本批次已推送服务商名称摘要，便于管理端快速识别推送对象", example = "安心家政、城南保洁队、张师傅")
    private String pushedMerchantNames;
}
