package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.security.config.SecurityProperties;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.enums.PayWayEnum;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangH5PaySubmitReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangH5PaySubmitRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangWechatMiniProgramPaySubmitRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositInfoRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositStatusRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.service.finance.LinbangFinanceService;
import cn.iocoder.yudao.module.linbang.service.match.MatchDispatchService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.orderflow.OrderFlowOrchestratorService;
import cn.iocoder.yudao.module.linbang.service.risk.LinbangRiskFacade;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.channel.PayChannelDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import cn.iocoder.yudao.module.pay.enums.PayChannelEnum;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.channel.PayChannelService;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_ACCESS_DENIED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_DEPOSIT_PAY_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_DEPOSIT_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_CALLBACK_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_ORDER_ALREADY_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_ORDER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_STATUS_NOT_ALLOWED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_WECHAT_MINI_PROGRAM_NOT_BOUND;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_WECHAT_PAY_APP_NOT_CONFIGURED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_WECHAT_PAY_PARAMS_INVALID;

@Service
@Validated
public class AppLinbangPayOrderServiceImpl implements AppLinbangPayOrderService {

    private static final long PAY_ORDER_EXPIRE_MINUTES = 30L;

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;

    @Resource
    private PayAppService payAppService;
    @Resource
    private PayChannelService payChannelService;
    @Resource
    private SocialUserApi socialUserApi;
    @Resource
    private PayOrderApi payOrderApi;
    @Resource
    private PayOrderService payOrderService;
    @Resource
    private SecurityProperties securityProperties;
    @Resource
    private MatchDispatchService matchDispatchService;
    @Resource
    private LinbangFinanceService linbangFinanceService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private LinbangRiskFacade linbangRiskFacade;
    @Resource
    private OrderFlowOrchestratorService orderFlowOrchestratorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPayOrder(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAccessibleOrder(loginUser.getId(), reqVO.getOrderId());
        if (order.getPayOrderId() != null) {
            PayOrderDO existingPayOrder = payOrderService.getOrder(order.getPayOrderId());
            if (existingPayOrder != null) {
                return ensurePayOrderReadyForSubmit(existingPayOrder).getId();
            }
        }
        if (!Objects.equals(order.getStatus(), "PENDING_PAY")) {
            throw exception(ORDER_PAY_STATUS_NOT_ALLOWED);
        }

        PayOrderDO payOrder = createOrRefreshPayOrder(loginUser, String.valueOf(order.getId()),
                buildSubject(order),
                StrUtil.maxLength(StrUtil.blankToDefault(order.getRequireDesc(), "邻里互助订单"), 128),
                toFen(order.getOrderAmount()));
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .payOrderId(payOrder.getId())
                .build());
        saveOperateLog(order.getId(), null, "CREATE_PAY_ORDER", "USER", loginUser.getId(),
                order.getStatus(), order.getStatus(), "用户创建支付单");
        return payOrder.getId();
    }

    private PayOrderDO createOrRefreshPayOrder(MemberUserDO loginUser, String merchantOrderId,
                                               String subject, String body, Integer price) {
        PayAppDO payApp = getEnabledPayApp();
        Long payOrderId = payOrderApi.createOrder(new PayOrderCreateReqDTO()
                .setAppKey(payApp.getAppKey())
                .setUserIp(ServletUtils.getClientIP())
                .setUserId(loginUser.getId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setMerchantOrderId(merchantOrderId)
                .setSubject(subject)
                .setBody(body)
                .setPrice(price)
                .setExpireTime(nextPayExpireTime()));
        PayOrderDO payOrder = payOrderService.getOrder(payOrderId);
        if (payOrder == null) {
            throw exception(ORDER_PAY_ORDER_NOT_EXISTS);
        }
        return ensurePayOrderReadyForSubmit(payOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppLinbangH5PaySubmitRespVO submitH5Pay(Long authUserId, @Valid AppLinbangH5PaySubmitReqVO reqVO) {
        AppLinbangPayOrderCreateReqVO createReqVO = new AppLinbangPayOrderCreateReqVO();
        createReqVO.setOrderId(reqVO.getOrderId());
        Long payOrderId = createPayOrder(authUserId, createReqVO);

        PayOrderSubmitReqVO submitReqVO = new PayOrderSubmitReqVO();
        submitReqVO.setId(payOrderId);
        submitReqVO.setChannelCode(resolveSubmitChannelCode());
        if (PayChannelEnum.AGGREGATE.getCode().equals(submitReqVO.getChannelCode())) {
            submitReqVO.setChannelExtras(buildPayChannelExtras(reqVO.getPayWay()));
        }
        submitReqVO.setReturnUrl(reqVO.getReturnUrl());
        PayOrderSubmitRespVO submitRespVO = payOrderService.submitOrder(submitReqVO, ServletUtils.getClientIP());

        AppLinbangH5PaySubmitRespVO respVO = new AppLinbangH5PaySubmitRespVO();
        respVO.setPayOrderId(payOrderId);
        respVO.setPayWay(reqVO.getPayWay());
        respVO.setPayWayName(resolvePayWayName(reqVO.getPayWay()));
        respVO.setStatus(submitRespVO.getStatus());
        respVO.setDisplayMode(submitRespVO.getDisplayMode());
        respVO.setDisplayContent(submitRespVO.getDisplayContent());
        if (PayChannelEnum.MOCK.getCode().equals(submitReqVO.getChannelCode())) {
            respVO.setDisplayMode("mock");
            respVO.setDisplayContent("MOCK_SUCCESS");
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppLinbangWechatMiniProgramPaySubmitRespVO submitWechatMiniProgramPay(
            Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        Long payOrderId = createPayOrder(authUserId, reqVO);
        return submitWechatMiniProgramPayOrder(loginUser, payOrderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppLinbangH5PaySubmitRespVO submitDepositH5Pay(Long authUserId, @Valid AppLinbangH5PaySubmitReqVO reqVO) {
        Long payOrderId = createDepositPayOrder(authUserId, reqVO.getOrderId());

        PayOrderSubmitReqVO submitReqVO = new PayOrderSubmitReqVO();
        submitReqVO.setId(payOrderId);
        submitReqVO.setChannelCode(resolveSubmitChannelCode());
        if (PayChannelEnum.AGGREGATE.getCode().equals(submitReqVO.getChannelCode())) {
            submitReqVO.setChannelExtras(buildPayChannelExtras(reqVO.getPayWay()));
        }
        submitReqVO.setReturnUrl(reqVO.getReturnUrl());
        PayOrderSubmitRespVO submitRespVO = payOrderService.submitOrder(submitReqVO, ServletUtils.getClientIP());

        AppLinbangH5PaySubmitRespVO respVO = new AppLinbangH5PaySubmitRespVO();
        respVO.setPayOrderId(payOrderId);
        respVO.setPayWay(reqVO.getPayWay());
        respVO.setPayWayName(resolvePayWayName(reqVO.getPayWay()));
        respVO.setStatus(submitRespVO.getStatus());
        respVO.setDisplayMode(submitRespVO.getDisplayMode());
        respVO.setDisplayContent(submitRespVO.getDisplayContent());
        if (PayChannelEnum.MOCK.getCode().equals(submitReqVO.getChannelCode())) {
            respVO.setDisplayMode("mock");
            respVO.setDisplayContent("MOCK_SUCCESS");
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppLinbangWechatMiniProgramPaySubmitRespVO submitDepositWechatMiniProgramPay(
            Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        Long payOrderId = createDepositPayOrder(authUserId, reqVO.getOrderId());
        return submitWechatMiniProgramPayOrder(loginUser, payOrderId);
    }

    @Override
    public Long simulatePaySuccess(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO) {
        if (!Boolean.TRUE.equals(securityProperties.getMockEnable())) {
            throw exception(ORDER_PAY_STATUS_NOT_ALLOWED);
        }
        Long payOrderId = createPayOrder(authUserId, reqVO);
        PayOrderDO payOrder = payOrderService.getOrder(payOrderId);
        if (payOrder != null && PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
            return payOrderId;
        }
        PayOrderSubmitReqVO submitReqVO = new PayOrderSubmitReqVO();
        submitReqVO.setId(payOrderId);
        submitReqVO.setChannelCode(PayChannelEnum.MOCK.getCode());
        payOrderService.submitOrder(submitReqVO, ServletUtils.getClientIP());
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
    public AppOrderDepositInfoRespVO getDepositInfo(Long authUserId, Long orderId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAccessibleOrder(loginUser.getId(), orderId);
        AppOrderDepositInfoRespVO respVO = new AppOrderDepositInfoRespVO();
        respVO.setOrderId(order.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setDepositRequired(order.getDepositRequired());
        respVO.setDepositAmount(order.getDepositAmount());
        respVO.setDepositPayStatus(order.getDepositPayStatus());
        respVO.setDepositPayOrderId(order.getDepositPayOrderId());
        respVO.setDepositPaidTime(order.getDepositPaidTime());
        respVO.setCanCreateDepositPayOrder(canCreateDepositPayOrder(order));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDepositPayOrder(Long authUserId, Long orderId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAccessibleOrder(loginUser.getId(), orderId);
        if (!Boolean.TRUE.equals(order.getDepositRequired())) {
            throw exception(ORDER_DEPOSIT_REQUIRED);
        }
        if (!canCreateDepositPayOrder(order)) {
            throw exception(ORDER_DEPOSIT_PAY_STATUS_INVALID);
        }
        if (order.getDepositPayOrderId() != null) {
            PayOrderDO oldOrder = payOrderService.getOrder(order.getDepositPayOrderId());
            if (oldOrder != null) {
                return ensurePayOrderReadyForSubmit(oldOrder).getId();
            }
        }

        PayOrderDO payOrder = createOrRefreshPayOrder(loginUser, buildDepositMerchantOrderId(order.getId()),
                buildDepositSubject(order),
                StrUtil.maxLength("邻里互助大额订单保证金", 128),
                toFen(order.getDepositAmount()));
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .depositPayOrderId(payOrder.getId())
                .depositPayStatus(LinbangRiskConstants.DEPOSIT_PAY_STATUS_UNPAID)
                .build());
        saveOperateLog(order.getId(), null, "CREATE_DEPOSIT_PAY_ORDER", "USER", loginUser.getId(),
                order.getDepositPayStatus(), LinbangRiskConstants.DEPOSIT_PAY_STATUS_UNPAID, "用户创建保证金支付单");
        return payOrder.getId();
    }

    @Override
    public AppOrderDepositStatusRespVO getDepositStatus(Long authUserId, Long orderId, Boolean sync) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAccessibleOrder(loginUser.getId(), orderId);
        AppOrderDepositStatusRespVO respVO = new AppOrderDepositStatusRespVO();
        respVO.setOrderId(order.getId());
        respVO.setDepositPayStatus(order.getDepositPayStatus());
        respVO.setDepositPayOrderId(order.getDepositPayOrderId());
        respVO.setDepositPaidTime(order.getDepositPaidTime());
        if (order.getDepositPayOrderId() == null) {
            respVO.setPayStatusName(resolveDepositStatusName(order.getDepositPayStatus()));
            return respVO;
        }
        PayOrderDO payOrder = payOrderService.getOrder(order.getDepositPayOrderId());
        if (payOrder != null && Boolean.TRUE.equals(sync) && PayOrderStatusEnum.isWaiting(payOrder.getStatus())) {
            payOrderService.syncOrderQuietly(payOrder.getId());
            payOrder = payOrderService.getOrder(payOrder.getId());
        }
        respVO.setPayStatusName(payOrder == null ? resolveDepositStatusName(order.getDepositPayStatus())
                : resolvePayOrderStatusName(payOrder.getStatus()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaid(@Valid PayOrderNotifyReqDTO notifyReqDTO) {
        if (linbangRiskFacade.markDepositPaid(notifyReqDTO)) {
            return;
        }
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
        linbangFinanceService.handleOrderPaid(order, notifyReqDTO.getPayOrderId());
        saveOperateLog(order.getId(), null, "PAY_SUCCESS", "SYSTEM", 0L,
                order.getStatus(), "PENDING_ACCEPT", "支付成功，订单进入待接单");
        notifyPaymentSuccess(order);
        notifyOrderStatusChanged(order, "PENDING_ACCEPT", "支付成功，订单已进入待接单");
        matchDispatchService.startInitialDispatch(order.getId());
        orderFlowOrchestratorService.onOrderPaid(order.getId());
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
        String requiredChannelCode = Boolean.TRUE.equals(securityProperties.getMockEnable())
                ? PayChannelEnum.MOCK.getCode() : PayChannelEnum.WX_LITE.getCode();
        List<PayAppDO> payApps = payAppService.getAppList();
        return payApps.stream()
                .filter(item -> Objects.equals(item.getStatus(), CommonStatusEnum.ENABLE.getStatus()))
                .filter(item -> {
                    PayChannelDO channel = payChannelService.getChannelByAppIdAndCode(
                            item.getId(), requiredChannelCode);
                    return channel != null
                            && Objects.equals(channel.getStatus(), CommonStatusEnum.ENABLE.getStatus());
                })
                .findFirst()
                .orElseThrow(() -> exception(ORDER_WECHAT_PAY_APP_NOT_CONFIGURED));
    }

    private AppLinbangWechatMiniProgramPaySubmitRespVO submitWechatMiniProgramPayOrder(
            MemberUserDO loginUser, Long payOrderId) {
        String channelCode = Boolean.TRUE.equals(securityProperties.getMockEnable())
                ? PayChannelEnum.MOCK.getCode() : PayChannelEnum.WX_LITE.getCode();
        PayOrderSubmitReqVO submitReqVO = new PayOrderSubmitReqVO();
        submitReqVO.setId(payOrderId);
        submitReqVO.setChannelCode(channelCode);
        if (PayChannelEnum.WX_LITE.getCode().equals(channelCode)) {
            SocialUserRespDTO socialUser = socialUserApi.getSocialUserByUserId(
                    UserTypeEnum.MEMBER.getValue(), loginUser.getId(),
                    SocialTypeEnum.WECHAT_MINI_PROGRAM.getType());
            if (socialUser == null || StrUtil.isBlank(socialUser.getOpenid())) {
                throw exception(ORDER_WECHAT_MINI_PROGRAM_NOT_BOUND);
            }
            submitReqVO.setChannelExtras(Collections.singletonMap("openid", socialUser.getOpenid()));
        }
        PayOrderSubmitRespVO submitRespVO = payOrderService.submitOrder(submitReqVO, ServletUtils.getClientIP());

        AppLinbangWechatMiniProgramPaySubmitRespVO respVO = new AppLinbangWechatMiniProgramPaySubmitRespVO();
        respVO.setPayOrderId(payOrderId);
        respVO.setChannelCode(channelCode);
        respVO.setStatus(submitRespVO.getStatus());
        respVO.setDisplayMode(submitRespVO.getDisplayMode());
        if (PayChannelEnum.MOCK.getCode().equals(channelCode)) {
            respVO.setDisplayMode("mock");
            return respVO;
        }
        AppLinbangWechatMiniProgramPaySubmitRespVO.WechatPaymentParams paymentParams = JsonUtils.parseObject(
                submitRespVO.getDisplayContent(),
                AppLinbangWechatMiniProgramPaySubmitRespVO.WechatPaymentParams.class);
        if (paymentParams == null || StrUtil.isBlank(paymentParams.getTimeStamp())
                || StrUtil.isBlank(paymentParams.getNonceStr())
                || StrUtil.isBlank(paymentParams.getPackageValue())
                || StrUtil.isBlank(paymentParams.getSignType())
                || StrUtil.isBlank(paymentParams.getPaySign())) {
            throw exception(ORDER_WECHAT_PAY_PARAMS_INVALID);
        }
        respVO.setPaymentParams(paymentParams);
        return respVO;
    }

    private String resolveSubmitChannelCode() {
        return Boolean.TRUE.equals(securityProperties.getMockEnable())
                ? PayChannelEnum.MOCK.getCode()
                : PayChannelEnum.AGGREGATE.getCode();
    }

    private String buildSubject(OrderInfoDO order) {
        return StrUtil.maxLength(StrUtil.blankToDefault(order.getRequireDesc(), "邻里互助订单支付"), 32);
    }

    private String buildDepositSubject(OrderInfoDO order) {
        return StrUtil.maxLength("订单保证金-" + StrUtil.blankToDefault(order.getOrderNo(), String.valueOf(order.getId())), 32);
    }

    private PayOrderDO ensurePayOrderReadyForSubmit(PayOrderDO payOrder) {
        if (payOrder == null) {
            throw exception(ORDER_PAY_ORDER_NOT_EXISTS);
        }
        if (PayOrderStatusEnum.isSuccess(payOrder.getStatus()) || PayOrderStatusEnum.isRefund(payOrder.getStatus())) {
            return payOrder;
        }
        if (!PayOrderStatusEnum.isWaiting(payOrder.getStatus())
                || payOrder.getExpireTime() == null
                || payOrder.getExpireTime().isBefore(LocalDateTime.now())) {
            LocalDateTime nextExpireTime = nextPayExpireTime();
            payOrderService.refreshOrderForSubmit(payOrder.getId(), nextExpireTime);
            payOrder = payOrderService.getOrder(payOrder.getId());
        }
        return payOrder;
    }

    private LocalDateTime nextPayExpireTime() {
        return LocalDateTime.now().plusMinutes(PAY_ORDER_EXPIRE_MINUTES);
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

    private String buildDepositMerchantOrderId(Long orderId) {
        return "DEPOSIT:" + orderId;
    }

    private boolean canCreateDepositPayOrder(OrderInfoDO order) {
        return Boolean.TRUE.equals(order.getDepositRequired())
                && !Objects.equals(order.getDepositPayStatus(), LinbangRiskConstants.DEPOSIT_PAY_STATUS_PAID);
    }

    private String resolveDepositStatusName(String depositPayStatus) {
        if (Objects.equals(depositPayStatus, LinbangRiskConstants.DEPOSIT_PAY_STATUS_PAID)) {
            return "SUCCESS";
        }
        if (Objects.equals(depositPayStatus, LinbangRiskConstants.DEPOSIT_PAY_STATUS_NOT_REQUIRED)) {
            return "NOT_REQUIRED";
        }
        return "WAITING";
    }

    private void notifyPaymentSuccess(OrderInfoDO order) {
        if (order == null || order.getUserId() == null) {
            return;
        }
        messagePushDispatchService.dispatchSingle("FINANCE_PAYMENT_SUCCESS", "支付成功通知", "PAY",
                order.getId(), order.getUserId(), "订单支付成功");
    }

    private void notifyOrderStatusChanged(OrderInfoDO order, String afterStatus, String remark) {
        if (order == null || order.getUserId() == null) {
            return;
        }
        messagePushDispatchService.dispatchSingle("ORDER_STATUS_CHANGED", "订单状态通知", "ORDER",
                order.getId(), order.getUserId(), StrUtil.blankToDefault(remark, afterStatus));
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
        PayOrderExtensionDO extension = payOrder.getExtensionId() != null
                ? payOrderService.getOrderExtension(payOrder.getExtensionId())
                : payOrderService.getLatestOrderExtension(payOrder.getId());
        AppLinbangPayOrderRespVO respVO = new AppLinbangPayOrderRespVO();
        respVO.setId(payOrder.getId());
        respVO.setOrderId(orderId);
        respVO.setChannelCode(payOrder.getChannelCode());
        if (extension != null && extension.getChannelExtras() != null) {
            String payWay = extension.getChannelExtras().get("payWay");
            respVO.setPayWay(payWay);
            respVO.setPayWayName(resolvePayWayName(payWay));
        }
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

    private Map<String, String> buildPayChannelExtras(String payWay) {
        Map<String, String> extras = new HashMap<>(2);
        extras.put("payWay", payWay);
        return extras;
    }

    private String resolvePayWayName(String payWay) {
        PayWayEnum payWayEnum = PayWayEnum.getByCode(payWay);
        return payWayEnum == null ? null : payWayEnum.getName();
    }

}
