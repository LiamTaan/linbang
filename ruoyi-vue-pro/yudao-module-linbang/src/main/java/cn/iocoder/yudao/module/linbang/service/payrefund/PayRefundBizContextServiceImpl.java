package cn.iocoder.yudao.module.linbang.service.payrefund;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.payrefund.vo.PayRefundBizContextRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class PayRefundBizContextServiceImpl implements PayRefundBizContextService {

    private static final String MERCHANT_REFUND_PREFIX = "LBR";

    @Resource
    private PayRefundMapper payRefundMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;

    @Override
    public PayRefundBizContextRespVO getPayRefundBizContext(Long payRefundId) {
        PayRefundDO refund = payRefundMapper.selectById(payRefundId);
        if (refund == null) {
            return new PayRefundBizContextRespVO();
        }
        RefundBizRef bizRef = parseRefundBizRef(refund.getMerchantRefundId());
        if (bizRef == null) {
            PayRefundBizContextRespVO respVO = new PayRefundBizContextRespVO();
            respVO.setPayRefundId(refund.getId());
            respVO.setMerchantRefundId(refund.getMerchantRefundId());
            return respVO;
        }

        OrderInfoDO order = bizRef.orderId == null ? null : orderInfoMapper.selectById(bizRef.orderId);
        OrderUnitDO unit = bizRef.unitId == null ? null : orderUnitMapper.selectById(bizRef.unitId);
        if (unit != null && order == null && unit.getOrderId() != null) {
            order = orderInfoMapper.selectById(unit.getOrderId());
        }

        List<WalletFlowDO> flows = walletFlowMapper.selectList(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getRelatedRefundId, refund.getId())
                .orderByDesc(WalletFlowDO::getCreateTime, WalletFlowDO::getId));
        List<ComplaintDO> complaints = loadComplaints(bizRef.orderId, bizRef.unitId);
        List<AppealDO> appeals = loadAppeals(bizRef.orderId, bizRef.unitId);
        List<OrderOperateLogDO> logs = loadLogs(bizRef.orderId, bizRef.unitId);

        PayRefundBizContextRespVO respVO = PayRefundBizContextAssembler.buildBase(refund, bizRef.orderId, bizRef.unitId);
        respVO.setOrder(PayRefundBizContextAssembler.buildOrder(order));
        respVO.setUnit(PayRefundBizContextAssembler.buildUnit(unit));
        respVO.setWalletFlows(PayRefundBizContextAssembler.buildFlows(flows));
        respVO.setComplaints(PayRefundBizContextAssembler.buildComplaints(complaints));
        respVO.setAppeals(PayRefundBizContextAssembler.buildAppeals(appeals));
        respVO.setOperateLogs(PayRefundBizContextAssembler.buildLogs(logs));
        return respVO;
    }

    private List<ComplaintDO> loadComplaints(Long orderId, Long unitId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        return complaintMapper.selectList(new LambdaQueryWrapperX<ComplaintDO>()
                .eq(ComplaintDO::getOrderId, orderId)
                .eqIfPresent(ComplaintDO::getUnitId, unitId)
                .orderByDesc(ComplaintDO::getCreateTime, ComplaintDO::getId));
    }

    private List<AppealDO> loadAppeals(Long orderId, Long unitId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        return appealMapper.selectList(new LambdaQueryWrapperX<AppealDO>()
                .eq(AppealDO::getOrderId, orderId)
                .eqIfPresent(AppealDO::getUnitId, unitId)
                .orderByDesc(AppealDO::getCreateTime, AppealDO::getId));
    }

    private List<OrderOperateLogDO> loadLogs(Long orderId, Long unitId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        return orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(OrderOperateLogDO::getOrderId, orderId)
                .eqIfPresent(OrderOperateLogDO::getUnitId, unitId)
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));
    }

    private RefundBizRef parseRefundBizRef(String merchantRefundId) {
        if (merchantRefundId == null) {
            return null;
        }
        String[] segments = merchantRefundId.split("-");
        if (segments.length < 4 || !Objects.equals(segments[0], MERCHANT_REFUND_PREFIX)) {
            return null;
        }
        try {
            Long orderId = Long.valueOf(segments[1]);
            Long unitId = Long.valueOf(segments[2]);
            return new RefundBizRef(orderId, Objects.equals(unitId, 0L) ? null : unitId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static final class RefundBizRef {
        private final Long orderId;
        private final Long unitId;

        private RefundBizRef(Long orderId, Long unitId) {
            this.orderId = orderId;
            this.unitId = unitId;
        }
    }

}
