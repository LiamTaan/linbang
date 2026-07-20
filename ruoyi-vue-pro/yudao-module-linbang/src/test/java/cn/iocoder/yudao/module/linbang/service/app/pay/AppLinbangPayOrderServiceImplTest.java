package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.security.config.SecurityProperties;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangWechatMiniProgramPaySubmitRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.channel.PayChannelDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.enums.PayChannelEnum;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.channel.PayChannelService;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppLinbangPayOrderServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AppLinbangPayOrderServiceImpl service;

    @Mock
    private MemberUserService memberUserService;
    @Mock
    private OrderInfoMapper orderInfoMapper;
    @Mock
    private OrderOperateLogMapper orderOperateLogMapper;
    @Mock
    private PayAppService payAppService;
    @Mock
    private PayChannelService payChannelService;
    @Mock
    private PayOrderApi payOrderApi;
    @Mock
    private PayOrderService payOrderService;
    @Mock
    private SocialUserApi socialUserApi;
    @Test
    void submitWechatMiniProgramPay_usesWxLiteAndBoundOpenid() {
        Long userId = 1L;
        Long orderId = 100L;
        Long payOrderId = 900L;
        Long payAppId = 20L;
        MemberUserDO user = MemberUserDO.builder().id(userId).build();
        OrderInfoDO order = OrderInfoDO.builder()
                .id(orderId).userId(userId).status("PENDING_PAY")
                .requireDesc("测试订单").orderAmount(new BigDecimal("12.34")).build();
        PayAppDO payApp = PayAppDO.builder().id(payAppId).appKey("linbang-mini-program")
                .status(CommonStatusEnum.ENABLE.getStatus()).build();
        PayChannelDO channel = PayChannelDO.builder().id(30L).appId(payAppId)
                .code(PayChannelEnum.WX_LITE.getCode()).status(CommonStatusEnum.ENABLE.getStatus()).build();
        PayOrderDO payOrder = PayOrderDO.builder().id(payOrderId).appId(payAppId)
                .status(PayOrderStatusEnum.WAITING.getStatus())
                .expireTime(LocalDateTime.now().plusMinutes(10)).build();
        SocialUserRespDTO socialUser = new SocialUserRespDTO("openid-001", null, null, userId);
        PayOrderSubmitRespVO submitResp = new PayOrderSubmitRespVO();
        submitResp.setStatus(PayOrderStatusEnum.WAITING.getStatus());
        submitResp.setDisplayMode("app");
        submitResp.setDisplayContent("{\"timeStamp\":\"1710000000\",\"nonceStr\":\"nonce\","
                + "\"packageValue\":\"prepay_id=wx001\",\"signType\":\"RSA\",\"paySign\":\"signed\"}");

        when(memberUserService.getOrCreateMemberUser(userId)).thenReturn(user);
        when(orderInfoMapper.selectById(orderId)).thenReturn(order);
        when(payAppService.getAppList()).thenReturn(Collections.singletonList(payApp));
        when(payChannelService.getChannelByAppIdAndCode(payAppId, PayChannelEnum.WX_LITE.getCode()))
                .thenReturn(channel);
        when(payOrderApi.createOrder(any())).thenReturn(payOrderId);
        when(payOrderService.getOrder(payOrderId)).thenReturn(payOrder);
        when(socialUserApi.getSocialUserByUserId(any(), anyLong(), any())).thenReturn(socialUser);
        when(payOrderService.submitOrder(any(), any())).thenReturn(submitResp);
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setMockEnable(false);
        ReflectionTestUtils.setField(service, "securityProperties", securityProperties);

        AppLinbangPayOrderCreateReqVO reqVO = new AppLinbangPayOrderCreateReqVO();
        reqVO.setOrderId(orderId);
        AppLinbangWechatMiniProgramPaySubmitRespVO result = service.submitWechatMiniProgramPay(userId, reqVO);

        ArgumentCaptor<PayOrderSubmitReqVO> submitCaptor = ArgumentCaptor.forClass(PayOrderSubmitReqVO.class);
        verify(payOrderService).submitOrder(submitCaptor.capture(), any());
        assertThat(submitCaptor.getValue().getChannelCode()).isEqualTo(PayChannelEnum.WX_LITE.getCode());
        assertThat(submitCaptor.getValue().getChannelExtras()).containsEntry("openid", "openid-001");
        assertThat(result.getChannelCode()).isEqualTo(PayChannelEnum.WX_LITE.getCode());
        assertThat(result.getPaymentParams().getPackageValue()).isEqualTo("prepay_id=wx001");
        assertThat(result.getPaymentParams().getPaySign()).isEqualTo("signed");
    }

}
