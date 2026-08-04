package cn.iocoder.yudao.module.pay.service.app;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.http.HttpUrlSecurityUtils;
import cn.iocoder.yudao.module.pay.controller.admin.app.vo.PayAppCreateReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.app.vo.PayAppPageReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.app.vo.PayAppUpdateReqVO;
import cn.iocoder.yudao.module.pay.convert.app.PayAppConvert;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.mysql.app.PayAppMapper;
import cn.iocoder.yudao.module.pay.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.pay.enums.notify.PayNotifyTypeEnum;
import cn.iocoder.yudao.module.pay.dal.dataobject.notify.PayNotifyTaskDO;
import cn.iocoder.yudao.module.pay.service.notify.PayNotifyInternalHandler;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import cn.iocoder.yudao.module.pay.service.refund.PayRefundService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.pay.enums.ErrorCodeConstants.*;

/**
 * 支付应用 Service 实现类
 *
 * @author aquan
 */
@Service
@Validated
public class PayAppServiceImpl implements PayAppService {

    @Resource
    private PayAppMapper appMapper;

    @Resource
    @Lazy // 延迟加载，避免循环依赖报错
    private PayOrderService orderService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖报错
    private PayRefundService refundService;
    @Resource
    private ObjectProvider<PayNotifyInternalHandler> internalHandlerProvider;

    @Override
    public Long createApp(PayAppCreateReqVO createReqVO) {
        // 验证 appKey 是否重复
        validateAppKeyUnique(null, createReqVO.getAppKey());
        validateNotifyUrls(createReqVO.getOrderNotifyUrl(), createReqVO.getRefundNotifyUrl(),
                createReqVO.getTransferNotifyUrl());

        // 插入
        PayAppDO app = PayAppConvert.INSTANCE.convert(createReqVO);
        appMapper.insert(app);
        // 返回
        return app.getId();
    }

    @Override
    public void updateApp(PayAppUpdateReqVO updateReqVO) {
        // 校验存在
        validateAppExists(updateReqVO.getId());
        // 验证 appKey 是否重复
        validateAppKeyUnique(updateReqVO.getId(), updateReqVO.getAppKey());
        validateNotifyUrls(updateReqVO.getOrderNotifyUrl(), updateReqVO.getRefundNotifyUrl(),
                updateReqVO.getTransferNotifyUrl());

        // 更新
        PayAppDO updateObj = PayAppConvert.INSTANCE.convert(updateReqVO);
        appMapper.updateById(updateObj);
    }

    void validateAppKeyUnique(Long id, String appKey) {
        PayAppDO app = appMapper.selectByAppKey(appKey);
        if (app == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 appKey 的应用
        if (id == null) {
            throw exception(APP_KEY_EXISTS);
        }
        if (!app.getId().equals(id)) {
            throw exception(APP_KEY_EXISTS);
        }
    }

    private void validateNotifyUrls(String orderNotifyUrl, String refundNotifyUrl, String transferNotifyUrl) {
        validateNotifyUrl(PayNotifyTypeEnum.ORDER.getType(), orderNotifyUrl, "支付结果");
        validateNotifyUrl(PayNotifyTypeEnum.REFUND.getType(), refundNotifyUrl, "退款结果");
        if (transferNotifyUrl != null && !transferNotifyUrl.trim().isEmpty()) {
            validateNotifyUrl(PayNotifyTypeEnum.TRANSFER.getType(), transferNotifyUrl, "转账结果");
        }
    }

    private void validateNotifyUrl(Integer type, String notifyUrl, String label) {
        PayNotifyTaskDO routeProbe = new PayNotifyTaskDO().setType(type).setNotifyUrl(notifyUrl);
        for (PayNotifyInternalHandler handler : internalHandlerProvider) {
            if (handler.supports(routeProbe)) {
                return;
            }
        }
        try {
            HttpUrlSecurityUtils.validatePublicHttpsUrl(notifyUrl);
        } catch (IllegalArgumentException ex) {
            throw exception(APP_NOTIFY_URL_INVALID, label);
        }
    }

    @Override
    public void updateAppStatus(Long id, Integer status) {
        // 校验商户存在
        validateAppExists(id);
        // 更新状态
        appMapper.updateById(new PayAppDO().setId(id).setStatus(status));
    }

    @Override
    public void deleteApp(Long id) {
        // 校验存在
        validateAppExists(id);
        // 校验关联数据是否存在
        if (orderService.getOrderCountByAppId(id) > 0) {
            throw exception(APP_EXIST_ORDER_CANT_DELETE);
        }
        if (refundService.getRefundCountByAppId(id) > 0) {
            throw exception(APP_EXIST_REFUND_CANT_DELETE);
        }

        // 删除
        appMapper.deleteById(id);
    }

    private void validateAppExists(Long id) {
        if (appMapper.selectById(id) == null) {
            throw exception(APP_NOT_FOUND);
        }
    }

    @Override
    public PayAppDO getApp(Long id) {
        return appMapper.selectById(id);
    }

    @Override
    public List<PayAppDO> getAppList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return appMapper.selectByIds(ids);
    }

    @Override
    public List<PayAppDO> getAppList() {
        return appMapper.selectList();
    }

    @Override
    public PageResult<PayAppDO> getAppPage(PayAppPageReqVO pageReqVO) {
        return appMapper.selectPage(pageReqVO);
    }

    @Override
    public PayAppDO validPayApp(Long appId) {
        PayAppDO app = appMapper.selectById(appId);
        return validatePayApp(app);
    }

    @Override
    public PayAppDO validPayApp(String appKey) {
        PayAppDO app = appMapper.selectByAppKey(appKey);
        return validatePayApp(app);
    }

    /**
     * 校验支付应用实体的有效性：存在 + 开启
     *
     * @param app 待校验的支付应用实体
     * @return 校验通过的支付应用实体
     */
    private PayAppDO validatePayApp(PayAppDO app) {
        // 校验是否存在
        if (app == null) {
            throw exception(ErrorCodeConstants.APP_NOT_FOUND);
        }
        // 校验是否禁用
        if (CommonStatusEnum.isDisable(app.getStatus())) {
            throw exception(ErrorCodeConstants.APP_IS_DISABLE);
        }
        return app;
    }

}
