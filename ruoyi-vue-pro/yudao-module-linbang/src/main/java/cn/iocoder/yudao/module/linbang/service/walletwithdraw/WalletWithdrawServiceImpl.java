package cn.iocoder.yudao.module.linbang.service.walletwithdraw;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
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
import cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.aggregate.AggregatePayClientConfig;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.channel.PayChannelService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
@Slf4j
public class WalletWithdrawServiceImpl implements WalletWithdrawService {

    private static final String[] PREFERRED_TRANSFER_CHANNELS = {
            PayChannelEnum.AGGREGATE.getCode()
    };
    private static final String WITHDRAW_STATUS_PROCESSING = "PROCESSING";
    private static final String WITHDRAW_STATUS_FAILED = "FAILED";
    private static final String AUDIT_STATUS_APPROVED = "APPROVED";
    private static final String AUDIT_STATUS_REJECTED = "REJECTED";

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
    @Resource
    private TransactionTemplate transactionTemplate;

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
                .eq(WalletFlowDO::getRelatedWithdrawId, withdraw.getId())
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
    public void auditWalletWithdraw(WithdrawAuditReqVO reqVO) {
        if (!AUDIT_STATUS_APPROVED.equals(reqVO.getAuditStatus())
                && !AUDIT_STATUS_REJECTED.equals(reqVO.getAuditStatus())) {
            throw exception(WALLET_WITHDRAW_AUDIT_STATUS_INVALID);
        }
        WalletWithdrawDO walletWithdraw = transactionTemplate.execute(status -> {
            WalletWithdrawDO current = walletWithdrawMapper.selectOneForUpdate(WalletWithdrawDO::getId, reqVO.getId());
            if (current == null) {
                throw exception(WALLET_WITHDRAW_NOT_EXISTS);
            }
            if (!Objects.equals(current.getAuditStatus(), "PENDING")
                    || !Objects.equals(current.getStatus(), "PENDING")) {
                throw exception(WALLET_WITHDRAW_AUDIT_STATUS_INVALID);
            }
            WalletWithdrawDO updateObj = new WalletWithdrawDO();
            updateObj.setId(reqVO.getId());
            updateObj.setAuditStatus(reqVO.getAuditStatus());
            updateObj.setAuditRemark(reqVO.getAuditRemark());
            updateObj.setRejectReason(reqVO.getRejectReason());
            updateObj.setAuditBy(SecurityFrameworkUtils.getLoginUserId());
            updateObj.setAuditTime(LocalDateTime.now());
            updateObj.setStatus(AUDIT_STATUS_APPROVED.equals(reqVO.getAuditStatus())
                    ? WITHDRAW_STATUS_PROCESSING : "REJECTED");
            int updated = walletWithdrawMapper.update(updateObj, new LambdaUpdateWrapper<WalletWithdrawDO>()
                    .eq(WalletWithdrawDO::getId, current.getId())
                    .eq(WalletWithdrawDO::getAuditStatus, "PENDING")
                    .eq(WalletWithdrawDO::getStatus, "PENDING"));
            if (updated == 0) {
                throw exception(WALLET_WITHDRAW_AUDIT_STATUS_INVALID);
            }
            if (AUDIT_STATUS_REJECTED.equals(reqVO.getAuditStatus())) {
                rollbackRejectedWithdraw(current);
            }
            return current;
        });
        if (AUDIT_STATUS_APPROVED.equals(reqVO.getAuditStatus())) {
            try {
                createTransfer(walletWithdraw);
            } catch (Exception ex) {
                log.error("[auditWalletWithdraw][withdraw({}) 发起打款失败]", walletWithdraw.getId(), ex);
                handleTransferCreateFailure(walletWithdraw, ex);
            }
        }
        messagePushDispatchService.dispatchSingle("lb_withdraw_audited", "提现审核结果通知", "WITHDRAW",
                walletWithdraw.getId(), walletWithdraw.getUserId(), "管理员审核提现后自动通知申请人");
    }

    @Override
    public Long retryWalletWithdrawTransfer(Long id) {
        WalletWithdrawDO withdraw = transactionTemplate.execute(status -> {
            WalletWithdrawDO current = walletWithdrawMapper.selectOneForUpdate(WalletWithdrawDO::getId, id);
            if (current == null) {
                throw exception(WALLET_WITHDRAW_NOT_EXISTS);
            }
            if (!Objects.equals(current.getAuditStatus(), AUDIT_STATUS_APPROVED)
                    || !Objects.equals(current.getStatus(), WITHDRAW_STATUS_FAILED)) {
                throw exception(WALLET_WITHDRAW_AUDIT_STATUS_INVALID);
            }
            WalletAccountDO walletAccount = walletAccountMapper.selectOneForUpdate(
                    WalletAccountDO::getId, current.getWalletAccountId());
            BigDecimal amount = current.getRealAmount() == null ? BigDecimal.ZERO : current.getRealAmount();
            if (walletAccount == null || amount.compareTo(BigDecimal.ZERO) <= 0
                    || walletAccount.getAvailableAmount() == null
                    || walletAccount.getAvailableAmount().compareTo(amount) < 0) {
                throw exception(WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH);
            }
            BigDecimal beforeAvailable = walletAccount.getAvailableAmount();
            BigDecimal beforeFrozen = walletAccount.getFrozenAmount() == null
                    ? BigDecimal.ZERO : walletAccount.getFrozenAmount();
            int walletUpdated = walletAccountMapper.update(null, new LambdaUpdateWrapper<WalletAccountDO>()
                    .eq(WalletAccountDO::getId, walletAccount.getId())
                    .ge(WalletAccountDO::getAvailableAmount, amount)
                    .set(WalletAccountDO::getAvailableAmount, beforeAvailable.subtract(amount))
                    .set(WalletAccountDO::getFrozenAmount, beforeFrozen.add(amount)));
            if (walletUpdated == 0) {
                throw exception(WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH);
            }
            int updated = walletWithdrawMapper.update(null, new LambdaUpdateWrapper<WalletWithdrawDO>()
                    .eq(WalletWithdrawDO::getId, current.getId())
                    .eq(WalletWithdrawDO::getAuditStatus, AUDIT_STATUS_APPROVED)
                    .eq(WalletWithdrawDO::getStatus, WITHDRAW_STATUS_FAILED)
                    .set(WalletWithdrawDO::getStatus, WITHDRAW_STATUS_PROCESSING)
                    .set(WalletWithdrawDO::getTransferErrorMsg, null));
            if (updated == 0) {
                throw exception(WALLET_WITHDRAW_AUDIT_STATUS_INVALID);
            }
            walletFlowMapper.insert(WalletFlowDO.builder()
                    .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                    .userId(current.getUserId())
                    .walletAccountId(walletAccount.getId())
                    .bizType("WITHDRAW_RETRY_FREEZE")
                    .flowType("OUT")
                    .changeAmount(amount.negate())
                    .beforeAmount(beforeAvailable)
                    .afterAmount(beforeAvailable.subtract(amount))
                    .relatedWithdrawId(current.getId())
                    .remark("提现打款失败后重新冻结余额")
                    .createTime(LocalDateTime.now())
                    .build());
            return current;
        });
        try {
            return createTransfer(withdraw);
        } catch (Exception ex) {
            log.error("[retryWalletWithdrawTransfer][withdraw({}) 重新发起打款失败]", withdraw.getId(), ex);
            handleTransferCreateFailure(withdraw, ex);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWalletWithdrawTransferred(PayTransferNotifyReqDTO notifyReqDTO) {
        if (notifyReqDTO == null || StrUtil.isBlank(notifyReqDTO.getMerchantTransferId())
                || notifyReqDTO.getPayTransferId() == null) {
            throw exception(WALLET_WITHDRAW_TRANSFER_NOTIFY_INVALID);
        }
        final Long withdrawId;
        try {
            withdrawId = Long.valueOf(notifyReqDTO.getMerchantTransferId());
        } catch (RuntimeException ex) {
            throw exception(WALLET_WITHDRAW_TRANSFER_NOTIFY_INVALID);
        }
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(withdrawId);
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
        if (transfer.getPrice() == null || withdraw.getRealAmount() == null
                || transfer.getPrice() != toFen(withdraw.getRealAmount())) {
            throw exception(WALLET_WITHDRAW_TRANSFER_NOTIFY_INVALID);
        }
        WalletAccountDO walletAccount = walletAccountMapper.selectById(withdraw.getWalletAccountId());
        if (walletAccount == null) {
            throw exception(WALLET_ACCOUNT_NOT_EXISTS);
        }
        reconcileTerminalTransfer(withdraw, walletAccount, transfer);
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
                item.setUserMobile(maskMobile(user.getMobile()));
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
        try {
            PayTransferCreateRespDTO transferRespDTO = payTransferApi.createTransfer(transferReqDTO);
            linkPayTransfer(withdraw, transferRespDTO.getId());
            return transferRespDTO.getId();
        } catch (Exception ex) {
            return recoverTransferAfterSubmitException(withdraw, payApp, ex);
        }
    }

    private Long recoverTransferAfterSubmitException(WalletWithdrawDO withdraw, PayAppDO payApp, Exception submitEx) {
        try {
            PayTransferRespDTO transfer = payTransferApi.getTransferByMerchantTransferId(
                    payApp.getAppKey(), String.valueOf(withdraw.getId()));
            if (transfer == null) {
                // A not-found result immediately after a timeout does not prove the channel rejected the transfer.
                // Keep funds frozen until reconciliation establishes a terminal result.
                log.error("[recoverTransferAfterSubmitException][withdraw({}) transfer not visible after submit failure]",
                        withdraw.getId(), submitEx);
                markTransferSubmissionUncertain(withdraw.getId());
                return null;
            }
            if (transfer.getId() == null
                    || !Objects.equals(transfer.getMerchantTransferId(), String.valueOf(withdraw.getId()))
                    || transfer.getPrice() == null || transfer.getPrice() != toFen(withdraw.getRealAmount())) {
                log.error("[recoverTransferAfterSubmitException][withdraw({}) 转账单关键字段不一致]", withdraw.getId());
                markTransferSubmissionUncertain(withdraw.getId());
                return null;
            }
            linkPayTransfer(withdraw, transfer.getId());
            if (PayTransferStatusEnum.isSuccessOrClosed(transfer.getStatus())) {
                try {
                    WalletAccountDO walletAccount = walletAccountMapper.selectById(withdraw.getWalletAccountId());
                    if (walletAccount == null) {
                        throw exception(WALLET_ACCOUNT_NOT_EXISTS);
                    }
                    reconcileTerminalTransfer(withdraw, walletAccount, transfer);
                } catch (Exception reconcileEx) {
                    // 已确认渠道单存在时不得按“创建失败”解冻；保留关联关系，等待回调、轮询或人工重试对账。
                    log.error("[recoverTransferAfterSubmitException][withdraw({}) 转账终态对账失败，保持资金冻结]",
                            withdraw.getId(), reconcileEx);
                    markTransferSubmissionUncertain(withdraw.getId());
                }
            } else if (!PayTransferStatusEnum.isWaitingOrProcessing(transfer.getStatus())) {
                log.error("[recoverTransferAfterSubmitException][withdraw({}) 转账单状态({}) 无法识别，等待人工核对]",
                        withdraw.getId(), transfer.getStatus());
                markTransferSubmissionUncertain(withdraw.getId());
            }
            return transfer.getId();
        } catch (Exception queryEx) {
            // 无法证明渠道转账单不存在时必须保持余额冻结，等待支付模块轮询或人工核对。
            log.error("[recoverTransferAfterSubmitException][withdraw({}) 查询支付转账单失败，保持处理中]",
                    withdraw.getId(), queryEx);
            markTransferSubmissionUncertain(withdraw.getId());
            return null;
        }
    }

    private void linkPayTransfer(WalletWithdrawDO withdraw, Long payTransferId) {
        if (payTransferId == null) {
            markTransferSubmissionUncertain(withdraw.getId());
            throw new IllegalStateException("Pay transfer id must not be null");
        }
        int updated = walletWithdrawMapper.update(null, new LambdaUpdateWrapper<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getId, withdraw.getId())
                .eq(WalletWithdrawDO::getStatus, WITHDRAW_STATUS_PROCESSING)
                .and(wrapper -> wrapper.isNull(WalletWithdrawDO::getPayTransferId)
                        .or().eq(WalletWithdrawDO::getPayTransferId, payTransferId))
                .set(WalletWithdrawDO::getPayTransferId, payTransferId)
                .set(WalletWithdrawDO::getTransferErrorMsg, null));
        if (updated > 0) {
            return;
        }
        WalletWithdrawDO latest = walletWithdrawMapper.selectById(withdraw.getId());
        if (latest != null && Objects.equals(latest.getPayTransferId(), payTransferId)) {
            return;
        }
        markTransferSubmissionUncertain(withdraw.getId());
        throw new IllegalStateException("Withdraw transfer link conflict: " + withdraw.getId());
    }

    private void markTransferSubmissionUncertain(Long withdrawId) {
        walletWithdrawMapper.update(null, new LambdaUpdateWrapper<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getId, withdrawId)
                .eq(WalletWithdrawDO::getStatus, WITHDRAW_STATUS_PROCESSING)
                .set(WalletWithdrawDO::getTransferErrorMsg, "提现打款结果待核对"));
    }

    private void reconcileTerminalTransfer(WalletWithdrawDO withdraw, WalletAccountDO walletAccount,
                                           PayTransferRespDTO transfer) {
        if (PayTransferStatusEnum.isSuccess(transfer.getStatus())) {
            linbangFinanceService.handleWithdrawTransferSuccess(walletAccount, withdraw.getId(), transfer);
            messagePushDispatchService.dispatchSingleIdempotent("lb_withdraw_arrived", "提现到账通知", "WITHDRAW",
                    withdraw.getId(), withdraw.getUserId(), "提现打款成功后通知申请人",
                    "lb_withdraw_arrived:" + withdraw.getId() + ":" + transfer.getId());
        } else if (PayTransferStatusEnum.isClosed(transfer.getStatus())) {
            linbangFinanceService.handleWithdrawTransferFailed(walletAccount, withdraw.getId(), transfer);
            messagePushDispatchService.dispatchSingleIdempotent("lb_withdraw_failed", "提现失败通知", "WITHDRAW",
                    withdraw.getId(), withdraw.getUserId(), "提现打款失败后通知申请人",
                    "lb_withdraw_failed:" + withdraw.getId() + ":" + transfer.getId());
        }
    }

    private void rollbackRejectedWithdraw(WalletWithdrawDO withdraw) {
        WalletAccountDO walletAccount = withdraw.getWalletAccountId() == null ? null
                : walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, withdraw.getWalletAccountId());
        if (walletAccount == null) {
            throw exception(WALLET_ACCOUNT_NOT_EXISTS);
        }
        BigDecimal amount = withdraw.getRealAmount() == null ? BigDecimal.ZERO : withdraw.getRealAmount();
        BigDecimal beforeAvailable = walletAccount.getAvailableAmount() == null ? BigDecimal.ZERO : walletAccount.getAvailableAmount();
        BigDecimal beforeFrozen = walletAccount.getFrozenAmount() == null ? BigDecimal.ZERO : walletAccount.getFrozenAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || beforeFrozen.compareTo(amount) < 0) {
            throw exception(WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH);
        }
        BigDecimal afterFrozen = beforeFrozen.subtract(amount);
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .availableAmount(beforeAvailable.add(amount))
                .frozenAmount(afterFrozen)
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
                .relatedWithdrawId(withdraw.getId())
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
        if (!Objects.equals(channel.getCode(), PayChannelEnum.AGGREGATE.getCode())) {
            return;
        }
        if (!(channel.getConfig() instanceof AggregatePayClientConfig)) {
            throw exception(WALLET_WITHDRAW_TRANSFER_UNSUPPORTED);
        }
        AggregatePayClientConfig config = (AggregatePayClientConfig) channel.getConfig();
        if (StrUtil.hasBlank(config.getPrivateKeyFilePath(), config.getPrivateKeyPassword(),
                config.getYsepayPublicKeyFilePath())) {
            throw new IllegalStateException(
                    "聚合支付银盛证书配置未补齐，请配置 privateKeyFilePath/privateKeyPassword/ysepayPublicKeyFilePath");
        }
    }

    private void handleTransferCreateFailure(WalletWithdrawDO withdraw, Exception ex) {
        WalletAccountDO walletAccount = withdraw.getWalletAccountId() == null ? null
                : walletAccountMapper.selectById(withdraw.getWalletAccountId());
        if (walletAccount == null) {
            throw exception(WALLET_ACCOUNT_NOT_EXISTS);
        }
        PayTransferRespDTO transfer = new PayTransferRespDTO();
        transfer.setId(withdraw.getPayTransferId());
        transfer.setNo(withdraw.getPayTransferNo());
        transfer.setChannelErrorMsg(resolveTransferCreateErrorMsg(ex));
        linbangFinanceService.handleWithdrawTransferFailed(walletAccount, withdraw.getId(), transfer);
    }

    private String resolveTransferCreateErrorMsg(Exception ex) {
        return "提现打款发起失败";
    }

    private String maskMobile(String mobile) {
        if (StrUtil.isBlank(mobile) || mobile.length() < 7) {
            return mobile == null ? null : "******";
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
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
        return MoneyUtils.yuanToFen(amount);
    }

}
