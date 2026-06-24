package cn.iocoder.yudao.module.linbang.service.orderinfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(order.getCategoryId());
        MerchantInfoDO merchant = order.getMerchantId() == null ? null : merchantInfoMapper.selectById(order.getMerchantId());
        List<OrderPriceItemDO> priceItems = orderPriceItemMapper.selectList(new LambdaQueryWrapperX<OrderPriceItemDO>()
                .eq(OrderPriceItemDO::getOrderId, id)
                .orderByAsc(OrderPriceItemDO::getSortNo, OrderPriceItemDO::getId));
        List<OrderAttachmentDO> attachments = orderAttachmentMapper.selectList(new LambdaQueryWrapperX<OrderAttachmentDO>()
                .eq(OrderAttachmentDO::getOrderId, id)
                .orderByAsc(OrderAttachmentDO::getSortNo, OrderAttachmentDO::getId));
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, id)
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
        List<OrderUnitProofDO> proofs = orderUnitProofMapper.selectList(new LambdaQueryWrapperX<OrderUnitProofDO>()
                .eq(OrderUnitProofDO::getOrderId, id)
                .orderByDesc(OrderUnitProofDO::getProofTime, OrderUnitProofDO::getId));
        List<OrderAcceptRecordDO> acceptRecords = orderAcceptRecordMapper.selectList(new LambdaQueryWrapperX<OrderAcceptRecordDO>()
                .eq(OrderAcceptRecordDO::getOrderId, id)
                .orderByDesc(OrderAcceptRecordDO::getAcceptTime, OrderAcceptRecordDO::getId));
        List<ComplaintDO> complaints = complaintMapper.selectList(new LambdaQueryWrapperX<ComplaintDO>()
                .eq(ComplaintDO::getOrderId, id)
                .orderByDesc(ComplaintDO::getCreateTime, ComplaintDO::getId));
        List<AppealDO> appeals = appealMapper.selectList(new LambdaQueryWrapperX<AppealDO>()
                .eq(AppealDO::getOrderId, id)
                .orderByDesc(AppealDO::getCreateTime, AppealDO::getId));
        PayOrderDO payOrder = order.getPayOrderId() == null ? null : payOrderMapper.selectById(order.getPayOrderId());
        List<PayRefundDO> refunds = order.getPayOrderId() == null ? Collections.emptyList()
                : payRefundMapper.selectList(new LambdaQueryWrapperX<PayRefundDO>()
                .eq(PayRefundDO::getOrderId, order.getPayOrderId())
                .orderByDesc(PayRefundDO::getCreateTime, PayRefundDO::getId));
        List<OrderOperateLogDO> operateLogs = orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(OrderOperateLogDO::getOrderId, id)
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));
        Set<Long> acceptMerchantIds = convertSet(acceptRecords, OrderAcceptRecordDO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> acceptRecordMerchantMap = acceptMerchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(acceptMerchantIds), MerchantInfoDO::getId);

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
            itemResp.setFileUrl(null);
            itemResp.setSortNo(item.getSortNo());
            return itemResp;
        }).collect(Collectors.toList()));
        respVO.setUnits(units.stream().map(unit -> {
            OrderInfoDetailRespVO.OrderUnitSimpleRespVO unitResp = new OrderInfoDetailRespVO.OrderUnitSimpleRespVO();
            unitResp.setId(unit.getId());
            unitResp.setUnitNo(unit.getUnitNo());
            unitResp.setUnitSeq(unit.getUnitSeq());
            unitResp.setUnitTitle(unit.getUnitTitle());
            unitResp.setUnitAmount(unit.getUnitAmount());
            unitResp.setSplitMode(unit.getSplitMode());
            unitResp.setPrevUnitId(unit.getPrevUnitId());
            unitResp.setIsLocked(unit.getIsLocked());
            unitResp.setLockReason(unit.getLockReason());
            unitResp.setMerchantId(unit.getMerchantId());
            unitResp.setStatus(unit.getStatus());
            unitResp.setAcceptDeadlineTime(unit.getAcceptDeadlineTime());
            unitResp.setFinishTime(unit.getFinishTime());
            unitResp.setCreateTime(unit.getCreateTime());
            return unitResp;
        }).collect(Collectors.toList()));
        respVO.setProofs(proofs.stream().map(proof -> {
            OrderInfoDetailRespVO.OrderUnitProofRespVO proofResp = new OrderInfoDetailRespVO.OrderUnitProofRespVO();
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

}
