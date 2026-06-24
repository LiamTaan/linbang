package cn.iocoder.yudao.module.linbang.service.app.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderattachment.OrderAttachmentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderpriceitem.OrderPriceItemDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunitproof.OrderUnitProofDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberaddress.MemberUserAddressMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord.OrderAcceptRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderattachment.OrderAttachmentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderpriceitem.OrderPriceItemMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunitproof.OrderUnitProofMapper;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;
import cn.iocoder.yudao.module.linbang.service.memberqualification.MemberQualificationExpiryService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AppOrderServiceImpl implements AppOrderService {

    private static final BigDecimal MAX_UNIT_AMOUNT = new BigDecimal("200");

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserAddressMapper memberUserAddressMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
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
    private MemberQualificationExpiryService memberQualificationExpiryService;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private PayRefundMapper payRefundMapper;
    @Resource
    private PayOrderMapper payOrderMapper;
    @Resource
    private AmapLocationService amapLocationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long authUserId, @Valid AppOrderCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        if (!Boolean.TRUE.equals(reqVO.getAgreementConfirmed())) {
            throw exception(ORDER_AGREEMENT_NOT_CONFIRMED);
        }

        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(reqVO.getCategoryId());
        if (category == null || !"ENABLE".equals(category.getStatus())) {
            throw exception(ORDER_CATEGORY_DISABLED);
        }
        MemberUserAddressDO address = memberUserAddressMapper.selectOne(new LambdaQueryWrapperX<MemberUserAddressDO>()
                .eq(MemberUserAddressDO::getId, reqVO.getAddressId())
                .eq(MemberUserAddressDO::getUserId, loginUser.getId()));
        if (address == null) {
            throw exception(ORDER_ADDRESS_INVALID);
        }
        address = ensureResolvedAddress(address);

        BigDecimal orderAmount = calculateOrderAmount(reqVO);
        boolean splitRequired = shouldSplit(reqVO, category, orderAmount);
        OrderInfoDO order = OrderInfoDO.builder()
                .orderNo("LBO" + IdUtil.getSnowflakeNextIdStr())
                .userId(loginUser.getId())
                .categoryId(category.getId())
                .title(reqVO.getTitle())
                .pricingMode(StrUtil.blankToDefault(reqVO.getPricingMode(), category.getDefaultPricingMode()))
                .budgetAmount(reqVO.getBudgetAmount())
                .orderAmount(orderAmount)
                .serviceDurationDesc(reqVO.getServiceDurationDesc())
                .quantity(reqVO.getQuantity())
                .requireDesc(reqVO.getRequireDesc())
                .addressId(address.getId())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .detailAddress(address.getDetailAddress())
                .longitude(address.getLongitude())
                .latitude(address.getLatitude())
                .needInvoice(reqVO.getNeedInvoice())
                .needSplit(splitRequired)
                .splitStatus(splitRequired ? "SPLIT" : "UNSPLIT")
                .agreementConfirmed(Boolean.TRUE)
                .status("PENDING_PAY")
                .build();
        orderInfoMapper.insert(order);

        savePriceItems(order.getId(), reqVO.getPriceItems());
        saveAttachments(order.getId(), reqVO.getAttachmentFileIds());
        saveUnits(order, splitRequired, orderAmount);
        saveOperateLog(order.getId(), null, "CREATE_ORDER", "USER", loginUser.getId(), null, order.getStatus(), "用户创建订单");
        return order.getId();
    }

    @Override
    public PageResult<AppOrderPageItemRespVO> getOrderPage(Long authUserId, AppOrderPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<OrderInfoDO> pageResult = orderInfoMapper.selectPage(reqVO, new LambdaQueryWrapperX<OrderInfoDO>()
                .eq(OrderInfoDO::getUserId, loginUser.getId())
                .eqIfPresent(OrderInfoDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OrderInfoDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(OrderInfoDO::getPricingMode, reqVO.getPricingMode())
                .orderByDesc(OrderInfoDO::getId));
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(pageResult.getList());
        List<AppOrderPageItemRespVO> list = pageResult.getList().stream().map(order -> {
            AppOrderPageItemRespVO respVO = new AppOrderPageItemRespVO();
            respVO.setId(order.getId());
            respVO.setOrderNo(order.getOrderNo());
            respVO.setCategoryId(order.getCategoryId());
            respVO.setCategoryName(Optional.ofNullable(categoryMap.get(order.getCategoryId()))
                    .map(MerchantServiceCategoryDO::getCategoryName).orElse(null));
            respVO.setTitle(order.getTitle());
            respVO.setPricingMode(order.getPricingMode());
            respVO.setOrderAmount(order.getOrderAmount());
            respVO.setServiceDurationDesc(order.getServiceDurationDesc());
            respVO.setDistanceKm(calculateOrderDistanceKm(order));
            respVO.setStatus(order.getStatus());
            respVO.setSplitStatus(order.getSplitStatus());
            respVO.setPayStatus(resolvePayStatus(order));
            respVO.setCreateTime(order.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public AppOrderDetailRespVO getOrderDetail(Long authUserId, Long orderId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), orderId);
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(order.getCategoryId());
        List<OrderPriceItemDO> priceItems = orderPriceItemMapper.selectList(new LambdaQueryWrapperX<OrderPriceItemDO>()
                .eq(OrderPriceItemDO::getOrderId, orderId).orderByAsc(OrderPriceItemDO::getSortNo, OrderPriceItemDO::getId));
        List<OrderAttachmentDO> attachments = orderAttachmentMapper.selectList(new LambdaQueryWrapperX<OrderAttachmentDO>()
                .eq(OrderAttachmentDO::getOrderId, orderId).orderByAsc(OrderAttachmentDO::getSortNo, OrderAttachmentDO::getId));
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, orderId).orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
        List<OrderUnitProofDO> proofs = orderUnitProofMapper.selectList(new LambdaQueryWrapperX<OrderUnitProofDO>()
                .eq(OrderUnitProofDO::getOrderId, orderId)
                .orderByDesc(OrderUnitProofDO::getProofTime, OrderUnitProofDO::getId));
        List<OrderAcceptRecordDO> acceptRecords = orderAcceptRecordMapper.selectList(new LambdaQueryWrapperX<OrderAcceptRecordDO>()
                .eq(OrderAcceptRecordDO::getOrderId, orderId)
                .orderByDesc(OrderAcceptRecordDO::getAcceptTime, OrderAcceptRecordDO::getId));
        List<ComplaintDO> complaints = complaintMapper.selectList(new LambdaQueryWrapperX<ComplaintDO>()
                .eq(ComplaintDO::getOrderId, orderId)
                .orderByDesc(ComplaintDO::getCreateTime, ComplaintDO::getId));
        List<AppealDO> appeals = appealMapper.selectList(new LambdaQueryWrapperX<AppealDO>()
                .eq(AppealDO::getOrderId, orderId)
                .orderByDesc(AppealDO::getCreateTime, AppealDO::getId));
        PayOrderDO payOrder = order.getPayOrderId() == null ? null : payOrderMapper.selectById(order.getPayOrderId());
        List<PayRefundDO> refunds = order.getPayOrderId() == null ? Collections.emptyList()
                : payRefundMapper.selectList(new LambdaQueryWrapperX<PayRefundDO>()
                .eq(PayRefundDO::getOrderId, order.getPayOrderId())
                .orderByDesc(PayRefundDO::getCreateTime, PayRefundDO::getId));
        List<OrderOperateLogDO> operateLogs = orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(OrderOperateLogDO::getOrderId, orderId)
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));

        AppOrderDetailRespVO respVO = new AppOrderDetailRespVO();
        respVO.setId(order.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setUserId(order.getUserId());
        respVO.setMerchantId(order.getMerchantId());
        respVO.setCategoryId(order.getCategoryId());
        respVO.setCategoryName(category != null ? category.getCategoryName() : null);
        respVO.setTitle(order.getTitle());
        respVO.setPricingMode(order.getPricingMode());
        respVO.setBudgetAmount(order.getBudgetAmount());
        respVO.setOrderAmount(order.getOrderAmount());
        respVO.setQuantity(order.getQuantity());
        respVO.setServiceDurationDesc(order.getServiceDurationDesc());
        respVO.setRequireDesc(order.getRequireDesc());
        respVO.setAddressId(order.getAddressId());
        respVO.setProvince(order.getProvince());
        respVO.setCity(order.getCity());
        respVO.setDistrict(order.getDistrict());
        respVO.setStreet(order.getStreet());
        respVO.setDetailAddress(order.getDetailAddress());
        respVO.setLongitude(order.getLongitude());
        respVO.setLatitude(order.getLatitude());
        respVO.setNeedInvoice(order.getNeedInvoice());
        respVO.setNeedSplit(order.getNeedSplit());
        respVO.setSplitStatus(order.getSplitStatus());
        respVO.setAgreementConfirmed(order.getAgreementConfirmed());
        respVO.setStatus(order.getStatus());
        respVO.setPayOrderId(order.getPayOrderId());
        if (payOrder != null) {
            AppOrderDetailRespVO.OrderPayRecordRespVO payRecordResp = new AppOrderDetailRespVO.OrderPayRecordRespVO();
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
        respVO.setPriceItems(priceItems.stream().map(item -> {
            AppOrderDetailRespVO.OrderPriceItemRespVO itemResp = new AppOrderDetailRespVO.OrderPriceItemRespVO();
            itemResp.setItemType(item.getItemType());
            itemResp.setItemName(item.getItemName());
            itemResp.setItemAmount(item.getItemAmount());
            itemResp.setSortNo(item.getSortNo());
            return itemResp;
        }).collect(Collectors.toList()));
        respVO.setAttachments(attachments.stream().map(item -> {
            AppOrderDetailRespVO.OrderAttachmentRespVO itemResp = new AppOrderDetailRespVO.OrderAttachmentRespVO();
            itemResp.setFileId(item.getFileId());
            itemResp.setFileType(item.getFileType());
            itemResp.setFileUrl(null);
            itemResp.setSortNo(item.getSortNo());
            return itemResp;
        }).collect(Collectors.toList()));
        respVO.setUnits(units.stream().map(unit -> {
            AppOrderDetailRespVO.OrderUnitSimpleRespVO unitResp = new AppOrderDetailRespVO.OrderUnitSimpleRespVO();
            unitResp.setId(unit.getId());
            unitResp.setUnitNo(unit.getUnitNo());
            unitResp.setUnitSeq(unit.getUnitSeq());
            unitResp.setUnitAmount(unit.getUnitAmount());
            unitResp.setStatus(unit.getStatus());
            unitResp.setIsLocked(unit.getIsLocked());
            return unitResp;
        }).collect(Collectors.toList()));
        respVO.setProofs(proofs.stream().map(proof -> {
            AppOrderDetailRespVO.OrderUnitProofRespVO proofResp = new AppOrderDetailRespVO.OrderUnitProofRespVO();
            proofResp.setId(proof.getId());
            proofResp.setUnitId(proof.getUnitId());
            proofResp.setMerchantId(proof.getMerchantId());
            proofResp.setFileId(proof.getFileId());
            proofResp.setProofType(proof.getProofType());
            proofResp.setProofDesc(proof.getProofDesc());
            proofResp.setProofTime(proof.getProofTime());
            proofResp.setLongitude(proof.getLongitude());
            proofResp.setLatitude(proof.getLatitude());
            return proofResp;
        }).collect(Collectors.toList()));
        respVO.setAcceptRecords(acceptRecords.stream().map(record -> {
            AppOrderDetailRespVO.OrderAcceptRecordRespVO recordResp = new AppOrderDetailRespVO.OrderAcceptRecordRespVO();
            recordResp.setId(record.getId());
            recordResp.setUnitId(record.getUnitId());
            recordResp.setMerchantId(record.getMerchantId());
            recordResp.setAcceptTime(record.getAcceptTime());
            recordResp.setDistanceKm(record.getDistanceKm());
            recordResp.setAcceptResult(record.getAcceptResult());
            recordResp.setRemark(record.getRemark());
            return recordResp;
        }).collect(Collectors.toList()));
        respVO.setRefunds(refunds.stream().map(refund -> {
            AppOrderDetailRespVO.OrderRefundRespVO refundResp = new AppOrderDetailRespVO.OrderRefundRespVO();
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
            AppOrderDetailRespVO.OrderComplaintRespVO complaintResp = new AppOrderDetailRespVO.OrderComplaintRespVO();
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
            AppOrderDetailRespVO.OrderAppealRespVO appealResp = new AppOrderDetailRespVO.OrderAppealRespVO();
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
            appealResp.setCreateTime(appeal.getCreateTime());
            return appealResp;
        }).collect(Collectors.toList()));
        respVO.setOperateLogs(operateLogs.stream().map(log -> {
            AppOrderDetailRespVO.OrderOperateLogRespVO logResp = new AppOrderDetailRespVO.OrderOperateLogRespVO();
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
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long authUserId, @Valid AppOrderCancelReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = orderInfoMapper.selectById(reqVO.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (!Objects.equals(order.getUserId(), loginUser.getId())) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        if (!Arrays.asList("PENDING_PAY", "PENDING_ACCEPT").contains(order.getStatus())) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CANCEL);
        }

        String beforeStatus = order.getStatus();
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .status("CLOSED")
                .build());
        orderUnitMapper.update(OrderUnitDO.builder().status("CLOSED").build(), new LambdaUpdateWrapper<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .in(OrderUnitDO::getStatus, Arrays.asList("PENDING_CREATE", "PENDING_ACCEPT", "ACCEPTED")));
        saveOperateLog(order.getId(), null, "CANCEL_ORDER", "USER", loginUser.getId(),
                beforeStatus, "CLOSED", StrUtil.blankToDefault(reqVO.getCancelReason(), "用户取消订单"));
        return Boolean.TRUE;
    }

    @Override
    public PageResult<AppOrderUnitRespVO> getOrderUnitPage(Long authUserId, AppOrderUnitPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        validateAndGetAccessibleOrder(authUserId, loginUser.getId(), reqVO.getOrderId());
        LambdaQueryWrapperX<OrderUnitDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(OrderUnitDO::getOrderId, reqVO.getOrderId());
        queryWrapper.eqIfPresent(OrderUnitDO::getStatus, reqVO.getStatus());
        queryWrapper.orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId);
        PageResult<OrderUnitDO> pageResult = orderUnitMapper.selectPage(reqVO, queryWrapper);
        List<AppOrderUnitRespVO> list = pageResult.getList().stream()
                .map(this::toOrderUnitRespVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public AppOrderUnitRespVO getOrderUnit(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(id);
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (!canAccessUnit(authUserId, loginUser.getId(), unit)) {
            throw exception(ORDER_UNIT_ACCESS_DENIED);
        }
        return toOrderUnitRespVO(unit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startOrderUnitService(Long authUserId, @Valid AppOrderUnitStartServiceReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        if (!"ENABLE".equals(merchant.getAcceptStatus())) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (!Objects.equals(unit.getMerchantId(), merchant.getId())) {
            throw exception(ORDER_UNIT_ACCESS_DENIED);
        }
        if (!Objects.equals(unit.getStatus(), "ACCEPTED")) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CONFIRM);
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }

        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .status("SERVING")
                .build());
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .status("SERVING")
                .build());
        saveOperateLog(order.getId(), unit.getId(), "START_UNIT_SERVICE", "MERCHANT", loginUser.getId(),
                unit.getStatus(), "SERVING", StrUtil.blankToDefault(reqVO.getStartRemark(), "服务商开始服务"));
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmOrderUnit(Long authUserId, @Valid AppOrderUnitConfirmReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), unit.getOrderId());
        if (!Objects.equals(order.getUserId(), loginUser.getId())) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        if (!Objects.equals(unit.getStatus(), "PENDING_CONFIRM")) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CONFIRM);
        }

        LocalDateTime now = LocalDateTime.now();
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .status("FINISHED")
                .finishTime(now)
                .build());
        saveOperateLog(order.getId(), unit.getId(), "CONFIRM_UNIT_FINISH", "USER", loginUser.getId(),
                unit.getStatus(), "FINISHED", StrUtil.blankToDefault(reqVO.getConfirmRemark(), "用户确认单元完成"));

        OrderUnitDO nextUnit = orderUnitMapper.selectOne(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .eq(OrderUnitDO::getPrevUnitId, unit.getId()));
        if (nextUnit != null && Boolean.TRUE.equals(nextUnit.getIsLocked())
                && Objects.equals(nextUnit.getStatus(), "PENDING_CREATE")) {
            orderUnitMapper.updateById(OrderUnitDO.builder()
                    .id(nextUnit.getId())
                    .isLocked(Boolean.FALSE)
                    .lockReason(null)
                    .status("PENDING_ACCEPT")
                    .build());
            saveOperateLog(order.getId(), nextUnit.getId(), "UNLOCK_NEXT_UNIT", "SYSTEM", 0L,
                    nextUnit.getStatus(), "PENDING_ACCEPT", "前序单元已确认完成，自动解锁下一单元");
        }

        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
        String nextOrderStatus = resolveOrderStatusAfterUnitConfirm(units);
        if (!Objects.equals(order.getStatus(), nextOrderStatus)) {
            orderInfoMapper.updateById(OrderInfoDO.builder()
                    .id(order.getId())
                    .status(nextOrderStatus)
                    .build());
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppOrderAcceptRespVO acceptOrder(Long authUserId, @Valid AppOrderAcceptCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        OrderInfoDO order = validateAndGetAccessibleOrderForAccept(loginUser.getId(), reqVO.getOrderId());
        order = ensureResolvedOrderLocation(order);
        OrderUnitDO unit = resolveTargetUnit(reqVO, order.getId());
        if (Boolean.TRUE.equals(unit.getIsLocked())) {
            throw exception(ORDER_UNIT_LOCKED);
        }

        int updated = orderUnitMapper.update(null, new LambdaUpdateWrapper<OrderUnitDO>()
                .eq(OrderUnitDO::getId, unit.getId())
                .eq(OrderUnitDO::getStatus, "PENDING_ACCEPT")
                .eq(OrderUnitDO::getIsLocked, Boolean.FALSE)
                .isNull(OrderUnitDO::getMerchantId)
                .set(OrderUnitDO::getMerchantId, merchant.getId())
                .set(OrderUnitDO::getStatus, "ACCEPTED"));
        BigDecimal distanceKm = calculateDistanceToMerchant(order, merchant.getId());
        OrderAcceptRecordDO acceptRecord = OrderAcceptRecordDO.builder()
                .orderId(order.getId())
                .unitId(unit.getId())
                .merchantId(merchant.getId())
                .acceptTime(LocalDateTime.now())
                .distanceKm(distanceKm)
                .acceptResult(updated > 0 ? "SUCCESS" : "FAILED")
                .build();
        orderAcceptRecordMapper.insert(acceptRecord);
        if (updated <= 0) {
            throw exception(ORDER_UNIT_ACCEPTED_BY_OTHER);
        }

        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .merchantId(merchant.getId())
                .status("ACCEPTED")
                .build());
        saveOperateLog(order.getId(), unit.getId(), "ACCEPT_ORDER", "MERCHANT", loginUser.getId(),
                unit.getStatus(), "ACCEPTED", "服务商接单成功");

        AppOrderAcceptRespVO respVO = new AppOrderAcceptRespVO();
        respVO.setOrderId(order.getId());
        respVO.setUnitId(unit.getId());
        respVO.setAcceptResult("SUCCESS");
        respVO.setStatus("ACCEPTED");
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean uploadDeliveryProof(Long authUserId, @Valid AppDeliveryProofUploadReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (!Objects.equals(unit.getMerchantId(), merchant.getId())) {
            throw exception(ORDER_UNIT_ACCESS_DENIED);
        }
        if (!Arrays.asList("ACCEPTED", "SERVING").contains(unit.getStatus())) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CONFIRM);
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        for (Long fileId : reqVO.getFileIds()) {
            orderUnitProofMapper.insert(OrderUnitProofDO.builder()
                    .unitId(unit.getId())
                    .orderId(order.getId())
                    .merchantId(merchant.getId())
                    .fileId(fileId)
                    .proofType(reqVO.getProofType())
                    .proofDesc(reqVO.getProofDesc())
                    .proofTime(now)
                    .longitude(reqVO.getLongitude())
                    .latitude(reqVO.getLatitude())
                    .build());
        }
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .status("PENDING_CONFIRM")
                .build());
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .status("PENDING_CONFIRM")
                .build());
        saveOperateLog(order.getId(), unit.getId(), "UPLOAD_DELIVERY_PROOF", "MERCHANT", loginUser.getId(),
                unit.getStatus(), "PENDING_CONFIRM", "服务商上传交付凭证");
        return Boolean.TRUE;
    }

    private OrderInfoDO validateAndGetAccessibleOrder(Long authUserId, Long lbUserId, Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(order.getUserId(), lbUserId)) {
            return order;
        }
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId()));
        if (merchant != null && Objects.equals(order.getMerchantId(), merchant.getId())) {
            return order;
        }
        throw exception(ORDER_ACCESS_DENIED);
    }

    private OrderInfoDO validateAndGetAccessibleOrderForAccept(Long lbUserId, Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(order.getUserId(), lbUserId)) {
            throw exception(ORDER_STATUS_NOT_ALLOW_ACCEPT);
        }
        if (!Arrays.asList("PENDING_ACCEPT", "ACCEPTED", "SERVING").contains(order.getStatus())) {
            throw exception(ORDER_STATUS_NOT_ALLOW_ACCEPT);
        }
        return order;
    }

    private boolean canAccessUnit(Long authUserId, Long lbUserId, OrderUnitDO unit) {
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(order.getUserId(), lbUserId)) {
            return true;
        }
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId()));
        return merchant != null && Objects.equals(order.getMerchantId(), merchant.getId());
    }

    private OrderUnitDO resolveTargetUnit(AppOrderAcceptCreateReqVO reqVO, Long orderId) {
        OrderUnitDO unit;
        if (reqVO.getUnitId() != null) {
            unit = orderUnitMapper.selectById(reqVO.getUnitId());
            if (unit == null || !Objects.equals(unit.getOrderId(), orderId)) {
                throw exception(ORDER_UNIT_NOT_EXISTS);
            }
        } else {
            List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                    .eq(OrderUnitDO::getOrderId, orderId)
                    .eq(OrderUnitDO::getStatus, "PENDING_ACCEPT")
                    .eq(OrderUnitDO::getIsLocked, Boolean.FALSE)
                    .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
            unit = CollUtil.getFirst(units);
            if (unit == null) {
                throw exception(ORDER_UNIT_NOT_EXISTS);
            }
        }
        return unit;
    }

    private MerchantInfoDO getRequiredMerchant(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        memberQualificationExpiryService.validateMerchantCanAccept(loginUser.getId());
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .eq(MerchantInfoDO::getStatus, "ENABLE"));
        if (merchant == null) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return merchant;
    }

    private MemberUserAddressDO ensureResolvedAddress(MemberUserAddressDO address) {
        if (address == null) {
            return null;
        }
        if (address.getLongitude() != null && address.getLatitude() != null
                && StrUtil.isNotBlank(address.getProvince()) && StrUtil.isNotBlank(address.getCity())
                && StrUtil.isNotBlank(address.getDistrict())) {
            return address;
        }
        AmapLocationService.ResolvedAddress resolved = amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .detailAddress(address.getDetailAddress())
                .longitude(address.getLongitude())
                .latitude(address.getLatitude())
                .adcode(address.getAdcode())
                .build());
        memberUserAddressMapper.updateById(MemberUserAddressDO.builder()
                .id(address.getId())
                .province(resolved.getProvince())
                .city(resolved.getCity())
                .district(resolved.getDistrict())
                .street(resolved.getStreet())
                .detailAddress(resolved.getDetailAddress())
                .longitude(resolved.getLongitude())
                .latitude(resolved.getLatitude())
                .adcode(resolved.getAdcode())
                .build());
        address.setProvince(resolved.getProvince());
        address.setCity(resolved.getCity());
        address.setDistrict(resolved.getDistrict());
        address.setStreet(resolved.getStreet());
        address.setDetailAddress(resolved.getDetailAddress());
        address.setLongitude(resolved.getLongitude());
        address.setLatitude(resolved.getLatitude());
        address.setAdcode(resolved.getAdcode());
        return address;
    }

    private OrderInfoDO ensureResolvedOrderLocation(OrderInfoDO order) {
        if (order == null) {
            return null;
        }
        if (order.getLongitude() != null && order.getLatitude() != null
                && StrUtil.isNotBlank(order.getProvince()) && StrUtil.isNotBlank(order.getCity())
                && StrUtil.isNotBlank(order.getDistrict())) {
            return order;
        }
        AmapLocationService.ResolvedAddress resolved = amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(order.getProvince())
                .city(order.getCity())
                .district(order.getDistrict())
                .street(order.getStreet())
                .detailAddress(order.getDetailAddress())
                .longitude(order.getLongitude())
                .latitude(order.getLatitude())
                .build());
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .province(resolved.getProvince())
                .city(resolved.getCity())
                .district(resolved.getDistrict())
                .street(resolved.getStreet())
                .detailAddress(resolved.getDetailAddress())
                .longitude(resolved.getLongitude())
                .latitude(resolved.getLatitude())
                .build());
        order.setProvince(resolved.getProvince());
        order.setCity(resolved.getCity());
        order.setDistrict(resolved.getDistrict());
        order.setStreet(resolved.getStreet());
        order.setDetailAddress(resolved.getDetailAddress());
        order.setLongitude(resolved.getLongitude());
        order.setLatitude(resolved.getLatitude());
        return order;
    }

    private MerchantServicePointDO ensureResolvedServicePoint(MerchantServicePointDO point) {
        if (point == null) {
            return null;
        }
        if (point.getLongitude() != null && point.getLatitude() != null
                && StrUtil.isNotBlank(point.getProvince()) && StrUtil.isNotBlank(point.getCity())
                && StrUtil.isNotBlank(point.getDistrict())) {
            return point;
        }
        AmapLocationService.ResolvedAddress resolved = amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(point.getProvince())
                .city(point.getCity())
                .district(point.getDistrict())
                .street(point.getStreet())
                .detailAddress(point.getDetailAddress())
                .longitude(point.getLongitude())
                .latitude(point.getLatitude())
                .build());
        merchantServicePointMapper.updateById(MerchantServicePointDO.builder()
                .id(point.getId())
                .province(resolved.getProvince())
                .city(resolved.getCity())
                .district(resolved.getDistrict())
                .street(resolved.getStreet())
                .detailAddress(resolved.getDetailAddress())
                .longitude(resolved.getLongitude())
                .latitude(resolved.getLatitude())
                .build());
        point.setProvince(resolved.getProvince());
        point.setCity(resolved.getCity());
        point.setDistrict(resolved.getDistrict());
        point.setStreet(resolved.getStreet());
        point.setDetailAddress(resolved.getDetailAddress());
        point.setLongitude(resolved.getLongitude());
        point.setLatitude(resolved.getLatitude());
        return point;
    }

    private BigDecimal calculateOrderDistanceKm(OrderInfoDO order) {
        if (order == null || order.getMerchantId() == null) {
            return null;
        }
        OrderInfoDO resolvedOrder = ensureResolvedOrderLocation(order);
        return calculateDistanceToMerchant(resolvedOrder, resolvedOrder.getMerchantId());
    }

    private BigDecimal calculateDistanceToMerchant(OrderInfoDO order, Long merchantId) {
        if (order == null || merchantId == null || order.getLongitude() == null || order.getLatitude() == null) {
            return null;
        }
        List<MerchantServicePointDO> servicePoints = merchantServicePointMapper.selectListByMerchantId(merchantId);
        if (CollUtil.isEmpty(servicePoints)) {
            return null;
        }
        BigDecimal minDistance = null;
        for (MerchantServicePointDO point : servicePoints) {
            if (!Objects.equals(point.getStatus(), "ENABLE")) {
                continue;
            }
            MerchantServicePointDO resolvedPoint = ensureResolvedServicePoint(point);
            BigDecimal distance = amapLocationService.calculateDistanceKm(
                    resolvedPoint.getLongitude(), resolvedPoint.getLatitude(),
                    order.getLongitude(), order.getLatitude());
            if (distance == null) {
                continue;
            }
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
            }
        }
        if (minDistance != null) {
            return minDistance;
        }
        for (MerchantServicePointDO point : servicePoints) {
            MerchantServicePointDO resolvedPoint = ensureResolvedServicePoint(point);
            BigDecimal distance = amapLocationService.calculateDistanceKm(
                    resolvedPoint.getLongitude(), resolvedPoint.getLatitude(),
                    order.getLongitude(), order.getLatitude());
            if (distance == null) {
                continue;
            }
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<OrderInfoDO> orders) {
        Set<Long> categoryIds = orders.stream().map(OrderInfoDO::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollUtil.isEmpty(categoryIds)) {
            return Collections.emptyMap();
        }
        return merchantServiceCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(MerchantServiceCategoryDO::getId, item -> item));
    }

    private BigDecimal calculateOrderAmount(AppOrderCreateReqVO reqVO) {
        if (CollUtil.isNotEmpty(reqVO.getPriceItems())) {
            return reqVO.getPriceItems().stream()
                    .map(AppOrderCreateReqVO.OrderPriceItemReqVO::getItemAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return Optional.ofNullable(reqVO.getBudgetAmount()).orElse(BigDecimal.ZERO);
    }

    private boolean shouldSplit(AppOrderCreateReqVO reqVO, MerchantServiceCategoryDO category, BigDecimal orderAmount) {
        return Boolean.TRUE.equals(reqVO.getNeedSplit())
                || (orderAmount != null && orderAmount.compareTo(MAX_UNIT_AMOUNT) > 0)
                || (reqVO.getQuantity() != null && reqVO.getQuantity().compareTo(new BigDecimal("5")) >= 0)
                || Boolean.TRUE.equals(category.getSupportSplit());
    }

    private void savePriceItems(Long orderId, List<AppOrderCreateReqVO.OrderPriceItemReqVO> priceItems) {
        if (CollUtil.isEmpty(priceItems)) {
            return;
        }
        for (int i = 0; i < priceItems.size(); i++) {
            AppOrderCreateReqVO.OrderPriceItemReqVO item = priceItems.get(i);
            orderPriceItemMapper.insert(OrderPriceItemDO.builder()
                    .orderId(orderId)
                    .itemType(item.getItemType())
                    .itemName(item.getItemName())
                    .itemAmount(item.getItemAmount())
                    .sortNo(item.getSortNo() != null ? item.getSortNo() : i + 1)
                    .build());
        }
    }

    private void saveAttachments(Long orderId, List<Long> attachmentFileIds) {
        if (CollUtil.isEmpty(attachmentFileIds)) {
            return;
        }
        for (int i = 0; i < attachmentFileIds.size(); i++) {
            orderAttachmentMapper.insert(OrderAttachmentDO.builder()
                    .orderId(orderId)
                    .fileId(attachmentFileIds.get(i))
                    .fileType("ORDER_ATTACH")
                    .sortNo(i + 1)
                    .build());
        }
    }

    private void saveUnits(OrderInfoDO order, boolean splitRequired, BigDecimal orderAmount) {
        List<BigDecimal> unitAmounts = buildUnitAmounts(splitRequired, orderAmount);
        Long prevUnitId = null;
        for (int i = 0; i < unitAmounts.size(); i++) {
            boolean locked = i > 0;
            OrderUnitDO unit = OrderUnitDO.builder()
                    .orderId(order.getId())
                    .unitNo("LBU" + IdUtil.getSnowflakeNextIdStr())
                    .unitSeq(i + 1)
                    .unitTitle(order.getTitle() + "-" + (i + 1))
                    .unitAmount(unitAmounts.get(i))
                    .splitMode(splitRequired ? "BY_AMOUNT" : "DIRECT")
                    .prevUnitId(prevUnitId)
                    .isLocked(locked)
                    .lockReason(locked ? "待前序单元完成" : null)
                    .status(locked ? "PENDING_CREATE" : "PENDING_ACCEPT")
                    .acceptDeadlineTime(LocalDateTime.now().plusHours(24))
                    .build();
            orderUnitMapper.insert(unit);
            prevUnitId = unit.getId();
        }
    }

    private List<BigDecimal> buildUnitAmounts(boolean splitRequired, BigDecimal orderAmount) {
        if (!splitRequired || orderAmount == null || orderAmount.compareTo(MAX_UNIT_AMOUNT) <= 0) {
            return Collections.singletonList(Optional.ofNullable(orderAmount).orElse(BigDecimal.ZERO));
        }
        List<BigDecimal> amounts = new ArrayList<>();
        BigDecimal remaining = orderAmount;
        while (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal current = remaining.compareTo(MAX_UNIT_AMOUNT) > 0 ? MAX_UNIT_AMOUNT : remaining;
            amounts.add(current.setScale(2, RoundingMode.HALF_UP));
            remaining = remaining.subtract(current);
        }
        return amounts;
    }

    private void saveOperateLog(Long orderId, Long unitId, String operateType, String operateRole,
                                Long operateBy, String beforeStatus, String afterStatus, String remark) {
        orderOperateLogMapper.insert(OrderOperateLogDO.builder()
                .orderId(orderId)
                .unitId(unitId)
                .operateType(operateType)
                .operateRole(operateRole)
                .operateBy(operateBy)
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .remark(remark)
                .operateTime(LocalDateTime.now())
                .build());
    }

    private AppOrderUnitRespVO toOrderUnitRespVO(OrderUnitDO unit) {
        AppOrderUnitRespVO respVO = new AppOrderUnitRespVO();
        respVO.setId(unit.getId());
        respVO.setOrderId(unit.getOrderId());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setUnitSeq(unit.getUnitSeq());
        respVO.setUnitTitle(unit.getUnitTitle());
        respVO.setUnitAmount(unit.getUnitAmount());
        respVO.setSplitMode(unit.getSplitMode());
        respVO.setPrevUnitId(unit.getPrevUnitId());
        respVO.setIsLocked(unit.getIsLocked());
        respVO.setLockReason(unit.getLockReason());
        respVO.setMerchantId(unit.getMerchantId());
        respVO.setStatus(unit.getStatus());
        respVO.setAcceptDeadlineTime(unit.getAcceptDeadlineTime());
        respVO.setFinishTime(unit.getFinishTime());
        respVO.setCreateTime(unit.getCreateTime());
        return respVO;
    }

    private String resolveOrderStatusAfterUnitConfirm(List<OrderUnitDO> units) {
        if (!units.isEmpty() && units.stream().allMatch(item -> Objects.equals(item.getStatus(), "FINISHED"))) {
            return "FINISHED";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "APPEALING"))) {
            return "AFTER_SALE";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "PENDING_CONFIRM"))) {
            return "PENDING_CONFIRM";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "SERVING"))) {
            return "SERVING";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "ACCEPTED"))) {
            return "ACCEPTED";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "PENDING_ACCEPT")
                || Objects.equals(item.getStatus(), "PENDING_CREATE"))) {
            return "PENDING_ACCEPT";
        }
        return "FINISHED";
    }

    private String resolvePayStatus(OrderInfoDO order) {
        if ("PENDING_PAY".equals(order.getStatus())) {
            return "WAITING";
        }
        if ("CLOSED".equals(order.getStatus())) {
            return "CLOSED";
        }
        return "SUCCESS";
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
}
