package cn.iocoder.yudao.module.pay.api.transfer;

import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferCreateRespDTO;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferRespDTO;
import javax.validation.Valid;

/**
 * 转账单 API 接口
 *
 * @author jason
 */
public interface PayTransferApi {

    /**
     * 创建转账单
     *
     * @param reqDTO 创建请求
     * @return 创建结果
     */
    PayTransferCreateRespDTO createTransfer(@Valid PayTransferCreateReqDTO reqDTO);

    /**
     * 获得转账单
     *
     * @param id 转账单编号
     * @return 转账单
     */
    PayTransferRespDTO getTransfer(Long id);

    /**
     * 根据支付应用和商户转账单号获得转账单
     *
     * @param appKey 支付应用标识
     * @param merchantTransferId 商户转账单号
     * @return 转账单
     */
    PayTransferRespDTO getTransferByMerchantTransferId(String appKey, String merchantTransferId);

}
