package cn.iocoder.yudao.module.linbang.service.orderinfo;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchpushbatch.MatchPushBatchDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderattachment.OrderAttachmentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderpriceitem.OrderPriceItemDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunitproof.OrderUnitProofDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.matchpushbatch.MatchPushBatchMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord.OrderAcceptRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderattachment.OrderAttachmentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderpriceitem.OrderPriceItemMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord.OrderMatchRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunitproof.OrderUnitProofMapper;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Service
public class OrderDetailAggregateService {

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
    private MatchPushBatchMapper matchPushBatchMapper;
    @Resource
    private OrderMatchRecordMapper orderMatchRecordMapper;
    @Resource
    private FileMapper fileMapper;

    public OrderDetailAggregate aggregate(OrderInfoDO order, boolean includeMatchData) {
        Long orderId = order.getId();
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(order.getCategoryId());
        MerchantInfoDO merchant = order.getMerchantId() == null ? null : merchantInfoMapper.selectById(order.getMerchantId());
        List<OrderPriceItemDO> priceItems = orderPriceItemMapper.selectList(new LambdaQueryWrapperX<OrderPriceItemDO>()
                .eq(OrderPriceItemDO::getOrderId, orderId)
                .orderByAsc(OrderPriceItemDO::getSortNo, OrderPriceItemDO::getId));
        List<OrderAttachmentDO> attachments = orderAttachmentMapper.selectList(new LambdaQueryWrapperX<OrderAttachmentDO>()
                .eq(OrderAttachmentDO::getOrderId, orderId)
                .orderByAsc(OrderAttachmentDO::getSortNo, OrderAttachmentDO::getId));
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, orderId)
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
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

        List<Long> unitIds = units.stream().map(OrderUnitDO::getId).collect(Collectors.toList());
        List<MatchPushBatchDO> matchBatches = includeMatchData && !unitIds.isEmpty()
                ? matchPushBatchMapper.selectList(new LambdaQueryWrapperX<MatchPushBatchDO>()
                .in(MatchPushBatchDO::getUnitId, unitIds)
                .orderByAsc(MatchPushBatchDO::getStageNo, MatchPushBatchDO::getId))
                : Collections.emptyList();
        List<OrderMatchRecordDO> matchRecords = includeMatchData && !unitIds.isEmpty()
                ? orderMatchRecordMapper.selectList(new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .in(OrderMatchRecordDO::getUnitId, unitIds)
                .orderByAsc(OrderMatchRecordDO::getStageNo, OrderMatchRecordDO::getId))
                : Collections.emptyList();

        Set<Long> fileIds = new LinkedHashSet<>();
        attachments.stream().map(OrderAttachmentDO::getFileId).filter(id -> id != null).forEach(fileIds::add);
        proofs.stream().map(OrderUnitProofDO::getFileId).filter(id -> id != null).forEach(fileIds::add);
        Map<Long, FileDO> fileMap = fileIds.isEmpty() ? Collections.emptyMap()
                : convertMap(fileMapper.selectBatchIds(fileIds), FileDO::getId);

        Set<Long> acceptMerchantIds = convertSet(acceptRecords, OrderAcceptRecordDO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> acceptRecordMerchantMap = acceptMerchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(acceptMerchantIds), MerchantInfoDO::getId);

        OrderDetailAggregate aggregate = new OrderDetailAggregate();
        aggregate.setOrder(order);
        aggregate.setCategory(category);
        aggregate.setMerchant(merchant);
        aggregate.setPriceItems(priceItems);
        aggregate.setAttachments(attachments);
        aggregate.setUnits(units);
        aggregate.setProofs(proofs);
        aggregate.setAcceptRecords(acceptRecords);
        aggregate.setComplaints(complaints);
        aggregate.setAppeals(appeals);
        aggregate.setPayOrder(payOrder);
        aggregate.setRefunds(refunds);
        aggregate.setOperateLogs(operateLogs);
        aggregate.setMatchBatches(matchBatches);
        aggregate.setMatchRecords(matchRecords);
        aggregate.setFileMap(fileMap);
        aggregate.setAcceptRecordMerchantMap(acceptRecordMerchantMap);
        return aggregate;
    }

    @Data
    public static class OrderDetailAggregate {

        private OrderInfoDO order;
        private MerchantServiceCategoryDO category;
        private MerchantInfoDO merchant;
        private List<OrderPriceItemDO> priceItems = Collections.emptyList();
        private List<OrderAttachmentDO> attachments = Collections.emptyList();
        private List<OrderUnitDO> units = Collections.emptyList();
        private List<OrderUnitProofDO> proofs = Collections.emptyList();
        private List<OrderAcceptRecordDO> acceptRecords = Collections.emptyList();
        private List<ComplaintDO> complaints = Collections.emptyList();
        private List<AppealDO> appeals = Collections.emptyList();
        private PayOrderDO payOrder;
        private List<PayRefundDO> refunds = Collections.emptyList();
        private List<OrderOperateLogDO> operateLogs = Collections.emptyList();
        private List<MatchPushBatchDO> matchBatches = Collections.emptyList();
        private List<OrderMatchRecordDO> matchRecords = Collections.emptyList();
        private Map<Long, FileDO> fileMap = Collections.emptyMap();
        private Map<Long, MerchantInfoDO> acceptRecordMerchantMap = Collections.emptyMap();

    }

}
