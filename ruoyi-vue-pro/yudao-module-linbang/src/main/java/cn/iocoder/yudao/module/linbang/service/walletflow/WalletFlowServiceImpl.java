package cn.iocoder.yudao.module.linbang.service.walletflow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.WalletFlowDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.WalletFlowPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.WalletFlowRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.WalletFlowSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 钱包流水 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class WalletFlowServiceImpl implements WalletFlowService {

    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private PayOrderMapper payOrderMapper;
    @Resource
    private PayRefundMapper payRefundMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;

    @Override
    public Long createWalletFlow(WalletFlowSaveReqVO createReqVO) {
        // 插入
        WalletFlowDO walletFlow = BeanUtils.toBean(createReqVO, WalletFlowDO.class);
        walletFlowMapper.insert(walletFlow);

        // 返回
        return walletFlow.getId();
    }

    @Override
    public void updateWalletFlow(WalletFlowSaveReqVO updateReqVO) {
        // 校验存在
        validateWalletFlowExists(updateReqVO.getId());
        // 更新
        WalletFlowDO updateObj = BeanUtils.toBean(updateReqVO, WalletFlowDO.class);
        walletFlowMapper.updateById(updateObj);
    }

    @Override
    public void deleteWalletFlow(Long id) {
        // 校验存在
        validateWalletFlowExists(id);
        // 删除
        walletFlowMapper.deleteById(id);
    }

    @Override
        public void deleteWalletFlowListByIds(List<Long> ids) {
        // 删除
        walletFlowMapper.deleteByIds(ids);
        }


    private void validateWalletFlowExists(Long id) {
        if (walletFlowMapper.selectById(id) == null) {
            throw exception(WALLET_FLOW_NOT_EXISTS);
        }
    }

    @Override
    public WalletFlowDO getWalletFlow(Long id) {
        return walletFlowMapper.selectById(id);
    }

    @Override
    public WalletFlowDetailRespVO getWalletFlowDetail(Long id) {
        WalletFlowDO walletFlow = walletFlowMapper.selectById(id);
        if (walletFlow == null) {
            throw exception(WALLET_FLOW_NOT_EXISTS);
        }
        MemberUserDO user = walletFlow.getUserId() == null ? null : memberUserMapper.selectById(walletFlow.getUserId());
        WalletAccountDO walletAccount = walletFlow.getWalletAccountId() == null ? null : walletAccountMapper.selectById(walletFlow.getWalletAccountId());
        OrderInfoDO order = walletFlow.getRelatedOrderId() == null ? null : orderInfoMapper.selectById(walletFlow.getRelatedOrderId());
        OrderUnitDO unit = walletFlow.getRelatedUnitId() == null ? null : orderUnitMapper.selectById(walletFlow.getRelatedUnitId());
        if (unit != null && order == null && unit.getOrderId() != null) {
            order = orderInfoMapper.selectById(unit.getOrderId());
        }
        MerchantInfoDO merchant = unit == null || unit.getMerchantId() == null ? null : merchantInfoMapper.selectById(unit.getMerchantId());
        PayOrderDO payOrder = walletFlow.getRelatedPayOrderId() == null ? null : payOrderMapper.selectById(walletFlow.getRelatedPayOrderId());
        if (payOrder == null && order != null && order.getPayOrderId() != null) {
            payOrder = payOrderMapper.selectById(order.getPayOrderId());
        }
        PayRefundDO refund = walletFlow.getRelatedRefundId() == null ? null : payRefundMapper.selectById(walletFlow.getRelatedRefundId());

        WalletFlowDetailRespVO respVO = WalletFlowDetailAssembler.buildDetail(walletFlow, user);
        respVO.setWalletAccount(WalletFlowDetailAssembler.buildWalletAccount(walletAccount));
        respVO.setOrder(WalletFlowDetailAssembler.buildOrder(order));
        respVO.setUnit(WalletFlowDetailAssembler.buildUnit(unit, merchant));
        respVO.setPayOrder(WalletFlowDetailAssembler.buildPayOrder(payOrder));
        respVO.setRefund(WalletFlowDetailAssembler.buildRefund(refund));
        return respVO;
    }

    @Override
    public PageResult<WalletFlowRespVO> getWalletFlowPage(WalletFlowPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO.getRelatedOrderNo());
        if (StrUtil.isNotBlank(pageReqVO.getRelatedOrderNo()) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        PageResult<WalletFlowDO> pageResult = walletFlowMapper.selectPage(pageReqVO, matchedUserIds, matchedOrderIds);
        List<WalletFlowRespVO> list = BeanUtils.toBean(pageResult.getList(), WalletFlowRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private List<Long> resolveMatchedOrderIds(String relatedOrderNo) {
        if (StrUtil.isBlank(relatedOrderNo)) {
            return null;
        }
        return convertList(orderInfoMapper.selectListByOrderNo(relatedOrderNo), OrderInfoDO::getId);
    }

    private void fillDisplayInfo(List<WalletFlowRespVO> list) {
        Set<Long> userIds = convertSet(list, WalletFlowRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        Set<Long> orderIds = convertSet(list, WalletFlowRespVO::getRelatedOrderId,
                item -> item.getRelatedOrderId() != null);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> unitIds = convertSet(list, WalletFlowRespVO::getRelatedUnitId,
                item -> item.getRelatedUnitId() != null);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        Set<Long> walletAccountIds = convertSet(list, WalletFlowRespVO::getWalletAccountId,
                item -> item.getWalletAccountId() != null);
        Map<Long, WalletAccountDO> walletAccountMap = walletAccountIds.isEmpty() ? Collections.emptyMap()
                : convertMap(walletAccountMapper.selectBatchIds(walletAccountIds), WalletAccountDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            WalletAccountDO walletAccount = walletAccountMap.get(item.getWalletAccountId());
            if (walletAccount != null) {
                item.setWalletRoleCode(walletAccount.getRoleCode());
                item.setWalletStatus(walletAccount.getStatus());
                item.setWalletAvailableAmount(walletAccount.getAvailableAmount());
            }
            OrderInfoDO order = orderMap.get(item.getRelatedOrderId());
            if (order != null) {
                item.setRelatedOrderNo(order.getOrderNo());
            }
            OrderUnitDO unit = unitMap.get(item.getRelatedUnitId());
            if (unit != null) {
                item.setRelatedUnitNo(unit.getUnitNo());
            }
        });
    }

}
