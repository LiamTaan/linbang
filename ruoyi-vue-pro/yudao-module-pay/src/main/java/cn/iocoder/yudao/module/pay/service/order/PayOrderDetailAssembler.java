package cn.iocoder.yudao.module.pay.service.order;

import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderDetailsRespVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class PayOrderDetailAssembler {

    private PayOrderDetailAssembler() {
    }

    static List<PayOrderDetailsRespVO.PayRefundSimple> buildRefunds(List<PayRefundDO> refunds) {
        if (refunds == null || refunds.isEmpty()) {
            return Collections.emptyList();
        }
        return refunds.stream().map(refund -> {
            PayOrderDetailsRespVO.PayRefundSimple respVO = new PayOrderDetailsRespVO.PayRefundSimple();
            respVO.setId(refund.getId());
            respVO.setNo(refund.getNo());
            respVO.setMerchantRefundId(refund.getMerchantRefundId());
            respVO.setStatus(refund.getStatus());
            respVO.setAuditStatus(refund.getAuditStatus());
            respVO.setRefundPrice(refund.getRefundPrice());
            respVO.setReason(refund.getReason());
            respVO.setChannelRefundNo(refund.getChannelRefundNo());
            respVO.setSuccessTime(refund.getSuccessTime());
            respVO.setCreateTime(refund.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    static List<PayOrderDetailsRespVO.PayOrderExtensionHistory> buildExtensionHistory(List<PayOrderExtensionDO> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return Collections.emptyList();
        }
        return extensions.stream().map(extension -> {
            PayOrderDetailsRespVO.PayOrderExtensionHistory respVO = new PayOrderDetailsRespVO.PayOrderExtensionHistory();
            respVO.setId(extension.getId());
            respVO.setNo(extension.getNo());
            respVO.setChannelCode(extension.getChannelCode());
            respVO.setStatus(extension.getStatus());
            respVO.setUserIp(extension.getUserIp());
            respVO.setChannelErrorCode(extension.getChannelErrorCode());
            respVO.setChannelErrorMsg(extension.getChannelErrorMsg());
            respVO.setCreateTime(extension.getCreateTime());
            respVO.setUpdateTime(extension.getUpdateTime());
            respVO.setChannelNotifyData(extension.getChannelNotifyData());
            return respVO;
        }).collect(Collectors.toList());
    }

}
