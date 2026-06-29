package cn.iocoder.yudao.module.linbang.service.orderinfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.OrderInfoDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.OrderInfoRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderattachment.OrderAttachmentDO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderpriceitem.OrderPriceItemDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunitproof.OrderUnitProofDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord.OrderAcceptRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderattachment.OrderAttachmentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderpriceitem.OrderPriceItemMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunitproof.OrderUnitProofMapper;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 订单主 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class OrderInfoServiceImpl implements OrderInfoService {

    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private OrderPriceItemMapper orderPriceItemMapper;
    @Resource
    private OrderAttachmentMapper orderAttachmentMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderAcceptRecordMapper orderAcceptRecordMapper;
    @Resource
    private OrderUnitProofMapper orderUnitProofMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private PayRefundMapper payRefundMapper;
    @Resource
    private PayOrderMapper payOrderMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private FileService fileService;
    @Resource
    private ConfigService configService;
    @Resource
    private OrderDetailAggregateService orderDetailAggregateService;

    @Override
    public Long createOrderInfo(OrderInfoSaveReqVO createReqVO) {
        // 插入
        OrderInfoDO orderInfo = BeanUtils.toBean(createReqVO, OrderInfoDO.class);
        orderInfoMapper.insert(orderInfo);

        // 返回
        return orderInfo.getId();
    }

    @Override
    public void updateOrderInfo(OrderInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderInfoExists(updateReqVO.getId());
        // 更新
        OrderInfoDO updateObj = BeanUtils.toBean(updateReqVO, OrderInfoDO.class);
        orderInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderInfo(Long id) {
        // 校验存在
        validateOrderInfoExists(id);
        // 删除
        orderInfoMapper.deleteById(id);
    }

    @Override
        public void deleteOrderInfoListByIds(List<Long> ids) {
        // 删除
        orderInfoMapper.deleteByIds(ids);
        }


    private void validateOrderInfoExists(Long id) {
        if (orderInfoMapper.selectById(id) == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
    }

    @Override
    public OrderInfoDO getOrderInfo(Long id) {
        return orderInfoMapper.selectById(id);
    }

    @Override
    public OrderInfoDetailRespVO getOrderInfoDetail(Long id) {
        OrderInfoDO order = orderInfoMapper.selectById(id);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        OrderDetailAggregateService.OrderDetailAggregate aggregate = orderDetailAggregateService.aggregate(order, false);
        MerchantServiceCategoryDO category = aggregate.getCategory();
        MerchantInfoDO merchant = aggregate.getMerchant();
        List<OrderPriceItemDO> priceItems = aggregate.getPriceItems();
        List<OrderAttachmentDO> attachments = aggregate.getAttachments();
        List<OrderUnitDO> units = aggregate.getUnits();
        List<OrderUnitProofDO> proofs = aggregate.getProofs();
        List<OrderAcceptRecordDO> acceptRecords = aggregate.getAcceptRecords();
        List<ComplaintDO> complaints = aggregate.getComplaints();
        List<AppealDO> appeals = aggregate.getAppeals();
        PayOrderDO payOrder = aggregate.getPayOrder();
        List<PayRefundDO> refunds = aggregate.getRefunds();
        List<OrderOperateLogDO> operateLogs = aggregate.getOperateLogs();
        Map<Long, FileDO> fileMap = aggregate.getFileMap();
        Map<Long, MerchantInfoDO> acceptRecordMerchantMap = aggregate.getAcceptRecordMerchantMap();

        OrderInfoDetailRespVO respVO = BeanUtils.toBean(order, OrderInfoDetailRespVO.class);
        respVO.setCategoryName(category != null ? category.getCategoryName() : null);
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, OrderInfoDetailRespVO.MerchantRespVO.class));
        }
        if (payOrder != null) {
            OrderInfoDetailRespVO.OrderPayRecordRespVO payRecordResp = new OrderInfoDetailRespVO.OrderPayRecordRespVO();
            payRecordResp.setId(payOrder.getId());
            payRecordResp.setMerchantOrderId(payOrder.getMerchantOrderId());
            payRecordResp.setSubject(payOrder.getSubject());
            payRecordResp.setPrice(payOrder.getPrice());
            payRecordResp.setStatus(payOrder.getStatus());
            payRecordResp.setChannelCode(payOrder.getChannelCode());
            payRecordResp.setChannelOrderNo(payOrder.getChannelOrderNo());
            payRecordResp.setRefundPrice(payOrder.getRefundPrice());
            payRecordResp.setExpireTime(payOrder.getExpireTime());
            payRecordResp.setSuccessTime(payOrder.getSuccessTime());
            payRecordResp.setCreateTime(payOrder.getCreateTime());
            respVO.setPayRecord(payRecordResp);
        }
        respVO.setPriceDetailEnabled(getBooleanConfigValue(PlatformConfigKeyConstants.APP_ORDER_PRICE_DETAIL_ENABLED, true));
        respVO.setMallEntry(buildMallEntryResp());
        respVO.setMallConsumeRelation(buildMallConsumeRelationResp(order));
        respVO.setPromoteDeduct(buildPromoteDeductResp(order));
        respVO.setPriceItems(priceItems.stream().map(item -> {
            OrderInfoDetailRespVO.OrderPriceItemRespVO itemResp = new OrderInfoDetailRespVO.OrderPriceItemRespVO();
            itemResp.setItemType(item.getItemType());
            itemResp.setItemName(item.getItemName());
            itemResp.setItemAmount(item.getItemAmount());
            itemResp.setSortNo(item.getSortNo());
            return itemResp;
        }).collect(Collectors.toList()));
        respVO.setAttachments(attachments.stream().map(item -> {
            OrderInfoDetailRespVO.OrderAttachmentRespVO itemResp = new OrderInfoDetailRespVO.OrderAttachmentRespVO();
            itemResp.setFileId(item.getFileId());
            itemResp.setFileType(item.getFileType());
            itemResp.setFileUrl(Optional.ofNullable(fileMap.get(item.getFileId())).map(FileDO::getUrl).orElse(null));
            itemResp.setSortNo(item.getSortNo());
            return itemResp;
        }).collect(Collectors.toList()));
        respVO.setUnits(units.stream().map(unit -> {
            OrderInfoDetailRespVO.OrderUnitSimpleRespVO unitResp = new OrderInfoDetailRespVO.OrderUnitSimpleRespVO();
            unitResp.setId(unit.getId());
            unitResp.setUnitNo(unit.getUnitNo());
            unitResp.setUnitSeq(unit.getUnitSeq());
            unitResp.setUnitTitle(unit.getUnitTitle());
            unitResp.setUnitType(unit.getUnitType());
            unitResp.setUnitAmount(unit.getUnitAmount());
            unitResp.setSplitMode(unit.getSplitMode());
            unitResp.setUnitContent(unit.getUnitContent());
            unitResp.setUnitProgress(unit.getUnitProgress());
            unitResp.setWorkerCount(unit.getWorkerCount());
            unitResp.setMaxAmountLimit(unit.getMaxAmountLimit());
            unitResp.setPrevUnitId(unit.getPrevUnitId());
            unitResp.setIsLocked(unit.getIsLocked());
            unitResp.setLockReason(unit.getLockReason());
            unitResp.setMerchantId(unit.getMerchantId());
            unitResp.setStatus(unit.getStatus());
            unitResp.setAcceptDeadlineTime(unit.getAcceptDeadlineTime());
            unitResp.setFinishTime(unit.getFinishTime());
            unitResp.setAppealExpireTime(unit.getAppealExpireTime());
            unitResp.setVerifyStatus(unit.getVerifyStatus());
            unitResp.setVerifyCode(unit.getVerifyCode());
            unitResp.setVerifyTime(unit.getVerifyTime());
            unitResp.setVerifyBy(unit.getVerifyBy());
            unitResp.setVerifyRemark(unit.getVerifyRemark());
            unitResp.setCreateTime(unit.getCreateTime());
            return unitResp;
        }).collect(Collectors.toList()));
        respVO.setProofs(proofs.stream().map(proof -> {
            OrderInfoDetailRespVO.OrderUnitProofRespVO proofResp = new OrderInfoDetailRespVO.OrderUnitProofRespVO();
            proofResp.setId(proof.getId());
            proofResp.setUnitId(proof.getUnitId());
            proofResp.setMerchantId(proof.getMerchantId());
            proofResp.setFileId(proof.getFileId());
            proofResp.setFileUrl(Optional.ofNullable(fileMap.get(proof.getFileId())).map(FileDO::getUrl).orElse(proof.getFileUrl()));
            proofResp.setFileHash(proof.getFileHash());
            proofResp.setProofType(proof.getProofType());
            proofResp.setProofDesc(proof.getProofDesc());
            proofResp.setProofTime(proof.getProofTime());
            proofResp.setDeviceTime(proof.getDeviceTime());
            proofResp.setLongitude(proof.getLongitude());
            proofResp.setLatitude(proof.getLatitude());
            proofResp.setAddressText(proof.getAddressText());
            return proofResp;
        }).collect(Collectors.toList()));
        respVO.setAcceptRecords(acceptRecords.stream().map(record -> {
            OrderInfoDetailRespVO.OrderAcceptRecordRespVO recordResp = new OrderInfoDetailRespVO.OrderAcceptRecordRespVO();
            recordResp.setId(record.getId());
            recordResp.setUnitId(record.getUnitId());
            recordResp.setMerchantId(record.getMerchantId());
            MerchantInfoDO acceptMerchant = acceptRecordMerchantMap.get(record.getMerchantId());
            if (acceptMerchant != null) {
                recordResp.setMerchantName(acceptMerchant.getMerchantName());
                recordResp.setMerchantContactName(acceptMerchant.getContactName());
                recordResp.setMerchantContactMobile(acceptMerchant.getContactMobile());
            }
            recordResp.setAcceptTime(record.getAcceptTime());
            recordResp.setDistanceKm(record.getDistanceKm());
            recordResp.setAcceptResult(record.getAcceptResult());
            recordResp.setRemark(record.getRemark());
            return recordResp;
        }).collect(Collectors.toList()));
        respVO.setRefunds(refunds.stream().map(refund -> {
            OrderInfoDetailRespVO.OrderRefundRespVO refundResp = new OrderInfoDetailRespVO.OrderRefundRespVO();
            refundResp.setId(refund.getId());
            refundResp.setPayOrderId(refund.getOrderId());
            refundResp.setMerchantRefundId(refund.getMerchantRefundId());
            refundResp.setStatus(refund.getStatus());
            refundResp.setStatusName(resolveRefundStatusName(refund.getStatus()));
            refundResp.setAuditStatus(refund.getAuditStatus());
            refundResp.setAuditRemark(refund.getAuditRemark());
            refundResp.setRejectReason(refund.getRejectReason());
            refundResp.setRefundPrice(refund.getRefundPrice());
            refundResp.setReason(refund.getReason());
            refundResp.setChannelErrorMsg(refund.getChannelErrorMsg());
            refundResp.setSuccessTime(refund.getSuccessTime());
            refundResp.setCreateTime(refund.getCreateTime());
            return refundResp;
        }).collect(Collectors.toList()));
        respVO.setComplaints(complaints.stream().map(complaint -> {
            OrderInfoDetailRespVO.OrderComplaintRespVO complaintResp = new OrderInfoDetailRespVO.OrderComplaintRespVO();
            complaintResp.setId(complaint.getId());
            complaintResp.setComplaintNo(complaint.getComplaintNo());
            complaintResp.setUnitId(complaint.getUnitId());
            complaintResp.setComplainantUserId(complaint.getComplainantUserId());
            complaintResp.setRespondentUserId(complaint.getRespondentUserId());
            complaintResp.setComplaintType(complaint.getComplaintType());
            complaintResp.setContent(complaint.getContent());
            complaintResp.setStatus(complaint.getStatus());
            complaintResp.setResultDesc(complaint.getResultDesc());
            complaintResp.setHandleTime(complaint.getHandleTime());
            complaintResp.setCreateTime(complaint.getCreateTime());
            return complaintResp;
        }).collect(Collectors.toList()));
        respVO.setAppeals(appeals.stream().map(appeal -> {
            OrderInfoDetailRespVO.OrderAppealRespVO appealResp = new OrderInfoDetailRespVO.OrderAppealRespVO();
            appealResp.setId(appeal.getId());
            appealResp.setAppealNo(appeal.getAppealNo());
            appealResp.setUnitId(appeal.getUnitId());
            appealResp.setUserId(appeal.getUserId());
            appealResp.setAppealType(appeal.getAppealType());
            appealResp.setContent(appeal.getContent());
            appealResp.setStatus(appeal.getStatus());
            appealResp.setAuditStatus(appeal.getAuditStatus());
            appealResp.setAuditRemark(appeal.getAuditRemark());
            appealResp.setRejectReason(appeal.getRejectReason());
            appealResp.setAuditTime(appeal.getAuditTime());
            appealResp.setAppealExpireTime(appeal.getAppealExpireTime());
            appealResp.setCreateTime(appeal.getCreateTime());
            return appealResp;
        }).collect(Collectors.toList()));
        respVO.setOperateLogs(operateLogs.stream().map(log -> {
            OrderInfoDetailRespVO.OrderOperateLogRespVO logResp = new OrderInfoDetailRespVO.OrderOperateLogRespVO();
            logResp.setId(log.getId());
            logResp.setUnitId(log.getUnitId());
            logResp.setOperateType(log.getOperateType());
            logResp.setOperateRole(log.getOperateRole());
            logResp.setOperateBy(log.getOperateBy());
            logResp.setBeforeStatus(log.getBeforeStatus());
            logResp.setAfterStatus(log.getAfterStatus());
            logResp.setRemark(log.getRemark());
            logResp.setOperateTime(log.getOperateTime());
            return logResp;
        }).collect(Collectors.toList()));
        respVO.setTimeline(buildTimeline(order, units, payOrder, refunds, complaints, appeals, operateLogs));
        return respVO;
    }

    @Override
    public PageResult<OrderInfoRespVO> getOrderInfoPage(OrderInfoPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<OrderInfoDO> pageResult = orderInfoMapper.selectPage(pageReqVO, matchedUserIds);
        List<OrderInfoRespVO> list = BeanUtils.toBean(pageResult.getList(), OrderInfoRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private String resolveRefundStatusName(Integer status) {
        if (PayRefundStatusEnum.isSuccess(status)) {
            return PayRefundStatusEnum.SUCCESS.getName();
        }
        if (PayRefundStatusEnum.isFailure(status)) {
            return PayRefundStatusEnum.FAILURE.getName();
        }
        return PayRefundStatusEnum.WAITING.getName();
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private Map<Long, FileDO> buildFileMap(Set<Long> fileIds) {
        Map<Long, FileDO> fileMap = new HashMap<>();
        for (Long fileId : fileIds) {
            if (fileId == null || fileMap.containsKey(fileId)) {
                continue;
            }
            try {
                fileMap.put(fileId, fileService.getFile(fileId));
            } catch (Exception ignored) {
            }
        }
        return fileMap;
    }

    private void fillUserDisplayInfo(List<OrderInfoRespVO> list) {
        Set<Long> userIds = convertSet(list, OrderInfoRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        Set<Long> merchantIds = convertSet(list, OrderInfoRespVO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        Set<Long> categoryIds = convertSet(list, OrderInfoRespVO::getCategoryId,
                item -> item.getCategoryId() != null);
        Map<Long, MerchantServiceCategoryDO> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantServiceCategoryMapper.selectBatchIds(categoryIds), MerchantServiceCategoryDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
            } else {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                item.setMerchantName(merchant.getMerchantName());
                item.setMerchantContactName(merchant.getContactName());
                item.setMerchantContactMobile(merchant.getContactMobile());
            }
            MerchantServiceCategoryDO category = categoryMap.get(item.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getCategoryName());
            }
        });
    }

    private List<OrderInfoDetailRespVO.OrderTimelineRespVO> buildTimeline(OrderInfoDO order,
                                                                          List<OrderUnitDO> units,
                                                                          PayOrderDO payOrder,
                                                                          List<PayRefundDO> refunds,
                                                                          List<ComplaintDO> complaints,
                                                                          List<AppealDO> appeals,
                                                                          List<OrderOperateLogDO> operateLogs) {
        List<OrderInfoDetailRespVO.OrderTimelineRespVO> timeline = new ArrayList<>();
        timeline.add(buildTimelineItem("ORDER", order.getId(), null, "订单创建", order.getRequireDesc(), order.getStatus(), order.getCreateTime()));
        if (payOrder != null) {
            timeline.add(buildTimelineItem("PAY", payOrder.getId(), null, "支付创建", payOrder.getSubject(), String.valueOf(payOrder.getStatus()), payOrder.getCreateTime()));
        }
        for (OrderUnitDO unit : units) {
            timeline.add(buildTimelineItem("UNIT", unit.getId(), unit.getId(), "单元创建", unit.getUnitTitle(), unit.getStatus(), unit.getCreateTime()));
            if (unit.getVerifyTime() != null) {
                timeline.add(buildTimelineItem("VERIFY", unit.getId(), unit.getId(), "单元核销", unit.getVerifyRemark(), unit.getVerifyStatus(), unit.getVerifyTime()));
            }
        }
        for (PayRefundDO refund : refunds) {
            timeline.add(buildTimelineItem("REFUND", refund.getId(), null, "退款申请", refund.getReason(), refund.getAuditStatus(), refund.getCreateTime()));
        }
        for (ComplaintDO complaint : complaints) {
            timeline.add(buildTimelineItem("COMPLAINT", complaint.getId(), complaint.getUnitId(), "投诉提交", complaint.getContent(), complaint.getStatus(), complaint.getCreateTime()));
        }
        for (AppealDO appeal : appeals) {
            timeline.add(buildTimelineItem("APPEAL", appeal.getId(), appeal.getUnitId(), "申诉提交", appeal.getContent(), appeal.getStatus(), appeal.getCreateTime()));
        }
        for (OrderOperateLogDO log : operateLogs) {
            timeline.add(buildTimelineItem("LOG", log.getId(), log.getUnitId(), log.getOperateType(), log.getRemark(), log.getAfterStatus(), log.getOperateTime()));
        }
        timeline.sort(Comparator.comparing(OrderInfoDetailRespVO.OrderTimelineRespVO::getEventTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return timeline;
    }

    private OrderInfoDetailRespVO.OrderTimelineRespVO buildTimelineItem(String type, Long bizId, Long unitId,
                                                                        String title, String content, String status,
                                                                        LocalDateTime eventTime) {
        OrderInfoDetailRespVO.OrderTimelineRespVO item = new OrderInfoDetailRespVO.OrderTimelineRespVO();
        item.setTimelineType(type);
        item.setBizId(bizId);
        item.setUnitId(unitId);
        item.setTitle(title);
        item.setContent(content);
        item.setStatus(status);
        item.setEventTime(eventTime);
        return item;
    }

    private OrderInfoDetailRespVO.MallEntryRespVO buildMallEntryResp() {
        OrderInfoDetailRespVO.MallEntryRespVO respVO = new OrderInfoDetailRespVO.MallEntryRespVO();
        respVO.setEnabled(getBooleanConfigValue(PlatformConfigKeyConstants.APP_MALL_ENTRY_ENABLED, false));
        respVO.setTitle(getConfigValue(PlatformConfigKeyConstants.APP_MALL_ENTRY_TITLE));
        respVO.setUrl(getConfigValue(PlatformConfigKeyConstants.APP_MALL_ENTRY_URL));
        return respVO;
    }

    private OrderInfoDetailRespVO.MallConsumeRelationRespVO buildMallConsumeRelationResp(OrderInfoDO order) {
        OrderInfoDetailRespVO.MallConsumeRelationRespVO respVO = new OrderInfoDetailRespVO.MallConsumeRelationRespVO();
        respVO.setConsumeRecordId(order.getMallConsumeRecordId());
        respVO.setConsumeRecordNo(order.getMallConsumeRecordNo());
        respVO.setConsumeAmount(order.getMallConsumeAmount());
        respVO.setConsumeStatus(order.getMallConsumeStatus());
        return respVO;
    }

    private OrderInfoDetailRespVO.PromoteDeductRespVO buildPromoteDeductResp(OrderInfoDO order) {
        OrderInfoDetailRespVO.PromoteDeductRespVO respVO = new OrderInfoDetailRespVO.PromoteDeductRespVO();
        respVO.setDeductAmount(order.getPromoteDeductAmount());
        respVO.setSourceType(order.getPromoteDeductSourceType());
        respVO.setSourceId(order.getPromoteDeductSourceId());
        respVO.setSourceNo(order.getPromoteDeductSourceNo());
        respVO.setPayableAmountAfterDeduct(order.getPayableAmountAfterDeduct());
        return respVO;
    }

    private String getConfigValue(String key) {
        return Optional.ofNullable(configService.getConfigByKey(key))
                .map(config -> config.getValue())
                .orElse("");
    }

    private Boolean getBooleanConfigValue(String key, boolean defaultValue) {
        String value = getConfigValue(key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

}
