package cn.iocoder.yudao.module.linbang.service.walletwithdraw;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.WalletWithdrawDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.service.finance.LinbangFinanceService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayTransferNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.transfer.PayTransferApi;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferCreateRespDTO;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferRespDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.channel.PayChannelDO;
import cn.iocoder.yudao.module.pay.enums.PayChannelEnum;
import cn.iocoder.yudao.module.pay.enums.transfer.PayTransferStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.channel.PayChannelService;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 提现申请 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class WalletWithdrawServiceImpl implements WalletWithdrawService {

    private static final String[] PREFERRED_TRANSFER_CHANNELS = {
            PayChannelEnum.AGGREGATE.getCode()
    };

    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private PayTransferApi payTransferApi;
    @Resource
    private PayAppService payAppService;
    @Resource
    private PayChannelService payChannelService;
    @Resource
    private LinbangFinanceService linbangFinanceService;

    @Override
    public Long createWalletWithdraw(WalletWithdrawSaveReqVO createReqVO) {
        // 插入
        WalletWithdrawDO walletWithdraw = BeanUtils.toBean(createReqVO, WalletWithdrawDO.class);
        walletWithdrawMapper.insert(walletWithdraw);

        // 返回
        return walletWithdraw.getId();
    }

    @Override
    public void updateWalletWithdraw(WalletWithdrawSaveReqVO updateReqVO) {
        // 校验存在
        validateWalletWithdrawExists(updateReqVO.getId());
        // 更新
        WalletWithdrawDO updateObj = BeanUtils.toBean(updateReqVO, WalletWithdrawDO.class);
        walletWithdrawMapper.updateById(updateObj);
    }

    @Override
    public void deleteWalletWithdraw(Long id) {
        // 校验存在
        validateWalletWithdrawExists(id);
        // 删除
        walletWithdrawMapper.deleteById(id);
    }

    @Override
        public void deleteWalletWithdrawListByIds(List<Long> ids) {
        // 删除
        walletWithdrawMapper.deleteByIds(ids);
        }


    private void validateWalletWithdrawExists(Long id) {
        if (walletWithdrawMapper.selectById(id) == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
    }

    @Override
    public WalletWithdrawDO getWalletWithdraw(Long id) {
        return walletWithdrawMapper.selectById(id);
    }

    @Override
    public WalletWithdrawDetailRespVO getWalletWithdrawDetail(Long id) {
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(id);
        if (withdraw == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
        MemberUserDO user = withdraw.getUserId() == null ? null : memberUserMapper.selectById(withdraw.getUserId());
        WalletAccountDO walletAccount = withdraw.getWalletAccountId() == null ? null : walletAccountMapper.selectById(withdraw.getWalletAccountId());
        WalletBankCardDO bankCard = withdraw.getBankCardId() == null ? null : walletBankCardMapper.selectById(withdraw.getBankCardId());
        List<WalletFlowDO> flows = walletFlowMapper.selectList(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getWalletAccountId, withdraw.getWalletAccountId())
                .eq(WalletFlowDO::getUserId, withdraw.getUserId())
                .or(wrapper -> wrapper.like(WalletFlowDO::getRemark, withdraw.getWithdrawNo()))
                .orderByDesc(WalletFlowDO::getCreateTime, WalletFlowDO::getId)
                .last("LIMIT 10"));

        WalletWithdrawDetailRespVO respVO = BeanUtils.toBean(withdraw, WalletWithdrawDetailRespVO.class);
        respVO.setUser(WalletWithdrawDetailAssembler.buildUser(user));
        respVO.setWalletAccount(WalletWithdrawDetailAssembler.buildWalletAccount(walletAccount));
        respVO.setBankCard(WalletWithdrawDetailAssembler.buildBankCard(bankCard));
        respVO.setRelatedFlows(WalletWithdrawDetailAssembler.buildFlows(flows));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditWalletWithdraw(WithdrawAuditReqVO reqVO) {
        WalletWithdrawDO walletWithdraw = walletWithdrawMapper.selectById(reqVO.getId());
        if (walletWithdraw == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
        if (!Objects.equals(walletWithdraw.getAuditStatus(), "PENDING")) {
            throw exception(WALLET_WITHDRAW_AUDIT_STATUS_INVALID);
        }
        WalletWithdrawDO updateObj = new WalletWithdrawDO();
        updateObj.setId(reqVO.getId());
        updateObj.setAuditStatus(reqVO.getAuditStatus());
        updateObj.setAuditRemark(reqVO.getAuditRemark());
        updateObj.setRejectReason(reqVO.getRejectReason());
        updateObj.setAuditBy(SecurityFrameworkUtils.getLoginUserId());
        updateObj.setAuditTime(LocalDateTime.now());
        if ("APPROVED".equals(reqVO.getAuditStatus())) {
            updateObj.setStatus("APPROVED");
        } else if ("REJECTED".equals(reqVO.getAuditStatus())) {
            updateObj.setStatus("REJECTED");
        }
        walletWithdrawMapper.updateById(updateObj);
        if ("APPROVED".equals(reqVO.getAuditStatus())) {
            createTransfer(walletWithdraw);
        } else if ("REJECTED".equals(reqVO.getAuditStatus())) {
            rollbackRejectedWithdraw(walletWithdraw);
        }
        messagePushDispatchService.dispatchSingle("lb_withdraw_audited", "提现审核结果通知", "WITHDRAW",
                walletWithdraw.getId(), walletWithdraw.getUserId(), "管理员审核提现后自动通知申请人");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long retryWalletWithdrawTransfer(Long id) {
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(id);
        if (withdraw == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
        if (!Objects.equals(withdraw.getAuditStatus(), "APPROVED")
                || (!Objects.equals(withdraw.getStatus(), "FAILED") && !Objects.equals(withdraw.getStatus(), "APPROVED"))) {
            throw exception(WALLET_WITHDRAW_AUDIT_STATUS_INVALID);
        }
        walletWithdrawMapper.updateById(WalletWithdrawDO.builder()
                .id(withdraw.getId())
                .status("APPROVED")
                .transferErrorMsg(null)
                .build());
        return createTransfer(withdraw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWalletWithdrawTransferred(PayTransferNotifyReqDTO notifyReqDTO) {
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(Long.valueOf(notifyReqDTO.getMerchantTransferId()));
        if (withdraw == null) {
            throw exception(WALLET_WITHDRAW_TRANSFER_NOTIFY_INVALID);
        }
        if (withdraw.getPayTransferId() != null
                && !ObjectUtil.equal(withdraw.getPayTransferId(), notifyReqDTO.getPayTransferId())) {
            throw exception(WALLET_WITHDRAW_TRANSFER_NOTIFY_INVALID);
        }
        PayTransferRespDTO transfer = payTransferApi.getTransfer(notifyReqDTO.getPayTransferId());
        if (transfer == null
                || !ObjectUtil.equal(transfer.getMerchantTransferId(), notifyReqDTO.getMerchantTransferId())) {
            throw exception(WALLET_WITHDRAW_TRANSFER_NOTIFY_INVALID);
        }
        WalletAccountDO walletAccount = walletAccountMapper.selectById(withdraw.getWalletAccountId());
        if (walletAccount == null) {
            throw exception(WALLET_ACCOUNT_NOT_EXISTS);
        }
        if (PayTransferStatusEnum.isSuccess(transfer.getStatus())) {
            linbangFinanceService.handleWithdrawTransferSuccess(walletAccount, withdraw.getId(), transfer);
            messagePushDispatchService.dispatchSingle("lb_withdraw_arrived", "提现到账通知", "WITHDRAW",
                    withdraw.getId(), withdraw.getUserId(), "提现打款成功后通知申请人");
        } else if (PayTransferStatusEnum.isClosed(transfer.getStatus())) {
            linbangFinanceService.handleWithdrawTransferFailed(walletAccount, withdraw.getId(), transfer);
            messagePushDispatchService.dispatchSingle("lb_withdraw_failed", "提现失败通知", "WITHDRAW",
                    withdraw.getId(), withdraw.getUserId(), "提现打款失败后通知申请人");
        }
    }

    @Override
    public PageResult<WalletWithdrawRespVO> getWalletWithdrawPage(WalletWithdrawPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<WalletWithdrawDO> pageResult = walletWithdrawMapper.selectPage(pageReqVO, matchedUserIds);
        List<WalletWithdrawRespVO> list = BeanUtils.toBean(pageResult.getList(), WalletWithdrawRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<WalletWithdrawRespVO> list) {
        Set<Long> userIds = convertSet(list, WalletWithdrawRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        Set<Long> walletAccountIds = convertSet(list, WalletWithdrawRespVO::getWalletAccountId,
                item -> item.getWalletAccountId() != null);
        Map<Long, WalletAccountDO> walletAccountMap = walletAccountIds.isEmpty() ? Collections.emptyMap()
                : convertMap(walletAccountMapper.selectBatchIds(walletAccountIds), WalletAccountDO::getId);
        Set<Long> bankCardIds = convertSet(list, WalletWithdrawRespVO::getBankCardId,
                item -> item.getBankCardId() != null);
        Map<Long, WalletBankCardDO> bankCardMap = bankCardIds.isEmpty() ? Collections.emptyMap()
                : convertMap(walletBankCardMapper.selectBatchIds(bankCardIds), WalletBankCardDO::getId);
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
            }
            WalletBankCardDO bankCard = bankCardMap.get(item.getBankCardId());
            if (bankCard != null) {
                item.setBankName(bankCard.getBankName());
                item.setCardNoMask(bankCard.getCardNoMask());
                item.setBankAccountName(bankCard.getAccountName());
            }
        });
    }

    private Long createTransfer(WalletWithdrawDO withdraw) {
        WalletBankCardDO bankCard = withdraw.getBankCardId() == null ? null : walletBankCardMapper.selectById(withdraw.getBankCardId());
        if (bankCard == null) {
            throw exception(WALLET_BANK_CARD_INVALID);
        }
        validateTransferBankCard(bankCard);
        PayAppDO payApp = getEnabledPayApp();
        PayChannelDO channel = selectTransferChannel(payApp.getId());
        assertTransferChannelSupported(channel);
        PayTransferCreateReqDTO transferReqDTO = new PayTransferCreateReqDTO()
                .setAppKey(payApp.getAppKey())
                .setUserIp("127.0.0.1")
                .setUserId(withdraw.getUserId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setMerchantTransferId(String.valueOf(withdraw.getId()))
                .setSubject("邻里互助提现-" + withdraw.getWithdrawNo())
                .setPrice(toFen(withdraw.getRealAmount()))
                .setUserAccount(bankCard.getTransferAccount())
                .setUserName(bankCard.getAccountName())
                .setChannelExtras(buildTransferExtras(bankCard))
                .setChannelCode(channel.getCode());
        PayTransferCreateRespDTO transferRespDTO = payTransferApi.createTransfer(transferReqDTO);
        walletWithdrawMapper.updateById(WalletWithdrawDO.builder()
                .id(withdraw.getId())
                .payTransferId(transferRespDTO.getId())
                .build());
        return transferRespDTO.getId();
    }

    private void rollbackRejectedWithdraw(WalletWithdrawDO withdraw) {
        WalletAccountDO walletAccount = withdraw.getWalletAccountId() == null ? null : walletAccountMapper.selectById(withdraw.getWalletAccountId());
        if (walletAccount == null) {
            throw exception(WALLET_ACCOUNT_NOT_EXISTS);
        }
        BigDecimal amount = withdraw.getRealAmount() == null ? BigDecimal.ZERO : withdraw.getRealAmount();
        BigDecimal beforeAvailable = walletAccount.getAvailableAmount() == null ? BigDecimal.ZERO : walletAccount.getAvailableAmount();
        BigDecimal beforeFrozen = walletAccount.getFrozenAmount() == null ? BigDecimal.ZERO : walletAccount.getFrozenAmount();
        BigDecimal afterFrozen = beforeFrozen.subtract(amount);
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .availableAmount(beforeAvailable.add(amount))
                .frozenAmount(afterFrozen.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : afterFrozen)
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(withdraw.getUserId())
                .walletAccountId(walletAccount.getId())
                .bizType("WITHDRAW_REJECTED")
                .flowType("IN")
                .changeAmount(amount)
                .beforeAmount(beforeAvailable)
                .afterAmount(beforeAvailable.add(amount))
                .remark("提现审核驳回，退回可提现余额")
                .createTime(LocalDateTime.now())
                .build());
    }

    private PayAppDO getEnabledPayApp() {
        List<PayAppDO> payApps = payAppService.getAppList();
        for (PayAppDO payApp : payApps) {
            if (Objects.equals(payApp.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
                return payApp;
            }
        }
        throw exception(WALLET_WITHDRAW_PAY_APP_NOT_CONFIGURED);
    }

    private PayChannelDO selectTransferChannel(Long appId) {
        List<PayChannelDO> channels = payChannelService.getEnableChannelList(appId);
        for (String channelCode : PREFERRED_TRANSFER_CHANNELS) {
            for (PayChannelDO channel : channels) {
                if (Objects.equals(channel.getCode(), channelCode)) {
                    return channel;
                }
            }
        }
        throw exception(WALLET_WITHDRAW_TRANSFER_UNSUPPORTED);
    }

    private void assertTransferChannelSupported(PayChannelDO channel) {
        // 聚合支付已支持通过银盛商户付款接口执行提现打款。
    }

    private Map<String, String> buildTransferExtras(WalletBankCardDO bankCard) {
        Map<String, String> extras = new HashMap<>(8);
        extras.put("bank_name", bankCard.getBankName());
        extras.put("bank_code", bankCard.getBankCode());
        extras.put("bank_province", bankCard.getBankProvince());
        extras.put("bank_city", bankCard.getBankCity());
        extras.put("bank_account_type", "personal");
        extras.put("bank_card_type", "debit");
        extras.put("bank_telephone_no", bankCard.getReservedMobile());
        return extras;
    }

    private void validateTransferBankCard(WalletBankCardDO bankCard) {
        if (StrUtil.hasBlank(bankCard.getTransferAccount(), bankCard.getBankName(), bankCard.getBankCode(),
                bankCard.getAccountName(), bankCard.getBankProvince(), bankCard.getBankCity(),
                bankCard.getReservedMobile())) {
            throw exception(WALLET_BANK_CARD_INVALID);
        }
    }

    private int toFen(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

}
