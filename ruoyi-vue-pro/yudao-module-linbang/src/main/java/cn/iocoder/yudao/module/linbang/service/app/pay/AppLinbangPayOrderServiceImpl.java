package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_ACCESS_DENIED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_CALLBACK_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_ORDER_ALREADY_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_ORDER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_STATUS_NOT_ALLOWED;

@Service
@Validated
public class AppLinbangPayOrderServiceImpl implements AppLinbangPayOrderService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;

    @Resource
    private PayAppService payAppService;
    @Resource
    private PayOrderApi payOrderApi;
    @Resource
    private PayOrderService payOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPayOrder(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAccessibleOrder(loginUser.getId(), reqVO.getOrderId());
        if (order.getPayOrderId() != null) {
            return order.getPayOrderId();
        }
        if (!Objects.equals(order.getStatus(), "PENDING_PAY")) {
            throw exception(ORDER_PAY_STATUS_NOT_ALLOWED);
        }

        PayAppDO payApp = getEnabledPayApp();
        Long payOrderId = payOrderApi.createOrder(new PayOrderCreateReqDTO()
                .setAppKey(payApp.getAppKey())
                .setUserIp(ServletUtils.getClientIP())
                .setUserId(loginUser.getId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setMerchantOrderId(String.valueOf(order.getId()))
                .setSubject(buildSubject(order))
                .setBody(StrUtil.maxLength(StrUtil.blankToDefault(order.getRequireDesc(), order.getTitle()), 128))
                .setPrice(toFen(order.getOrderAmount()))
                .setExpireTime(LocalDateTime.now().plusMinutes(30)));

        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .payOrderId(payOrderId)
                .build());
        saveOperateLog(order.getId(), null, "CREATE_PAY_ORDER", "USER", loginUser.getId(),
                order.getStatus(), order.getStatus(), "用户创建支付单");
        return payOrderId;
    }

    @Override
    public AppLinbangPayOrderRespVO getPayOrder(Long authUserId, Long id, Long orderId, Boolean sync) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = null;
        if (orderId != null) {
            order = validateAccessibleOrder(loginUser.getId(), orderId);
            if (order.getPayOrderId() == null) {
                return null;
            }
            id = order.getPayOrderId();
        }
        if (id == null) {
            return null;
        }

        PayOrderDO payOrder = payOrderService.getOrder(id);
        if (payOrder == null) {
            return null;
        }
        if (order == null) {
            order = validateAccessibleOrder(loginUser.getId(), parseOrderId(payOrder.getMerchantOrderId()));
        }
        if (order.getPayOrderId() != null && !Objects.equals(order.getPayOrderId(), payOrder.getId())) {
            throw exception(ORDER_PAY_CALLBACK_INVALID);
        }
        if (Boolean.TRUE.equals(sync) && PayOrderStatusEnum.isWaiting(payOrder.getStatus())) {
            payOrderService.syncOrderQuietly(payOrder.getId());
            payOrder = payOrderService.getOrder(payOrder.getId());
        }
        return toPayOrderRespVO(payOrder, order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaid(@Valid PayOrderNotifyReqDTO notifyReqDTO) {
        Long orderId = parseOrderId(notifyReqDTO.getMerchantOrderId());
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }

        if (order.getPayOrderId() != null && !Objects.equals(order.getPayOrderId(), notifyReqDTO.getPayOrderId())) {
            throw exception(ORDER_PAY_CALLBACK_INVALID);
        }
        if (!Objects.equals(order.getStatus(), "PENDING_PAY")) {
            if (Objects.equals(order.getPayOrderId(), notifyReqDTO.getPayOrderId())) {
                return;
            }
            throw exception(ORDER_PAY_STATUS_NOT_ALLOWED);
        }

        PayOrderRespDTO payOrder = payOrderApi.getOrder(notifyReqDTO.getPayOrderId());
        if (payOrder == null || !PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
            throw exception(ORDER_PAY_CALLBACK_INVALID);
        }
        if (!Objects.equals(payOrder.getMerchantOrderId(), notifyReqDTO.getMerchantOrderId())) {
            throw exception(ORDER_PAY_CALLBACK_INVALID);
        }
        if (toFen(order.getOrderAmount()) != payOrder.getPrice()) {
            throw exception(ORDER_PAY_CALLBACK_INVALID);
        }

        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .payOrderId(notifyReqDTO.getPayOrderId())
                .status("PENDING_ACCEPT")
                .build());
        saveOperateLog(order.getId(), null, "PAY_SUCCESS", "SYSTEM", 0L,
                order.getStatus(), "PENDING_ACCEPT", "支付成功，订单进入待接单");
    }

    private OrderInfoDO validateAccessibleOrder(Long lbUserId, Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (!Objects.equals(order.getUserId(), lbUserId)) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        return order;
    }

    private PayAppDO getEnabledPayApp() {
        List<PayAppDO> payApps = payAppService.getAppList();
        return payApps.stream()
                .filter(item -> Objects.equals(item.getStatus(), CommonStatusEnum.ENABLE.getStatus()))
                .findFirst()
                .orElseThrow(() -> exception(ORDER_PAY_ORDER_NOT_EXISTS));
    }

    private String buildSubject(OrderInfoDO order) {
        return StrUtil.maxLength(StrUtil.blankToDefault(order.getTitle(), "邻里互助订单支付"), 32);
    }

    private int toFen(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private Long parseOrderId(String merchantOrderId) {
        if (!StrUtil.isNumeric(merchantOrderId)) {
            throw exception(ORDER_PAY_CALLBACK_INVALID);
        }
        return Long.valueOf(merchantOrderId);
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

    private AppLinbangPayOrderRespVO toPayOrderRespVO(PayOrderDO payOrder, Long orderId) {
        AppLinbangPayOrderRespVO respVO = new AppLinbangPayOrderRespVO();
        respVO.setId(payOrder.getId());
        respVO.setOrderId(orderId);
        respVO.setChannelCode(payOrder.getChannelCode());
        respVO.setMerchantOrderId(payOrder.getMerchantOrderId());
        respVO.setSubject(payOrder.getSubject());
        respVO.setPrice(payOrder.getPrice());
        respVO.setStatus(payOrder.getStatus());
        respVO.setStatusName(resolvePayOrderStatusName(payOrder.getStatus()));
        respVO.setNo(payOrder.getNo());
        respVO.setChannelOrderNo(payOrder.getChannelOrderNo());
        respVO.setExpireTime(payOrder.getExpireTime());
        respVO.setSuccessTime(payOrder.getSuccessTime());
        respVO.setRefundPrice(payOrder.getRefundPrice());
        respVO.setCreateTime(payOrder.getCreateTime());
        return respVO;
    }

    private String resolvePayOrderStatusName(Integer status) {
        if (PayOrderStatusEnum.isWaiting(status)) {
            return PayOrderStatusEnum.WAITING.getName();
        }
        if (PayOrderStatusEnum.isSuccess(status)) {
            return PayOrderStatusEnum.SUCCESS.getName();
        }
        if (PayOrderStatusEnum.isRefund(status)) {
            return PayOrderStatusEnum.REFUND.getName();
        }
        if (PayOrderStatusEnum.isClosed(status)) {
            return PayOrderStatusEnum.CLOSED.getName();
        }
        return "UNKNOWN";
    }

}
